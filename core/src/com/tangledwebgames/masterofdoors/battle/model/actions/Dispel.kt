package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.util.listBuilder

object Dispel : BattleAction {
    override val id: String = "dispel"
    override val name: String = "Dispel"
    override val manaCost: Int = 8
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Removes all debuffs and ailments from one ally."
        """.trimIndent()

    val baseHealing = 10

    val curedEffectIds = listOf(
        BattleConstants.POISON_ID, BattleConstants.DAMAGE_DOWN_ID, BattleConstants.INCOMING_DAMAGE_UP_ID
    )

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && target.isAlly(actor) && target.statusEffects.any {
            it.id in curedEffectIds
        }
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${actor.name} casts $name."
            statusChange(battlerId = actor.id, mana = actor.mana)
            wait = BattleConstants.WAIT_AFTER_ACTION_DECLARATION
        }.also { add(it) }

        var success: Boolean = false
        target.statusEffects.filter {
            it.id in curedEffectIds
        }.forEach {
            target.statusEffects.remove(it)
            success = true
        }

        if (success) {
            viewStateChange {
                logMessage = "${target.name} was cured!"
                statusChange(
                    battlerId = target.id,
                    statusEffects = target.statusEffects.deepCopy()
                )
                wait = BattleConstants.MEDIUM_BATTLE_WAIT
            }.also { add(it) }
        }
    }
}