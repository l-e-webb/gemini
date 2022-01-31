package com.tangledwebgames.masterofdoors.battle.model

import com.badlogic.gdx.graphics.Color
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_DOWN_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.POISON_ID
import com.tangledwebgames.masterofdoors.battle.model.actions.*
import com.tangledwebgames.masterofdoors.battle.model.actions.Guard.GUARDING_STATUS_EFFECT_ID
import com.tangledwebgames.masterofdoors.battle.model.actions.Stealth.STEALTH_EFFECT_ID
import com.tangledwebgames.masterofdoors.battle.model.battlers.attackGeminusMag
import com.tangledwebgames.masterofdoors.battle.model.battlers.attackGeminusPhys
import kotlin.random.Random

fun determineEnemyAction(
    battle: Battle,
    enemy: Battler
): Pair<BattleAction, List<Battler>> {
    return when (enemy.id) {
        "attack_geminus" -> determineAttackGeminusPhysAction(
            battle, enemy
        )
        "support_geminus" -> determineSupportGeminusAction(
            battle, enemy
        )
        else -> Attack to getPlayerTarget(battle)
    }.let { (action, target) ->
        if (!action.canExecute(enemy) || !action.isValid(enemy, target)) {
            Attack to target
        } else {
            action to target
        }
    }.let { it.first to listOf(it.second) }
}

fun determineAttackGeminusAction(
    battle: Battle, attackGeminus: Battler
): Pair<BattleAction, Battler> {
    return if (attackGeminus.physique > attackGeminus.spirit) {
        determineAttackGeminusPhysAction(battle, attackGeminus)
    } else {
        determineAttackGeminusMagAction(battle, attackGeminus)
    }
}

fun determineAttackGeminusPhysAction(
    battle: Battle,
    attackGeminus: Battler
): Pair<BattleAction, Battler> {
    val turnsInForm = attackGeminus.variables["turnsInForm"]?.toIntOrNull() ?: 0
    if (turnsInForm >= 5) {
        val attackGeminusMag = attackGeminusMag()
        with (attackGeminus) {
            name = attackGeminusMag.name
            physique = attackGeminusMag.physique
            spirit = attackGeminusMag.spirit
            power = attackGeminusMag.power
            finesse = attackGeminusMag.finesse
            aggression = attackGeminusMag.aggression
            caution = attackGeminusMag.caution
            bonusHealth = attackGeminusMag.bonusHealth
            skills.clear()
            skills.addAll(attackGeminusMag.skills)
        }
        battle.pushStateChange {
            logMessage = "Attack Geminus enters Wand Form!"
            statusChange(
                battlerId = attackGeminus.id,
                name = attackGeminus.name,
                maxHealth = attackGeminus.maxHealth,
                maxMana = attackGeminus.maxMana
            )
            flash(battlerId = attackGeminus.id, color = Color.BLUE)
            wait = MEDIUM_BATTLE_WAIT
        }
        attackGeminus.variables["turnsInForm"] = "0"
        return determineAttackGeminusMagAction(battle, attackGeminus)
    } else {
        attackGeminus.variables["turnInForm"] = (turnsInForm + 1).toString()
    }

    if (attackGeminus.health < attackGeminus.maxHealth / 3 &&
        attackGeminus.variables["regenerated"] != "true"
    ) {
        return SecondWind to attackGeminus
    }

    if (!attackGeminus.isAffectedBy(DAMAGE_UP_ID) && Random.nextBoolean()) {
        return Fury to attackGeminus
    }

    val seed = Random.nextFloat()
    return when {
        seed < 0.33f -> CrushingBlow to getPlayerTarget(
            battle = battle, preferWeakened = true
        )
        seed < 0.66f -> SunderArmor to getPlayerTarget(battle)
        else -> Attack to getPlayerTarget(battle)
    }
}

fun determineAttackGeminusMagAction(
    battle: Battle,
    attackGeminus: Battler
): Pair<BattleAction, Battler> {
    val turnsInForm = attackGeminus.variables["turnsInForm"]?.toIntOrNull() ?: 0
    if (turnsInForm >= 5) {
        val attackGeminusPhys = attackGeminusPhys()
        with (attackGeminus) {
            name = attackGeminusPhys.name
            physique = attackGeminusPhys.physique
            spirit = attackGeminusPhys.spirit
            power = attackGeminusPhys.power
            finesse = attackGeminusPhys.finesse
            aggression = attackGeminusPhys.aggression
            caution = attackGeminusPhys.caution
            bonusHealth = attackGeminusPhys.bonusHealth
            skills.clear()
            skills.addAll(attackGeminusPhys.skills)
        }
        battle.pushStateChange {
            logMessage = "Attack Geminus enters Sword Form!"
            statusChange(
                battlerId = attackGeminus.id,
                name = attackGeminus.name,
                maxHealth = attackGeminus.maxHealth,
                maxMana = attackGeminus.maxMana
            )
            flash(battlerId = attackGeminus.id, color = Color.BLUE)
            wait = MEDIUM_BATTLE_WAIT
        }
        attackGeminus.variables["turnsInForm"] = "0"
        return determineAttackGeminusPhysAction(battle, attackGeminus)
    } else {
        attackGeminus.variables["turnInForm"] = (turnsInForm + 1).toString()
    }

    if (Random.nextFloat() < 0.2f) {
        val playersWithBuffs = battle.playerBattlers.filter { battler ->
            battler.statusEffects.any { it.id in EssenceShift.positiveEffectsStolen }
        }
        if (
            attackGeminus.statusEffects.any {
                it.id in EssenceShift.negativeEffectsTransferred
            } && playersWithBuffs.isNotEmpty()
        ) {
            return EssenceShift to getPlayerTarget(battle) {
                it in playersWithBuffs
            }
        }
    }

    if (Random.nextFloat() < 0.33f && battle.playerBattlers.any { !it.isAffectedBy(POISON_ID) }) {
        return ToxicCloud to getPlayerTarget(battle) {
            !it.isAffectedBy(POISON_ID)
        }
    }

    if (Random.nextFloat() < 0.33 && battle.playerBattlers.any { !it.isAffectedBy(INCOMING_DAMAGE_UP_ID)} ) {
        return AcidRain to getPlayerTarget(battle) {
            !it.isAffectedBy(INCOMING_DAMAGE_UP_ID)
        }
    }

    if (Random.nextBoolean()) {
        return EnergyBolt to getPlayerTarget(battle = battle, preferVulnerable = true)
    }
    
    return Attack to getPlayerTarget(battle)
}

fun determineSupportGeminusAction(
    battle: Battle,
    supportGeminus: Battler
): Pair<BattleAction, Battler> {
    val attackGeminus = battle.enemyBattlers.first { it.id == "attack_geminus" }
    if (attackGeminus.isAlive() && Random.nextFloat() < 0.66f) {
        val possibleBuffs = mutableListOf<BattleAction>()
        if (!attackGeminus.isAffectedBy(DAMAGE_UP_ID)) {
            possibleBuffs.add(PowerSurge)
            possibleBuffs.add(PowerSurge)
        }
        if (!attackGeminus.isAffectedBy(INCOMING_DAMAGE_DOWN_ID)) {
            possibleBuffs.add(ConjureArmor)
            possibleBuffs.add(ConjureArmor)
        }
        if (attackGeminus.statusEffects.any {
            it.id in Cure.curedEffectIds
            }) {
            possibleBuffs.add(Cure)
        }
        return possibleBuffs.random() to attackGeminus
    }
    return if (Random.nextBoolean()) {
        EnergyBolt to getPlayerTarget(battle)
    } else {
        Attack to getPlayerTarget(battle)
    }
}

fun getPlayerTarget(
    battle: Battle,
    preferVulnerable: Boolean = false,
    preferWeakened: Boolean = false,
    filter: (Battler) -> Boolean = { true }
): Battler {
    val weightMap = battle.playerBattlers
        .filter(filter)
        .filter { it.isAlive() }
        .associate {
            var weight = 10f
            if (preferVulnerable && it.isAffectedBy(INCOMING_DAMAGE_UP_ID)) {
                weight += 5f
            }
            if (preferWeakened && it.health < it.maxHealth / 4) {
                weight += 5f
            }
            if (it.isAffectedBy(STEALTH_EFFECT_ID)) {
                weight *= 5f / it.precision.coerceAtLeast(5)
            }
            battle.playerBattlers.firstOrNull { other ->
                other != it &&
                        other.isAlive() &&
                        other.isAffectedBy(GUARDING_STATUS_EFFECT_ID)
            }?.let { guardingBattler ->
                weight *= 5f / guardingBattler.precision.coerceAtLeast(5)
            }
            it.id to weight
        }
    val totalWeight = weightMap.values.sum()
    val roll = Random.nextFloat() * totalWeight
    var accumulator: Float = 0f
    var targetId: String = ""
    for ((id, weight) in weightMap) {
        accumulator += weight
        if (accumulator > roll) {
            targetId = id
            break
        }
    }

    return battle.getBattler(targetId) ?: battle.playerBattlers
        .filter { it.isAlive() }
        .random()
}