package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_FONT_SIZE
import com.tangledwebgames.masterofdoors.UiConstants.DAMAGE_POPUP_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.GENERIC_POPUP_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.HEALING_POPUP_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_HEIGHT
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_PADDING
import com.tangledwebgames.masterofdoors.UiConstants.MANA_BAR_COLOR
import ktx.freetype.generateFont
import ktx.style.add
import ktx.style.get
import ktx.style.label
import ktx.style.progressBar

const val HEALTH_BAR_STYLE: String = "health_bar_style"
const val MANA_BAR_STYLE: String = "mana_bar_style"

const val POPUP_FONT: String = "popup_font"
const val DAMAGE_POPUP_STYLE: String = "damage_popup"
const val HEAL_POPUP_STYLE: String = "heal_popup"
const val GENERIC_POPUP_STYLE: String = "other_popup"

val skin: Skin by lazy {
    Assets.skinAsset.let {
        it.finishLoading()
        it.asset
    }.also {
        initSkin(it)
    }
}

private fun initSkin(skin: Skin) = with(skin) {
    add(newDrawable("button-c"), "panel")

    newDrawable("panel")
        ?.let { it as? NinePatchDrawable }
        ?.apply {
            leftWidth = HEALTH_BAR_PADDING
            rightWidth = HEALTH_BAR_PADDING
            topHeight = HEALTH_BAR_PADDING
            rightWidth = HEALTH_BAR_PADDING
            minHeight = HEALTH_BAR_HEIGHT
        }?.also {
            add(it, "health-bar-bg")
        }

    Assets.arialFontGenerator.finishLoading()
    Assets.arialBoldFontGenerator.finishLoading()
    Assets.arialBoldFontGenerator.asset.generateFont {
        size = BATTLE_POPUP_FONT_SIZE
        color = Color.WHITE
    }.also { add(it, POPUP_FONT)}

    label(GENERIC_POPUP_STYLE) {
        font = skin[POPUP_FONT]
        fontColor = GENERIC_POPUP_COLOR
    }

    label(DAMAGE_POPUP_STYLE, extend = GENERIC_POPUP_STYLE) {
        fontColor = DAMAGE_POPUP_COLOR
    }

    label(HEAL_POPUP_STYLE, extend = GENERIC_POPUP_STYLE) {
        fontColor = HEALING_POPUP_COLOR
    }

    progressBar(HEALTH_BAR_STYLE) {
        knobBefore = newDrawable("progress-bar-c")
            ?.let { it as? SpriteDrawable }
            ?.tint(HEALTH_BAR_COLOR)
            ?.apply {
                minHeight = HEALTH_BAR_HEIGHT - HEALTH_BAR_PADDING * 2
                minWidth = 0f
            }
    }

    progressBar(MANA_BAR_STYLE) {
        knobBefore = newDrawable("progress-bar-c")
            ?.let { it as? SpriteDrawable }
            ?.tint(MANA_BAR_COLOR)
            ?.apply {
                minHeight = HEALTH_BAR_HEIGHT - HEALTH_BAR_PADDING * 2
                minWidth = 0f
            }
    }
}