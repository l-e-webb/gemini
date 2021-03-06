package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.BUFF_FLASH_COLOR
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.INCOMING_DAMAGE_DOWN_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.util.listBuilder

object ConjureArmor : BattleAction {
    override val id: String = "conjure_armor"
    override val name: String = "Conjure Armor"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Summon magical armor to reduce incoming damage to one ally by 50% for 3 turns. Cannot stack with other defense buffs.
        """.trimIndent()

    val damageReduction = 3 to 2

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && target.isAlly(actor) && !target.isAffectedBy(INCOMING_DAMAGE_DOWN_ID)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        target.addStatusEffect(
            id = INCOMING_DAMAGE_DOWN_ID,
            name = "Damage Reduction 50%",
            duration = 3,
            statSet = StatSet(
                damageResistance = damageReduction
            )
        )

        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} summoned armor for ${if (actor == target) { "themselves"} else { target.name } }."
                statusChange(
                    battlerId = actor.id,
                    mana = actor.mana
                )
                wait = WAIT_AFTER_ACTION_DECLARATION
            }.also { add(it) }

            viewStateChange {
                flash(battlerId = target.id, color = BUFF_FLASH_COLOR)
                statusChange(
                    battlerId = target.id,
                    statusEffects = target.statusEffects.deepCopy()
                )
                wait = MEDIUM_BATTLE_WAIT
            }.also { add(it) }
        }
    }
}