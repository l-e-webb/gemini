package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tangledwebgames.masterofdoors.*
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_MEDIUM
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_SMALL
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.EFFECT_FLASH_TIME
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.TINT_FADE_TIME
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.StatusEffect
import com.tangledwebgames.masterofdoors.util.textProperty
import ktx.actors.alpha
import ktx.actors.then
import ktx.scene2d.*
import ktx.style.get

fun battlerView(
    battlerId: String,
    includeBackground: Boolean,
    includeManaBar: Boolean
): BattlerViewHolder {
    val viewHolder = if (includeManaBar) {
        BattlerViewHolder(battlerId = battlerId)
    } else {
        BattlerViewHolder(battlerId = battlerId, manaBar = null)
    }
    viewHolder.rootTable.apply {
        row()
        actor(viewHolder.statusEffectGroup) {
            it.height(0f).bottom().fillX()
            wrap().pad(UiConstants.PADDING_SMALL).left().bottom().space(UiConstants.PADDING_SMALL)
        }

        row()
        stack {
            if (includeBackground) {
                image("panel") {
                    setFillParent(true)
                }
            }
            actor(viewHolder.tintOverlay) {
                setFillParent(true)
                alpha = 0f
            }

            table {
                pad(PADDING_MEDIUM)
                defaults().space(PADDING_MEDIUM)

                actor(viewHolder.nameLabel) { it.left() }

                row()
                actor(viewHolder.healthBar.rootTable)

                viewHolder.manaBar?.let {
                    row()
                    actor(it.rootTable)
                }
            }

            container {
                setFillParent(true)
                right().top().pad(PADDING_MEDIUM).padTop(-PADDING_MEDIUM)

                actor(viewHolder.activeLabelContainer)
            }
        }
    }
    return viewHolder
}

class BattlerViewHolder(
    var battlerId: String,
    val rootTable: KTableWidget = KTableWidget(skin),
    val nameLabel: Label = Label("", skin, LABEL_LARGE_STYLE),
    val activeLabelContainer: KContainer<Label> = scene2d.container(Label("", skin, "sub-title")),
    val healthBar: HealthBar = HealthBar(
        progressBarStyle = HEALTH_BAR_STYLE,
        labelGenerator = { current, max ->
            "Health: $current / $max"
        }
    ),
    val manaBar: HealthBar? = HealthBar(
        progressBarStyle = MANA_BAR_STYLE,
        labelGenerator = { current, max ->
            "Mana: $current / $max"
        }
    ),
    val statusEffectGroup: HorizontalGroup = HorizontalGroup(),
    val tintOverlay: Image = Image(skin, "progress-bar-c")
) {
    var name: String by nameLabel.textProperty()
    var maxHealth: Int by healthBar::maxValue
    var health: Int by healthBar::currentValue
    var maxMana: Int?
        get() = manaBar?.maxValue
        set(value) {
            manaBar?.maxValue = value ?: 0
        }
    var mana: Int?
        get() = manaBar?.currentValue
        set(value) {
            manaBar?.currentValue = value ?: 0
        }

    fun setFrom(battler: Battler) {
        battlerId = battler.id
        name = battler.name
        maxHealth = battler.maxHealth
        health = battler.health
        maxMana = battler.maxMana
        mana = battler.mana
        setStatusEffects(battler.statusEffects)

    }

    fun setStatusEffects(statusEffects: List<StatusEffect>) {
        statusEffectGroup.clear()
        statusEffects.map { statusEffect ->
            statusEffect.duration?.let {
                "${statusEffect.name} [$it]"
            } ?: statusEffect.name
        }.forEach { statusEffectText ->
            statusEffectGroup.addActor(Label(statusEffectText, skin))
        }
    }

    fun tint(color: Color, time: Float = TINT_FADE_TIME, interpolation: Interpolation = Interpolation.linear) {
        finishTinting()
        tintOverlay.addAction(Actions.color(color, time, interpolation))
    }

    fun flash(
        color: Color,
        time: Float = EFFECT_FLASH_TIME,
        interpolationIn: Interpolation = Interpolation.smooth,
        interpolationOut: Interpolation = Interpolation.smooth
    ) {
        finishTinting()
        val priorColor = tintOverlay.color
        tintOverlay.addAction(
            Actions.color(color, time, interpolationIn) then
                    Actions.color(priorColor, time, interpolationOut)
        )
    }

    fun fade(time: Float = TINT_FADE_TIME, interpolation: Interpolation = Interpolation.linear) = tint(
        Color(1f, 1f, 1f, 0f), time, interpolation
    )

    fun finishTinting() {
        tintOverlay.actions.mapNotNull { it as? ColorAction }
            .forEach {
                tintOverlay.removeAction(it)
                tintOverlay.color = it.endColor
            }
    }

    fun showPopupText(text: String, labelStyle: String){
        val (x, y) = rootTable.let {
            it.localToStageCoordinates(Vector2(it.width / 2, it.height / 2))
        }.let { it.x to it.y }

        scene2d.label(text, labelStyle) {
            width = prefWidth
            height = prefHeight
            setScale(UiConstants.BATTLE_POPUP_INITIAL_SCALE)
            alpha = UiConstants.BATTLE_POPUP_INITIAL_ALPHA
            setPosition(x, y, Align.center)
            val action = Actions.parallel(
                Actions.scaleTo(1f, 1f, UiConstants.BATTLE_POPUP_SCALE_UP_TIME),
                Actions.alpha(1f, UiConstants.BATTLE_POPUP_FADE_IN_TIME),
                Actions.moveToAligned(x, y + UiConstants.BATTLE_POPUP_VERTICAL_SHIFT, Align.center,
                    UiConstants.BATTLE_POPUP_SHIFT_TIME
                )
            ) then
                    Actions.delay(UiConstants.BATTLE_POPUP_WAIT_TIME) then
                    Actions.alpha(0f, UiConstants.BATTLE_POPUP_FADE_TIME) then
                    Actions.removeActor()
            addAction(action)
        }.also { rootTable.stage?.addActor(it) }
    }

    fun showBoxedLabel(text: String) {
        activeLabelContainer.apply {
            background = skin["panel"]
            pad(PADDING_SMALL)
            actor.setText(text)
        }
    }

    fun hideBoxedLabel() {
        activeLabelContainer.apply {
            background = null
            actor.setText("")
        }
    }
}