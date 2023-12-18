package y2023

import y2022.Point2L
import y2022.p

private data class Universe(val galaxies: List<Galaxy>, val horizontal: List<String>, val vertical: List<String>)
private data class Galaxy(val point: Point2L) {
    var offsetX = 0L
    var offsetY = 0L

    fun finalPos() = p(point.x + offsetX, point.y + offsetY)
}

private fun parse(): Universe {
    val list = mutableListOf<Galaxy>()
    val horizontal = getInput(11)
    horizontal.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c == '#') list.add(Galaxy(p(x.toLong(), y.toLong())))
        }
    }

    val vertical = mutableListOf<String>()
    repeat(horizontal.size) { i ->
        vertical.add(buildString {
            repeat(horizontal.size) { j ->
                append(horizontal[j][i])
            }
        })
    }

    return Universe(list, horizontal, vertical)
}

private fun resolve(factor: Long = 1L): Long {
    val (galaxies, horizontal, vertical) = parse()

    horizontal.forEachIndexed { y, line ->
        if (line.contains('#')) return@forEachIndexed
        galaxies.forEach {
            if (y < it.point.y) it.offsetY += factor
        }
    }

    vertical.forEachIndexed { x, line ->
        if (line.contains('#')) return@forEachIndexed
        galaxies.forEach {
            if (x < it.point.x) it.offsetX += factor
        }
    }

    val finalPositions = galaxies.map { it.finalPos() }
    var sum = 0L
    finalPositions.forEach { point ->
        finalPositions.forEach { other ->
            sum += (point - other).abs.sum
        }
    }

    println(finalPositions)
    return sum / 2
}

fun main() {
    println("Part one: " + resolve())
    println("Part Two: " + resolve(factor = 999999L))
}