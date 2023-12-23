package y2023

import y2022.Point2
import y2022.Point3
import y2022.p
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

private fun parse(): List<Brick> {
    val regex = "\\d+".toRegex()
    return getInput(22).map { line ->
        val values = regex.findAll(line).map { it.value.toInt() }.toList()
        val points = mutableListOf<Point3>()
        for (x in min(values[0], values[3]) .. max(values[0], values[3])) {
            for (y in min(values[1], values[4]) .. max(values[1], values[4])) {
                for (z in min(values[2], values[5]) .. max(values[2], values[5])) {
                    points += Point3(x, y, z)
                }
            }
        }

        Brick(points)
    }
}

private class Brick(var points: List<Point3>) {
    var zMin by Delegates.notNull<Int>()
    var zMax by Delegates.notNull<Int>()
    lateinit var footprint: Set<Point2>

    lateinit var supportedBy: Set<Brick>
    lateinit var supports: Set<Brick>
    private var allAbove: Set<Brick>? = null

    init {
        update()
    }

    private fun update() {
        zMin = points.minOf { it.z }
        zMax = points.maxOf { it.z }
        footprint = points.map { p(it.x, it.y) }.toSet()
    }

    fun translateZ(z: Int) {
        points = points.map { it + p(0, 0, z) }
        update()
    }

    fun secondPass(map: Map<Point3, Brick>) {
        val lower = footprint.map { p(it.x, it.y, zMin - 1) }.toSet()
        val upper = footprint.map { p(it.x, it.y, zMax + 1) }.toSet()
        supportedBy = lower.mapNotNull { map[it] }.toSet()
        supports = upper.mapNotNull { map[it] }.toSet()
    }

    fun getAllAbove(): Set<Brick> {
        if (allAbove != null) return allAbove!!
        allAbove = supports.flatMap { it.getAllAbove() }.plus(supports).toSet()
        return allAbove!!
    }
}

fun main() {
    val tower = parse().toMutableList()

    tower.filter { it.zMin > 1 }.sortedBy { it.zMin }.forEach { toFall ->
        val newLowerZ = tower.filter { it.zMax < toFall.zMin }
            .filter { it.footprint.any { p -> p in toFall.footprint } }
            .maxOfOrNull { it.zMax + 1 } ?: 1
        toFall.translateZ(newLowerZ - toFall.zMin)
    }

    val pointsToBrick = tower.flatMap { brick -> brick.points.map { it to brick } }
        .associate { it }

    tower.forEach { it.secondPass(pointsToBrick) }

    val canDisintegrate = tower.count { brick ->
        brick.supports.all { it.supportedBy.size > 1 }
    }
    println("Part one: $canDisintegrate")

    val chainReaction = tower.sumOf { brick ->
        val allAbove = brick.getAllAbove().toList().sortedBy { it.zMin }
        val supportsWhichWouldGoAway = mutableSetOf(brick)

        allAbove.forEach { other ->
            if (supportsWhichWouldGoAway.containsAll(other.supportedBy)) {
                supportsWhichWouldGoAway.add(other)
            }
        }
        supportsWhichWouldGoAway.size - 1
    }

    println("Part two: $chainReaction") // 1095 too low
}