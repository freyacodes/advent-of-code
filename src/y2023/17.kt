package y2023

import y2022.Point2
import y2022.p
import java.util.*

private val north = p(0, -1)
private val south = p(0, 1)
private val west = p(-1, 0)
private val east = p(1, 0)
private val directions = listOf(north, east, south, west)

private fun parse(): Map<Point2, Int> {
    return getInput(17).flatMapIndexed { y, line ->
        line.mapIndexed { x, c ->
            p(x, y) to c.digitToInt()
        }
    }.toMap()
}

private data class CrucibleNode(
    val map: Map<Point2, Int>,
    val position: Point2,
    val direction: Int,
    val heatLoss: Int,
    val straightMoves: Int,
    val partTwo: Boolean
) : Comparable<CrucibleNode> {
    fun getNextNodes(): List<CrucibleNode> {
        return (-1..1).mapNotNull { i ->
            if (!partTwo && i == 0 && straightMoves == 3) return@mapNotNull null
            if (partTwo && i == 0 && straightMoves == 10) return@mapNotNull null
            if (partTwo && i != 0 && straightMoves < 4) return@mapNotNull null

            var newDirection = (direction + i) % 4
            if (newDirection < 0) newDirection = 3
            val delta = directions[newDirection]
            val newPosition = position + delta
            val heatLossFromMap = map[newPosition] ?: return@mapNotNull null
            CrucibleNode(
                map,
                newPosition,
                newDirection,
                heatLoss + heatLossFromMap,
                if (i == 0) straightMoves + 1 else 1,
                partTwo
            )
        }
    }

    override fun compareTo(other: CrucibleNode): Int {
        return heatLoss - other.heatLoss
    }

    override fun toString(): String {
        return "$position $heatLoss"
    }

    fun getState() = CrucibleState(position, direction, straightMoves)
}

private data class CrucibleState(val position: Point2, val direction: Int, val straightMoves: Int)

private fun resolve(partTwo: Boolean): Int {
    val map = parse()
    val maxX = map.maxOf { it.key.x }
    val maxY = map.maxOf { it.key.y }
    val goal = p(maxX, maxY)
    val remaining = PriorityQueue<CrucibleNode>()
    val visited = mutableSetOf<CrucibleState>()
    remaining.add(CrucibleNode(map, p(1,0), 1, map[p(1,0)]!!, 1, partTwo))
    remaining.add(CrucibleNode(map, p(0,1), 2, map[p(0,1)]!!, 1, partTwo))

    while (remaining.isNotEmpty()) {
        val node = remaining.remove()
        if (visited.contains(node.getState())) continue
        if (node.position == goal) {
            if (!partTwo || node.straightMoves >= 4) return node.heatLoss
        }
        val neighbors = node.getNextNodes()
        visited.add(node.getState())
        remaining.addAll(neighbors)
    }

    error("Panic!")
}

fun main() {
    println("Part one " + resolve(partTwo = false))
    println("Part two " + resolve(partTwo = true))
}