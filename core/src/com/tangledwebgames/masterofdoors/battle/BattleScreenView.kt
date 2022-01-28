package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.tangledwebgames.masterofdoors.HEALTH_BAR_STYLE
import com.tangledwebgames.masterofdoors.MANA_BAR_STYLE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_SMALL
import com.tangledwebgames.masterofdoors.skin
import com.tangledwebgames.masterofdoors.util.textProperty
import ktx.actors.onClick
import ktx.scene2d.*
import ktx.style.get
import kotlin.properties.Delegates

class BattleScreenView(private val stage: Stage) {

    private val characterOneNameLabel: Label
    private val characterOneHealthBar: HealthBar = HealthBar(
        labelStyle = "sub-title",
        progressBarStyle = HEALTH_BAR_STYLE,
        labelGenerator = { current, max ->
            "Health: $current / $max"
        }
    )
    private val characterOneManaBar: HealthBar = HealthBar(
        labelStyle = "sub-title",
        progressBarStyle = MANA_BAR_STYLE,
        labelGenerator = { current, max ->
            "Mana: $current / $max"
        }
    )
    private val characterTwoNameLabel: Label
    private val characterTwoHealthBar: HealthBar = HealthBar(
        labelStyle = "sub-title",
        progressBarStyle = HEALTH_BAR_STYLE,
        labelGenerator = { current, max ->
            "Health: $current / $max"
        }
    )
    private val characterTwoManaBar: HealthBar = HealthBar(
        labelStyle = "sub-title",
        progressBarStyle = MANA_BAR_STYLE,
        labelGenerator = { current, max ->
            "Mana: $current / $max"
        }
    )

    private val enemyHorizontalGroup: KHorizontalGroup
    private val enemyOneNameLabel: Label
    private val enemyOneHealthBar: HealthBar = HealthBar(
        labelStyle = "sub-title",
        progressBarStyle = HEALTH_BAR_STYLE,
        labelGenerator = { current, max ->
            "Health: $current / $max"
        }
    )
    private val enemyTwoTable: KTableWidget
    private val enemyTwoNameLabel: Label
    private val enemyTwoHealthBar: HealthBar = HealthBar(
        labelStyle = "sub-title",
        progressBarStyle = HEALTH_BAR_STYLE,
        labelGenerator = { current, max ->
            "Health: $current / $max"
        }
    )

    private val menuContainer: Container<KVerticalGroup>

    init {
        stage.actors {
            table {
                setFillParent(true)
                pad(PADDING_LARGE)
                defaults().space(PADDING_LARGE)

                row().expand()
                horizontalGroup { cell ->
                    cell.colspan(2)
                    enemyHorizontalGroup = this
                    space(PADDING_LARGE)

                    table {
                        defaults().space(PADDING_SMALL)

                        label("") {
                            it.left()
                            enemyOneNameLabel = this
                        }

                        row()
                        actor(enemyOneHealthBar.rootTable)
                    }

                    table {
                        enemyTwoTable = this
                        defaults().space(PADDING_SMALL)

                        label("") {
                            it.left()
                            enemyTwoNameLabel = this
                        }

                        row()
                        actor(enemyTwoHealthBar.rootTable)
                    }
                }

                row()
                table { cell ->
                    cell.right()
                    background = skin["panel"]
                    pad(PADDING_SMALL)
                    defaults().space(PADDING_SMALL)

                    label("") {
                        it.left()
                        characterOneNameLabel = this
                    }

                    row()
                    actor(characterOneHealthBar.rootTable)

                    row()
                    actor(characterOneManaBar.rootTable)
                }

                table { cell ->
                    cell.left()
                    background = skin["panel"]
                    pad(PADDING_SMALL)
                    defaults().space(PADDING_SMALL)

                    label("") {
                        it.left()
                        characterTwoNameLabel = this
                    }

                    row()
                    actor(characterTwoHealthBar.rootTable)

                    row()
                    actor(characterTwoManaBar.rootTable)
                }
            }

            table {
                setFillParent(true)
                pad(PADDING_LARGE)
                container(KVerticalGroup()) {
                    it.expand().bottom().left()
                    background = this@table.skin["panel"]
                    fillX().bottom().pad(PADDING_SMALL).prefWidth(300f)
                    actor.apply {
                        grow()
                        space(PADDING_SMALL)
                    }
                    menuContainer = this
                }
            }
        }
    }

    var characterOneName: String by characterOneNameLabel.textProperty()
    var characterOneCurrentHealth: Int by characterOneHealthBar::currentValue
    var characterOneMaxHealth: Int by characterOneHealthBar::maxValue
    var characterOneCurrentMana: Int by characterOneManaBar::currentValue
    var characterOneMaxMana: Int by characterOneManaBar::maxValue

    var characterTwoName: String by characterTwoNameLabel.textProperty()
    var characterTwoCurrentHealth: Int by characterTwoHealthBar::currentValue
    var characterTwoMaxHealth: Int by characterTwoHealthBar::maxValue
    var characterTwoCurrentMana: Int by characterTwoManaBar::currentValue
    var characterTwoMaxMana: Int by characterTwoManaBar::maxValue

    var enemyOneName: String by enemyOneNameLabel.textProperty()
    var enemyOneCurrentHealth: Int by enemyOneHealthBar::currentValue
    var enemyOneMaxHealth: Int by enemyOneHealthBar::maxValue
    var enemyTwoName: String by enemyTwoNameLabel.textProperty()
    var enemyTwoCurrentHealth: Int by enemyTwoHealthBar::currentValue
    var enemyTwoMaxHealth: Int by enemyTwoHealthBar::maxValue

    var showEnemyTwo: Boolean by Delegates.observable(true) { _, old, new ->
        if (old != new) {
            if (new) {
                enemyHorizontalGroup.addActor(enemyTwoTable)
            } else {
                enemyHorizontalGroup.removeActor(enemyTwoTable)
            }
        }
    }

    var onMenuItemClick: (String) -> Unit = {}

    var menuItems: List<BattleMenuItem> by Delegates.observable(emptyList()) { _, old, new ->
        if (old != new) {
            with (menuContainer.actor) {
                clearChildren()
                for (item in new) {
                    textButton(item.text) {
                        onClick {
                            onMenuItemClick(item.id)
                        }
                    }
                }
            }

            menuContainer.background = if (new.isEmpty()) {
                null
            } else {
                skin["panel"]
            }
        }
    }

    inline fun setMenu(items: List<BattleMenuItem>, crossinline onClick: (String) -> Unit) {
        menuItems = items
        onMenuItemClick = { onClick(it) }
    }
}