package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.calculateMagicDamage
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheck
import com.tangledwebgames.masterofdoors.util.listBuilder

object AcidRain : BattleAction {
    override val id: String = "acid_rain"
    override val name: String = "Acid Rain"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String = """
        Weak magic attack which makes the target more vulnerable to damage. Severity and duration of debuff depeond on Spellcraft vs Defense check.
        Base power: 18
    """.trimIndent()

    val baseDamage: Int = 18

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder{
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${actor.name} casts $name!"
            wait = WAIT_AFTER_ACTION_DECLARATION
            statusChange(battlerId = actor.id, mana = actor.mana)
        }

        val damage = calculateMagicDamage(
            actor = actor, target = target, baseDamage = baseDamage
        )
        add(damageViewStateChange(target = target, damage = damage, isCrit = false))

        if (!target.isAlive()) {
            add(targetDiesViewStateChange(target))
        } else if (damage > 0) {
            var success: Boolean = false
            val spellcraftCheck = statCheck(
                stat = actor.spellcraft,
                modifier = 0,
                difficulty = target.defense
            )

            val incomingDamageUpName: String
            val incomingDamageRate: Pair<Int, Int>
            if (spellcraftCheck > 3) {
                incomingDamageRate = 4 to 6
                incomingDamageUpName = "Damage Taken +50%"
            } else {
                incomingDamageRate = 4 to 5
                incomingDamageUpName = "Damage Taken +25%"
            }
            val duration = when {
                spellcraftCheck < 4 -> 1
                spellcraftCheck < 0 -> 2
                spellcraftCheck == 0 -> 3
                spellcraftCheck < 4 -> 4
                else -> 5
            }

            target.statusEffects.firstOrNull { it.id == INCOMING_DAMAGE_UP_ID }
                ?.let { effect ->
                    if (effect.statSet.damageResistance.first > incomingDamageRate.first) {
                        effect.statSet.damageResistance = incomingDamageRate
                        effect.name = incomingDamageUpName
                        success = true
                    }
                    if (effect.setDurationToLonger(duration)) {
                        success = true
                    }
                } ?: run {
                    target.addStatusEffect(
                        id = INCOMING_DAMAGE_UP_ID,
                        name = incomingDamageUpName,
                        duration = duration,
                        statSet = StatSet(damageResistance = incomingDamageRate)
                    )
                success = true
            }

            if (success) {
                viewStateChange {
                    logMessage = "${target.name} is more vulnerable to damage."
                    statusChange(
                        battlerId = target.id,
                        statusEffects = target.statusEffects.deepCopy()
                    )
                    wait = MEDIUM_BATTLE_WAIT
                }.also { add(it) }
            }
        }
    }
}