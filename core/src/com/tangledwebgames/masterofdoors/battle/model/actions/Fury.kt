package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.BUFF_FLASH_COLOR
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.util.listBuilder

object Fury : BattleAction {
    override val id: String = "fury"
    override val name: String = "Fury"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Become enraged, greatly increasing damage dealt by 75% and increasing damage received by 33% for 3 turns. Cannot stack with other attack buffs.
        """.trimIndent()

    val damageUpRatio = 7 to 4
    val damageTakenRatio = 3 to 4

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return actor == target && target.isAlive() && !target.isAffectedBy(DAMAGE_UP_ID)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> = listBuilder {
        actor.mana -= manaCost
        viewStateChange {
            logMessage = "${target.name} has become enraged."
            statusChange(
                battlerId = actor.id,
                mana = actor.mana
            )
            wait = WAIT_AFTER_ACTION_DECLARATION
        }.also { add(it) }

        actor.addStatusEffect(
            id = DAMAGE_UP_ID,
            name = "Damage +75%",
            duration = 3,
            statSet = StatSet(
                damageMultiplier = damageUpRatio,
            )
        )
        actor.addStatusEffect(
            id = INCOMING_DAMAGE_UP_ID,
            name = "Damage Taken +33%",
            duration = 3,
            statSet = StatSet(
                damageResistance = damageTakenRatio
            )
        )

        viewStateChange {
            flash(battlerId = target.id, color = BUFF_FLASH_COLOR)
            statusChange(
                battlerId = target.name,
                statusEffects = target.statusEffects.deepCopy()
            )
            wait = MEDIUM_BATTLE_WAIT
        }.also { add(it) }
    }
}