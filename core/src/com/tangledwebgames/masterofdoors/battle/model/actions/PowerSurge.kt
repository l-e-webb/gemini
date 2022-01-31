package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.*
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.BUFF_FLASH_COLOR
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.DAMAGE_UP_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.MEDIUM_BATTLE_WAIT
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AFTER_ACTION_DECLARATION
import com.tangledwebgames.masterofdoors.util.listBuilder

object PowerSurge : BattleAction {
    override val id: String = "power_surge"
    override val name: String = "Power Surge"
    override val manaCost: Int = 15
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE
    override val description: String
        get() = """
            Increase one ally's damage output from 25% - 50% (based on Spellcraft check) for 3 turns. Cannot stack with other attack buffs.
        """.trimIndent()

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && target.isAlly(actor) && !target.isAffectedBy(DAMAGE_UP_ID)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        actor.mana -= manaCost
        val spellcraftCheck = BattleFunctions.statCheckPassFail(
            stat = actor.spellcraft,
            modifier = 0,
            difficulty = 15
        )
        val damageBonus: Pair<Int, Int>
        val effectName: String
        if (spellcraftCheck) {
            damageBonus = 3 to 2
            effectName = "Damage +50%"
        } else {
            damageBonus = 5 to 4
            effectName = "Damage +25%"
        }
        val duration = if (actor == target) { 4 } else { 3 }
        target.addStatusEffect(
            id = DAMAGE_UP_ID,
            name = effectName,
            duration = duration,
            removeAtTurnEnd = true,
            statSet = StatSet(
                damageMultiplier = damageBonus
            )
        )

        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} casts $name!"
                statusChange(
                    battlerId = actor.id,
                    mana = actor.mana
                )
                WAIT_AFTER_ACTION_DECLARATION
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