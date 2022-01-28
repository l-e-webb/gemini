package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.clearScreen

class BattleScreen(private val stage: Stage) : ScreenAdapter() {

    private lateinit var view: BattleScreenView

    override fun show() {
        stage.clear()
        view = BattleScreenView(stage)
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