package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.HEALTH_BAR_STYLE
import com.tangledwebgames.masterofdoors.MANA_BAR_STYLE
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.progressBar
import ktx.scene2d.table
import ktx.style.get

fun battleScreenView(stage: Stage) = stage.actors {
    table {
        setFillParent(true)

        row().expandY().bottom().space(16f)
        table {
            background = skin["panel"]
            pad(8f)
            defaults().space(5f)

            label("Character Name") { it.left() }

            row()
            progressBar(style = HEALTH_BAR_STYLE) {
                it.width(200f)
                value = 1f
            }

            row()
            progressBar(style = MANA_BAR_STYLE) {
                it.width(200f)
                value = 1f
            }
        }

        table {
            background = skin["panel"]
            pad(8f)
            defaults().space(5f)

            label("Character Name") { it.left() }

            row()
            progressBar(style = HEALTH_BAR_STYLE) {
                it.width(200f)
                value = 1f
            }

            row()
            progressBar(style = MANA_BAR_STYLE) {
                it.width(200f)
                value = 1f
            }
        }
    }
}