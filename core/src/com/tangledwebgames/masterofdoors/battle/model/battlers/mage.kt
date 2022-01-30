package com.tangledwebgames.masterofdoors.battle.model.battlers

import com.tangledwebgames.masterofdoors.battle.model.BattleConstants
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_BONUS_HEALTH
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.actions.EnergyBolt
import com.tangledwebgames.masterofdoors.battle.model.actions.Heal

fun mage(): Battler = Battler(
    id = "mage",
    name = "Mage",
    isEnemy = false,
    physique = 2,
    spirit = 8,
    power = 7,
    finesse = 3,
    aggression = 6,
    caution = 4,
    bonusHealth = PLAYER_BONUS_HEALTH,
    bonusMana = BattleConstants.PLAYER_BONUS_MANA,
    skills = mutableListOf(EnergyBolt, Heal)
)