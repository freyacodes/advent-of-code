package y2023

import y2022.Point2
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

private fun bfs(map: Map<Point2, Char>, start: Point2, distanceTillStart: Int, finalGoal: Point2): MutableList<TrailGraphNode> {
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

private data class PathSegment(val from: Point2, val to: Point2, val distance: Int)

private fun bfs2(map: Map<Point2, Char>, pointsOfInterest: Set<Point2>, origin: Point2) {
    data class Bfs2Node(val point: Point2, val char: Char, val dist: Int) {
        fun getNeighbors() {
            directions.mapNotNull { direction ->
                val newPoint = point + direction
                val char = map[newPoint]
                if (char != null && char != '#') Bfs2Node(newPoint, char, dist + 1)
                else null
            }
        }
    }

    val segments = mutableListOf<PathSegment>()
    val queue = mutableListOf(Bfs2Node(origin, '.', 0))
    val added = mutableSetOf(queue.first())

    while (queue.isNotEmpty()) {
        val removed = queue.removeFirst()
    }
}

private fun partTwo() {
    val map = parse()
    val start = p(1, 0)
    val finalGoal = p(map.maxOf { it.key.x } - 1, map.maxOf { it.key.y })
    val pointsOfInterest = map.filter { it.value !in listOf('.', '#') }.keys + setOf(start, finalGoal)


}

fun main() {
    println("Part one: " + partOne())
}