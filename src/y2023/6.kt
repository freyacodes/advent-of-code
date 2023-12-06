package y2023


private fun partOne(): Int {
    val (timeList, distanceList) = getInput(6).map { l ->
        l.drop(9).split(Regex("\\s")).mapNotNull { it.toIntOrNull() }
    }

    return timeList.zip(distanceList).map { (time, distance) ->
        var wins = 0
        repeat(time) { wait ->
            if (wait * (time - wait) > distance) wins++
        }
        wins
    }.reduce { acc, i -> acc*i }
}

private fun partTwo(): Long {
    val (timeLimit, distance) = getInput(6).map { l ->
        l.drop(9).replace(" ", "").toLong()
    }

    var marginLow = 0
    var marginHigh = 0
    for (wait in 1..timeLimit) {
        if (wait * (timeLimit - wait) <= distance) marginLow++
        else break
    }
    for (wait in timeLimit downTo 1) {
        if (wait * (timeLimit - wait) <= distance) marginHigh++
        else break
    }

    return timeLimit - marginHigh - marginLow
}

fun main() {
    println("Part one " + partOne())
    println("Part two " + partTwo())
}