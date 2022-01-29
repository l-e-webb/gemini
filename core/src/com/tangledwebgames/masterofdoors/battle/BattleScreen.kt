package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.battle.model.Battle
import com.tangledwebgames.masterofdoors.battle.model.Battler
import ktx.app.clearScreen

class BattleScreen(private val stage: Stage) : ScreenAdapter() {

    private lateinit var view: BattleScreenView
    private lateinit var presenter: BattlePresenter
    private lateinit var battle: Battle

    override fun show() {
        stage.clear()
        view = BattleScreenView(stage)
        battle = Battle()
        presenter = BattlePresenter(view, battle)
        battle.playerBattlers.apply {
            add(
                Battler(
                    id = "p1",
                    name = "Player 1",
                    isEnemy = false
                )
            )
            add(
                Battler(
                    id = "p2",
                    name = "Player 2",
                    isEnemy = false
                )
            )
        }
        battle.enemyBattlers.apply {
            add(
                Battler(
                    id = "e1",
                    name = "Enemy 1",
                    isEnemy = true
                )
            )
        }
        battle.begin()
    }

    override fun render(delta: Float) {
        clearScreen(0f, 0f, 0f, 1f)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}