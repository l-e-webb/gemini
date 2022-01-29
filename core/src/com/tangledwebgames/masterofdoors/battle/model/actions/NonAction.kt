package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.Action
import com.tangledwebgames.masterofdoors.battle.model.BattleEvent
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.viewStateChange

class NonAction : Action {
    override val id: String = "non-action"
    override val name: String = "No Action"
    override val manaCost: Int = 0
    override val targetType: Action.TargetType = Action.TargetType.SINGLE

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return actor.id == target.id
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        return listOf(viewStateChange {
            logMessage = "...Nothing happened"
        })
    }
}