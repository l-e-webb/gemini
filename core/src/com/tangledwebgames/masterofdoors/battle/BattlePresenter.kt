package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.tangledwebgames.masterofdoors.battle.model.Battle
import com.tangledwebgames.masterofdoors.battle.model.BattleAction
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.WAIT_AT_START_OF_ENEMY_TURN
import com.tangledwebgames.masterofdoors.battle.model.BattleEvent
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.actions.Attack
import ktx.actors.then

class BattlePresenter(
    val battleScreenView: BattleScreenView,
    val battle: Battle
) {

    init {
        battle.addBattleEventListener(::onBattleEvent)
        battleScreenView.onInfoButtonClick = {
            battleScreenView.showStatusDialog(battle.battlers)
        }
        battleScreenView.onMenuItemOver = {
            updateInfoText(it)
        }
    }

    var pendingAction: BattleAction? = null

    fun onBattleEvent(event: BattleEvent) {
        Gdx.app.log(BattlePresenter::class.simpleName, event.toString())
        when (event) {
            is BattleEvent.PhaseChange -> {
                if (event.phase == Battle.Phase.PLAYER_TURN) {
                    battleScreenView.enqueueAction(Actions.run { showActionMenu() })
                } else {
                    battleScreenView.enqueueAction(Actions.run { hideMenu() })
                }
                if (event.phase == Battle.Phase.ENEMY_TURN) {
                    battleScreenView.enqueueAction(delay(WAIT_AT_START_OF_ENEMY_TURN))
                }
                if (event.phase == Battle.Phase.BATTLE_START) {
                    battleScreenView.clearAllBattlerViews()
                    battle.battlers.forEach {
                        addBattlerView(it)
                    }
                }
            }
            is BattleEvent.ViewStateChange -> {
                applyViewStateChange(event)
            }
            is BattleEvent.AddBattler -> {
                battle.getBattler(event.battlerId)?.let {
                    battleScreenView.enqueueAction(Actions.run {
                        addBattlerView(it)
                    })
                }
            }
            is BattleEvent.RemoveBattler -> {
                battleScreenView.enqueueAction(Actions.run {
                    battleScreenView.removeBattlerView(event.battlerId)
                })
            }
            is BattleEvent.BattleOver -> {
                battleScreenView.enqueueAction(Actions.run {
                    showEndDialog(playerWins = event.playerWins)
                })
            }
        }
    }

    fun addBattlerView(battler: Battler) {
        battleScreenView.addBattlerView(
            battlerId = battler.id,
            isPlayer = !battler.isEnemy
        )?.setFrom(battler)
    }

    fun showActionMenu() {
        pendingAction = null
        updateInfoText(null)
        battleScreenView.setMenu(
            listOf(
                BattleMenuItem(id = Attack.id, text = Attack.name),
                BattleMenuItem(id = "skill", text = "Skills")
            )
        ) {
            when (it) {
                Attack.id -> {
                    pendingAction = Attack
                    showTargetingMenu()
                }
                "skill" -> showSkillMenu()
            }
        }
    }

    fun showTargetingMenu() {
        val pendingAction = pendingAction ?: return
        val playerBattler = battle.getCurrentPlayerBattler() ?: return
        val targets = battle.battlers.filter {
            pendingAction.isValid(playerBattler, it)
        }

        updateInfoText(null)

        targets.map { target ->
            BattleMenuItem(id = target.id, text = target.name)
        }.let { menuItems ->
            menuItems + BattleMenuItem(id = "back", text = "Back")
        }.let { menuItems ->
            battleScreenView.setMenu(menuItems) { id ->
                if (id == "back") {
                    if (pendingAction.id == Attack.id) {
                        showActionMenu()
                    } else {
                        showSkillMenu()
                    }
                } else {
                    targets.first { it.id == id }
                        .let { target ->
                            hideMenu()
                            battle.takePlayerAction(
                                action = pendingAction,
                                targets = listOf(target)
                            )
                        }
                }
            }
        }
    }

    fun showSkillMenu() {
        pendingAction = null
        updateInfoText(null)
        val playerBattler = battle.getCurrentPlayerBattler() ?: return
        val menuItems = playerBattler.skills.map {
            BattleMenuItem(
                id = it.id,
                text = it.name,
                endText = it.manaCost.toString()
            )
        } + BattleMenuItem(id = "back", text = "Back")
        battleScreenView.setMenu(menuItems) { id ->
            if (id == "back") {
                showActionMenu()
            } else {
                playerBattler.skills
                    .first { it.id == id }
                    .let {
                        when {
                            it.canExecute(playerBattler) -> {
                                pendingAction = it
                                showTargetingMenu()
                            }
                            playerBattler.mana < it.manaCost -> {
                                battleScreenView.showDialog(
                                    title = "Insufficient mana!",
                                    buttons = listOf(BattleMenuItem("Continue", ""))
                                ) {}
                            }
                            else -> {
                                battleScreenView.showDialog(
                                    title = "You cannot use that skill",
                                    buttons = listOf(BattleMenuItem("Continue", ""))
                                ) {}
                            }
                        }
                    }
            }
        }
    }

    fun hideMenu() {
        battleScreenView.menuItems = emptyList()
    }

    fun applyViewStateChange(event: BattleEvent.ViewStateChange) {
        var action: Action = Actions.run {
            event.logMessage?.let {
                battleScreenView.pushLogItem(it)
            }
            event.textPopups?.forEach {
                battleScreenView.showPopupText(
                    it.battlerId, it.text, it.labelStyle
                )
            }
            event.statusChanges?.forEach { change ->
                battleScreenView.getViewHolder(change.battlerId)?.apply {
                    change.name?.let {
                        name = it
                    }
                    change.maxHealth?.let {
                        maxHealth = it
                    }
                    change.health?.let {
                        health = it
                    }
                    change.maxMana?.let {
                        maxMana = it
                    }
                    change.mana?.let {
                        mana = it
                    }
                    // TODO: status effects
                }
            }
        }
        event.wait?.let {
            action = action then Actions.delay(it)
        }
        battleScreenView.enqueueAction(action)
    }

    fun showEndDialog(playerWins: Boolean) {
        val title = if (playerWins) {
            "Victory!"
        } else {
            "Game over..."
        }
        val buttons = listOf(
            BattleMenuItem(
                text = "Play again",
                id = "play"
            ),
            BattleMenuItem(
                text = "Quit",
                id = "quit"
            )
        )

        battleScreenView.showDialog(title = title, buttons = buttons) {
            when (it) {
                "play" -> battle.begin()
                "quit" -> Gdx.app.exit()
            }
        }
    }

    fun updateInfoText(highlightedItem: BattleMenuItem?) {
        battleScreenView.infoText = highlightedItem
            ?.id
            ?.let { id ->
                if (id == Attack.id) {
                    Attack.infoText()
                } else {
                    battle.getCurrentPlayerBattler()
                        ?.skills
                        ?.firstOrNull { it.id == id }
                        ?.infoText()
                }
            } ?: pendingAction?.infoText() ?: ""
    }

    fun clear() {
        battle.clearBattleEventListeners()
        battleScreenView.stage.clear()
    }
}