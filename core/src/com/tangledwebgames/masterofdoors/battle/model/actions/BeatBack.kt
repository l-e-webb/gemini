package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.calculatePhysicalDamage
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheck
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheckPassFail
import com.tangledwebgames.masterofdoors.util.listBuilder

object BeatBack : BattleAction {

    const val BEAT_BACK_STATUS_ID = "beat_back_status"
    const val BEAT_BACK_STATUS_NAME = "Beat Back"

    override val id: String = "beat_back"
    override val name: String = "Beat Back"
    override val manaCost: Int = 20
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
        Physical attack which greatly reduces the target's attack for one turn. Damage is reduces by 25% - 75% based on Precision vs Precision check.
        Base power: $baseDamage
    """.trimIndent()
    val baseDamage: Int = Attack.baseDamage

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${actor.name} beats ${target.name} back!"
            wait = WAIT_AFTER_ACTION_DECLARATION
            statusChange(battlerId = actor.id, mana = actor.mana)
        }.also { add(it) }

        val isCrit = statCheckPassFail(
            stat = actor.precision,
            modifier = -5,
            difficulty = target.defense
        )

        val damage = calculatePhysicalDamage(
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
            val precisionCheck = statCheck(
                stat = actor.precision,
                modifier = 0,
                difficulty = target.precision
            )
            val damageDownRate: Pair<Int, Int> = when {
                precisionCheck < 0 -> 3 to 4
                precisionCheck < 3 -> 2 to 4
                else -> 1 to 4
            }
            target.statusEffects.firstOrNull { it.id == BEAT_BACK_STATUS_ID }
                ?.let { effect ->
                    if (effect.statSet.damageMultiplier.first > damageDownRate.first) {
                        effect.statSet.damageMultiplier = damageDownRate
                        success = true
                    }
                } ?: run {
                target.addStatusEffect(
                    id = BEAT_BACK_STATUS_ID,
                    name = BEAT_BACK_STATUS_NAME,
                    duration = 1,
                    removeAtTurnEnd = true,
                    statSet = StatSet(damageMultiplier = damageDownRate)
                )
                success = true
            }

            if (success) {
                viewStateChange {
                    logMessage = "${target.name}'s next attack will be weaker."
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