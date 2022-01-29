package com.tangledwebgames.masterofdoors.util

inline fun <T> listBuilder(
    buildBlock: MutableList<T>.() -> Unit
): List<T> = mutableListOf<T>().apply(buildBlock).toList()