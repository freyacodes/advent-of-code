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
    val remainingStraightMoves: Int
) : Comparable<CrucibleNode> {
    fun getNextNodes(): List<CrucibleNode> {
        return (-1..1).mapNotNull { i ->
            if (i == 0 && remainingStraightMoves == 0) return@mapNotNull null
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
                if (i == 0) remainingStraightMoves.dec() else 2
            )
        }
    }

    override fun compareTo(other: CrucibleNode): Int {
        return heatLoss - other.heatLoss
    }

    override fun toString(): String {
        return "$position $heatLoss"
    }

    fun getState() = CrucibleState(position, direction, remainingStraightMoves)
}

private data class CrucibleState(val position: Point2, val direction: Int, val remainingStraightMoves: Int)

private fun partOne(): Int {
    val map = parse()
    val maxX = map.maxOf { it.key.x }
    val maxY = map.maxOf { it.key.y }
    val goal = p(maxX, maxY)
    val remaining = PriorityQueue<CrucibleNode>()
    val visited = mutableSetOf<CrucibleState>()
    remaining.add(CrucibleNode(map, p(1,0), 1, map[p(1,0)]!!, 2))
    remaining.add(CrucibleNode(map, p(0,1), 2, map[p(0,1)]!!, 2))

    while (remaining.isNotEmpty()) {
        val node = remaining.remove()
        if (visited.contains(node.getState())) continue
        if (node.position == goal) return node.heatLoss
        val neighbors = node.getNextNodes()
        visited.add(node.getState())
        remaining.addAll(neighbors)
    }

    error("Panic!")
}

fun main() {
    println("Part one " + partOne()) // 644 too high
}