package y2023

import y2022.Point2
import y2022.p

private val north = p(0, -1)
private val south = p(0, 1)
private val west = p(-1, 0)
private val east = p(1, 0)

private sealed class BeamTile {
    var energised = false
}

private class EmptyTile: BeamTile()
private class MirrorFront : BeamTile()
private class MirrorBack : BeamTile()
private class SplitterHorizontal : BeamTile() {
    var hasSplitBefore = false
}
private class SplitterVertical : BeamTile() {
    var hasSplitBefore = false
}

private fun parse(): Map<Point2, BeamTile> {
    return getInput(16).flatMapIndexed { y, line ->
        line.mapIndexed { x, c ->
            p(x, y) to when (c) {
                '.' -> EmptyTile()
                '/' -> MirrorFront()
                '\\' -> MirrorBack()
                '-' -> SplitterHorizontal()
                '|' -> SplitterVertical()
                else -> error("Panic!")
            }
        }
    }.toMap()
}

private fun partOne(): Int {
    val map = parse()
    runBeam(map, p(0, 0), east)
    return map.count { it.value.energised }
}

private fun partTwo(): Int {
    val mapLength = getInput(16).first().length
    val range = 0 until mapLength

    val startingPoints = range.map { p(0, it) to east } +
            range.map { p(mapLength.dec(), it) to west } +
            range.map { p(it, 0) to south } +
            range.map { p(it, mapLength.dec()) to north }

    return startingPoints.maxOf { (start, direction) ->
        val map = parse()
        runBeam(map, start, direction)
        map.count { it.value.energised }
    }
}

private fun runBeam(map: Map<Point2, BeamTile>, startingPosition: Point2, startingDirection: Point2) {
    var direction = startingDirection
    var position = startingPosition
    while (true) {
        val tile = map[position] ?: return
        tile.energised = true

        when (tile) {
            is EmptyTile -> position += direction
            is SplitterVertical -> {
                if (direction == east || direction == west) {
                    if (tile.hasSplitBefore) return
                    tile.hasSplitBefore = true
                    runBeam(map, position + north, north)
                    runBeam(map, position + south, south)
                    return
                } else position += direction
            }
            is SplitterHorizontal -> {
                if (direction == north || direction == south) {
                    if (tile.hasSplitBefore) return
                    tile.hasSplitBefore = true
                    runBeam(map, position + east, east)
                    runBeam(map, position + west, west)
                    return
                } else position += direction
            }
            is MirrorFront -> {
                // /
                direction = when(direction) {
                    north -> east
                    east -> north
                    south -> west
                    west -> south
                    else -> error("Panic!")
                }
                position += direction
            }
            is MirrorBack -> {
                // \
                direction = when(direction) {
                    north -> west
                    west -> north
                    south -> east
                    east -> south
                    else -> error("Panic!")
                }
                position += direction
            }
        }
    }
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}