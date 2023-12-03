package y2022

import y2022.Heading.*

private enum class MapTile {
    OPEN,
    BLOCKED
}

private enum class Heading(val offset: Point2) {
    RIGHT(p(1, 0)),
    DOWN(p(0, 1)),
    LEFT(p(-1, 0)),
    UP(p(0, -1))
}

private fun wrapOne(map: Map<Point2, MapTile>, position: Point2, heading: Heading): WrapResult {
    val wrap = when (heading) {
        LEFT -> map.filter { it.key.y == position.y }.maxBy { it.key.x }
        RIGHT -> map.filter { it.key.y == position.y }.minBy { it.key.x }
        UP -> map.filter { it.key.x == position.x }.maxBy { it.key.y }
        DOWN -> map.filter { it.key.x == position.x }.minBy { it.key.y }
    }
    return WrapResult(wrap.key, heading)
}

private fun wrapTwo(map: Map<Point2, MapTile>, position: Point2, heading: Heading): WrapResult {
    val isSample = false
    val sideLength = if (isSample) 4 else 50
    val cubeIndex = (position) / sideLength
    val (newCubeIndex, newHeading) = if (isSample) when (cubeIndex to heading) {
        p(2, 1) to RIGHT -> p(3, 2) to DOWN
        p(2, 2) to DOWN -> p(0, 1) to UP
        p(1, 1) to UP -> p(2, 0) to RIGHT
        else -> error("Missing: ${cubeIndex to heading}")
    } else when (cubeIndex to heading) {
        p(0, 2) to LEFT -> p(1, 0) to RIGHT
        p(2, 0) to DOWN -> p(1, 1) to LEFT
        p(1, 1) to RIGHT -> p(2, 0) to UP
        p(2, 0) to RIGHT -> p(1, 2) to LEFT
        p(1, 2) to RIGHT -> p(2, 0) to LEFT
        p(0, 3) to DOWN -> p(2, 0) to DOWN
        p(2, 0) to UP -> p(0, 3) to UP
        p(1, 2) to DOWN -> p(0, 3) to LEFT
        p(0, 3) to RIGHT -> p(1, 2) to UP
        p(1, 1) to LEFT -> p(0, 2) to DOWN
        p(1, 0) to LEFT -> p(0, 2) to RIGHT
        p(1, 0) to UP -> p(0, 3) to RIGHT
        p(0, 3) to LEFT -> p(1, 0) to DOWN
        p(0, 2) to UP -> p(1, 1) to RIGHT
        else -> error("Missing: ${cubeIndex to heading}")
    }

    /*
    ...111444
    ...222...
    333666...
    555......
     */

    val cubeX = position.x % sideLength
    val cubeY = position.y % sideLength

    val offset = when (heading) {
        LEFT -> sideLength.dec() - cubeY
        RIGHT -> cubeY
        UP -> cubeX
        DOWN -> sideLength.dec() - cubeX
    }

    val finalX = when (newHeading) {
        LEFT -> sideLength.dec()
        RIGHT -> 0
        UP -> offset
        DOWN -> sideLength.dec() - offset
    } + sideLength * newCubeIndex.x

    val finalY = when (newHeading) {
        LEFT -> sideLength.dec() - offset
        RIGHT -> offset
        UP -> sideLength.dec()
        DOWN -> 0
    } + sideLength * newCubeIndex.y

    return WrapResult(p(finalX, finalY), newHeading)
}

private data class WrapResult(val position: Point2, val heading: Heading)

private fun parse(): Pair<Map<Point2, MapTile>, List<Pair<Char, Int>>> {
    val (upper, lower) = getInputStringUntrimmed(22).split("\n\n")

    val map = mutableMapOf<Point2, MapTile>()

    upper.lines().forEachIndexed() { y, line ->
        line.forEachIndexed { x, c ->
            when (c) {
                ' ' -> {}
                '.' -> map[p(x, y)] = MapTile.OPEN
                '#' -> map[p(x, y)] = MapTile.BLOCKED
            }
        }
    }

    val regex = """\D\d+""".toRegex()
    val commands = regex.findAll(lower).map { it.value.first() to it.value.drop(1).toInt() }.toMutableList()
    commands.add(0, 'ø' to lower.takeWhile { it.isDigit() }.toInt())

    return map to commands
}

private fun partOne(wrap: (Map<Point2, MapTile>, Point2, Heading) -> WrapResult): Int {
    val (map, commands) = parse()
    var position = map.keys.filter { it.y == 0 }.minBy { it.x }
    var heading = RIGHT

    commands.forEach { (lr, length) ->
        if (lr == 'L') heading = Heading.values()[(heading.ordinal - 1).mod(4)]
        else if (lr == 'R') heading = Heading.values()[(heading.ordinal + 1).mod(4)]

        repeat(length) {
            var newPosition = position + heading.offset
            var next = map[position + heading.offset]
            var newHeading = heading
            if (next == null) {
                val res = wrap(map, position, heading)
                newPosition = res.position
                next = map[res.position]!!
                newHeading = res.heading
            }

            if (next == MapTile.BLOCKED) {
                //println("Position: $position, $heading")
                return@forEach
            }
            position = newPosition
            heading = newHeading
        }
        //println("Position: $position, $heading")
    }

    position += p(1, 1)

    return 1000 * position.y + 4 * position.x + heading.ordinal
}

fun main() {
    println("Part one: " + partOne(::wrapOne))
    println("Part two: " + partOne(::wrapTwo))
}