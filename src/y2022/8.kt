package y2022

private fun scanTrees(): Map<Point2, Int> {
    val map = mutableMapOf<Point2, Int>()
    getInput(8).mapIndexed { y, line ->
        line.forEachIndexed { x, c ->
            map[Point2(x, y)] = c.digitToInt()
        }
    }
    return map
}

private fun partOne(trees: Map<Point2, Int>) {
    val count = trees.count { (pos, height) ->
        val (x1, y1) = pos
        val north = trees.filterKeys { (x2, y2) -> x1 == x2 && y2 < y1 }
        val south = trees.filterKeys { (x2, y2) -> x1 == x2 && y2 > y1 }
        val east = trees.filterKeys { (x2, y2) -> x1 < x2 && y2 == y1 }
        val west = trees.filterKeys { (x2, y2) -> x1 > x2 && y2 == y1 }
        listOf(north, south, east, west).any { it.all { (_, h) -> h < height } }
    }
    println("Part one: $count")
}

private fun partTwo(trees: Map<Point2, Int>) {
    val count = trees.maxOf { (pos, height) ->
        val (x1, y1) = pos
        val north = trees.filterKeys { (x2, y2) -> x1 == x2 && y2 < y1 }.toList().sortedByDescending { (p, _) -> p.y }
        val south = trees.filterKeys { (x2, y2) -> x1 == x2 && y2 > y1 }.toList().sortedBy { (p, _) -> p.y }
        val east = trees.filterKeys { (x2, y2) -> x1 < x2 && y2 == y1 }.toList().sortedBy { (p, _) -> p.x }
        val west = trees.filterKeys { (x2, y2) -> x1 > x2 && y2 == y1 }.toList().sortedByDescending { (p, _) -> p.x }
        listOf(north, south, east, west).map { l ->
            l.takeWhile { (_, h) -> h < height }.size.plus(1).coerceAtMost(l.size)
        }.fold(1) { a, b -> a*b }
    }
    println("Part two: $count")
}

fun main() {
    val trees = scanTrees()
    partOne(trees)
    partTwo(trees)
}