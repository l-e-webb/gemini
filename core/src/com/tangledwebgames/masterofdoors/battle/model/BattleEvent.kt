package com.tangledwebgames.masterofdoors.battle.model

import com.tangledwebgames.masterofdoors.DAMAGE_POPUP_STYLE
import com.tangledwebgames.masterofdoors.GENERIC_POPUP_STYLE
import com.tangledwebgames.masterofdoors.HEAL_POPUP_STYLE

sealed class BattleEvent {
    data class ViewStateChange(
        val logMessage: String? = null,
        val statusChanges: List<StatusChange>? = null,
        val textPopups: List<TextPopup>? = null,
        val special: String? = null,
        val wait: Float? = null
    ) : BattleEvent()

    data class PhaseChange(val phase: Battle.Phase) : BattleEvent()

    data class BattleOver(val playerWins: Boolean) : BattleEvent()

    data class AddBattler(val battlerId: String) : BattleEvent()

    data class RemoveBattler(val battlerId: String) : BattleEvent()
}

data class TextPopup(
    val battlerId: String,
    val text: String,
    val labelStyle: String
)

data class StatusChange(
    val battlerId: String,
    val name: String? = null,
    val health: Int? = null,
    val maxHealth: Int? = null,
    val mana: Int? = null,
    val maxMana: Int? = null,
    val statusEffects: List<StatusEffect>? = null
)

class StateChangeBuilder {
    var logMessage: String? = null
    var wait: Float? = null

    private val _statusChanges = mutableListOf<StatusChange>()
    val statusChanges: List<StatusChange> = _statusChanges

    private val _textPopups = mutableListOf<TextPopup>()
    val textPopups: List<TextPopup> = _textPopups

    fun damagePopup(battlerId: String, text: String) {
        _textPopups.add(TextPopup(battlerId, text, DAMAGE_POPUP_STYLE))
    }

    fun damagePopup(battlerId: String, amount: Int) {
        damagePopup(battlerId, amount.toString())
    }

    fun healingPopup(battlerId: String, text: String) {
        _textPopups.add(TextPopup(battlerId, text, HEAL_POPUP_STYLE))
    }

    fun healingPopup(battlerId: String, amount: Int) {
        healingPopup(battlerId, amount.toString())
    }

    fun textPopup(
        battlerId: String,
        text: String,
        labelStyle: String = GENERIC_POPUP_STYLE
    ) {
        _textPopups.add(TextPopup(battlerId, text, labelStyle))
    }

    fun statusChange(
        battlerId: String,
        name: String? = null,
        health: Int? = null,
        maxHealth: Int? = null,
        mana: Int? = null,
        maxMana: Int? = null,
        statusEffects: List<StatusEffect>? = null
    ) {
        _statusChanges.add(
            StatusChange(
                battlerId, name, health, maxHealth, mana, maxMana, statusEffects
            )
        )
    }

    fun build(): BattleEvent.ViewStateChange = BattleEvent.ViewStateChange(
        logMessage, statusChanges, textPopups, null, wait
    )
}

inline fun viewStateChange(
    buildBlock: StateChangeBuilder.() -> Unit
): BattleEvent = StateChangeBuilder().apply(buildBlock).build()

fun damageViewStateChange(target: Battler, damage: Int, isCrit: Boolean) = viewStateChange {
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
    wait = if (isCrit) {
        BattleConstants.WAIT_AFTER_DAMAGE_BEFORE_CRIT
    } else {
        BattleConstants.WAIT_AFTER_DAMAGE_OR_HEALING
    }
}

fun critViewStateChange() = viewStateChange {
    logMessage = "Critical hit!"
    wait = BattleConstants.WAIT_AFTER_CRIT_DECLARATION
}

fun targetDiesViewStateChange(target: Battler) = viewStateChange {
    logMessage = "${target.name} falls!"
    wait = BattleConstants.WAIT_AFTER_TARGET_DIES_DECLARATION
}
