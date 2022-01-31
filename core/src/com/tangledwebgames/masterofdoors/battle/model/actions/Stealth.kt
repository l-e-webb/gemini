package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.BUFF_FLASH_COLOR
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.util.listBuilder

object Stealth : BattleAction {

    const val STEALTH_EFFECT_ID = "stealth_effect"
    const val STEALTH_EFFECT_NAME = "Stealth"

    override val id: String = "stealth"
    override val name: String = "Stealth"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Hide in the shadows to divert enemy attention and increase damage next turn. Avoidance is based on Precision check. Damage increase is 200% - 300% based on Precision check.
        """.trimIndent()

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && target == actor && !target.isAffectedBy(STEALTH_EFFECT_ID)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        val precisionCheck = BattleFunctions.statCheck(
            stat = actor.precision,
            modifier = 0,
            difficulty = 15
        )
        val damageMultiplier = Pair(
            8 + (precisionCheck / 2).coerceIn(0, 4),
            4
        )
        actor.addStatusEffect(
            id = STEALTH_EFFECT_ID,
            name = STEALTH_EFFECT_NAME,
            duration = 2,
            removeAtTurnEnd = true,
            statSet = StatSet(
                damageMultiplier = damageMultiplier
            )
        )

        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} hid in the shadows."
                statusChange(
                    battlerId = actor.id,
                    mana = actor.mana
                )
                wait = WAIT_AFTER_ACTION_DECLARATION
            }.also { add(it) }

            viewStateChange {
                flash(battlerId = actor.id, color = BUFF_FLASH_COLOR)
                statusChange(
                    battlerId = actor.id,
                    statusEffects = actor.statusEffects.deepCopy()
                )
                wait = MEDIUM_BATTLE_WAIT
            }.also { add(it) }
        }
    }
}