package y2022

import kotlin.math.absoluteValue

private fun parse(): List<Pair<Point2, Int>> = getInput(9).map {
    val (op, distance) = it.split(" ")
    when (op) {
        "U" -> p(0, -1)
        "D" -> p(0, 1)
        "L" -> p(-1, 0)
        "R" -> p(1, 0)
        else -> error("Parse error $it")
    } to distance.toInt()
}

fun simulate(head: Point2, tail: Point2): Point2 {
    val delta = tail - head
    return when {
        delta.x.absoluteValue < 2 && delta.y.absoluteValue < 2 -> p(0, 0)
        delta == p(0, 2) -> p(0, -1)
        delta == p(0, -2) -> p(0, 1)
        delta == p(2, 0) -> p(-1, 0)
        delta == p(-2, 0) -> p(1, 0)
        delta.x > 0 && delta.y > 0 -> p(-1, -1)
        delta.x < 0 && delta.y > 0 -> p(1, -1)
        delta.x > 0 && delta.y < 0 -> p(-1, 1)
        delta.x < 0 && delta.y < 0 -> p(1, 1)
        else -> p(0, 0)
    } + tail
}

private fun partOne() {
    var tail = p(0, 0)
    var head = p(0, 0)
    val tailVisits = mutableSetOf<Point2>()
    tailVisits.add(tail)

    parse().forEach { (shift, count) ->
        repeat(count) {
            head += shift
            tail = simulate(head, tail)
            tailVisits.add(tail)
        }
    }

    println("Part one: ${tailVisits.size}")
}

private fun partTwo() {
    val knots = (0..9).map { p(0,0) }.toMutableList()
    val tailVisits = mutableSetOf<Point2>()
    tailVisits.add(knots.last())
    parse().forEach { (shift, count) ->
        repeat(count) {
            knots[0] = knots[0] + shift
            repeat(9) { i ->
                knots[i+1] = simulate(knots[i], knots[i+1])
            }
            tailVisits.add(knots.last())
        }
    }

    println("Part two: ${tailVisits.size}")
}

fun main() {
    partOne()
    partTwo()
}