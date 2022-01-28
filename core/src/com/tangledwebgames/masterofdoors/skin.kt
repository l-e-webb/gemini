package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
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
            minHeight = 16f
            topHeight = 2f
            bottomHeight = 2f
            leftWidth = 2f
            rightWidth = 2f
        }
    progressBar(HEALTH_BAR_STYLE) {
        knobBefore = newDrawable("progress-bar-c")
            ?.let { it as? SpriteDrawable }
            ?.tint(Color.GREEN)
            ?.apply {
                minHeight = 12f
            }
        background = healthBarBg
    }

    progressBar(MANA_BAR_STYLE) {
        knobBefore = newDrawable("progress-bar-c")
            ?.let { it as? SpriteDrawable }
            ?.tint(Color.BLUE)
            ?.apply {
                minHeight = 12f
            }

        background = healthBarBg
    }
}