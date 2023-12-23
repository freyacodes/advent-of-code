package y2023

import y2022.Point2
import y2022.p
import java.lang.StrictMath.pow

private val north = p(0, -1)
private val south = p(0, 1)
private val west = p(-1, 0)
private val east = p(1, 0)
private val directions = listOf(north, east, south, west)

private fun parse(): MutableMap<Point2, Char> {
    val map = mutableMapOf<Point2, Char>()
    getInput(21).forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            map[p(x, y)] = c
        }
    }
    return map
}

private class GardenNode(val map: Map<Point2, Char>, val point2: Point2) {
    fun getNeighbors(): List<GardenNode> {
        return directions.map { GardenNode(map, point2 + it) }.filter {
            val v = map[it.point2]
            v == '.' || v == 'S'
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GardenNode

        return point2 == other.point2
    }

    override fun hashCode() = point2.hashCode()
}

private fun solveSlow(map: Map<Point2, Char>, steps: Int, start: Point2 = p(65, 65)): Long {
    var lastVisited = setOf(GardenNode(map, start))
    repeat(steps) {
        lastVisited = lastVisited.flatMap { it.getNeighbors() }.toSet()
    }
    return lastVisited.size.toLong()
}

private fun partOne(): Long {
    val map = parse()
    val start = map.entries.single { it.value == 'S' }.key
    return solveSlow(map, 64, start)
}

private fun generateLargeMap(): Map<Point2, Char> {
    val input = parse()
    val map = mutableMapOf<Point2, Char>()
    for (x in -5..5) {
        for (y in -5..5) {
            map.plusAssign(input.mapKeys { it.key + (p(x, y) * 131) })
        }
    }

    return map
}

private fun partTwo(stepsRequired: Long = 26501365): Long {
    val map = generateLargeMap()

    val y0 = solveSlow(map, 65)
    println(y0)
    val y1 = solveSlow(map, 65 + 131)
    println(y1)
    val y2 = solveSlow(map, 65 + 131*2)
    println(y2)

    // G(x) = ax² + bx + c
    // (a * 0²) + (b * 0) + c = y0
    val c = y0

    // G(1) = (a * 1²) + (b * 1) + c
    // a + b + y0 = y1
    // b = y1 - y0 - a

    // G(2) = (2a)² + 2b + y0 = y2
    // 4a + 2b = y2 - y0
    // 4a + 2(y1 - y0 - a) = y2 - y0
    // 2a + 2y1 - 2y0 = y2 - y0
    // 2a + 2y1 - y0 = y2
    // 2a = y2 + y0 - 2y1
    // a = (y2 + y0 - 2 * y1) / 2
    val a = ((y2 + y0 - (2 * y1)) / 2).toLong()
    val b = (y1 - y0 - a).toLong()

    val g = 639051580070841

    val x = (stepsRequired - 65) / 131L
    return a * pow(x.toDouble(), 2.0).toLong() + b * x + c
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}
