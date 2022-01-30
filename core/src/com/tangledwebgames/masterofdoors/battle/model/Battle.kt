package com.tangledwebgames.masterofdoors.battle.model

import com.badlogic.gdx.Gdx
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.onTurnEnd
import com.tangledwebgames.masterofdoors.battle.model.BattleFunctions.onTurnStart

data class Battle(
    val playerBattlers: MutableList<Battler> = mutableListOf(),
    val enemyBattlers: MutableList<Battler> = mutableListOf(),
    var round: Int = 0,
    var playerTurnIndex: Int = 0,
    var enemyTurnIndex: Int = 0,
    var phase: Phase = Phase.BATTLE_START
) {
    enum class Phase {
        BATTLE_START,
        ROUND_START,
        PLAYER_TURN_START,
        PLAYER_TURN,
        ENEMY_TURN,
        ROUND_END,
        BATTLE_END
    }

    val battlers: List<Battler>
        get() = playerBattlers + enemyBattlers

    private val _battleEvents = mutableListOf<BattleEvent>()
    val battleEvents: List<BattleEvent> = _battleEvents
    private val battleEventListeners: MutableSet<(BattleEvent) -> Unit> = mutableSetOf()

    fun getBattler(battlerId: String): Battler? = playerBattlers.firstOrNull { it.id == battlerId }
        ?: enemyBattlers.firstOrNull { it.id == battlerId }

    fun getCurrentPlayerBattler() = playerBattlers.getOrNull(playerTurnIndex)

    fun getCurrentEnemyBattler() = enemyBattlers.getOrNull(enemyTurnIndex)

    fun reset() {
        round = 0
        playerTurnIndex = 0
        enemyTurnIndex = 0
        battlers.forEach { it.initialize() }
        phase = Phase.BATTLE_START
        _battleEvents.clear()
    }

    fun begin() {
        reset()
        updatePhase(Phase.BATTLE_START)
        advance()
    }

    fun advance() {
        when (phase) {
            Phase.BATTLE_START -> {
                updatePhase(Phase.ROUND_START)
                advance()
            }
            Phase.ROUND_START -> {
                pushStateChange {
                    logMessage = "Begin round $round"
                    wait = 0.5f
                }
                playerTurnIndex = 0
                enemyTurnIndex = 0
                updatePhase(Phase.PLAYER_TURN_START)
                advance()
            }
            Phase.PLAYER_TURN_START -> {
                getCurrentPlayerBattler()?.let {
                    onPlayerTurnStart(it)
                    if (checkEndBattle()) {
                        return
                    }
                    if (it.canAct()) {
                        updatePhase(Phase.PLAYER_TURN)
                        // Do not advance, wait for action.
                    } else {
                        playerTurnIndex++
                        advance()
                    }
                } ?: run {
                    updatePhase(Phase.ENEMY_TURN)
                    advance()
                }
            }
            Phase.PLAYER_TURN -> {
                /* Do nothing, wait for player to select action */
            }
            Phase.ENEMY_TURN -> {
                getCurrentEnemyBattler()?.let {
                    takeEnemyTurn(it)
                }

                if (checkEndBattle()) {
                    return
                }

                if (phase == Phase.ENEMY_TURN && enemyTurnIndex == 0) {
                    enemyTurnIndex++
                    updatePhase(Phase.ENEMY_TURN)
                } else {
                    updatePhase(Phase.ROUND_END)
                }
                advance()
            }
            Phase.ROUND_END -> {
                round++
                updatePhase(Phase.ROUND_START)
                advance()
            }
            Phase.BATTLE_END -> {
                /* do nothing */
            }
        }
    }

    fun takePlayerAction(
        action: BattleAction,
        targets: List<Battler>
    ) {
        getCurrentPlayerBattler()?.let {
            takeAction(action, it, targets)
        }

        if (checkEndBattle()) {
            return
        }

        playerTurnIndex++
        updatePhase(Phase.PLAYER_TURN_START)
        advance()
    }

    fun takeEnemyTurn(enemy: Battler) {
        onEnemyTurnStart(enemy)
        if (checkEndBattle() || !enemy.canAct()) {
            return
        }
        determineEnemyAction(
            battle = this,
            enemy = enemy
        ).let { (action, targets) ->
            takeAction(
                action = action,
                actor = enemy,
                targets = targets
            )
        }
    }

    fun takeAction(
        action: BattleAction,
        actor: Battler,
        targets: List<Battler>
    ) {
        val battlers = battlers
        if (actor !in battlers || !battlers.containsAll(targets)) {
            Gdx.app.log(Battle::class.simpleName, "Attempting to invoke action with actor or battlers not in battle.")
            return
        }
        if (!action.canExecute(actor)) {
            Gdx.app.log(Battle::class.simpleName, "Attempting to take action that cannot be executed by this actor.")
            return
        }
        targets.filter { action.isValid(actor, it) }
            .takeIf { it.isNotEmpty() }
            ?.let { action.execute(actor, targets) }
            ?.forEach { pushBattleEvent(it) }
            ?: Gdx.app.log(Battle::class.simpleName, "Action invoked with no valid targets.")

        if (actor.isAlive() && !checkEndBattle()) {
            onTurnEnd(actor).forEach { pushBattleEvent(it) }
        }
    }

    fun checkEndBattle(): Boolean {
        return if (playerBattlers.none { it.isAlive() }) {
            pushBattleEvent(BattleEvent.BattleOver(playerWins = false))
            true
        } else if (enemyBattlers.none { it.isAlive() }) {
            pushBattleEvent(BattleEvent.BattleOver(playerWins = true))
            true
        } else {
            false
        }
    }

    fun onPlayerTurnStart(battler: Battler) {
        onTurnStart(battler).forEach { pushBattleEvent(it) }
    }

    fun onEnemyTurnStart(battler: Battler) {
        onTurnStart(battler).forEach { pushBattleEvent(it) }
    }

    fun addBattleEventListener(listener: (BattleEvent) -> Unit) {
        battleEventListeners.add(listener)
    }

    fun removeBattleEventListener(listener: (BattleEvent) -> Unit) {
        battleEventListeners.remove(listener)
    }

    fun clearBattleEventListeners() {
        battleEventListeners.clear()
    }

    fun updatePhase(phase: Phase) {
        this.phase = phase
        pushBattleEvent(BattleEvent.PhaseChange(phase))
    }

    fun pushBattleEvent(battleEvent: BattleEvent) {
        _battleEvents.add(battleEvent)
        battleEventListeners.forEach { it.invoke(battleEvent) }
    }

    inline fun pushStateChange(
        crossinline buildBlock: StateChangeBuilder.() -> Unit
    ) {
        pushBattleEvent(viewStateChange { buildBlock() })
    }
}