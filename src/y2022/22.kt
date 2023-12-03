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

private fun wrap1(map: Map<Point2, MapTile>, position: Point2, heading: Heading): WrapResult {
    val wrap = when (heading) {
        LEFT -> map.filter { it.key.y == position.y }.maxBy { it.key.x }
        RIGHT -> map.filter { it.key.y == position.y }.minBy { it.key.x }
        UP -> map.filter { it.key.x == position.x }.maxBy { it.key.y }
        DOWN -> map.filter { it.key.x == position.x }.minBy { it.key.y }
    }
    return WrapResult(wrap.key, wrap.value, heading)
}

private data class WrapResult(val position: Point2, val tile: MapTile, val heading: Heading)

private fun parse(): Pair<Map<Point2, MapTile>, List<Pair<Char, Int>>> {
    val (upper, lower) = getInputStringUntrimmed(22).split("\n\n")

    val map = mutableMapOf<Point2, MapTile>()

    upper.lines().forEachIndexed() { y, line ->
        line.forEachIndexed { x, c ->
            when (c) {
                ' ' -> {}
                '.' -> map[p(x+1,y+1)] = MapTile.OPEN
                '#' -> map[p(x+1,y+1)] = MapTile.BLOCKED
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
    var position = map.keys.filter { it.y == 1 }.minBy { it.x }
    var heading = RIGHT
    println(commands)

    commands.forEach { (lr, length) ->
        if (lr == 'L') heading = Heading.values()[(heading.ordinal - 1).mod(4)]
        else if (lr == 'R') heading = Heading.values()[(heading.ordinal + 1).mod(4)]

        repeat(length) {
            var newPosition = position + heading.offset
            var next = map[newPosition]
            var newHeading = heading
            if (next == null) {
                val res = wrap(map, position, heading)
                newPosition = res.position
                next = res.tile
                newHeading = res.heading
            }

            if (next == MapTile.BLOCKED) {
                println("Position: $position, $heading")
                return@forEach
            }
            position = newPosition
            heading = newHeading
        }
        println("Position: $position, $heading")
    }

    return 1000 * position.y + 4 * position.x + heading.ordinal
}

fun main() {
    println("Part one: " + partOne(::wrap1))
}