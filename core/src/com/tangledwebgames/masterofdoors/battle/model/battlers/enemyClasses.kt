package com.tangledwebgames.masterofdoors.battle.model.battlers

import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.actions.*

fun attackGeminusPhys(): Battler = Battler(
    id = "attack_geminus",
    name = "Attack Geminus - Sword Form",
    isEnemy = true,
    physique = 7,
    spirit = 3,
    power = 7,
    finesse = 3,
    aggression = 7,
    caution = 3,
    bonusHealth = 210,
    skills = mutableListOf(
        CrushingBlow,
        Fury,
        SunderArmor,
        SecondWind
    )
)

fun attackGeminusMag(): Battler = Battler(
    id = "attack_geminus",
    name = "Attack Geminus - Wand Form",
    isEnemy = true,
    physique = 3,
    spirit = 7,
    power = 7,
    finesse = 3,
    aggression = 7,
    caution = 3,
    bonusHealth = 245,
    skills = mutableListOf(
        EnergyBolt,
        AcidRain,
        ToxicCloud,
        EssenceShift
    )
)

fun supportGeminus(): Battler = Battler(
    id = "support_geminus",
    name = "Support Geminus",
    isEnemy = true,
    physique = 3,
    spirit = 7,
    power = 3,
    finesse = 7,
    aggression = 3,
    caution = 7,
    bonusHealth = 180,
    skills = mutableListOf(
        EnergyBolt,
        ConjureArmor,
        PowerSurge,
        Cure,
    )
)