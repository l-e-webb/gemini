package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.util.listBuilder
import com.tangledwebgames.masterofdoors.util.reciproal
import com.tangledwebgames.masterofdoors.util.times

object EnergyBolt : BattleAction {
    override val id: String = "energy_bolt"
    override val name: String = "Energy Bolt"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        val multiplier = (3 to 1) *
                actor.damageMultiplier *
                target.damageResistance.reciproal()
        val damage = (actor.magicAttack * multiplier - target.defense).coerceAtLeast(0)
        target.health = (target.health - damage).coerceAtLeast(0)
        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} casts $name!"
                wait = WAIT_AFTER_ACTION_DECLARATION
                statusChange(battlerId = actor.id, mana = actor.mana)
            }

            add(damageViewStateChange(target = target, damage = damage, isCrit = false))

            if (!target.isAlive()) {
                add(targetDiesViewStateChange(target))
            }
        }
    }
}