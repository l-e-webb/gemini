package com.tangledwebgames.masterofdoors.battle.model

import com.badlogic.gdx.graphics.Color
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_COLOR

object BattleConstants {

    const val POISON_ID = "poison"
    const val INCOMING_DAMAGE_UP_ID = "incoming_damage_up"

    const val MANA_REGEN_RATIO = 25
    const val PLAYER_BONUS_HEALTH = 50
    const val PLAYER_BONUS_MANA = 20

    const val BOSS_FORM_ONE_BONUS_HEALTH = 400

    const val TINT_FADE_TIME = 0.1f
    const val EFFECT_FLASH_TIME = 0.25f
    val DAMAGE_FLASH_COLOR = Color.RED.cpy().apply { a = 0.5f }
    val HEALING_FLASH_COLOR = HEALTH_BAR_COLOR.cpy().apply { a = 0.5f }
    val HIGHLIGHTED_BATTLER_COLOR = Color.WHITE.cpy().apply { a = 0.2f }

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