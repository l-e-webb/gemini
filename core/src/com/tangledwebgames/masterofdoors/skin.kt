package com.tangledwebgames.masterofdoors

import com.badlogic.gdx.scenes.scene2d.ui.Skin

val skin: Skin
    get() {
        Assets.skinAsset.finishLoading()
        return Assets.skinAsset.asset
    }