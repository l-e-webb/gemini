package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.util.listBuilder

object FirstAid : BattleAction {
    override val id: String = "first_aid"
    override val name: String = "First Aid"
    override val manaCost: Int = 8
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Weak but universally usable healing skill. Restores Health to one ally.
            Base power: 12
        """.trimIndent()

    val baseHealing = 12

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlly(actor) &&
                target.isAlive() &&
                target.health < target.maxHealth
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        val healing = (baseHealing * actor.healing) / 10
        target.health = (target.health + healing).coerceIn(0, target.maxHealth)

        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} casts $name."
                wait = BattleConstants.WAIT_AFTER_ACTION_DECLARATION
                statusChange(battlerId = actor.id, mana = actor.mana)
            }.also { add(it) }

            add(healViewStateChange(target = target, healing = healing))
        }
    }
}