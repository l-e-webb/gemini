package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.tangledwebgames.masterofdoors.HEALTH_BAR_STYLE
import com.tangledwebgames.masterofdoors.MANA_BAR_STYLE
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_FADE_IN_TIME
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_FADE_TIME
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_INITIAL_ALPHA
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_INITIAL_SCALE
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_SCALE_UP_TIME
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_SHIFT_TIME
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_VERTICAL_SHIFT
import com.tangledwebgames.masterofdoors.UiConstants.BATTLE_POPUP_WAIT_TIME
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_MEDIUM
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_SMALL
import com.tangledwebgames.masterofdoors.skin
import com.tangledwebgames.masterofdoors.util.textProperty
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.actors.then
import ktx.scene2d.*
import ktx.style.get
import kotlin.math.pow
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

    private val logScrollPane: ScrollPane
    private val logVerticalGroup: KVerticalGroup

    private val menuContainer: Container<KVerticalGroup>

    init {
        stage.actors {
            table {
                setFillParent(true)
                pad(PADDING_LARGE)
                defaults().space(PADDING_LARGE)

                row().height(180f).colspan(2)
                scrollPane {
                    it.fillY().width(500f)
                    logScrollPane = this
                    setScrollingDisabled(true, false)
                    verticalGroup {
                        logVerticalGroup = this
                        bottom()
                        grow()
                        space(PADDING_SMALL)
                    }
                }


                row().expand()
                horizontalGroup { cell ->
                    cell.colspan(2)
                    enemyHorizontalGroup = this
                    space(PADDING_LARGE)

                    table {
                        defaults().space(PADDING_MEDIUM)

                        label("") {
                            it.left()
                            enemyOneNameLabel = this
                        }

                        row()
                        actor(enemyOneHealthBar.rootTable)
                    }

                    table {
                        enemyTwoTable = this
                        defaults().space(PADDING_MEDIUM)

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
                    pad(PADDING_MEDIUM)
                    defaults().space(PADDING_MEDIUM)

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
                    pad(PADDING_MEDIUM)
                    defaults().space(PADDING_MEDIUM)

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
                    fillX().bottom().pad(PADDING_MEDIUM).prefWidth(300f)
                    actor.apply {
                        grow()
                        space(PADDING_MEDIUM)
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

    var numLogItems: Int by Delegates.observable(10) { _, _, _ ->
        checkRemoveLogItems()
    }

    private fun checkRemoveLogItems() {
        while (numLogItems < logVerticalGroup.children.size) {
            logVerticalGroup.removeActorAt(0, true).also {
                logScrollPane.scrollY -= it.height + logVerticalGroup.space
            }
        }
        logScrollPane.updateVisualScroll()
        logVerticalGroup.children.reversed().forEachIndexed { index, actor ->
            actor.clearActions()
            actor.addAction(
                Actions.alpha(
                    ((numLogItems - index).toFloat() / numLogItems).pow(2),
                    0.5f
                )
            )
        }
    }

    fun pushLogItem(text: String) {
        logVerticalGroup.label(text).apply {
            wrap = true
            setAlignment(Align.center)
        }
        checkRemoveLogItems()
        val action = Actions.delay(0.02f) then Actions.run {
            logScrollPane.scrollPercentY = 1f
        }
        logScrollPane.addAction(action)
    }

    fun showPopupText(target: String, text: String, labelStyle: String) {
        val (x, y) = when (target) {
            "c1" -> characterOneHealthBar.rootTable
            "c2" -> characterTwoHealthBar.rootTable
            "e1" -> enemyOneHealthBar.rootTable
            "e2" -> enemyTwoHealthBar.rootTable
            else -> return
        }.let {
            it.localToStageCoordinates(Vector2(it.width / 2, it.height / 2))
        }.let { it.x to it.y }

        scene2d.label(text, labelStyle) {
            width = prefWidth
            height = prefHeight
            setScale(BATTLE_POPUP_INITIAL_SCALE)
            alpha = BATTLE_POPUP_INITIAL_ALPHA
            setPosition(x, y, Align.center)
            val action = Actions.parallel(
                Actions.scaleTo(1f, 1f, BATTLE_POPUP_SCALE_UP_TIME),
                Actions.alpha(1f, BATTLE_POPUP_FADE_IN_TIME),
                Actions.moveToAligned(x, y + BATTLE_POPUP_VERTICAL_SHIFT, Align.center, BATTLE_POPUP_SHIFT_TIME)
            ) then
                    delay(BATTLE_POPUP_WAIT_TIME) then
                    Actions.alpha(0f, BATTLE_POPUP_FADE_TIME) then
                    Actions.removeActor()
            addAction(action)
        }.also { stage.addActor(it) }
    }
}