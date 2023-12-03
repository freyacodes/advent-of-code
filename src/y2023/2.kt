package y2023

import kotlin.math.max

private fun partOne(): Int {
    return getInput(2).mapIndexed { game, s ->
        game.inc() to s.dropWhile { !it.isDigit() }.drop(1).dropWhile { !it.isDigit() }
    }.sumOf { (game, string) ->
        val allMatch = string.split(";").all { substring ->
            var red = 0
            var green = 0
            var blue = 0

            val regex = "(\\d+) (\\w+)".toRegex()
            regex.findAll(substring).forEach {
                val count = it.groups[1]!!.value.toInt()
                val color = it.groups[2]!!.value
                when (color) {
                    "red" -> red += count
                    "green" -> green += count
                    "blue" -> blue += count
                    else -> error(string)
                }
            }

            red <= 12 && green <= 13 && blue <= 14
        }

        if (allMatch) game else 0
    }
}

private fun partTwo(): Int {
    return getInput(2).map { s ->
        s.dropWhile { !it.isDigit() }.drop(1).dropWhile { !it.isDigit() }
    }.sumOf { string ->
        var minRed = 0
        var minGreen = 0
        var minBlue = 0

        string.split(";").forEach { substring ->
            var red = 0
            var green = 0
            var blue = 0

            val regex = "(\\d+) (\\w+)".toRegex()
            regex.findAll(substring).forEach {
                val count = it.groups[1]!!.value.toInt()
                val color = it.groups[2]!!.value
                when (color) {
                    "red" -> red += count
                    "green" -> green += count
                    "blue" -> blue += count
                    else -> error(string)
                }
            }

            minRed = max(minRed, red)
            minGreen = max(minGreen, green)
            minBlue = max(minBlue, blue)
        }

        minRed * minGreen * minBlue
    }
}

fun main() {
    println("Part one: ${partOne()}")
    println("Part two: ${partTwo()}")
}