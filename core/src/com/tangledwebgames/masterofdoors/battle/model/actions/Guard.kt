package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.BattleAction
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleEvent
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.viewStateChange
import com.tangledwebgames.masterofdoors.util.listBuilder

object Guard : BattleAction {

    const val GUARDING_STATUS_EFFECT_ID = "guarding"
    const val GUARDING_STATUS_EFFECT_NAME = "Guarding"

    override val id: String = "guard"
    override val name: String = "Guard"
    override val manaCost: Int = 12
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Enter Guarding state for 3 turns. While Guarding, attacks directed at ally may be redirected to self. Incidence rate based on Precision.
        """.trimIndent()

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return actor == target &&
                !actor.isAffectedBy(GUARDING_STATUS_EFFECT_ID)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        actor.addStatusEffect(
            id = GUARDING_STATUS_EFFECT_ID,
            name = GUARDING_STATUS_EFFECT_NAME,
            duration = 3
        )

        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} is now guarding their ally. They may intercept incoming attacks."
                statusChange(
                    battlerId = actor.id,
                    mana = actor.mana,
                    statusEffects = actor.statusEffects.toList()
                )
                wait = MEDIUM_BATTLE_WAIT
            }.also { add(it) }
        }
    }
}