package com.tangledwebgames.masterofdoors.battle.model

data class StatusEffect(
    var id: String,
    var name: String,
    var battlerId: String,
    var duration: Int? = null
)