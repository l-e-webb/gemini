package com.tangledwebgames.masterofdoors.battle.model

interface BattleAction {

    enum class TargetType {
        SINGLE, SIDE, ALL
    }

    val id: String
    val name: String
    val manaCost: Int
    val targetType: TargetType
    val description: String

    fun infoText(): String = "$name ($manaCost Mana)\n$description"

    fun isValid(actor: Battler, target: Battler): Boolean

    fun execute(actor: Battler, targets: List<Battler>): List<BattleEvent> {
        return targets.flatMap { execute(actor, it) }
    }

    fun canExecute(actor: Battler): Boolean {
        return actor.canAct() && (actor.isEnemy || actor.mana >= manaCost)
    }

    fun execute(actor: Battler, target: Battler): List<BattleEvent>
}