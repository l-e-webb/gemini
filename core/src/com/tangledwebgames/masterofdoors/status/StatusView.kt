package com.tangledwebgames.masterofdoors.status

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.tangledwebgames.masterofdoors.STAT_VALUE_LARGE_STYLE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_MEDIUM
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_SMALL
import com.tangledwebgames.masterofdoors.battle.model.Battler
import ktx.scene2d.*
import ktx.style.get

fun KTableWidget.statusView(battler: Battler) {
    clear()
    pad(PADDING_LARGE)
    defaults().space(PADDING_LARGE)

    row()
    label(battler.name, "title")

    row()
    table {
        defaults().space(PADDING_SMALL)

        row()
        label("Physique") {
            it.uniformX()
        }

        actor(Actor())

        label("Spirit") {
            it.uniformX()
        }

        row()
        label(battler.physique.toString(), STAT_VALUE_LARGE_STYLE) {
            it.uniformX()
        }

        slider(
            min = 0f,
            max = 1f,
            step = 0.01f,
            style = "stat-slider"
        ) {
            width = 150f
            value = battler.spirit.toFloat() / (battler.physique + battler.spirit)
            touchable = Touchable.disabled
        }

        label(battler.spirit.toString(), STAT_VALUE_LARGE_STYLE) {
            it.uniformX()
        }

        row().spaceTop(PADDING_LARGE)
        label("Power") {
            it.uniformX()
        }

        actor(Actor())

        label("Finesse") {
            it.uniformX()
        }

        row()
        label(battler.power.toString(), STAT_VALUE_LARGE_STYLE) {
            it.uniformX()
        }

        slider(
            min = 0f,
            max = 1f,
            step = 0.01f,
            style = "stat-slider"
        ) {
            width = 150f
            value = battler.finesse.toFloat() / (battler.power + battler.finesse)
            touchable = Touchable.disabled
        }

        label(battler.finesse.toString(), STAT_VALUE_LARGE_STYLE) {
            it.uniformX()
        }

        row().spaceTop(PADDING_LARGE)
        label("Aggression") {
            it.uniformX()
        }

        actor(Actor())

        label("Caution") {
            it.uniformX()
        }

        row()
        label(battler.aggression.toString(), STAT_VALUE_LARGE_STYLE)

        slider(
            min = 0f,
            max = 1f,
            step = 0.01f,
            style = "stat-slider"
        ) {
            width = 150f
            value = battler.caution.toFloat() / (battler.aggression + battler.caution)
            touchable = Touchable.disabled
        }

        label(battler.caution.toString(), STAT_VALUE_LARGE_STYLE)
    }

    row()
    table {
        defaults().space(PADDING_LARGE)
        row()
        table {
            defaults().space(PADDING_MEDIUM)
            columnDefaults(0).left().width(120f)
            columnDefaults(1).right()
            row()
            label("Health")
            label(battler.maxHealth.toString(), STAT_VALUE_LARGE_STYLE)

            row()
            label("Attack")
            label(battler.attack.toString(), STAT_VALUE_LARGE_STYLE)

            row()
            label("Defense")
            label(battler.defense.toString(), STAT_VALUE_LARGE_STYLE)

            row()
            label("Precision")
            label(battler.precision.toString(), STAT_VALUE_LARGE_STYLE)
        }

        image(drawable = skin["progress-bar-c"]) {
            it.growY()
        }

        table {
            defaults().space(PADDING_MEDIUM)
            columnDefaults(0).left().width(120f)
            columnDefaults(1).right()

            row()
            label("Mana") { it.left() }
            label(battler.maxMana.toString(), STAT_VALUE_LARGE_STYLE) { it.right() }

            row()
            label("Magic Attack") { it.left() }
            label(battler.magicAttack.toString(), STAT_VALUE_LARGE_STYLE) { it.right() }

            row()
            label("Spellcraft") { it.left() }
            label(battler.spellcraft.toString(), STAT_VALUE_LARGE_STYLE) { it.right() }

            row()
            label("Healing") { it.left() }
            label(battler.healing.toString(), STAT_VALUE_LARGE_STYLE) { it.right() }
        }
    }
}