package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.assets.Asset
import ktx.assets.load
import ktx.freetype.registerFreeTypeFontLoaders

object Assets : AssetManager() {
    lateinit var skinAsset: Asset<Skin>
    lateinit var arialFontGenerator: Asset<FreeTypeFontGenerator>
    lateinit var arialBoldFontGenerator: Asset<FreeTypeFontGenerator>
    lateinit var blackPanelNinePatch: Asset<Texture>

    fun init() {
        skinAsset = load("skin/tracer-ui.json")
        registerFreeTypeFontLoaders()
        FreeTypeFontGenerator.setMaxTextureSize(2048)
        arialFontGenerator = load("arial.ttf")
        arialBoldFontGenerator = load("arial-bold.ttf")
        blackPanelNinePatch = load("black_panel.png")
    }
}