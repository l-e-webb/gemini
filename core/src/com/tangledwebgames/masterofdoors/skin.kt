package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_COLOR
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_HEIGHT
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_PADDING
import com.tangledwebgames.masterofdoors.UiConstants.MANA_BAR_COLOR
import ktx.style.add
import ktx.style.progressBar

const val HEALTH_BAR_STYLE: String = "health_bar_style"
const val MANA_BAR_STYLE: String = "mana_bar_style"

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