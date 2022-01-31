package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.util.listBuilder

object Disarm : BattleAction {

    override val id: String = "disarm"
    override val name: String = "Disarm"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
        Physical attack which also applies Damage Down to target. Damage is reduced by 25% - 50% for 2 - 5 turns, depending on Precision vs. Precision check.
        Base power: $baseDamage
    """.trimIndent()
    val baseDamage: Int = Attack.baseDamage - 5

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${actor.name} attempts to disarm ${target.name}!"
            wait = BattleConstants.WAIT_AFTER_ACTION_DECLARATION
            statusChange(battlerId = actor.id, mana = actor.mana)
        }.also { add(it) }


        val isCrit = BattleFunctions.statCheckPassFail(
            stat = actor.precision,
            modifier = -5,
            difficulty = target.defense
        )

        val damage = BattleFunctions.calculatePhysicalDamage(
            actor = actor, target = target, baseDamage = baseDamage, isCrit = isCrit
        )
        target.health = (target.health - damage).coerceAtLeast(0)

        add(damageViewStateChange(target = target, damage = damage, isCrit = isCrit))

        if (isCrit) {
            add(critViewStateChange())
        }

        if (!target.isAlive()) {
            add(targetDiesViewStateChange(target))
        }

        if (damage > 0 && target.isAlive()) {
            var success: Boolean = false
            val precisionCheck = BattleFunctions.statCheck(
                stat = actor.precision,
                modifier = 0,
                difficulty = target.precision
            )
            val damageDownName: String
            val damageDownRate: Pair<Int, Int>
            if (precisionCheck > 3) {
                damageDownRate = 1 to 2
                damageDownName = "Damage -50%"
            } else {
                damageDownRate = 3 to 4
                damageDownName = "Damage -25%"
            }
            val duration = when {
                precisionCheck < -4 -> 1
                precisionCheck < 0 -> 2
                precisionCheck == 0 -> 3
                precisionCheck < 4 -> 4
                else -> 5
            }
            target.statusEffects.firstOrNull { it.id == BattleConstants.DAMAGE_DOWN_ID }
                ?.let { effect ->
                    if (effect.statSet.damageMultiplier.first < damageDownRate.first) {
                        effect.statSet.damageMultiplier = damageDownRate
                        effect.name = damageDownName
                        success = true
                    }
                    if (effect.setDurationToLonger(duration)) {
                        success = true
                    }
                } ?: run {
                target.addStatusEffect(
                    id = BattleConstants.DAMAGE_DOWN_ID,
                    name = damageDownName,
                    duration = duration,
                    removeAtTurnEnd = true,
                    statSet = StatSet(damageMultiplier = damageDownRate)
                )
                success = true
            }

            if (success) {
                viewStateChange {
                    logMessage = "${target.name} had their damage lowered."
                    statusChange(
                        battlerId = target.id,
                        statusEffects = target.statusEffects.deepCopy()
                    )
                    wait = BattleConstants.MEDIUM_BATTLE_WAIT
                }.also { add(it) }
            }
        }
    }
}