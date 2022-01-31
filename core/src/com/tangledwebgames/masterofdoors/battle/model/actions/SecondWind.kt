package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.HEALING_FLASH_COLOR
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.REGEN_ID
import com.tangledwebgames.masterofdoors.util.listBuilder

object SecondWind : BattleAction {
    override val id: String = "second_wind"
    override val name: String = "Second Wind"
    override val manaCost: Int = 12
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Restore Health to self every turn for 3 turns. Cannot stack with other regen effects
            Base healing: 10
        """.trimIndent()

    val baseHealing = 10

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return actor == target && target.isAlive() && !target.isAffectedBy(REGEN_ID)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        val healing = (baseHealing * actor.healing) / 10
        actor.addStatusEffect(
            id = REGEN_ID,
            name = "Regen (+$healing)",
            duration = 3,
            healingOverTime = healing
        )

        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} is regenerating health."
                wait = BattleConstants.WAIT_AFTER_ACTION_DECLARATION
                statusChange(
                    battlerId = actor.id,
                    mana = actor.mana
                )
            }.also { add(it) }

            viewStateChange {
                flash(battlerId = target.id, color = HEALING_FLASH_COLOR)
                statusChange(
                    battlerId = actor.id,
                    statusEffects = actor.statusEffects.deepCopy()
                )
                wait = MEDIUM_BATTLE_WAIT
            }.also { add(it) }
        }
    }
}