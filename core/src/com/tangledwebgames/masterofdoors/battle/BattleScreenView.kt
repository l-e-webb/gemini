package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.HEALTH_BAR_STYLE
import com.tangledwebgames.masterofdoors.MANA_BAR_STYLE
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_WIDTH
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_SMALL
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.progressBar
import ktx.scene2d.table
import ktx.style.get

class BattleScreenView(private val stage: Stage) {

    init {
        stage.actors {
            table {
                setFillParent(true)
                pad(PADDING_LARGE)
                defaults().space(PADDING_LARGE)

                row().expandY().bottom()
                table {
                    background = skin["panel"]
                    pad(PADDING_SMALL)
                    defaults().space(PADDING_SMALL)

                    label("Character Name") { it.left() }

                    row()
                    progressBar(style = HEALTH_BAR_STYLE) {
                        it.width(HEALTH_BAR_WIDTH)
                        value = 1f
                    }

                    row()
                    progressBar(style = MANA_BAR_STYLE) {
                        it.width(HEALTH_BAR_WIDTH)
                        value = 1f
                    }
                }

                table {
                    background = skin["panel"]
                    pad(PADDING_SMALL)
                    defaults().space(PADDING_SMALL)

                    label("Character Name") { it.left() }

                    row()
                    progressBar(style = HEALTH_BAR_STYLE) {
                        it.width(HEALTH_BAR_WIDTH)
                        value = 1f
                    }

                    row()
                    progressBar(style = MANA_BAR_STYLE) {
                        it.width(HEALTH_BAR_WIDTH)
                        value = 1f
                    }
                }
            }
        }
    }
}