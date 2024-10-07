package y2022

private enum class Direction(val offset: Point2, val outlook: List<Point2>) {
    NORTH(p(0, -1), listOf(p(0, -1), p(1, -1), p(-1, -1))),
    SOUTH(p(0, 1), listOf(p(0, 1), p(1, 1), p(-1, 1))),
    WEST(p(-1, 0), listOf(p(-1, 0), p(-1, -1), p(-1, 1))),
    EAST(p(1, 0), listOf(p(1, 0), p(1, -1), p(1, 1)));

    val next by lazy { values()[(ordinal + 1) % 4] }
}

fun main() {
}