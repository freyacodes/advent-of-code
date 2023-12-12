package y2023

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class MapNode(val name: String, val left: String, val right: String)

private fun parse(): Pair<Sequence<Char>, Map<String, MapNode>> {
    val lines = getInput(8)
    val directions = lines.first()
    val regex = "\\w\\w\\w".toRegex()
    val map = lines.drop(2).map {
        val found = regex.findAll(it).iterator()
        MapNode(found.next().value, found.next().value, found.next().value)
    }.associateBy { it.name }

    var index = 0
    return sequence {
        while (true) {
            yield(directions[index++ % directions.length])
        }
    } to map
}

private fun partOne(): Int {
    val (directionsSeq, map) = parse()
    val directions = directionsSeq.iterator()

    var currentNode = map["AAA"]!!
    var steps = 0
    while (currentNode.name != "ZZZ") {
        val direction = directions.next()
        val nextName = if (direction == 'L') currentNode.left else currentNode.right
        currentNode = map[nextName]!!
        steps++
    }

    return steps
}

private fun partTwo(): Long {
    val (directionsSeq, map) = parse()

    val stepCounts = map.values.filter { it.name.endsWith('A') }.map { initial ->
        var current = initial
        var steps = 0L
        val directions = directionsSeq.iterator()
        while (!current.name.endsWith('Z')) {
            val direction = directions.next()
            val nextName = if (direction == 'L') current.left else current.right
            current = map[nextName]!!
            steps++
        }
        steps
    }

    println("Step counts: $stepCounts")

    return lcf(stepCounts)
}

fun gcd(a: Long, b: Long): Long {
    if (a == b) return a
    val min = min(a, b)
    val max = max(a, b)
    if (min == 0L) return max

    return gcd(min, max % min)
}

fun lcf(numbers: List<Long>) = numbers.reduce { a, b ->
    a.absoluteValue * (b.absoluteValue / gcd(a, b))
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}