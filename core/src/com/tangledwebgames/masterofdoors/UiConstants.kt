package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.graphics.Color

object UiConstants {
    const val SCREEN_WIDTH = 1600
    const val SCREEN_HEIGHT = 1000

    const val PADDING_LARGE = 16f
    const val PADDING_MEDIUM = 8f
    const val PADDING_SMALL = 4f
    const val PADDING_X_SMALL = 2f

    const val FONT_SIZE_PRIMARY_LARGE = 22

    const val HEALTH_BAR_WIDTH = 300f
    const val HEALTH_BAR_HEIGHT = 20f
    const val HEALTH_BAR_PADDING = 2f
    const val HEALTH_BAR_INTERPOLATION_TIME = 0.4f

    const val BATTLE_POPUP_INITIAL_SCALE = 0.5f
    const val BATTLE_POPUP_INITIAL_ALPHA = 0.5f
    const val BATTLE_POPUP_SCALE_UP_TIME = 0.5f
    const val BATTLE_POPUP_FADE_IN_TIME = 0.25f
    const val BATTLE_POPUP_VERTICAL_SHIFT = 100f
    const val BATTLE_POPUP_SHIFT_TIME = 0.5f
    const val BATTLE_POPUP_WAIT_TIME = 0.75f
    const val BATTLE_POPUP_FADE_TIME = 0.25f
    const val BATTLE_POPUP_FONT_SIZE = 48

    val HEALTH_BAR_COLOR: Color = Color(0x20ff20ff)
    val MANA_BAR_COLOR: Color = Color(0x1020ffff)

    val DAMAGE_POPUP_COLOR: Color = Color(0xff2020ff.toInt())
    val HEALING_POPUP_COLOR: Color = HEALTH_BAR_COLOR
    val GENERIC_POPUP_COLOR: Color = Color.LIGHT_GRAY
}