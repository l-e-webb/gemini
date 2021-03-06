package com.tangledwebgames.masterofdoors.battle.model

import com.badlogic.gdx.graphics.Color
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_COLOR

object BattleConstants {

    const val POISON_ID = "poison"
    const val INCOMING_DAMAGE_UP_ID = "incoming_damage_up"
    const val DAMAGE_DOWN_ID = "damage_down"
    const val DAMAGE_UP_ID = "damage_up"
    const val REGEN_ID = "regen"
    const val INCOMING_DAMAGE_DOWN_ID = "incoming_damage_down"

    val MANA_REGEN_RATIO = 1 to 5
    const val PLAYER_BONUS_HEALTH = 75
    const val PLAYER_BONUS_MANA = 20

    const val ATTACK_DEFENSE_RATIO_EXPONENT = 0.7f

    const val ATTACK_GEMINUS_FORM_TURNS = 4

    const val TINT_FADE_TIME = 0.1f
    const val EFFECT_FLASH_TIME = 0.25f
    val DAMAGE_FLASH_COLOR = Color.RED.cpy().apply { a = 0.5f }
    val HEALING_FLASH_COLOR = HEALTH_BAR_COLOR.cpy().apply { a = 0.5f }
    val HIGHLIGHTED_BATTLER_COLOR = Color.WHITE.cpy().apply { a = 0.2f }
    val BUFF_FLASH_COLOR = Color.TEAL.cpy().apply { a = 0.5f }

    const val LONG_BATTLE_WAIT = 1f
    const val MEDIUM_BATTLE_WAIT = 0.75f
    const val SHORT_BATTLE_WAIT = 0.5f
    const val WAIT_AFTER_ACTION_DECLARATION = SHORT_BATTLE_WAIT
    const val WAIT_AFTER_DAMAGE_BEFORE_CRIT = SHORT_BATTLE_WAIT
    const val WAIT_AFTER_DAMAGE_OR_HEALING = MEDIUM_BATTLE_WAIT
    const val WAIT_AFTER_CRIT_DECLARATION = MEDIUM_BATTLE_WAIT
    const val WAIT_AFTER_TARGET_DIES_DECLARATION = MEDIUM_BATTLE_WAIT
    const val WAIT_AT_START_OF_ENEMY_TURN = LONG_BATTLE_WAIT
}