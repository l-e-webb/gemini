package com.tangledwebgames.masterofdoors.status

import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.battle.model.Battler
import ktx.actors.onClick
import ktx.scene2d.*

class ClassSelectView(
    val stage: Stage,
    val playerClasses: List<Battler>,
    initialSelectionOne: Int,
    initialSelectionTwo: Int
) {
    val rootTable: KTableWidget
    val statusView: StatusView = StatusView(includeSelectionArrows = true)

    var selectionOne = initialSelectionOne.coerceIn(playerClasses.indices)
    var selectionTwo = initialSelectionTwo
        .coerceIn(playerClasses.indices)
        .takeIf { it != selectionOne }
        ?: playerClasses.indices.first { it != selectionOne }

    var onBeginButtonClick: (List<Battler>) -> Unit = {}

    init {
        rootTable = scene2d.table {
            setFillParent(true)
            pad(PADDING_LARGE)
            defaults().space(PADDING_LARGE)
            label("Choose Your Characters", "title")

            row()
            actor(statusView.rootTable) {
                it.grow()
            }

            row()
            textButton("Begin") {
                onClick {
                    onBeginButtonClick(
                        listOf(
                            playerClasses[selectionOne],
                            playerClasses[selectionTwo]
                        )
                    )
                }
            }
        }.also {
            stage.addActor(it)
        }

        statusView.onLeftSelectionArrowClick = {
            when (it) {
                0 -> decrementSelectionOne()
                1 -> decrementSelectionTwo()
            }
        }

        statusView.onRightSelectionArrowClick = {
            when (it) {
                0 -> incrementSelectionOne()
                1 -> incrementSelectionTwo()
            }
        }

        refreshBattlerStatusViews()
    }

    fun incrementSelectionOne() {
        do {
            selectionOne = (selectionOne + 1) % playerClasses.size
        } while (selectionOne == selectionTwo)
        refreshBattlerStatusViews()
    }

    fun incrementSelectionTwo() {
        do {
            selectionTwo = (selectionTwo + 1) % playerClasses.size
        } while (selectionOne == selectionTwo)
        refreshBattlerStatusViews()
    }

    fun decrementSelectionOne() {
        do {
            selectionOne--
            if (selectionOne < 0) {
                selectionOne = playerClasses.indices.last
            }
        } while (selectionOne == selectionTwo)
        refreshBattlerStatusViews()
    }

    fun decrementSelectionTwo() {
        do {
            selectionTwo--
            if (selectionTwo < 0) {
                selectionTwo = playerClasses.indices.last
            }
        } while (selectionOne == selectionTwo)
        refreshBattlerStatusViews()
    }

    fun refreshBattlerStatusViews() {
        statusView.buildBattlerTable(
            battlers = listOf(
                playerClasses[selectionOne], playerClasses[selectionTwo]
            )
        )
    }
}