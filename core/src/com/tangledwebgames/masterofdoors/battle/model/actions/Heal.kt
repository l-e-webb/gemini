package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.BattleAction
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_DAMAGE_OR_HEALING
import com.tangledwebgames.masterofdoors.battle.model.BattleEvent
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.viewStateChange
import com.tangledwebgames.masterofdoors.util.listBuilder

object Heal : BattleAction {
    override val id: String = "heal"
    override val name: String = "Heal"
    override val manaCost: Int = 12
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlly(actor) &&
                target.isAlive() &&
                target.health < target.maxHealth
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        target.health = (target.health + actor.healing).coerceIn(0, target.maxHealth)

        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} casts $name."
                wait = WAIT_AFTER_ACTION_DECLARATION
                statusChange(battlerId = actor.id, mana = actor.mana)
            }.also { add(it) }

            viewStateChange {
                logMessage = "${target.name} was healed by ${actor.healing}!"
                healingPopup(target.id, actor.healing)
                statusChange(battlerId = target.id, health = target.health)
                wait = WAIT_AFTER_DAMAGE_OR_HEALING
            }.also { add(it) }
        }
    }
}