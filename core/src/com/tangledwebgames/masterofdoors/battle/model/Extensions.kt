package com.tangledwebgames.masterofdoors.battle.model

fun StatusEffect.deepCopy() = copy(
    statSet = statSet.copy()
)

fun List<StatusEffect>.deepCopy() = map { it.deepCopy() }