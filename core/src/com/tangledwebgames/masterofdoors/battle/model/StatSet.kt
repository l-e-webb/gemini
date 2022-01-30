package com.tangledwebgames.masterofdoors.battle.model

data class StatSet(
    var attack: Int = 0,
    var defense: Int = 0,
    var precision: Int = 0,
    var magicAttack: Int = 0,
    var spellcraft: Int = 0,
    var healing: Int = 0,
    var damageMultiplier: Pair<Int, Int> = 1 to 1,
    var healingMultiplier: Pair<Int, Int> = 1 to 1,
    var damageResistance: Pair<Int, Int> = 1 to 1
)