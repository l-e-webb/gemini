package com.tangledwebgames.masterofdoors.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.tangledwebgames.masterofdoors.HEALTH_BAR_STYLE
import com.tangledwebgames.masterofdoors.MANA_BAR_STYLE
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_HEIGHT
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_PADDING
import com.tangledwebgames.masterofdoors.UiConstants.HEALTH_BAR_WIDTH
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_SMALL
import com.tangledwebgames.masterofdoors.skin
import com.tangledwebgames.masterofdoors.util.textProperty
import ktx.actors.onClick
import ktx.scene2d.*
import ktx.style.get
import kotlin.properties.Delegates

class BattleScreenView(private val stage: Stage) {

    private val characterOneNameLabel: Label
    private val characterOneHealthBar: ProgressBar
    private val characterOneManaBar: ProgressBar
    private val characterTwoNameLabel: Label
    private val characterTwoHealthBar: ProgressBar
    private val characterTwoManaBar: ProgressBar

    private val menuContainer: Container<KVerticalGroup>

    init {
        val healthBarBg: NinePatchDrawable = skin["health-bar-bg"]
        stage.actors {
            table {
                setFillParent(true)
                pad(PADDING_LARGE)
                defaults().space(PADDING_LARGE)

                row().expandY().bottom()
                table {
                    background = skin["panel"]
                    pad(PADDING_SMALL)
                    defaults().space(PADDING_SMALL)

                    label("") {
                        it.left()
                        characterOneNameLabel = this
                    }

                    row()
                    container {
                        it.width(HEALTH_BAR_WIDTH).height(HEALTH_BAR_HEIGHT).grow()
                        background = healthBarBg
                        fill().pad(HEALTH_BAR_PADDING)

                        progressBar(style = HEALTH_BAR_STYLE) {
                            characterOneHealthBar = this
                        }
                    }

                    row()
                    container {
                        it.width(HEALTH_BAR_WIDTH).height(HEALTH_BAR_HEIGHT).grow()
                        background = healthBarBg
                        fill().pad(HEALTH_BAR_PADDING)

                        progressBar(style = MANA_BAR_STYLE) {
                            characterOneManaBar = this
                        }
                    }
                }

                table {
                    background = skin["panel"]
                    pad(PADDING_SMALL)
                    defaults().space(PADDING_SMALL)

                    label("") {
                        it.left()
                        characterTwoNameLabel = this
                    }

                    row()
                    container {
                        it.width(HEALTH_BAR_WIDTH).height(HEALTH_BAR_HEIGHT).grow()
                        background = healthBarBg
                        fill().pad(HEALTH_BAR_PADDING)

                        progressBar(style = HEALTH_BAR_STYLE) {
                            characterTwoHealthBar = this
                        }
                    }

                    row()
                    container {
                        it.width(HEALTH_BAR_WIDTH).height(HEALTH_BAR_HEIGHT).grow()
                        background = healthBarBg
                        fill().pad(HEALTH_BAR_PADDING)

                        progressBar(style = MANA_BAR_STYLE) {
                            characterTwoManaBar = this
                        }
                    }
                }
            }

            table {
                setFillParent(true)
                container(KVerticalGroup()) {
                    it.expand().bottom().left()
                    fillX().bottom().pad(PADDING_SMALL).prefWidth(300f)
                    actor.apply {
                        grow()
                        space(PADDING_SMALL)
                    }
                    menuContainer = this
                }
            }
        }
    }

    var characterOneName: String by characterOneNameLabel.textProperty()
    var characterOneHealthPercentage: Float by characterOneHealthBar::value
    var characterOneManaPercentage: Float by characterOneManaBar::value

    var characterTwoName: String by characterTwoNameLabel.textProperty()
    var characterTwoHealthPercentage: Float by characterTwoHealthBar::value
    var characterTwoManaPercentage: Float by characterTwoManaBar::value

    var onMenuItemClick: (String) -> Unit = {}

    var menuItems: List<BattleMenuItem> by Delegates.observable(emptyList()) { _, old, new ->
        if (old != new) {
            with (menuContainer.actor) {
                clearChildren()
                for (item in new) {
                    textButton(item.text) {
                        onClick {
                            onMenuItemClickInner(item.id)
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

    private fun onMenuItemClickInner(itemId: String) = onMenuItemClick(itemId)
}