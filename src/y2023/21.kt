package y2023

import y2022.Point2
import y2022.p

private val north = p(0, -1)
private val south = p(0, 1)
private val west = p(-1, 0)
private val east = p(1, 0)
private val directions = listOf(north, east, south, west)

private fun parse(): MutableMap<Point2, Char> {
    val map = mutableMapOf<Point2, Char>()
    getInput(21).forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            map[p(x, y)] = c
        }
    }
    return map
}

private class GardenNode(val map: Map<Point2, Char>, val point2: Point2) {
    fun getNeighbors(): List<GardenNode> {
        return directions.map { GardenNode(map, point2 + it) }.filter {
            val v = map[it.point2]
            v == '.' || v == 'S'
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GardenNode

        return point2 == other.point2
    }

    override fun hashCode() = point2.hashCode()
}

private fun solveSlow(map: Map<Point2, Char>, steps: Int, start: Point2): Long {
    var lastVisited = setOf(GardenNode(map, start))
    repeat(steps) {
        lastVisited = lastVisited.flatMap { it.getNeighbors() }.toSet()
    }
    return lastVisited.size.toLong()
}

private fun partOne(): Long {
    val map = parse()
    val start = map.entries.single { it.value == 'S' }.key
    return solveSlow(map, 64, start)
}

private fun partTwo(stepsRequired: Long = 26501365): Long {
    val map = parse()
    val start = map.entries.single { it.value == 'S' }.key

    val sideLength = 131
    val halfLength = sideLength / 2
    val mapDistanceFromCenter = (stepsRequired - halfLength + 1) / sideLength
    val fullyCoveredMaps = diamondArea(mapDistanceFromCenter.toInt())
    val partialMapsPerDiagonal = (diamondArea(mapDistanceFromCenter.toInt() + 1) - fullyCoveredMaps) / 4

    val coveredSector = solveSlow(map, sideLength * 2 + 1, p(halfLength, halfLength))

    val northSector = solveSlow(map, halfLength, p(halfLength, 0))
    val eastSector = solveSlow(map, halfLength, p(sideLength - 1, halfLength))
    val southSector = solveSlow(map, halfLength, p(halfLength, sideLength - 1))
    val westSector = solveSlow(map, halfLength, p(0, halfLength))

    val diagonalCoverage = listOf(
        solveSlow(map, sideLength - 1, p(0, 0)),
        solveSlow(map, sideLength - 1, p(sideLength - 1, 0)),
        solveSlow(map, sideLength - 1, p(0, sideLength - 1)),
        solveSlow(map, sideLength - 1, p(sideLength - 1, sideLength - 1))
    ).sum() * partialMapsPerDiagonal

    // 34995 Verification

    return northSector + eastSector + southSector + westSector + diagonalCoverage + coveredSector * fullyCoveredMaps
}

fun verificationTest(): Long {
    val delta = p(131, 131)
    val offsets = listOf(
        p(-1, -1), p(0, -1), p(1, -1),
        p(-1, 0), p(1, 0),
        p(-1, 1), p(0, 1), p(1, 1)
    )
    val input = parse()
    val map = offsets.map { offset ->
        input.mapKeys { it.key + (offset * 131) }
    }.reduce { acc, map -> acc + map } + input

    return solveSlow(map, 131 + 64, p(65, 65))
}

fun diamondArea(length: Int): Long {
    if (length == 1) return 1
    var value = 1L
    for (n in 2..length) {
        value += n * 2 + (n-1) * 2 - 2
    }
    return value
}

fun main() {
    println(parse().filter { it.value == '.' }.count()/2)
    println(parse().filter { it.value == '.' }.count()/2 * (1 + 4 / 2 + 1))
    println("Part one: " + partOne())
    println("Part two: " + partTwo())//131 + 131/2)) // 37196050414 too low, not 638107177748370
    println("Verification: " + verificationTest())
}
