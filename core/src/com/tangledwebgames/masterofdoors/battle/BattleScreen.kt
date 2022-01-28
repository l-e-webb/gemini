package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.clearScreen

class BattleScreen(private val stage: Stage) : ScreenAdapter() {

    private lateinit var view: BattleScreenView

    override fun show() {
        stage.clear()
        view = BattleScreenView(stage).apply {
            characterOneName = "Alpha"
            characterOneMaxHealth = 75
            characterOneCurrentHealth = 75
            characterOneMaxMana = 20
            characterOneCurrentMana = 0
            characterTwoName = "Omega"
            characterTwoMaxHealth = 50
            characterTwoCurrentHealth = 25
            characterTwoMaxMana = 25
            characterTwoCurrentMana = 20
            enemyOneName = "Janus - Left"
            enemyOneMaxHealth = 1000
            enemyOneCurrentHealth = 500
            enemyTwoName = "Janus - Right"
            enemyTwoMaxHealth = 1000
            enemyTwoCurrentHealth = 1000

            var i = 0
            setMenu(listOf(
                BattleMenuItem("Attack", "attack"),
                BattleMenuItem("Skill", "skill"),
                BattleMenuItem("Spell", "spell")
            )) {
                characterOneCurrentHealth -= 5
                showEnemyTwo = !showEnemyTwo
                pushLogItem("Log item $i")
                i++
            }
        }
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