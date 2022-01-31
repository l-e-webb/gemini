package com.tangledwebgames.masterofdoors.battle.model

import com.badlogic.gdx.Gdx
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.ATTACK_DEFENSE_RATIO_EXPONENT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MANA_REGEN_RATIO
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.util.reciproal
import com.tangledwebgames.masterofdoors.util.times
import kotlin.math.pow
import kotlin.random.Random

object BattleFunctions {

    fun calculateDamage(
        attack: Int,
        defense: Int,
        baseDamage: Int,
        attackDamageMultiplier: Pair<Int, Int>,
        defenseDamageMultiplier: Pair<Int, Int>,
    ): Int = Pair(
        attackDefenseExponentiation(attack),
        attackDefenseExponentiation(defense)
    )
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
        Gdx.app.log(BattleFunctions::class.simpleName, "Stat check: $stat + $modifier vs $difficulty")
        return (stat + modifier - difficulty + Random.nextInt(-5, 5)).also {
            Gdx.app.log(BattleFunctions::class.simpleName, "Outcome: $it")
        }
    }

    fun onTurnStart(battler: Battler): List<BattleEvent> {
        if (!battler.isAlive()) {
            battler.statusEffects.clear()
            viewStateChange {
                statusChange(
                    battlerId = battler.id,
                    statusEffects = battler.statusEffects.deepCopy()
                )
            }.let {
                return listOf(it)
            }
        }

        if (!battler.isEnemy) {
            battler.mana = (battler.mana + (battler.maxMana * MANA_REGEN_RATIO)).coerceIn(0, battler.maxMana)
        }

        val healthChange: Int = battler.statusEffects.sumOf {
            (it.damageOverTime?.times(-1) ?: 0) +
                    (it.healingOverTime ?: 0)
        }

        battler.health = (battler.health + healthChange).coerceIn(0, battler.maxHealth)

        if (battler.isAlive()) {
            battler.statusEffects
                .filter { effect ->
                    !effect.removeAtTurnEnd
                }.forEach { effect ->
                    effect.adjustDuration(-1)
                    if (effect.duration?.let { it <= 0 } == true) {
                        battler.statusEffects.remove(effect)
                    }
                }
        }

        return viewStateChange {
            if (healthChange > 0) {
                healingPopup(battler.id, healthChange)
            } else if (healthChange < 0) {
                damagePopup(battler.id, healthChange)
            }
            statusChange(
                battlerId = battler.id,
                health = battler.health,
                mana = battler.mana,
                statusEffects = battler.statusEffects.deepCopy()
            )
            wait = MEDIUM_BATTLE_WAIT
        }.let { listOf(it) }
    }

    fun onTurnEnd(battler: Battler): List<BattleEvent> {
        battler.statusEffects
            .filter { effect -> effect.removeAtTurnEnd }
            .forEach { effect ->
                effect.adjustDuration(-1)
                if (effect.duration?.let { it <= 0 } == true) {
                    battler.statusEffects.remove(effect)
                }
            }

        return viewStateChange {
            statusChange(
                battlerId = battler.id,
                statusEffects = battler.statusEffects.deepCopy()
            )
        }.let { listOf(it) }
    }

    fun attackDefenseExponentiation(value: Int) = value
        .toFloat()
        .times(100)
        .pow(ATTACK_DEFENSE_RATIO_EXPONENT)
        .toInt()
}