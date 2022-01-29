package com.tangledwebgames.masterofdoors.battle.model

import kotlin.random.Random

object BattleFunctions {
    fun statCheck(
        stat: Int,
        modifier: Int,
        difficulty: Int
    ): Boolean {
        return stat + modifier +  Random.nextInt(-5, 5) > difficulty
    }
}