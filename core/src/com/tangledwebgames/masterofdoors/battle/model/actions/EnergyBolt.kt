package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.calculateMagicDamage
import com.tangledwebgames.masterofdoors.util.listBuilder

object EnergyBolt : BattleAction {
    override val id: String = "energy_bolt"
    override val name: String = "Energy Bolt"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Powerful magic attack.
            Base power: $baseDamage
        """.trimIndent()
    val baseDamage: Int = 40

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        val damage = calculateMagicDamage(actor = actor, target = target, baseDamage = baseDamage)
        target.health = (target.health - damage).coerceAtLeast(0)
        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} casts $name!"
                wait = WAIT_AFTER_ACTION_DECLARATION
                statusChange(battlerId = actor.id, mana = actor.mana)
            }.also { add(it) }

            add(damageViewStateChange(target = target, damage = damage, isCrit = false))

            if (!target.isAlive()) {
                add(targetDiesViewStateChange(target))
            }
        }
    }
}