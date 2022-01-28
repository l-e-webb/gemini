package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.ScreenUtils
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table

class MasterOfDoorsGame : Game() {
    var stage: Stage? = null

    override fun create() {
        Assets.init()
        defaultSkin = skin
        stage = Stage()
        stage?.actors {
            table {
                setFillParent(true)
                label("Hello, world!")
            }
        }
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        stage?.act(Gdx.graphics.deltaTime)
        stage?.draw()
    }

    override fun dispose() {
        stage?.dispose()
        Assets.dispose()
    }
}