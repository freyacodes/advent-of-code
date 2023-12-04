package y2023

import java.lang.StrictMath.pow

private fun winnerCount(line: String): Int {
    val (s1, s2) = line.split("|")
    val winning = s1.trim().split(Regex("\\s+")).map { it.toInt() }
    val actual = s2.trim().split(Regex("\\s+")).map { it.toInt() }
    return actual.count { winning.contains(it) }
}

private fun partOne(): Double {
    return getInput(4).map { it.drop(10) }.sumOf { line ->
        val matched = winnerCount(line)
        if (matched == 0) return@sumOf 0.0
        pow(2.0, matched.toDouble() - 1)
    }
}

private fun partTwo(): Int {
    val input = getInput(4)
    val points = input.map { 1 }.toMutableList()
    input.map { it.drop(10) }.forEachIndexed { index, line ->
        val matched = winnerCount(line)
        val magnitude = points[index]
        for (offset in 1..matched) {
            points[index + offset] += magnitude
        }
    }
    return points.sum()
}

fun main() {
    println("Part one: ${partOne()}")
    println("Part one: ${partTwo()}")
}