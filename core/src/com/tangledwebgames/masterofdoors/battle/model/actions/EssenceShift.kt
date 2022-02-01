package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.DAMAGE_DOWN_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_DOWN_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.POISON_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.REGEN_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.util.listBuilder

object EssenceShift : BattleAction {
    override val id: String = "essence_shift"
    override val name: String = "Essence Shift"
    override val manaCost: Int = 18
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Steal positive effects from an enemy and transfer negative effects on self to them. Transferred effects are random, number of effects depends on Spellcraft vs Spellcraft check.
        """.trimIndent()

    val positiveEffectsStolen = listOf(
        INCOMING_DAMAGE_DOWN_ID, REGEN_ID, DAMAGE_UP_ID
    )

    val negativeEffectsTransferred = listOf(
        POISON_ID, INCOMING_DAMAGE_UP_ID, DAMAGE_DOWN_ID
    )

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor) && (
                positiveEffectsStolen.any { target.isAffectedBy(it) } ||
                        negativeEffectsTransferred.any { actor.isAffectedBy(it) }
                )
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost

        viewStateChange {
            logMessage = "${actor.name} casts $name!"
            statusChange(
                battlerId = actor.id, mana = actor.mana
            )
            wait = WAIT_AFTER_ACTION_DECLARATION
        }.also { add(it) }

        val spellcraftCheck = BattleFunctions.statCheck(
            stat = actor.spellcraft,
            modifier = 0,
            difficulty = target.spellcraft
        )

        val numEffectsTransferred = when {
            spellcraftCheck < -3 -> 1
            spellcraftCheck <= 0 -> 2
            spellcraftCheck < 3 -> 3
            else -> Int.MAX_VALUE
        }

        val effectsToSteal = target.statusEffects
            .filter { it.id in positiveEffectsStolen }
            .toMutableList()
        val effectsToTransfer = actor.statusEffects
            .filter { it.id in negativeEffectsTransferred }
            .toMutableList()

        var effectsTransferred = 0
        while (effectsTransferred < numEffectsTransferred &&
            (effectsToSteal.isNotEmpty() || effectsToTransfer.isNotEmpty())
        ) {
            val effect = (effectsToSteal + effectsToTransfer).random()
            if (effect in effectsToSteal) {
                effectsToSteal.remove(effect)
                target.statusEffects.remove(effect)
                actor.statusEffects.add(effect)
                effect.battlerId = actor.id
            } else {
                effectsToTransfer.remove(effect)
                actor.statusEffects.remove(effect)
                target.statusEffects.add(effect)
                effect.battlerId = target.id
            }
            effectsTransferred++
        }

        viewStateChange {
            logMessage = "Effects were transferred between ${actor.name} and ${target.name}"
            statusChange(
                battlerId = actor.id,
                statusEffects = actor.statusEffects.deepCopy()
            )
            statusChange(
                battlerId = target.id,
                statusEffects = target.statusEffects.deepCopy()
            )
            wait = MEDIUM_BATTLE_WAIT
        }.also { add(it) }
    }
}