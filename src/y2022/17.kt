package y2022

import java.util.SortedSet
import java.util.TreeSet

private enum class Rock(private vararg val tiles: Point2) {
    HORIZONTAL(
        p(2, 0),
        p(3, 0),
        p(4, 0),
        p(5, 0)
    ),
    PLUS(
        p(3, 2),

        p(2, 1),
        p(3, 1),
        p(4, 1),

        p(3, 0)
    ),
    L(
        p(2, 0),
        p(3, 0),
        p(4, 0),

        p(4, 1),
        p(4, 2)
    ),
    VERTICAL(
        p(2, 0),
        p(2, 1),
        p(2, 2),
        p(2, 3),
    ),
    SQUARE(
        p(2, 0),
        p(3, 0),
        p(2, 1),
        p(3, 1),
    );

    fun from(highestPoint: Int): List<Point2> {
        return tiles.map { it + p(0, highestPoint + 4) }
    }
}

private fun print(chamber: Set<Point2>) {
    val highestPoint = chamber.maxOf { it.y }
    val str = buildString {
        (highestPoint downTo 0).forEach { y ->
            append("|")
            repeat(7) { x ->
                if (chamber.contains(p(x, y))) append("#")
                else append(".")
            }
            append("|\n")
        }
        appendLine("+-------+")
    }
    println(str)
    println()
}

private data class State(
    val shiftNum: Int,
    val rockNum: Int,
    val topRow: SortedSet<Int>,
    val lowerRow: SortedSet<Int>,
    val rockTotal: Int,
    val height: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (shiftNum != other.shiftNum) return false
        if (rockNum != other.rockNum) return false
        if (topRow != other.topRow) return false
        //if (lowerRow != other.lowerRow) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shiftNum
        result = 31 * result + rockNum
        result = 31 * result + topRow.hashCode()
        //result = 31 * result + lowerRow.hashCode()
        return result
    }
}

private fun run(chamber: MutableSet<Point2> = mutableSetOf(), startingShiftCount: Int = 0, startingRock: Int = 0, iterations: Int = 2022, partTwo: Boolean = false): Long {
    val jets = getInputString(17).toList()
    var highestPoint = chamber.maxOfOrNull { it.y } ?: -1
    var shiftCount = startingShiftCount
    val xRange = 0..6
    val states = mutableListOf<State>()
    val duplicateStates = mutableMapOf<State, MutableList<State>>()

    println((startingRock until startingRock + iterations).count())
    (startingRock until startingRock + iterations).forEach { rockNum ->
    //repeat(iterations) { rockNum ->
        var rock = Rock.values()[rockNum % 5].from(highestPoint)
        while (true) {
            val jetDelta = if (jets[shiftCount++ % jets.size] == '>') p(1, 0) else p(-1, 0)
            val shiftedRock = rock.map { it + jetDelta }
            shiftedRock.takeIf {
                it.none { p ->
                    chamber.contains(p) || p.x !in xRange
                }
            }?.let { rock = it }

            val fallingRock = rock.map { it + p(0, -1) }
            if (fallingRock.none() { chamber.contains(it) || it.y < 0 }) {
                rock = fallingRock
            } else {
                chamber.addAll(rock)
                break
            }
        }
        highestPoint = chamber.maxOf { it.y }
        val state = State(
            shiftCount % jets.size,
            rockNum % Rock.values().size,
            TreeSet<Int>().apply { addAll(chamber.filter { it.y == highestPoint }.map { it.x }) },
            TreeSet<Int>().apply { addAll(chamber.filter { it.y == highestPoint - 1 }.map { it.x }) },
            rockNum,
            highestPoint
        )
        if (!partTwo || state.lowerRow.size != 7) return@forEach

        if (states.contains(state)) {
            val list = duplicateStates.compute(state) { _, v ->
                v?.apply { add(state) } ?: mutableListOf(states.first() { it == state }, state)
            }!!
            if (list.size != 3) return@forEach
            val secondState = list[1]
            println(secondState)
            println(state)

            val rockDiff = rockNum - secondState.rockTotal
            val skipFactor = (1000000000000 - rockNum) / rockDiff
            val skippedHeight = skipFactor * (highestPoint - secondState.height)
            val remainingIterations = 1000000000000 - rockNum - skipFactor * rockDiff
            val lastStretch = run(chamber, shiftCount, rockNum, remainingIterations.toInt())
            return skippedHeight + lastStretch
        } else states.add(state)
    }

    return highestPoint.toLong()
}

fun main() {
    println("Part one: ${run() + 1}")
    println("Part two: ${run(partTwo = true, iterations = 5000) + 2}")
}