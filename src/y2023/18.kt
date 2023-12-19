package y2023

import y2022.Point2L
import y2022.p

private val up = p(0L, -1L)
private val down = p(0L, 1L)
private val left = p(-1L, 0L)
private val right = p(1L, 0L)
private val directions = listOf(right, down, left, up)

private data class DigInput(val delta: Point2L, val length: Long)

private fun parse(partTwo: Boolean): List<DigInput> {
    val regex = "(\\w) (\\d+) \\(#(\\w+)".toRegex()
    return getInput(18).map {
        val (_, d, l, hex) = regex.find(it)!!.groupValues
        val delta: Point2L
        val length: Long

        if (!partTwo) {
            length = l.toLong()
            delta = when(d) {
                "U" -> up
                "D" -> down
                "L" -> left
                "R" -> right
                else -> error("Panic!")
            }
        } else {
            length = hex.take(5).toLong(16)
            delta = directions[hex.last().digitToInt()]
        }

        DigInput(delta, length)
    }
}

private fun resolve(partTwo: Boolean = false): Long {
    val shoelacePoints = mutableListOf(p(0L, 0L))
    var boundaryPoints = 1L
    var position = p(0L, 0L)
    parse(partTwo).forEach { digInput ->
        position += digInput.delta * digInput.length
        shoelacePoints.add(position)
        boundaryPoints += digInput.length
    }

    shoelacePoints.add(shoelacePoints.first())
    println(shoelacePoints.zipWithNext().size)
    val shoelaceSum = shoelacePoints.zipWithNext().sumOf { (p1, p2) ->
        p1.x * p2.y - p2.x * p1.y
    }
    return shoelaceSum/2 + boundaryPoints/2 + 1
}

fun main() {
    println("Part one: " + resolve())
    println("Part two: " + resolve(partTwo = true))
}