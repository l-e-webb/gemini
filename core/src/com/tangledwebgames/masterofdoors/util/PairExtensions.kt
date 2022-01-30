package com.tangledwebgames.masterofdoors.util

operator fun Pair<Int, Int>.times(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first * other.first, second * other.second)
}

operator fun Pair<Int, Int>.times(n: Int): Int = (first * n) / second

operator fun Int.times(multiplier: Pair<Int, Int>) =
    (this * multiplier.first) / multiplier.second

fun Pair<Int, Int>.reciproal(): Pair<Int, Int> = second to first