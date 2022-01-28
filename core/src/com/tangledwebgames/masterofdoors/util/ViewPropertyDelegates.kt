package com.tangledwebgames.masterofdoors.util

import com.badlogic.gdx.scenes.scene2d.ui.Label
import kotlin.reflect.KProperty

fun Label.textProperty() = LabelTextDelegate(this)

class LabelTextDelegate(private val label: Label) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return label.text.toString()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        label.setText(value)
    }
}