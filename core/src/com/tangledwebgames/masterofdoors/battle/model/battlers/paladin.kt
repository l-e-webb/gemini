package com.tangledwebgames.masterofdoors.battle.model.battlers

import com.tangledwebgames.masterofdoors.battle.model.BattleConstants
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.actions.BeatBack
import com.tangledwebgames.masterofdoors.battle.model.actions.Guard
import com.tangledwebgames.masterofdoors.battle.model.actions.Heal

fun paladin(): Battler = Battler(
    id = "paladin",
    name = "Paladin",
    isEnemy = false,
    physique = 6,
    spirit = 4,
    power = 5,
    finesse = 5,
    aggression = 3,
    caution = 7,
    bonusHealth = BattleConstants.PLAYER_BONUS_HEALTH,
    skills = mutableListOf(Guard, Heal, BeatBack)
)