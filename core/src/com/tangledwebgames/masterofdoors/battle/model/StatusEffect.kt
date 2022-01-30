package com.tangledwebgames.masterofdoors.battle.model

data class StatusEffect(
    var id: String,
    var name: String,
    var battlerId: String,
    var duration: Int? = null,
    var removeAtTurnEnd: Boolean = false,
    var damageOverTime: Int? = null,
    var healingOverTime: Int? = null,
    val statSet: StatSet = StatSet()
) {
    fun adjustDuration(amount: Int) {
        duration = duration?.let { it + amount }
    }

    fun setDurationToLonger(otherDuration: Int?): Boolean {
        val currentDuration = duration
        if (currentDuration != null && otherDuration != null && otherDuration > currentDuration) {
            duration = otherDuration
            return true
        } else if (currentDuration != null && otherDuration == null) {
            duration = null
            return true
        }
        return false
    }
}