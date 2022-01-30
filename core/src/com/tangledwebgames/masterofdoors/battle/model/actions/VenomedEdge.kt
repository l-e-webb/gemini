package com.tangledwebgames.masterofdoors.battle.model.actions

import com.badlogic.gdx.Gdx
import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.POISON_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.calculatePhysicalDamage
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheck
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheckPassFail
import com.tangledwebgames.masterofdoors.util.listBuilder

object VenomedEdge : BattleAction {

    override val id: String = "venomed_edge"
    override val name: String = "Venomed Edge"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Physical attack with chance to poison. Severity and duration of poison depend on Precision vs Defense check.
            Base power: $baseDamage
        """.trimIndent()

    val baseDamage = Attack.baseDamage

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${actor.name} strikes with a poisoned weapon!"
            statusChange(battlerId = actor.id, mana = actor.mana)
            wait = WAIT_AFTER_ACTION_DECLARATION
        }.also { add(it) }

        val isCrit = statCheckPassFail(
            stat = actor.precision,
            modifier = -5,
            difficulty = target.defense
        )
        val damage = calculatePhysicalDamage(
            actor = actor, target = target, baseDamage = baseDamage, isCrit = isCrit
        )
        target.health = (target.health - damage).coerceAtLeast(0)

        add(damageViewStateChange(target = target, damage = damage, isCrit = isCrit))

        if (isCrit) {
            add(critViewStateChange())
        }

        if (!target.isAlive()) {
            add(targetDiesViewStateChange(target))
        } else if (damage > 0) {
            var success = false

            val precisionCheck = statCheck(
                stat = actor.precision,
                modifier = 0,
                difficulty = target.defense
            )
            val poisonDamage: Int
            val duration: Int
            when {
                precisionCheck < 5 -> return@listBuilder
                precisionCheck < 3 -> {
                    duration = 1
                    poisonDamage = actor.precision / 3
                }
                precisionCheck < 0 -> {
                    duration = 2
                    poisonDamage = actor.precision / 3
                }
                precisionCheck < 4 -> {
                    duration = 3
                    poisonDamage = actor.precision / 3
                }
                else -> {
                    duration = 3
                    poisonDamage = actor.precision / 2
                }
            }

            target.statusEffects.firstOrNull { it.id == POISON_ID }
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
                    id = POISON_ID,
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
                    wait = MEDIUM_BATTLE_WAIT
                }.also { add(it) }
            }
        }
    }
}