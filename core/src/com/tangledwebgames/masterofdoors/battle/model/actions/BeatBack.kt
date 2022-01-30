package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.calculatePhysicalDamage
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheck
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheckPassFail
import com.tangledwebgames.masterofdoors.util.listBuilder

object BeatBack : BattleAction {

    const val DAMAGE_DOWN_ID = "damage_down"

    override val id: String = "beat_back"
    override val name: String = "Beat Back"
    override val manaCost: Int = 12
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE

    val baseDamage: Int = Attack.baseDamage

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        val isCrit = statCheckPassFail(
            stat = actor.precision,
            modifier = -5,
            difficulty = target.defense
        )

        val damage = calculatePhysicalDamage(
            actor = actor, target = target, baseDamage = baseDamage, isCrit = isCrit
        )

        viewStateChange {
            logMessage = "${actor.name} beats ${target.name} back!"
            wait = WAIT_AFTER_ACTION_DECLARATION
            statusChange(battlerId = actor.id, mana = actor.mana)
        }.also { add(it) }

        add(damageViewStateChange(target = target, damage = damage, isCrit = isCrit))

        if (isCrit) {
            add(critViewStateChange())
        }

        if (!target.isAlive()) {
            add(targetDiesViewStateChange(target))
        }

        if (damage > 0 && target.isAlive()) {
            var success: Boolean = false
            val precisionCheck = statCheck(
                stat = actor.precision,
                modifier = 0,
                difficulty = target.defense
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
                precisionCheck < 4 -> 1
                precisionCheck < 0 -> 2
                precisionCheck == 0 -> 3
                precisionCheck < 4 -> 4
                else -> 5
            }
            target.statusEffects.firstOrNull { it.id == DAMAGE_DOWN_ID }
                ?.let { effect ->
                    if (effect.statSet.damageMultiplier.first < damageDownRate.first) {
                        effect.statSet.damageMultiplier = damageDownRate
                        success = true
                    }
                    if (effect.duration?.let { it < duration } == true) {
                        effect.duration = duration
                        success = true
                    }
                } ?: run {
                target.addStatusEffect(
                    id = DAMAGE_DOWN_ID,
                    name = damageDownName,
                    duration = duration,
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
                    wait = MEDIUM_BATTLE_WAIT
                }.also { add(it) }
            }
        }
    }
}