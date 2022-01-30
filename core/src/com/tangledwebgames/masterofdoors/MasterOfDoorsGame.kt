package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.status.ClassSelectScreen
import ktx.scene2d.Scene2DSkin.defaultSkin

class MasterOfDoorsGame : Game() {
    lateinit var stage: Stage

    override fun create() {
        Assets.init()
        defaultSkin = skin
        stage = Stage().also {
            Gdx.input.inputProcessor = it
        }
        setScreen(ClassSelectScreen(this))
    }

    override fun dispose() {
        stage.dispose()
        Assets.dispose()
    }
}