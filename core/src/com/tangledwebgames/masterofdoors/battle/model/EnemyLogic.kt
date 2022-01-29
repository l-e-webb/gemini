package com.tangledwebgames.masterofdoors.battle.model

import com.tangledwebgames.masterofdoors.battle.model.actions.Attack

fun determineEnemyAction(
    battle: Battle,
    enemyIndex: Int = 0
): Pair<BattleAction, List<Battler>> = Attack() to (
        if (battle.playerBattlerOne.isAlive()) {
            battle.playerBattlerOne
        } else {
            battle.playerBattlerTwo
        }.let { listOf(it) }
    )