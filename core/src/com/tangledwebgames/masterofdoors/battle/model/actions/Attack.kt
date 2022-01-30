package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.calculatePhysicalDamage
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheckPassFail
import com.tangledwebgames.masterofdoors.util.listBuilder

object Attack : BattleAction {

    override val id: String = "attack"
    override val name: String = "Attack"
    override val manaCost: Int = 0
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE

    val baseDamage: Int = 25

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        val isCrit = statCheckPassFail(
            stat = actor.precision,
            modifier = -5,
            difficulty = target.defense
        )
        val damage = calculatePhysicalDamage(
            actor = actor, target = target, baseDamage = baseDamage, isCrit = isCrit
        )
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