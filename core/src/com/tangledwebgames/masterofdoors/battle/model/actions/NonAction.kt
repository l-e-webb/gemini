package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.BattleAction
import com.tangledwebgames.masterofdoors.battle.model.BattleEvent
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.viewStateChange

object NonAction : BattleAction {
    override val id: String = "non-action"
    override val name: String = "No Action"
    override val manaCost: Int = 0
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = "Does nothing"

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return actor.id == target.id
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        return listOf(viewStateChange {
            logMessage = "...Nothing happened"
        })
    }
}