package com.tangledwebgames.masterofdoors.battle.model

data class Battler(
    var id: String,
    var name: String,
    var health: Int = 0,
    var mana: Int = 0,
    var physique: Int = 5,
    var spirit: Int = 5,
    var power: Int = 5,
    var finesse: Int = 5,
    var aggression: Int = 5,
    var caution: Int = 5,
    var isEnemy: Boolean = false,
    val statusEffects: MutableList<StatusEffect> = mutableListOf(),
    val skills: MutableList<BattleAction> = mutableListOf()
) {
    val maxHealth: Int
        get() = 50 + (physique + power + caution) * 5

    val attack: Int
        get() = physique + power + aggression

    val defense: Int
        get() = physique + finesse + caution

    val precision: Int
        get() = physique + finesse + aggression

    val maxMana: Int
        get() = 20 + (spirit + power + caution) * 2

    val magicAttack: Int
        get() = spirit + power + aggression

    val spellcraft: Int
        get() = spirit + finesse + aggression

    val healing: Int
        get() = spirit + finesse + caution

    fun initialize() {
        health = maxHealth
        mana = maxMana
        statusEffects.clear()
    }

    fun canAct(): Boolean {
        return isAlive()
    }

    fun isAlive(): Boolean = health > 0

    fun isAlly(other: Battler) = isEnemy != other.isEnemy

}