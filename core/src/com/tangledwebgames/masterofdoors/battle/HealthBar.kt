package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_HEIGHT
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_PADDING
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_WIDTH
import com.tangledwebgames.masterofdoors.skin
import ktx.scene2d.*
import ktx.style.get

class HealthBar(
    labelStyle: String = defaultStyle,
    progressBarStyle: String = defaultStyle,
    currentValue: Int = 0,
    maxValue: Int = 1,
    var labelGenerator: (Int, Int) -> String = { current, max -> "$current / $max" }
) {
    val rootTable: KTableWidget
    val progressBar: ProgressBar
    val numberLabel: Label

    init {
        val healthBarBg = skin.get<NinePatchDrawable>("health-bar-bg")
        rootTable = scene2d.table {
            defaults().space(HEALTH_BAR_PADDING)

            row()
            label("", labelStyle) {
                it.left().expandX()
                numberLabel = this
            }

            row()
            container {
                it.width(HEALTH_BAR_WIDTH).height(HEALTH_BAR_HEIGHT).fill()
                background = healthBarBg
                fill().pad(HEALTH_BAR_PADDING)

                progressBar(
                    min = 0f,
                    max = maxValue.toFloat(),
                    step = 1.toFloat(),
                    style = progressBarStyle
                ) {
                    progressBar = this
                }
            }
        }
    }

    var currentValue: Int
        get() = progressBar.value.toInt()
        set(value) {
            progressBar.value = value.toFloat()
            refreshText()
        }

    var maxValue: Int
        get() = progressBar.maxValue.toInt()
        set(value) {
            progressBar.setRange(0f, value.toFloat())
            refreshText()
        }

    init {
        this.currentValue = currentValue
    }

    private fun refreshText() {
        numberLabel.setText(labelGenerator(currentValue, maxValue))
    }
}