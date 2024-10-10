package y2023

import y2022.Point2
import y2022.getAdjacentNoDiagonals
import y2022.p

private val north = p(0, -1)
private val south = p(0, 1)
private val west = p(-1, 0)
private val east = p(1, 0)
private val directions = listOf(north, east, south, west)

private fun parse(): Map<Point2, Char> {
    val map = mutableMapOf<Point2, Char>()
    getInput(23).forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            map[p(x, y)] = c
        }
    }
    return map
}

private data class TrailGraphNode(
    val distance: Int,
    val continuationPoint: Point2?
) {
    lateinit var children: Set<TrailGraphNode>

    fun findChildren(map: Map<Point2, Char>, finalGoal: Point2) {
        children = if (continuationPoint == null) {
            emptySet()
        } else {
            bfs(map, continuationPoint, distance, finalGoal).toSet()
        }
        children.forEach { it.findChildren(map, finalGoal) }
    }

    fun getNodesRecursive(): Set<TrailGraphNode> {
        return children + children.flatMap { it.getNodesRecursive() }
    }
}

private fun bfs(
    map: Map<Point2, Char>,
    start: Point2,
    distanceTillStart: Int,
    finalGoal: Point2
): MutableList<TrailGraphNode> {
    data class BfsNode(val point: Point2, val dist: Int, val char: Char) {
        fun getNeighbors(): List<BfsNode> {
            return directions.mapNotNull { direction ->
                val newPoint = point + direction
                val c = map[newPoint]
                val legal = when {
                    c == '.' -> true
                    c == '^' && direction == north -> true
                    c == '>' && direction == east -> true
                    c == 'v' && direction == south -> true
                    c == '<' && direction == west -> true
                    else -> false
                }
                if (!legal) return@mapNotNull null
                else BfsNode(newPoint, dist + 1, c!!)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other !is BfsNode) return false
            return point == other.point
        }

        override fun hashCode(): Int {
            return point.hashCode()
        }
    }

    val queue = mutableListOf(BfsNode(start, 0, '.'))
    val added = mutableSetOf(queue.first())
    val graphNodes = mutableListOf<TrailGraphNode>()

    while (queue.isNotEmpty()) {
        val removed = queue.removeFirst()
        if (removed.char == '.') {
            val newNodes = removed.getNeighbors().filter { it !in added }
            added.addAll(newNodes)
            queue.addAll(newNodes)
        }

        when (removed.char) {
            '^' -> graphNodes.add(TrailGraphNode(distanceTillStart + removed.dist + 1, removed.point + north))
            '>' -> graphNodes.add(TrailGraphNode(distanceTillStart + removed.dist + 1, removed.point + east))
            'v' -> graphNodes.add(TrailGraphNode(distanceTillStart + removed.dist + 1, removed.point + south))
            '<' -> graphNodes.add(TrailGraphNode(distanceTillStart + removed.dist + 1, removed.point + west))
        }
        if (removed.point == finalGoal) graphNodes.add(TrailGraphNode(distanceTillStart + removed.dist, null))
    }

    return graphNodes
}

private fun partOne(): Int {
    val map = parse()
    val start = p(1, 0)
    val finalGoal = p(map.maxOf { it.key.x } - 1, map.maxOf { it.key.y })
    val firstNode = bfs(map, start, 0, finalGoal).first()
    firstNode.findChildren(map, finalGoal)
    //println(firstNode.getNodesRecursive().sortedByDescending { it.distance }.filter { it.continuationPoint == null }.map { it.distance })
    return firstNode.getNodesRecursive().filter { it.continuationPoint == null }.maxOf { it.distance }
}

private class PathSegment(points: Pair<Point2, Point2>, val distance: Int) {
    val pointA = points.toList().minBy { it.hashCode() }
    val pointB = points.toList().maxBy { it.hashCode() }
    override fun toString() = "[$pointA <-> $pointB dist=$distance]"

    fun other(one: Point2): Point2 {
        return when (one) {
            pointA -> pointB
            pointB -> pointA
            else -> error("$one not in $this")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathSegment

        if (pointA != other.pointA) return false
        if (pointB != other.pointB) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pointA.hashCode()
        result = 31 * result + pointB.hashCode()
        return result
    }
}

private fun buildMap2(
    stringMap: Map<Point2, Char>,
    start: Point2,
    finalGoal: Point2
): MutableMap<Point2, MutableSet<PathSegment>> {
    val endpoints = stringMap.filter {
        it.value == '.'
    }.filter {
        stringMap.getAdjacentNoDiagonals(it.key).count { (_, c) -> c in setOf('>', 'v') } > 2
    }.keys + setOf(start, finalGoal)

    val segmentMap = mutableMapOf<Point2, MutableSet<PathSegment>>()

    endpoints.flatMap { stringMap.getAdjacentNoDiagonals(it).map { a -> it to a } }
        .filter { (_, adjacent) -> adjacent.second in setOf('>', 'v') }
        .map { (firstEndpoint, adjacent) -> firstEndpoint to adjacent.first }
        .forEach { (firstEndpoint, adjacent) ->
            var distance = 1
            val visited = mutableSetOf(firstEndpoint)
            var lastPosition = adjacent
            while (true) {
                val nextPosition = stringMap.getAdjacentNoDiagonals(lastPosition)
                    .filter { it.second != '#' }
                    .single { it.first !in visited }
                    .first
                visited.add(lastPosition)
                lastPosition = nextPosition
                distance++
                if (nextPosition in endpoints) {
                    val segment = PathSegment(firstEndpoint to nextPosition, distance)
                    segmentMap.getOrPut(firstEndpoint) { mutableSetOf() }.add(segment)
                    segmentMap.getOrPut(nextPosition) { mutableSetOf() }.add(segment)
                    break
                }
            }
        }

    return segmentMap
}

private fun bfs2(map: Map<Point2, MutableSet<PathSegment>>, origin: Point2, finalGoal: Point2): Int {
    data class Bfs2Node(val location: Point2, val dist: Int, val visitedCrossroads: List<Point2>) {
        fun getNeighbors(): List<Bfs2Node> = map[location]!!
            .filter { it.other(location) !in visitedCrossroads }
            .map { Bfs2Node(it.other(location), dist + it.distance, visitedCrossroads + location) }
    }

    val segments = mutableListOf<PathSegment>()
    val queue = mutableListOf(Bfs2Node(origin, 0, emptyList()))
    var longestNode = queue.single()
    var i = 0

    while (queue.isNotEmpty()) {
        i++
        val removed = queue.removeFirst()
        if (i % 100000 == 0) println("${queue.size}: ${removed.dist}")
        if (removed.location == finalGoal && removed.dist > longestNode.dist) {
            longestNode = removed
            println("$i: Longest dist: " + longestNode.dist)
        }
        queue.addAll(removed.getNeighbors())
    }

    return longestNode.dist
}

private fun partTwo(): Int {
    val stringMap = parse()
    val start = p(1, 0)
    val finalGoal = p(stringMap.maxOf { it.key.x } - 1, stringMap.maxOf { it.key.y })
    val segmentMap = buildMap2(stringMap, start, finalGoal)
    return bfs2(segmentMap, start, finalGoal)
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}