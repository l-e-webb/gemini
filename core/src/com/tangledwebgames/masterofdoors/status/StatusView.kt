package com.tangledwebgames.masterofdoors.status

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tangledwebgames.masterofdoors.LABEL_LARGE_STYLE
import com.tangledwebgames.masterofdoors.UiConstants
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_LARGE
import com.tangledwebgames.masterofdoors.UiConstants.PADDING_MEDIUM
import com.tangledwebgames.masterofdoors.battle.model.BattleConstants
import com.tangledwebgames.masterofdoors.battle.model.Battler
import com.tangledwebgames.masterofdoors.util.textProperty
import ktx.actors.onClick
import ktx.scene2d.*
import ktx.style.get

class StatusView(val includeSelectionArrows: Boolean = false) {
    val rootTable: KTableWidget
    val battlerStatusTable: KTableWidget

    private val infoLabel: Label = scene2d.label("")
    var infoText: String by infoLabel.textProperty()

    var onLeftSelectionArrowClick: (Int) -> Unit = {}
    var onRightSelectionArrowClick: (Int) -> Unit = {}

    init {
        rootTable = scene2d.table {
            defaults().space(PADDING_LARGE)

            table { infoCell ->
                infoCell.expandY().bottom()
                background = skin["panel"]
                pad(UiConstants.PADDING_SMALL)
                defaults().space(UiConstants.PADDING_SMALL)

                row().left()
                label("Skill Info", "title")
                row().fill()
                actor(infoLabel) {
                    it.width(400f).height(150f)
                    setAlignment(Align.topLeft)
                    wrap = true
                }
            }

            table {
                battlerStatusTable = this
            }

            row()
            table { keyCell ->
                keyCell.colspan(2)
                background = skin["panel"]
                pad(PADDING_LARGE)
                defaults().space(UiConstants.PADDING_MEDIUM)
                label("Key", "title")

                row()
                table {
                    defaults()
                        .spaceTop(UiConstants.PADDING_SMALL)
                        .spaceBottom(UiConstants.PADDING_SMALL)
                        .spaceLeft(PADDING_LARGE * 2)
                        .spaceRight(PADDING_LARGE * 2)
                        .uniformX()
                        .left()

                    row()
                    label("Health", LABEL_LARGE_STYLE)
                    label("Attack", LABEL_LARGE_STYLE)
                    label("Defense", LABEL_LARGE_STYLE)
                    label("Precision", LABEL_LARGE_STYLE)

                    row()
                    label("${BattleConstants.PLAYER_BONUS_HEALTH} + (Physique + Power + Caution) x 5", "stat-value")
                    label("Physique + Power + Aggression", "stat-value")
                    label("Physique + Finesse + Caution", "stat-value")
                    label("Physique + Finesse + Aggression", "stat-value")

                    row().fillX().uniformY().top()
                    label("Reduced by attacks; if reduced to 0, character falls.") {
                        wrap = true
                    }
                    label("Increases damage dealt by attacks and physical skills.") {
                        wrap = true
                    }
                    label("Decreases damage from all attacks.") {
                        wrap = true
                    }
                    label("Increases crit rate and effectiveness of bonus effects of physical skills.") {
                        wrap = true
                    }

                    row().spaceTop(PADDING_LARGE)
                    label("Mana", LABEL_LARGE_STYLE)
                    label("Magic Attack", LABEL_LARGE_STYLE)
                    label("Healing", LABEL_LARGE_STYLE)
                    label("Spellcraft", LABEL_LARGE_STYLE)

                    row()
                    label("${BattleConstants.PLAYER_BONUS_MANA} + (Spirit + Power + Caution) x 2", "stat-value")
                    label("Spirit + Power + Aggression", "stat-value")
                    label("Spirit + Finesse + Caution", "stat-value")
                    label("Spriit + Finesse + Agression", "stat-value")

                    row().fillX().uniformY().top()
                    label("Resource required to use skills. Regenerates by 25% of max value every turn.") {
                        wrap = true
                    }
                    label("Increases damage dealt by magic skills.") {
                        wrap = true
                    }
                    label("Increases effectiveness of healing skills.") {
                        wrap = true
                    }
                    label("Increases effectiveness of bonus effects of spells.") {
                        wrap = true
                    }
                }
            }
        }
    }

    fun buildBattlerTable(battlers: List<Battler>) {
        battlerStatusTable.apply {
            clear()
            defaults().space(PADDING_LARGE)
            row().uniformX().fillX()
            battlers.forEachIndexed { index, battler ->
                table {
                    clear()
                    defaults().space(PADDING_MEDIUM)
                    if (includeSelectionArrows) {
                        textButton("<") {
                            it.right()
                            onClick{
                                onLeftSelectionArrowClick(index)
                            }
                        }
                        textButton(">") {
                            it.left()
                            onClick {
                                onRightSelectionArrowClick(index)
                            }
                        }
                    }

                    row()
                    table { cell ->
                        cell.colspan(2)
                        battlerStatusView(
                            battler = battler,
                            onSkillSelect = {
                                infoText = it?.infoText() ?: ""
                            }
                        )
                    }
                }
            }
        }
    }
}