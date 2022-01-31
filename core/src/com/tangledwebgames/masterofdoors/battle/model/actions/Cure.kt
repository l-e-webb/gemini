package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.DAMAGE_DOWN_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.POISON_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.util.listBuilder

object Cure : BattleAction {
    override val id: String = "cure"
    override val name: String = "Cure"
    override val manaCost: Int = 10
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Slightly heals one ally, and removes all ailments and debuffs."
            Base healing: $baseHealing
        """.trimIndent()

    val baseHealing = 10

    val curedEffectIds = listOf(
        POISON_ID, DAMAGE_DOWN_ID, INCOMING_DAMAGE_UP_ID
    )

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${actor.name} casts $name."
            statusChange(battlerId = actor.id, mana = actor.mana)
            wait = WAIT_AFTER_ACTION_DECLARATION
        }.also { add(it) }

        val healing = (baseHealing * actor.healing) / 10
        target.health = (target.health + healing).coerceIn(0, target.maxHealth)

        add(healViewStateChange(target = target, healing = healing))

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
                wait = MEDIUM_BATTLE_WAIT
            }.also { add(it) }
        }
    }
}