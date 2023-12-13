package y2023

import y2022.Point2
import y2022.p

private val north = p(0, -1)
private val south = p(0, 1)
private val west = p(-1, 0)
private val east = p(1, 0)

private class MapNode(val point: Point2, val character: Char) {
    val corners = (0..3).map { PipeCorner(this, it) }

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

    val neighborSets: Pair<List<Int>, List<Int>> = when(character) {
        '|' -> listOf(0, 2) to listOf(1, 3)
        '-' -> listOf(0, 1) to listOf(2, 3)
        'L' -> listOf(0, 2, 3) to listOf(1)
        'J' -> listOf(0) to listOf(1, 2, 3)
        '7' -> listOf(0, 1, 3) to listOf(2)
        'F' -> listOf(0, 1, 2) to listOf(3)
        '.' -> listOf(0, 1, 2, 3) to listOf()
        'S' -> emptyList<Int>() to emptyList()
        else -> error("Panic!")
    }

    val neighborPoints = deltas.map { it + point }

    override fun toString(): String {
        return character + point.toString()
    }
}

private data class PipeCorner(val node: MapNode, val index: Int) {
    val neighborCardinals = when(index) {
        0 -> listOf(west to 1, north to 2)
        1 -> listOf(north to 3, east to 0)
        2 -> listOf(west to 3, south to 0)
        3 -> listOf(south to 1, east to 2)
        else -> error("Panic!")
    }

    fun getConnecting(map: Map<Point2, MapNode>): List<PipeCorner> {
        val (left, right) = node.neighborSets

        val localNeighbors = (if (left.contains(index)) left else right)
            .toMutableList()
            .apply { remove(index) }
            .map { node.corners[it] }

        val remoteNeighbors = neighborCardinals
            .mapNotNull { (offset, index) -> map[node.point + offset]?.corners?.get(index) }

        return localNeighbors + remoteNeighbors
    }
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

private fun partTwo(): Int {
    val map = parse()
    val visited = mutableSetOf<PipeCorner>()

    val floods = map.values.filter { it.character == '.' }
        .mapNotNull { initialPipe ->
            if (visited.contains(initialPipe.corners.first())) return@mapNotNull null
            val fringe = mutableListOf<PipeCorner>()
            val found = mutableSetOf<PipeCorner>()
            fringe.add(initialPipe.corners.first())

            while (fringe.isNotEmpty()) {
                val corner = fringe.removeFirst()
                found.add(corner)
                visited.add(corner)
                corner.getConnecting(map)
                    .filter { !visited.contains(it) && !fringe.contains(it) }
                    .let { fringe.addAll(it) }
            }

            return@mapNotNull found
        }

    return floods.map { list ->
        list.map { it.node }
            .toSet()
            .count { list.containsAll(it.corners) }
    }.sortedDescending()[1]
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}