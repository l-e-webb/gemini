package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_FONT_SIZE
import com.tangledwebgames.masterofdoors.UiConstants.DAMAGE_POPUP_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.FONT_SIZE_PRIMARY_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.GENERIC_POPUP_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.HEALING_POPUP_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_HEIGHT
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_PADDING
import com.tangledwebgames.masterofdoors.UiConstants.MANA_BAR_COLOR
import ktx.freetype.generateFont
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.style.add
import ktx.style.get
import ktx.style.label
import ktx.style.progressBar

const val HEALTH_BAR_STYLE: String = "health_bar_style"
const val MANA_BAR_STYLE: String = "mana_bar_style"

const val FONT_LARGE: String = "font_large"
const val LABEL_LARGE_STYLE: String = "label_large"
const val STAT_VALUE_LARGE_STYLE = "stat_value_large"
const val STAT_VALUE_STYLE = "stat-value"
const val BOXED_METADATA_STYLE = "boxed_metadata"

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
    defaultSkin = this
    val blackPanel: Drawable = Assets.blackPanelNinePatch.let {
        it.finishLoading()
        it.asset
    }.let {
        NinePatch(it, 2, 2, 2, 2)
    }.let {
        NinePatchDrawable(it)
    }
    add(blackPanel, "panel")

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
    }.also { add(it, POPUP_FONT) }

    Assets.arialBoldFontGenerator.asset.generateFont {
        size = FONT_SIZE_PRIMARY_LARGE
        color = Color.WHITE
    }.also { add(it, FONT_LARGE) }

    label(LABEL_LARGE_STYLE) {
        font = skin[FONT_LARGE]
    }

    label(STAT_VALUE_LARGE_STYLE, extend = STAT_VALUE_STYLE) {
        font = skin[FONT_LARGE]
    }

    label(BOXED_METADATA_STYLE) {
        font = skin["sub-title"]
        background = skin["panel"]
    }

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

    get<TextButton.TextButtonStyle>().font = get(FONT_LARGE)
    get<Window.WindowStyle>().background = blackPanel
}