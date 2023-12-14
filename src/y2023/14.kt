package y2023

import y2022.Point2
import y2022.p

private sealed interface Rock
object RoundRock : Rock
object CubeRock : Rock

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

private fun tryMoveUp(map: MutableMap<Point2, Rock>, point: Point2): Boolean {
    if (point.y == 0) return false
    val above = point - p(0, 1)
    if (map[above] != null) return false
    map.remove(point)
    map[above] = RoundRock
    return true
}

private fun partOne(): Int {
    val map = parse()
    var cont = true
    while (cont) {
        cont = map.filterValues { it is RoundRock }
            .any { tryMoveUp(map, it.key) }
    }

    val height = map.maxOf { it.key.y } + 1
    return map.filterValues { it is RoundRock }
        .keys
        .sumOf { height - it.y }
}

fun main() {
    println("Part one: ${partOne()}")
}