package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.util.listBuilder

object ToxicCloud : BattleAction {
    override val id: String = "toxic_cloud"
    override val name: String = "Toxic Cloud"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Magic attack which is relatively weak but applies strong poison. Severity and duration of poison depend on Spellcraft vs Healing check.
            Base power: $baseDamage
        """.trimIndent()

    val baseDamage = 18

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${actor.name} casts $name"
            statusChange(battlerId = actor.id, mana = actor.mana)
            wait = BattleConstants.WAIT_AFTER_ACTION_DECLARATION
        }.also { add(it) }

        val damage = BattleFunctions.calculateMagicDamage(
            actor = actor, target = target, baseDamage = baseDamage
        )
        target.health = (target.health - damage).coerceAtLeast(0)

        add(damageViewStateChange(target = target, damage = damage, isCrit = false))

        if (!target.isAlive()) {
            add(targetDiesViewStateChange(target))
        } else if (damage > 0) {
            var success = false

            val spellcraftCheck = BattleFunctions.statCheck(
                stat = actor.spellcraft,
                modifier = 0,
                difficulty = target.healing
            )
            val poisonDamage: Int
            val duration: Int
            when {
                spellcraftCheck < -5 -> return@listBuilder
                spellcraftCheck < -3 -> {
                    duration = 1
                    poisonDamage = actor.spellcraft / 3 + 1
                }
                spellcraftCheck < 0 -> {
                    duration = 2
                    poisonDamage = actor.precision / 3 + 2
                }
                spellcraftCheck < 4 -> {
                    duration = 3
                    poisonDamage = actor.precision / 3 + 3
                }
                else -> {
                    duration = 3
                    poisonDamage = actor.precision / 2 + 3
                }
            }

            target.statusEffects.firstOrNull { it.id == BattleConstants.POISON_ID }
                ?.let { effect ->
                    if (effect.damageOverTime?.let { it > poisonDamage } != true) {
                        effect.damageOverTime = poisonDamage
                        effect.name = "Poison ($poisonDamage)"
                        success = true
                    }
                    if (effect.setDurationToLonger(duration)) {
                        success = true
                    }
                } ?: run {
                target.addStatusEffect(
                    id = BattleConstants.POISON_ID,
                    name = "Poison ($poisonDamage)",
                    duration = duration,
                    damageOverTime = poisonDamage
                )
                success = true
            }

            if (success) {
                viewStateChange {
                    logMessage = "${target.name} was poisoned."
                    statusChange(
                        battlerId = target.id,
                        statusEffects = target.statusEffects.deepCopy()
                    )
                    wait = BattleConstants.MEDIUM_BATTLE_WAIT
                }.also { add(it) }
            }
        }
    }
}