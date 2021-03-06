package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.tangledwebgames.masterofdoors.LABEL_LARGE_STYLE
import com.tangledwebgames.masterofdoors.STAT_VALUE_LARGE_STYLE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_MEDIUM
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_SMALL
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.skin
import com.tangledwebgames.masterofdoors.status.StatusView
import com.tangledwebgames.masterofdoors.util.textProperty
import ktx.actors.contains
import ktx.actors.onClick
import ktx.actors.then
import ktx.scene2d.*
import ktx.style.get
import kotlin.math.pow
import kotlin.properties.Delegates

class BattleScreenView(
    val battleScreen: BattleScreen,
    val stage: Stage
) {

    val sequenceAction = SequenceAction()

    val playerBattlerViewHolders = mutableListOf<BattlerViewHolder>()
    val enemyBattlerViewHolders = mutableListOf<BattlerViewHolder>()

    private val enemyHorizontalGroup: KHorizontalGroup
    private val playerHorizontalGroup: KHorizontalGroup

    private val logScrollPane: ScrollPane
    private val logVerticalGroup: KVerticalGroup

    private val menuContainer: Container<KVerticalGroup>
    private val infoLabel: Label

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
                horizontalGroup {
                    enemyHorizontalGroup = this
                    space(PADDING_LARGE)
                }

                row()
                horizontalGroup {
                    playerHorizontalGroup = this
                    space(PADDING_LARGE)
                }
            }

            table {
                setFillParent(true)
                pad(PADDING_LARGE)
                defaults().space(PADDING_MEDIUM)
                bottom().left()
                container(KVerticalGroup()) {
                    it.width(400f).fillX().left()
                    background = this@table.skin["panel"]
                    fillX().pad(PADDING_MEDIUM)
                    actor.apply {
                        grow()
                        space(PADDING_MEDIUM)
                    }
                    menuContainer = this
                }

                row()
                table { cell ->
                    cell.width(400f).height(150f).fill()
                    background = skin["panel"]
                    pad(PADDING_MEDIUM)
                    defaults().space(PADDING_MEDIUM)

                    row()
                    label("Info", "title") {
                        it.left()
                    }

                    row()
                    label("") {
                        it.grow()
                        setAlignment(Align.topLeft)
                        wrap = true
                        infoLabel = this
                    }
                }
            }

            container {
                pad(PADDING_LARGE)
                setFillParent(true)
                bottom().right()
                button(style = "info-uncheckable") {
                    onClick {
                        onInfoButtonClick()
                    }
                }
            }

            container {
                pad(PADDING_LARGE)
                setFillParent(true)
                top().right()
                textButton("Return to class select") {
                    onClick {
                        battleScreen.returnToClassSelect()
                    }
                }
            }
        }
    }

    var onMenuItemClick: (String) -> Unit = {}
    var onMenuItemOver: (BattleMenuItem?) -> Unit = {}
    var onInfoButtonClick: () -> Unit = {}

    var infoText: String by infoLabel.textProperty()

    var menuItems: List<BattleMenuItem> by Delegates.observable(emptyList()) { _, old, new ->
        if (old != new) {
            with(menuContainer.actor) {
                clearChildren()
                for (item in new) {
                    textButton(item.text) {
                        padLeft(PADDING_MEDIUM).padRight(PADDING_MEDIUM)
                        label.setAlignment(Align.left)
                        item.endText?.let { text ->
                            label(text, STAT_VALUE_LARGE_STYLE)  {
                                it.right()
                            }
                        }
                        onClick {
                            onMenuItemClick(item.id)
                        }
                        addListener(object : InputListener() {
                            override fun enter(
                                event: InputEvent?,
                                x: Float,
                                y: Float,
                                pointer: Int,
                                fromActor: Actor?
                            ) {
                                super.enter(event, x, y, pointer, fromActor)
                                onMenuItemOver(item)
                            }

                            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                                super.exit(event, x, y, pointer, toActor)
                                if (toActor?.let { a -> menuContainer.actor?.contains(a) } != true) {
                                    onMenuItemOver(null)
                                }
                            }
                        })
                    }
                }
            }

            menuContainer.background = if (new.isEmpty()) {
                null
            } else {
                skin["panel"]
            }
            menuContainer.pad(PADDING_MEDIUM)
        }
    }

    inline fun setMenu(items: List<BattleMenuItem>, crossinline onClick: (String) -> Unit) {
        menuItems = items
        onMenuItemClick = { onClick(it) }
    }

    var numLogItems: Int by Delegates.observable(10) { _, _, _ ->
        checkRemoveLogItems()
    }

    fun addBattlerView(battlerId: String, isPlayer: Boolean): BattlerViewHolder? {
        val viewHolderList = if (isPlayer) {
            playerBattlerViewHolders
        } else {
            enemyBattlerViewHolders
        }
        val horizontalGroup = if (isPlayer) {
            playerHorizontalGroup
        } else {
            enemyHorizontalGroup
        }

        if (viewHolderList.any { it.battlerId == battlerId }) {
            Gdx.app.log(
                BattleScreenView::class.simpleName,
                "Attempting to add battler view for $battlerId when view for that battler ID is already present."
            )
            return null
        }

        return battlerView(
            battlerId = battlerId,
            includeBackground = isPlayer,
            includeManaBar = isPlayer
        ).also {
            viewHolderList.add(it)
            horizontalGroup.addActor(it.rootTable)
        }
    }

    fun addPlayerBattlerView(battlerId: String) = addBattlerView(battlerId = battlerId, isPlayer = true)

    fun addEnemyBattlerView(battlerId: String) = addBattlerView(battlerId = battlerId, isPlayer = false)

    fun removeBattlerView(battlerId: String) {
        getViewHolder(battlerId)?.let {
            playerBattlerViewHolders.remove(it)
            enemyBattlerViewHolders.remove(it)
            playerHorizontalGroup.removeActor(it.rootTable)
            enemyHorizontalGroup.removeActor(it.rootTable)
        }
    }

    fun clearAllBattlerViews() {
        enemyBattlerViewHolders.clear()
        playerBattlerViewHolders.clear()
        enemyHorizontalGroup.clearChildren()
        playerHorizontalGroup.clearChildren()
    }

    fun getViewHolder(battlerId: String): BattlerViewHolder? {
        return enemyBattlerViewHolders.firstOrNull { it.battlerId == battlerId }
            ?: playerBattlerViewHolders.firstOrNull { it.battlerId == battlerId }
    }

    fun pushLogItem(text: String) {
        logVerticalGroup.label(text, LABEL_LARGE_STYLE).apply {
            wrap = true
            setAlignment(Align.center)
        }
        checkRemoveLogItems()
        val action = Actions.delay(0.02f) then Actions.run {
            logScrollPane.scrollPercentY = 1f
        }
        logScrollPane.addAction(action)
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

    fun enqueueAction(action: Action) {
        if (sequenceAction.actor == null) {
            sequenceAction.reset()
        }
        sequenceAction.addAction(action)
        if (sequenceAction.actor == null) {
            stage.addAction(sequenceAction)
        }
    }

    fun showDialog(
        title: String? = null,
        body: String? = null,
        buttons: List<BattleMenuItem>,
        onClick: (String) -> Unit
    ) {
        object : Dialog(title ?: "", skin) {
            override fun result(`object`: Any?) {
                onClick(`object`.toString())
            }
        }.apply {
            pad(PADDING_LARGE)
            padTop(titleLabel.prefHeight + PADDING_MEDIUM)
            defaults().space(PADDING_LARGE)

            titleLabel.setAlignment(Align.center)

            body?.let {

                contentTable.add(Label(it, skin)).apply {
                    fill().minWidth(300f)
                    actor.wrap = true
                }
            }
            buttonTable.defaults().space(PADDING_MEDIUM)
            buttons.forEach {
                button(it.text, it.id)
            }

            show(this@BattleScreenView.stage)
        }
    }

    fun showStatusDialog(battlers: List<Battler>) {
        scene2d.dialog("Status") {
            setFillParent(true)
            titleLabel.setAlignment(Align.center)
            pad(PADDING_LARGE)
            padTop(titleLabel.prefHeight + PADDING_MEDIUM)
            defaults().space(PADDING_LARGE)

            val statusView = StatusView()

            scene2d.table {
                defaults().space(PADDING_LARGE)

                row()
                val buttonGroup = ButtonGroup<TextButton>().apply {
                    setMinCheckCount(1)
                    setMaxCheckCount(1)
                }
                textButton("Player", "checkable") { cell ->
                    cell.right()
                    buttonGroup.add(this)
                    onClick {
                        statusView.buildBattlerTable(
                            battlers = battlers.filter { !it.isEnemy }
                        )
                    }
                }
                textButton("Enemy", "checkable") { cell ->
                    cell.left()
                    buttonGroup.add(this)
                    onClick {
                        statusView.buildBattlerTable(
                            battlers = battlers.filter { it.isEnemy }
                        )
                    }
                }

                row()
                actor(statusView.rootTable) { statusViewCell ->
                    statusViewCell.grow().colspan(2)
                }
            }.let {
                contentTable.add(it)
            }

            button("Exit")

            statusView.buildBattlerTable(
                battlers = battlers.filter { !it.isEnemy }
            )
            show(this@BattleScreenView.stage)
        }
    }
}