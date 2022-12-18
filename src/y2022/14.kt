package y2022

import y2022.Material.ROCK
import y2022.Material.SAND
import kotlin.math.absoluteValue

private enum class Material {
    ROCK, SAND
}

private fun parse(): MutableMap<Point2, Material> {
    val map = mutableMapOf<Point2, Material>()
    getInput(14).forEach { line ->
        line.split(" -> ").map { point ->
            val (x, y) = point.split(",")
            p(x.toInt(), y.toInt())
        }.windowed(2).forEach { (origin, destination) ->
            val delta = destination - origin
            val unit = delta / delta.sum.absoluteValue
            repeat(delta.sum.absoluteValue + 1) {
                map[origin + (unit * it)] = ROCK
            }
        }
    }
    return map
}

private fun print(map: Map<Point2, Material>) {
    val minX = map.keys.minOf { it.x }
    val maxX = map.keys.maxOf { it.x }
    val rangeX = minX..maxX
    val minY = map.keys.minOf { it.y }
    val maxY = map.keys.maxOf { it.y }
    val rangeY = minY .. maxY
    println(buildString {
        rangeY.forEach { y ->
            rangeX.forEach { x ->
                when (map[p(x, y)]) {
                    ROCK -> append("#")
                    SAND -> append("o")
                    else -> append(" ")
                }
            }
            appendLine()
        }
    })
}

private fun tryAddSand(map: MutableMap<Point2, Material>): Boolean {
    val origin = p(500, 0)
    var point = origin

    fun tryMove(p: Point2): Unit? {
        if(map[p] == null) {
            point = p
            return Unit
        }
        return null
    }

    while (point.y < 170) {
        //println(point)
        val p = tryMove(p(point.x, point.y + 1))
            ?: tryMove(p(point.x - 1, point.y + 1))
            ?: tryMove(p(point.x + 1, point.y + 1))
            ?: point
        if (p is Point2) {
            map[p] = SAND
            if (p == origin) {
                println("Sand reached origin")
                return false
            }
            return true
        }
    }
    println("Reached abyss")
    return false
}

fun main() {
    val map = parse()
    print(map)
    var sand = 0
    while(tryAddSand(map)) {
        println(++sand)
    }
    println("Part one: $sand")

    val maxY = map.filter { it.value == ROCK }.maxOf { it.key.y } + 2
    (0..1000).forEach { map[p(it, maxY)] = ROCK }
    print(map)
    while(tryAddSand(map)) {
        println(++sand)
    }
    print(map)
    println("Part two: ${sand + 1}") // 26830 too low
}