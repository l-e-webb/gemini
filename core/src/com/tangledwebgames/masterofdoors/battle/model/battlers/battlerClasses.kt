package com.tangledwebgames.masterofdoors.battle.model.battlers

import com.tangledwebgames.masterofdoors.battle.model.BattleConstants
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_BONUS_HEALTH
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_BONUS_MANA
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.actions.*

fun battlerClasses() = listOf(
    warrior(), mage(), rogue(), paladin(), acolyte(), plagueDoctor()
)

fun warrior(): Battler = Battler(
    id = "warrior",
    name = "Warrior",
    isEnemy = false,
    physique = 9,
    spirit = 1,
    power = 7,
    finesse = 3,
    aggression = 8,
    caution = 2,
    bonusHealth = PLAYER_BONUS_HEALTH,
    bonusMana = PLAYER_BONUS_MANA,
    skills = mutableListOf(
        CrushingBlow,
        Fury,
        SunderArmor,
        SecondWind,
        FirstAid
    )
)

fun paladin(): Battler = Battler(
    id = "paladin",
    name = "Paladin",
    isEnemy = false,
    physique = 7,
    spirit = 3,
    power = 5,
    finesse = 5,
    aggression = 3,
    caution = 7,
    bonusHealth = BattleConstants.PLAYER_BONUS_HEALTH,
    bonusMana = BattleConstants.PLAYER_BONUS_MANA,
    skills = mutableListOf(
        BeatBack,
        Heal,
        SecondWind,
        Guard,
        FirstAid
    )
)

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
    bonusHealth = BattleConstants.PLAYER_BONUS_HEALTH,
    bonusMana = BattleConstants.PLAYER_BONUS_MANA,
    skills = mutableListOf(
        VenomedEdge,
        SunderArmor,
        Disarm,
        Stealth,
        FirstAid
    )
)

fun mage(): Battler = Battler(
    id = "mage",
    name = "Mage",
    isEnemy = false,
    physique = 1,
    spirit = 9,
    power = 5,
    finesse = 5,
    aggression = 7,
    caution = 3,
    bonusHealth = BattleConstants.PLAYER_BONUS_HEALTH,
    bonusMana = BattleConstants.PLAYER_BONUS_MANA,
    skills = mutableListOf(
        EnergyBolt,
        ConjureArmor,
        ToxicCloud,
        EssenceShift,
        FirstAid
    )
)

fun acolyte(): Battler = Battler(
    id = "acolyte",
    name = "Acolyte",
    isEnemy = false,
    physique = 2,
    spirit = 8,
    power = 4,
    finesse = 6,
    aggression = 1,
    caution = 9,
    bonusHealth = BattleConstants.PLAYER_BONUS_HEALTH,
    bonusMana = BattleConstants.PLAYER_BONUS_MANA,
    skills = mutableListOf(
        Cure,
        Heal,
        PowerSurge,
        ConjureArmor,
        FirstAid
    )
)

fun plagueDoctor(): Battler = Battler(
    id = "plague_doctor",
    name = "Plague Doctor",
    isEnemy = false,
    physique = 2,
    spirit = 8,
    power = 2,
    finesse = 8,
    aggression = 5,
    caution = 5,
    bonusHealth = BattleConstants.PLAYER_BONUS_HEALTH,
    bonusMana = BattleConstants.PLAYER_BONUS_MANA,
    skills = mutableListOf(
        AcidRain,
        ToxicCloud,
        Regenerate,
        Cure,
        FirstAid
    )
)