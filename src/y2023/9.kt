package y2023

private fun parse(): List<List<Long>> {
    return getInput(9).map { s -> s.split(" ").map { it.toLong() } }
}

private fun partOne(): Long {
    return parse().sumOf { initial ->
        predict(initial).last()
    }
}

private fun partTwo(): Long {
    return parse().sumOf { initial ->
        predict(initial.reversed()).last()
    }
}

fun predict(initial: List<Long>): List<Long> {
    if (initial.all { it == initial.first() }) {
        return initial
    }

    val newSequence = initial.zipWithNext().map {
        it.second - it.first
    }

    return initial + (initial.last() + predict(newSequence).last())
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}