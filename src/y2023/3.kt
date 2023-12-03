package y2023

import y2022.Point2
import y2022.getAdjacent
import y2022.p

private sealed interface SchematicElement
private data class Symbol(val char: Char) : SchematicElement
private data class Number(val digit: Int) : SchematicElement {
    var exhausted = false
}

private fun parse(): Map<Point2, SchematicElement> {
    val map = mutableMapOf<Point2, SchematicElement>()
    getInput(3).forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            val element = if (char.isDigit()) {
                Number(char.digitToInt())
            } else if (char == '.') {
                null
            } else {
                Symbol(char)
            }
            element?.let { map[p(x, y)] = it }
        }
    }
    return map
}

private fun getFullNumber(map: Map<Point2, SchematicElement>, origin: Point2): Int {
    var cursor = origin
    while (map[cursor - p(1, 0)] is Number) {
        cursor -= p(1, 0)
    }

    (map[cursor] as Number).exhausted = true
    var number = (map[cursor] as Number).digit
    while (map[cursor + p(1, 0)] is Number) {
        cursor += p(1, 0)
        (map[cursor] as Number).exhausted = true
        number = number * 10 + (map[cursor] as Number).digit
    }

    return number
}

private fun partOne(): Int {
    val map = parse()
    return map.entries.sumOf { (origin, element) ->
        if (element !is Symbol) return@sumOf 0
        map.getAdjacent(origin).sumOf { (point, element) ->
            if (element is Number && !element.exhausted) getFullNumber(map, point)
            else 0
        }
    }
}

private fun partTwo(): Int {
    val map = parse()
    return map.entries.sumOf { (origin, element) ->
        if (element !is Symbol || element.char != '*') return@sumOf 0
        val list = map.getAdjacent(origin).mapNotNull { (point, element) ->
            if (element is Number && !element.exhausted) getFullNumber(map, point)
            else null
        }
        if (list.size != 2) return@sumOf 0
        list.first() * list.last()
    }
}

fun main() {
    println("Part one: ${partOne()}")
    println("Part two: ${partTwo()}")
}