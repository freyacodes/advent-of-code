package y2023

import kotlin.math.max
import kotlin.math.min

private data class MapEntry(val destinationStart: Long, val sourceStart: Long, val length: Long) {
    val sourceRange = sourceStart until (sourceStart + length)
}

private fun LongRange.intersectionWith(other: LongRange): LongRange {
    return max(first, other.first) .. min(last, other.last)
}

val LongRange.length: Long get() = last - first + 1

private fun parse(): Pair<List<Long>, List<List<MapEntry>>> {
    val sections = getInputString(5).split("\n\n").toMutableList()
    val seeds = sections.removeFirst().drop(7).split(" ").map { it.toLong() }

    return seeds to sections.map { s ->
        val nums = s.split("\n").drop(1).map { it.split(" ").map { n -> n.toLong() } }
        nums.map { MapEntry(it[0], it[1], it[2]) }
    }
}

private fun partOne(): Long {
    val (seeds, almanac) = parse()

    return seeds.minOf { seed ->
        var currentValue = seed
        almanac.forEach { map ->
            val entry = map.singleOrNull { currentValue in it.sourceRange } ?: return@forEach
            val offset = currentValue - entry.sourceStart
            currentValue = entry.destinationStart + offset
        }
        currentValue
    }
}

private fun resolve(map: List<MapEntry>, input: LongRange): List<LongRange> {
    println("Input $input")
    val intersections = mutableListOf<LongRange>()
    val destinations = mutableListOf<LongRange>()

    map.forEach { entry ->
        val intersection = entry.sourceRange.intersectionWith(input)
        println(intersection.length)
        if (intersection.isEmpty()) return@forEach

        intersections.add(intersection)
        val offset = intersection.first - entry.sourceStart
        val destination = (entry.destinationStart + offset) until (entry.destinationStart + offset + intersection.length)
        destinations.add(destination)
    }

    var cursor = input.first
    intersections.sortedBy { it.first }.forEach { intersection ->
        if (cursor < intersection.first) {
            destinations.add(cursor until intersection.first)
        }
        cursor = intersection.last + 1
    }

    if (cursor <= input.last) {
        destinations.add(cursor .. input.last)
    }

    println(destinations.sortedBy { it.first })

    return destinations
}

private fun partTwo(): Long {
    val (seeds, almanac) = parse()
    val seedPairs = seeds.windowed(2, 2).map { (a,b) -> a until (a+b) }

    return seedPairs.minOf { seedPair ->
        var currentRanges = mutableListOf(seedPair)
        almanac.forEach { map ->
            val nextPairs = mutableListOf<LongRange>()
            currentRanges.map { input ->
                nextPairs.addAll(resolve(map, input))
            }
            currentRanges = nextPairs.toMutableList()
            println(currentRanges.sortedBy { it.first })
        }

        currentRanges.minOf { it.first }
    }
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}