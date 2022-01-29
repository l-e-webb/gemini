package com.tangledwebgames.masterofdoors.battle.model.actions

import com.tangledwebgames.masterofdoors.battle.model.BattleAction
import com.tangledwebgames.masterofdoors.battle.model.BattleEvent
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.statCheck
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.viewStateChange
import com.tangledwebgames.masterofdoors.util.listBuilder

class Attack: BattleAction {

    companion object {
        const val ATTACK_ID = "attack"
        const val NAME = "Attack"
    }

    override val id: String = ATTACK_ID
    override val name: String = NAME
    override val manaCost: Int = 0
    override val targetType: BattleAction.TargetType = BattleAction.TargetType.SINGLE

    override fun isValid(actor: Battler, target: Battler): Boolean {
        return target.isAlive() && !target.isAlly(actor)
    }

    override fun execute(actor: Battler, target: Battler): List<BattleEvent> {
        val isCrit = statCheck(
            stat = actor.precision,
            modifier = -3,
            difficulty = target.defense
        )
        val multiplier = if (isCrit) { 3 } else { 2 }
        val damage = (actor.attack * multiplier - target.defense).coerceAtLeast(0)
        target.health = (target.health - damage).coerceAtLeast(0)
        return listBuilder {
            viewStateChange {
                logMessage = "${actor.name} attacks!"
                wait = 1f
            }.also { add(it) }

            viewStateChange {
                if (damage == 0) {
                    logMessage = "The attack had no effect on ${target.name}"
                    textPopup(
                        battlerId = target.id,
                        text = "*no effect*"
                    )
                } else {
                    logMessage = "${target.name} takes $damage damage!"
                    damagePopup(battlerId = target.id, amount = damage)
                    statusChange(battlerId = target.id, health = target.health)
                }
                wait = 1f
            }.also { add(it) }

            if (isCrit) {
                viewStateChange {
                    logMessage = "Critical hit!"
                    wait = 1f
                }.also { add(it) }
            }

            if (!target.isAlive()) {
                viewStateChange {
                    logMessage = "${target.name} falls!"
                    wait = 1f
                }.also { add(it) }
            }
        }
    }
}