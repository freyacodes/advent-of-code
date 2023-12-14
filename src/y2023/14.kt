package y2023

import y2022.Point2
import y2022.p

private sealed interface Rock
object RoundRock : Rock
object CubeRock : Rock

private class Bounds(map: Map<Point2, Rock>) {
    val x: IntRange
    val y: IntRange

    init {
        x = 0 .. map.keys.maxOf { it.x }
        y = 0 .. map.keys.maxOf { it.y }
    }

    fun contains(point: Point2): Boolean {
        return x.contains(point.x) && y.contains(point.y)
    }
}

private fun parse(): MutableMap<Point2, Rock> {
    val map = mutableMapOf<Point2, Rock>()
    getInput(14).forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            when (c) {
                'O' -> map[p(x, y)] = RoundRock
                '#' -> map[p(x, y)] = CubeRock
            }
        }
    }
    return map
}

private fun tryMove(map: MutableMap<Point2, Rock>, point: Point2, delta: Point2, bounds: Bounds): Boolean {
    val dest = point + delta
    if (!bounds.contains(dest)) return false
    if (map[dest] != null) return false
    map.remove(point)
    map[dest] = RoundRock
    return true
}

private fun score(map: MutableMap<Point2, Rock>): Int {
    val height = map.maxOf { it.key.y } + 1
    return map.filterValues { it is RoundRock }
        .keys
        .sumOf { height - it.y }
}

private fun moveAll(map: MutableMap<Point2, Rock>, delta: Point2, bounds: Bounds) {
    var cont = true
    while (cont) {
        cont = map.filterValues { it is RoundRock }
            .any { tryMove(map, it.key, delta, bounds) }
    }
}

private fun partOne(): Int {
    val map = parse()
    val bounds = Bounds(map)
    moveAll(map, p(0, -1), bounds)
    return score(map)
}

private fun partTwo(): Int {
    val target = 1_000_000_000L
    var iteration = 0L

    //val firstHash = 2L
    //val firstHashRecurrence = 9L
    val firstHash = 95L
    val firstHashRecurrence = 173

    val deltas = listOf(p(0, -1), p(-1, 0), p(0, 1), p(1, 0))
    val map = parse()
    val bounds = Bounds(map)
    val formerHashes = mutableMapOf<Int, Long>()
    while (iteration < target) {
        moveAll(map, deltas[0], bounds)
        moveAll(map, deltas[1], bounds)
        moveAll(map, deltas[2], bounds)
        moveAll(map, deltas[3], bounds)
        println(iteration)

        /*val hash = map.hashCode()
        if (formerHashes.contains(hash)) {
            println("Found hash! ${formerHashes[hash]}")
        }
        formerHashes[hash] = iteration*/
        if (iteration == firstHash) {
            val diff = firstHashRecurrence - firstHash
            val remaining = target - iteration
            val toSkip = (remaining / diff) * diff
            println("Skipped ${remaining / diff} * $diff = $toSkip")
            iteration += toSkip
        }
        iteration++
    }

    return score(map)
}

/**
 * 5
 * 6
 * 7
 * 8
 * 9
 * Found hash! 2
 * 10
 */

/**
 * 168
 * 169
 * 170
 * 171
 * 172
 * 173
 * Found hash! 95
 * 174
 * Found hash! 96
 * 175
 * Found hash! 97
 * 176
 * Found hash! 98
 * 177
 * Found hash! 99
 * 178
 * Found hash! 100
 */

fun main() {
    println("Part one: ${partOne()}")
    println("Part two: ${partTwo()}")
}