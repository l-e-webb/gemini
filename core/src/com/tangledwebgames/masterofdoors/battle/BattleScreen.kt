package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.MasterOfDoorsGame
import com.tangledwebgames.masterofdoors.battle.model.Battle
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.battlers.attackGeminusPhys
import com.tangledwebgames.masterofdoors.battle.model.battlers.supportGeminus
import com.tangledwebgames.masterofdoors.status.ClassSelectScreen
import ktx.app.clearScreen

class BattleScreen(
    private val game: MasterOfDoorsGame,
    private val playerClasses: List<Battler>
) : ScreenAdapter() {

    private val stage: Stage
        get() = game.stage

    private lateinit var view: BattleScreenView
    private lateinit var presenter: BattlePresenter
    private lateinit var battle: Battle

    override fun show() {
        stage.clear()
        view = BattleScreenView(this, stage)
        battle = Battle(
            playerBattlers = playerClasses.toMutableList()
        )
        presenter = BattlePresenter(this, view, battle)
        battle.enemyBattlers.apply {
            add(attackGeminusPhys())
            add(supportGeminus())
        }
        battle.begin()
    }

    fun returnToClassSelect() {
        game.screen = ClassSelectScreen(game)
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