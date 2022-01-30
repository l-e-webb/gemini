package com.tangledwebgames.masterofdoors.battle.model.battlers

import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_BONUS_HEALTH
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_BONUS_MANA
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.actions.AcidRain
import com.tangledwebgames.masterofdoors.battle.model.actions.VenomedEdge

fun rogue(): Battler = Battler(
    id = "rogue",
    name = "Rogue",
    isEnemy = false,
    physique = 7,
    spirit = 3,
    power = 2,
    finesse = 8,
    aggression = 8,
    caution = 2,
    bonusHealth = PLAYER_BONUS_HEALTH,
    bonusMana = PLAYER_BONUS_MANA,
    skills = mutableListOf(VenomedEdge, AcidRain)
)