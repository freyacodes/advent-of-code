package y2022

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

private data class InputSet(val sensor: Point2, val beacon: Point2)

val pattern = "[^\\d-]+(-?\\d+)[^\\d-]+(-?\\d+)[^\\d-]+(-?\\d+)[^\\d-]+(-?\\d+)".toPattern()

private fun parse(): List<InputSet> {
    return getInput(15).map { line ->
        val m = pattern.matcher(line)
        m.find()
        InputSet(p(m.group(1).toInt(), m.group(2).toInt()), p(m.group(3).toInt(), m.group(4).toInt()))
    }
}

private fun partOne(input: List<InputSet>): Set<Point2> {
    val map = mutableSetOf<Point2>()

    input.forEach { (sensor, beacon) ->
        val distance = (beacon - sensor).abs.sum

        (-distance..distance).forEach dist@ { y ->
            if (y + sensor.y != 2000000) return@dist
            val spread = distance - y.absoluteValue
            (-spread..spread).forEach { x ->
                map.add(p(x, y) + sensor)
            }
        }
    }
    return map
}

private fun partTwo(input: List<InputSet>): Long {
    val searchArea = 0..4000000
    val ranges = mutableMapOf<Int, MutableList<IntRange>>()

    input.forEach { (sensor, beacon) ->
        val distance = (beacon - sensor).abs.sum
        println("$sensor $beacon, distance = $distance")
         (-distance..distance).forEach dist@ { y ->
            val absY = y + sensor.y
            if (absY !in searchArea) return@dist
            val spread = distance - y.absoluteValue
            val range = (sensor.x - spread).coerceAtLeast(0) .. (sensor.x + spread).coerceAtMost(4000000)
            ranges.computeIfAbsent(absY) { mutableListOf() }.add(range)
        }
        println("Ranges = " + ranges.values.sumOf { it.size })
    }

    ranges.forEach { (y, ranges) ->
        ranges.sortedBy { it.first }.reduce { r1, r2 ->
            if (r1.first in r2 || r1.last in r2 || r2.first in r1 || r2.last in r1) {
                val r3 = min(r1.first, r2.first) .. max(r1.last, r2.last)
                return@reduce r3
            }
            val beacon = p(r1.last + 1, y)
            println("Distress beacon is at $beacon")
            return beacon.x.toLong() * 4000000 + beacon.y
        }
    }

    error("Not found")
}

private fun print(map: Set<Point2>) {
    println(buildString {
        (0..20).forEach { y ->
            (0..20).forEach { x ->
                if (map.contains(p(x, y))) append('#') else append('.')
            }
            appendLine()
        }
    })
}

fun main() {
    val list = parse()
    val beacons = list.filter { it.beacon.y == 2000000 }.map { it.beacon.x }
    val p1 = partOne(list).count { it.y == 2000000 && !beacons.contains(it.x) }
    val p2 = partTwo(list)
    println("Part one: $p1")
    println("Part two: $p2")
}