package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.tangledwebgames.masterofdoors.HEALTH_BAR_STYLE
import com.tangledwebgames.masterofdoors.LABEL_LARGE_STYLE
import com.tangledwebgames.masterofdoors.MANA_BAR_STYLE
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.StatusEffect
import com.tangledwebgames.masterofdoors.skin
import com.tangledwebgames.masterofdoors.util.textProperty
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.scene2d

class BattlerViewHolder(
    var battlerId: String,
    val rootTable: KTableWidget = KTableWidget(skin),
    val nameLabel: Label = Label("", skin, LABEL_LARGE_STYLE),
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
    val statusEffectGroup: HorizontalGroup = HorizontalGroup()
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
}