package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.Game
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.battle.BattleScreen
import ktx.scene2d.Scene2DSkin.defaultSkin

class MasterOfDoorsGame : Game() {
    lateinit var stage: Stage

    override fun create() {
        Assets.init()
        defaultSkin = skin
        stage = Stage()
        setScreen(BattleScreen(stage))
    }

    override fun dispose() {
        stage.dispose()
        Assets.dispose()
    }
}