package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_UP_ID
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
        actor.addStatusEffect(
            id = DAMAGE_UP_ID,
            name = "Damage +75%",
            statSet = StatSet(
                damageMultiplier = damageUpRatio,
            )
        )
        actor.addStatusEffect(
            id = INCOMING_DAMAGE_UP_ID,
            name = "Damage Taken + 33%",
            statSet = StatSet(
                damageResistance = damageTakenRatio
            )
        )

        viewStateChange {
            logMessage = "${target.name} has become enraged."
            statusChange(
                battlerId = target.name,
                statusEffects = target.statusEffects.deepCopy()
            )
            statusChange(
                battlerId = actor.name,
                mana = actor.mana
            )
        }
    }
}