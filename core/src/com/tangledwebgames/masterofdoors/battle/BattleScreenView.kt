package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
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
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.actors.then
import ktx.scene2d.*
import ktx.style.get
import kotlin.math.pow
import kotlin.properties.Delegates

class BattleScreenView(val stage: Stage) {

    val sequenceAction = SequenceAction()

    val playerBattlerViewHolders = mutableListOf<BattlerViewHolder>()
    val enemyBattlerViewHolders = mutableListOf<BattlerViewHolder>()

    private val enemyHorizontalGroup: KHorizontalGroup
    private val playerHorizontalGroup: KHorizontalGroup

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
            if (includeBackground) {
                background = skin["panel"]
                pad(PADDING_MEDIUM)
            }
            defaults().space(PADDING_MEDIUM)

            actor(viewHolder.nameLabel) { it.left() }

            row()
            actor(viewHolder.healthBar.rootTable)

            viewHolder.manaBar?.let {
                row()
                actor(it.rootTable)
            }
        }
        return viewHolder
    }

    var onMenuItemClick: (String) -> Unit = {}

    var menuItems: List<BattleMenuItem> by Delegates.observable(emptyList()) { _, old, new ->
        if (old != new) {
            with(menuContainer.actor) {
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

    fun showPopupText(target: String, text: String, labelStyle: String) {
        val (x, y) = getViewHolder(target)
            ?.rootTable
            ?.let {
                it.localToStageCoordinates(Vector2(it.width / 2, it.height / 2))
            }?.let { it.x to it.y } ?: return

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
}