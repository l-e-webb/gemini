package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.tangledwebgames.masterofdoors.battle.model.Battle
import com.tangledwebgames.masterofdoors.battle.model.BattleAction
import com.tangledwebgames.masterofdoors.battle.model.BattleEvent
import com.tangledwebgames.masterofdoors.battle.model.actions.Attack
import ktx.actors.then

class BattlePresenter(
    val battleScreenView: BattleScreenView,
    val battle: Battle
) {

    init {
        battle.addBattleEventListener(::onBattleEvent)
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
                    battleScreenView.enqueueAction(delay(1f))
                }
                if (event.phase == Battle.Phase.BATTLE_START) {
                    battle.battlers.forEach {
                        battleScreenView.getViewHolder(it.id).apply {
                            name = it.name
                            maxHealth = it.maxHealth
                            health = it.health
                            maxMana = it.maxMana
                            mana = it .mana
                            // TODO: status effect
                        }
                    }
                }
            }
            is BattleEvent.ViewStateChange -> {
                applyViewStateChange(event)
            }
        }
    }

    fun showActionMenu() {
        battleScreenView.setMenu(
            listOf(
                BattleMenuItem(Attack.NAME, Attack.ATTACK_ID),
                BattleMenuItem("Skills", "skill")
            )
        ) {
            when (it) {
                Attack.ATTACK_ID -> {
                    pendingAction = Attack.instance
                    showTargetingMenu()
                }
                "skill" -> showSkillMenu()
            }
        }
    }

    fun showTargetingMenu() {
        val pendingAction = pendingAction ?: return
        val playerBattler = battle.getCurrentPlayerBattler()
        val targets = battle.battlers.filter {
            pendingAction.isValid(playerBattler, it)
        }

        targets.map { target ->
            BattleMenuItem(text = target.name, id = target.id)
        }.let { menuItems ->
            menuItems + BattleMenuItem("Back", "back")
        }.let { menuItems ->
            battleScreenView.setMenu(menuItems) { id ->
                if (id == "back") {
                    if (pendingAction.id == Attack.ATTACK_ID) {
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
        val playerBattler = battle.getCurrentPlayerBattler()
        val menuItems = playerBattler.skills.map {
            BattleMenuItem(text = it.name, id = it.id)
        } + BattleMenuItem("Back", "back")
        battleScreenView.setMenu(menuItems) { id ->
            if (id == "back") {
                showActionMenu()
            } else {
                playerBattler.skills
                    .first { it.id == id }
                    .let {
                        pendingAction = it
                        showTargetingMenu()
                    }
            }
        }
    }



    fun hideMenu() {
        battleScreenView.menuItems = emptyList()
    }

    fun applyViewStateChange(event: BattleEvent.ViewStateChange) {
        var action: Action  = Actions.run {
            event.logMessage?.let {
                battleScreenView.pushLogItem(it)
            }
            event.textPopups?.forEach {
                battleScreenView.showPopupText(
                    it.battlerId, it.text, it.labelStyle
                )
            }
            event.statusChanges?.forEach { change ->
                with (battleScreenView.getViewHolder(change.battlerId)) {
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

    fun clear() {
        battle.clearBattleEventListeners()
        battleScreenView.stage.clear()
    }
}