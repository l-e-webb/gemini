package com.tangledwebgames.masterofdoors.status

import com.badlogic.gdx.ScreenAdapter
import com.tangledwebgames.masterofdoors.MasterOfDoorsGame
import com.tangledwebgames.masterofdoors.battle.BattleScreen
import com.tangledwebgames.masterofdoors.battle.model.battlers.getPlayerClasses
import ktx.app.clearScreen

class ClassSelectScreen(
    private val game: MasterOfDoorsGame
): ScreenAdapter() {

    private val stage
        get() = game.stage

    lateinit var view: ClassSelectView

    override fun show() {
        stage.clear()
        view = ClassSelectView(
            stage = stage,
            playerClasses = getPlayerClasses(),
            initialSelectionOne = 0,
            initialSelectionTwo = 1
        )
        view.onBeginButtonClick = { playerClasses ->
            game.screen = BattleScreen(
                game = game,
                playerClasses = playerClasses
            )
        }
    }

    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f, 1f)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}