package com.tangledwebgames.masterofdoors.battle.model

import com.badlogic.gdx.Gdx
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.ENEMY_ONE_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.ENEMY_TWO_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_ONE_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_TWO_ID

data class Battle(
    val playerBattlerOne: Battler,
    val playerBattlerTwo: Battler,
    val enemyBattlerOne: Battler,
    val enemyBattlerTwo: Battler,
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

    init {
        playerBattlerOne.id = PLAYER_ONE_ID
        playerBattlerTwo.id = PLAYER_TWO_ID
        enemyBattlerOne.id = ENEMY_ONE_ID
        enemyBattlerTwo.id = ENEMY_TWO_ID
    }

    val battlers = listOf(
        playerBattlerOne,
        playerBattlerTwo,
        enemyBattlerOne,
        enemyBattlerTwo
    )

    val battlerMap = battlers.associateBy { it.id }

    private val _battleEvents = mutableListOf<BattleEvent>()
    val battleEvents: List<BattleEvent> = _battleEvents
    private val battleEventListeners: MutableSet<(BattleEvent) -> Unit> = mutableSetOf()

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
                when (playerTurnIndex) {
                    0 -> playerBattlerOne
                    1 -> playerBattlerTwo
                    else -> null
                }?.let {
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
                if (enemyTurnIndex < 2) {
                    takeEnemyTurn()
                }

                if (checkEndBattle()) { return }

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
        when (playerTurnIndex) {
            0 -> playerBattlerOne
            1 -> playerBattlerTwo
            else -> null
        }?.let {
            takeAction(action, it, targets)
        }

        if (checkEndBattle()) { return }

        playerTurnIndex++
        updatePhase(Phase.PLAYER_TURN_START)
        advance()
    }

    fun takeAction(
        action: BattleAction,
        actor: Battler,
        targets: List<Battler>
    ) {
        if (actor !in battlers || !battlers.containsAll(targets)) {
            Gdx.app.log(Battle::class.simpleName, "Attempting to invoke action with actor or battlers not in battle.")
            return
        }
        if (!actor.canAct()) {
            Gdx.app.log(Battle::class.simpleName, "Attempting to act as ${actor.name}, but cannot act.")
            return
        }
        targets.filter { action.isValid(actor, it) }
            .takeIf { it.isNotEmpty() }
            ?.let { action.execute(actor, targets) }
            ?.forEach { pushBattleEvent(it) }
            ?: Gdx.app.log(Battle::class.simpleName, "Action invoked with no valid targets.")
    }

    fun takeEnemyTurn() {
        val enemy = when (enemyTurnIndex) {
            0 -> enemyBattlerOne
            1 -> enemyBattlerTwo
            else -> return
        }
        if (!enemy.canAct()) {
            return
        }
        determineEnemyAction(
            battle = this,
            enemyIndex = enemyTurnIndex
        ).let { (action, targets) ->
            takeAction(
                action = action,
                actor = enemy,
                targets = targets
            )
        }
    }

    fun checkEndBattle(): Boolean {
        return if (!playerBattlerOne.isAlive() && !playerBattlerTwo.isAlive()) {
            pushBattleEvent(BattleEvent.BattleOver(playerWins = false))
            true
        } else if (!enemyBattlerOne.isAlive() && !enemyBattlerOne.isAlive()) {
            pushBattleEvent(BattleEvent.BattleOver(playerWins = true))
            true
        } else {
            false
        }
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