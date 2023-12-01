package y2022

import y2022.Heading.*

private enum class MapTile {
    OPEN,
    BLOCKED
}

private enum class Heading(val factor: Point2) {
    RIGHT(p(1, 0)),
    DOWN(p(0, 1)),
    LEFT(p(-1, 0)),
    UP(p(0, -1))
}

private fun parse(): Pair<Map<Point2, MapTile>, List<Pair<Char, Int>>> {
    val (upper, lower) = getInputString(22).split("\n\n")

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

private fun partOne() {
    val (map, commands) = parse()
    var position = map.keys.filter { it.y == 1 }.minBy { it.x }
    var heading = RIGHT

    commands.forEach { (lr, length) ->
        if (lr == 'L') heading = Heading.values()[(heading.ordinal - 1).mod(4)]
        else if (lr == 'R') heading = Heading.values()[(heading.ordinal + 1).mod(4)]

        repeat(length) {
            //val next =
        }
    }
}

fun main() {
    partOne()
}