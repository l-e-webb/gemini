package com.tangledwebgames.masterofdoors.battle.model

import com.tangledwebgames.masterofdoors.util.times

data class Battler(
    var id: String,
    var name: String,
    var isEnemy: Boolean = false,
    var health: Int = 0,
    var bonusHealth: Int = 0,
    var bonusMana: Int = 0,
    var mana: Int = 0,
    var physique: Int = 5,
    var spirit: Int = 5,
    var power: Int = 5,
    var finesse: Int = 5,
    var aggression: Int = 5,
    var caution: Int = 5,
    val statusEffects: MutableList<StatusEffect> = mutableListOf(),
    val skills: MutableList<BattleAction> = mutableListOf()
) {
    val maxHealth: Int
        get() = bonusHealth + (physique + power + caution) * 5

    val attack: Int
        get() = physique + power + aggression

    val defense: Int
        get() = physique + finesse + caution

    val precision: Int
        get() = physique + finesse + aggression

    val maxMana: Int
        get() = bonusMana + (spirit + power + caution) * 2

    val magicAttack: Int
        get() = spirit + power + aggression

    val spellcraft: Int
        get() = spirit + finesse + aggression

    val healing: Int
        get() = spirit + finesse + caution

    val damageMultiplier: Pair<Int, Int>
        get() = statusEffects.map { it.statSet.damageMultiplier }
            .fold(1 to 1) { acc, mult ->
                acc * mult
            }

    val healingMultiplier: Pair<Int, Int>
        get() = statusEffects.map { it.statSet.healingMultiplier }
            .fold(1 to 1) { acc, mult ->
                acc * mult
            }

    val damageResistance: Pair<Int, Int>
        get() = statusEffects.map { it.statSet.damageResistance }
            .fold(1 to 1) { acc, mult ->
                acc * mult
            }


    fun initialize() {
        health = maxHealth
        mana = maxMana
        statusEffects.clear()
    }

    fun canAct(): Boolean {
        return isAlive()
    }

    fun isAlive(): Boolean = health > 0

    fun isAlly(other: Battler) = isEnemy == other.isEnemy

    fun isAffectedBy(statusEffectId: String): Boolean = statusEffects
        .any { it.id == statusEffectId }

    fun addStatusEffect(
        id: String,
        name: String,
        duration: Int? = null,
        removeAtTurnEnd: Boolean = false,
        damageOverTime: Int? = null,
        healingOverTIme: Int? = null,
        statSet: StatSet = StatSet()
    ) {
        statusEffects.firstOrNull { it.id == id }
            ?.let {
                val currentDuration = it.duration
                if (currentDuration != null && duration != null) {
                    it.duration = currentDuration + duration
                }
            } ?: statusEffects.add(
            StatusEffect(
                id = id,
                name = name,
                battlerId = this@Battler.id,
                duration = duration,
                removeAtTurnEnd = removeAtTurnEnd,
                damageOverTime = damageOverTime,
                healingOverTime = healingOverTIme,
                statSet = statSet
            )
        )
    }

}