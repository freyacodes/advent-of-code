package y2020

import y2020.Direction.*

private data class Point(val x: Float, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    val neighbors by lazy { Direction.values().map { this + it.translation } }
}

private enum class Direction(val translation: Point) {
    NE(Point(0.5f, 1)),
    E(Point(1f, 0)),
    SE(Point(0.5f, -1)),
    SW(Point(-0.5f, -1)),
    W(Point(-1f, 0)),
    NW(Point(-0.5f, 1));
}

private class Tile(val point: Point, var black: Boolean = false) {
    fun flip() {
        black = !black
    }

    operator fun plus(direction: Direction) = point + direction.translation
}

private fun parse(line: String): List<Direction> {
    val instructions = mutableListOf<Direction>()
    val remaining = line.toMutableList()
    while (remaining.isNotEmpty()) {
        val a = remaining.removeFirst()
        val b = remaining.getOrNull(0)
        val dir = when {
            a == 'n' && b == 'e' -> NE
            a == 's' && b == 'e' -> SE
            a == 's' && b == 'w' -> SW
            a == 'n' && b == 'w' -> NW
            a == 'e' -> E
            a == 'w' -> W
            else -> error("Error parsing")
        }
        if (dir != E && dir != W) remaining.removeFirst()
        instructions.add(dir)
    }
    return instructions
}

private fun dayZero(): Map<Point, Tile> {
    val tiles = mutableMapOf<Point, Tile>()
    val reference = Tile(Point(0f, 0))
    tiles[reference.point] = reference

    getInput(24).map { line ->
        var currentTile = reference
        parse(line).forEach { direction ->
            val newPoint = currentTile + direction
            currentTile = tiles.getOrPut(newPoint) { Tile(newPoint) }
        }
        currentTile.flip()
    }

    return tiles
}

private fun iterate(lastState: Map<Point, Tile>): Map<Point, Tile> {
    val blackTiles = lastState.values.filter { it.black }
    val neighborsToBlacks = blackTiles.flatMap { it.point.neighbors }.distinct()
    val newState = mutableMapOf<Point, Tile>()

    blackTiles.filter {
        it.point.neighbors.count { p -> lastState[p]?.black == true } in 1..2
    }.forEach { newState[it.point] = it }

    neighborsToBlacks.filter { p -> lastState[p]?.black != true }
        .filter {
            it.neighbors.count { p -> lastState[p]?.black == true } == 2
        }.forEach { newState[it] = Tile(it, true) }

    return newState
}

fun main() {
    var state = dayZero()
    println("Day 0: " + state.count { it.value.black })

    repeat(100) { i ->
        state = iterate(state)
        val day = i + 1
        if ((i + 1) % 10 == 0) println("Day $day: ${state.size}")
    }
}