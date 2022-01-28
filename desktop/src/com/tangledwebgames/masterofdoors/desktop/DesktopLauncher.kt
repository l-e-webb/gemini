package com.tangledwebgames.masterofdoors.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.tangledwebgames.masterofdoors.MasterOfDoorsGame
import com.tangledwebgames.masterofdoors.UiConstants

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            width = UiConstants.SCREEN_WIDTH
            height = UiConstants.SCREEN_HEIGHT
            resizable = false
        }
        LwjglApplication(MasterOfDoorsGame(), config)
    }
}