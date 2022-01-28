package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.assets.Asset
import ktx.assets.load

object Assets : AssetManager() {
    lateinit var skinAsset: Asset<Skin>

    fun init() {
        skinAsset = load("skin/tracer-ui.json")
    }
}