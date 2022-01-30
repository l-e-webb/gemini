package com.tangledwebgames.masterofdoors.battle.model

import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MANA_REGEN_RATIO
import com.tangledwebgames.masterofdoors.util.reciproal
import com.tangledwebgames.masterofdoors.util.times
import kotlin.random.Random

object BattleFunctions {

    fun calculateDamage(
        attack: Int,
        defense: Int,
        baseDamage: Int,
        attackDamageMultiplier: Pair<Int, Int>,
        defenseDamageMultiplier: Pair<Int, Int>,
    ): Int = Pair(attack, defense)
        .times(attackDamageMultiplier)
        .times(defenseDamageMultiplier.reciproal())
        .times(baseDamage)
        .coerceAtLeast(0)

    fun calculatePhysicalDamage(
        actor: Battler,
        target: Battler,
        baseDamage: Int,
        isCrit: Boolean,
    ): Int = calculateDamage(
        attack = actor.attack,
        defense = target.defense,
        baseDamage = baseDamage,
        attackDamageMultiplier = actor.damageMultiplier *
                if (isCrit) {
                    3 to 2
                } else {
                    1 to 1
                },
        defenseDamageMultiplier = target.damageResistance
    )

    fun calculateMagicDamage(
        actor: Battler,
        target: Battler,
        baseDamage: Int
    ): Int = calculateDamage(
        attack = actor.magicAttack,
        defense = target.defense,
        baseDamage = baseDamage,
        attackDamageMultiplier = actor.damageMultiplier,
        defenseDamageMultiplier = target.damageResistance
    )

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