package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.util.listBuilder

object CrushingBlow : BattleAction {

    override val id: String = "crushing_blow"
    override val name: String = "Crushing Blow"
    override val manaCost: Int = 12
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            A heavy physical attack. Has lowered crit chance, but deals increased damage based on the attacker's Physique.
            Base power: $baseDamage + Physique
        """.trimIndent()

    val baseDamage: Int = 30

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        val isCrit = BattleFunctions.statCheckPassFail(
            stat = actor.precision,
            modifier = -8,
            difficulty = target.defense
        )
        val damage = BattleFunctions.calculatePhysicalDamage(
            actor = actor,
            target = target,
            baseDamage = baseDamage + actor.physique,
            isCrit = isCrit
        )
        target.health = (target.health - damage).coerceAtLeast(0)
        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} attacks!"
                statusChange(
                    battlerId = actor.id,
                    mana = actor.mana
                )
                wait = BattleConstants.WAIT_AFTER_ACTION_DECLARATION
            }.also { add(it) }

            add(damageViewStateChange(target = target, damage = damage, isCrit = isCrit))

            if (isCrit) {
                add(critViewStateChange())
            }

            if (!target.isAlive()) {
                add(targetDiesViewStateChange(target))
            }
        }
    }
}