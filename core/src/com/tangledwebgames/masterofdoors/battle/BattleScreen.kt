package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.battle.model.Battle
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.ENEMY_ONE_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.ENEMY_TWO_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_ONE_ID
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.PLAYER_TWO_ID
import com.tangledwebgames.masterofdoors.battle.model.Battler
import ktx.app.clearScreen

class BattleScreen(private val stage: Stage) : ScreenAdapter() {

    private lateinit var view: BattleScreenView
    private lateinit var presenter: BattlePresenter
    private lateinit var battle: Battle

    override fun show() {
        stage.clear()
        view = BattleScreenView(stage)
        battle = Battle(
            Battler(PLAYER_ONE_ID, "Player One"),
            Battler(PLAYER_TWO_ID, "Player Two"),
            Battler(ENEMY_ONE_ID, "Enemy One"),
            Battler(ENEMY_TWO_ID, "Enemy Two")
        )
        presenter = BattlePresenter(view, battle)
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