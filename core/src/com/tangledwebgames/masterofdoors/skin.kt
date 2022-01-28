package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_HEIGHT
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_PADDING
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
    add(getDrawable("button-c"), "panel")

    val healthBarBg = newDrawable("panel")
        ?.let { it as? NinePatchDrawable }
        ?.apply {
            minHeight = HEALTH_BAR_HEIGHT
            topHeight = HEALTH_BAR_PADDING
            bottomHeight = HEALTH_BAR_PADDING
            leftWidth = HEALTH_BAR_PADDING
            rightWidth = HEALTH_BAR_PADDING
        }
    progressBar(HEALTH_BAR_STYLE) {
        background = healthBarBg
        knobBefore = newDrawable("progress-bar-c")
            ?.let { it as? SpriteDrawable }
            ?.tint(Color.GREEN)
            ?.apply {
                minHeight = HEALTH_BAR_HEIGHT - HEALTH_BAR_PADDING * 2
            }
    }

    progressBar(MANA_BAR_STYLE) {
        background = healthBarBg
        knobBefore = newDrawable("progress-bar-c")
            ?.let { it as? SpriteDrawable }
            ?.tint(Color.BLUE)
            ?.apply {
                minHeight = HEALTH_BAR_HEIGHT - HEALTH_BAR_PADDING * 2
            }
    }
}