package com.tangledwebgames.masterofdoors.battle.model

import com.tangledwebgames.masterofdoors.battle.model.actions.Attack

fun determineEnemyAction(
    battle: Battle,
    enemy: Battler
): Pair<BattleAction, List<Battler>> = Attack.instance to listOf(
    battle.playerBattlers.first()
)