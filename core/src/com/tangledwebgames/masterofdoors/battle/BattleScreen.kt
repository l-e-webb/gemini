package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tangledwebgames.masterofdoors.battle.model.Battle
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants.BOSS_FORM_ONE_BONUS_HEALTH
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.battle.model.battlers.paladin
import com.tangledwebgames.masterofdoors.battle.model.battlers.rogue
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
            add(paladin())
            add(rogue())
        }
        battle.enemyBattlers.apply {
            add(
                Battler(
                    id = "e1",
                    name = "Enemy 1",
                    isEnemy = true,
                    bonusHealth = BOSS_FORM_ONE_BONUS_HEALTH,
                    physique = 6,
                    spirit = 6,
                    power = 6,
                    finesse = 6,
                    aggression = 6,
                    caution = 6
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