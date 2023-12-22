package y2023

import y2022.Point2
import y2022.p

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
            v == '.' ||  v == 'S'
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

private fun partOne(): Int {
    val map = parse()
    var lastVisited = setOf(GardenNode(map, map.entries.single { it.value == 'S' }.key))

    repeat(64) {
        lastVisited = lastVisited.flatMap { it.getNeighbors() }.toSet()
    }

    return lastVisited.size
}

fun main() {
    println("Part one: " + partOne())
}