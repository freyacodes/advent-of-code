package y2023

import y2022.Point2
import y2022.p

private val north = p(0, -1)
private val south = p(0, 1)
private val west = p(-1, 0)
private val east = p(1, 0)

private class MapNode(val point: Point2, val character: Char) {
    val deltas: List<Point2> = when (character) {
        '|' -> listOf(north, south)
        '-' -> listOf(east, west)
        'L' -> listOf(north, east)
        'J' -> listOf(north, west)
        '7' -> listOf(west, south)
        'F' -> listOf(east, south)
        '.' -> listOf()
        'S' -> listOf(north, east, south, west)
        else -> error("Panic!")
    }

    val neighborPoints = deltas.map { it + point }
}

private fun parse(): Map<Point2, MapNode> {
    return getInputString(10).lines().flatMapIndexed { y, line ->
        line.mapIndexed { x, c ->
            MapNode(p(x, y), c)
        }
    }.associateBy { it.point }
}

private fun partOne(): Int {
    val map = parse()
    val start = map.values.single { it.character == 'S' }

    var queue = start.neighborPoints.filter { neighbor ->
        map[neighbor]?.neighborPoints?.contains(start.point) == true
    }.map { map[it]!! }.toSet()
    val visited = queue.map { it.point }.toMutableSet()
    var steps = 0

    while (queue.isNotEmpty()) {
        val newQueue = mutableSetOf<MapNode>()
        queue.flatMap { origin ->
            origin.neighborPoints.mapNotNull { map[it] }
                .filter { it.neighborPoints.contains(origin.point) }
        }.filter { !visited.contains(it.point) }
            .forEach { new ->
                visited.add(new.point)
                newQueue.add(new)
            }
        queue = newQueue
        steps++
    }

    return steps
}

fun main() {
    println("Part one: " + partOne())
}