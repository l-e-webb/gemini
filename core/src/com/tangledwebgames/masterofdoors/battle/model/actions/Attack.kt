package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheckPassFail
import com.tangledwebgames.masterofdoors.util.listBuilder
import com.tangledwebgames.masterofdoors.util.reciproal
import com.tangledwebgames.masterofdoors.util.times

object Attack : BattleAction {

    override val id: String = "attack"
    override val name: String = "Attack"
    override val manaCost: Int = 0
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        val isCrit = statCheckPassFail(
            stat = actor.precision,
            modifier = -5,
            difficulty = target.defense
        )
        val multiplier = if (isCrit) {
            3 to 1
        } else {
            2 to 1
        } * actor.damageMultiplier * target.damageResistance.reciproal()
        val damage = (actor.attack * multiplier - target.defense).coerceAtLeast(0)
        target.health = (target.health - damage).coerceAtLeast(0)
        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} attacks!"
                wait = WAIT_AFTER_ACTION_DECLARATION
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