package com.tangledwebgames.masterofdoors.battle.model

import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MANA_REGEN_RATIO
import kotlin.random.Random

object BattleFunctions {

    fun statCheckPassFail(
        stat: Int,
        modifier: Int,
        difficulty: Int
    ): Boolean {
        return statCheck(stat, modifier, difficulty) > 0
    }

    fun statCheck(
        stat: Int, modifier: Int, difficulty: Int
    ): Int {
        return stat + modifier - difficulty + Random.nextInt(-5, 5)
    }

    fun onTurnStart(battler: Battler): List<BattleEvent> {
        if (!battler.isEnemy) {
            battler.mana = (battler.mana + battler.maxMana / MANA_REGEN_RATIO).coerceIn(0, battler.maxMana)
        }

        battler.statusEffects.forEach { effect ->
            effect.adjustDuration(-1)
        }
        battler.statusEffects.filter { effect ->
            effect.duration?.let { it <= 0 } ?: false
        }

        // TODO

        return viewStateChange {
            statusChange(
                battlerId = battler.id,
                mana = battler.mana,
                statusEffects = battler.statusEffects.toList()
            )
        }.let { listOf(it) }
    }
}