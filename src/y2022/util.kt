package y2022

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.ceil

fun getInput(day: Int) = File("src/y2022/input/${day}.txt").readText().trim().lines()
fun getInputStringUntrimmed(day: Int) = File("src/y2022/input/${day}.txt").readText()
fun getInputString(day: Int) = getInputStringUntrimmed(day).trim()

fun p(x: Int, y: Int) = Point2(x, y)
fun p(x: Int, y: Int, z: Int) = Point3(x, y, z)

fun p2(list: List<Int>): Point2 {
    if (list.size != 2) error("Bad size: ${list.size}")
    return Point2(list[0], list[1])
}

fun p3(list: List<Int>): Point3 {
    if (list.size != 3) error("Bad size: ${list.size}")
    return Point3(list[0], list[1], list[2])
}


data class Point2(val x: Int, val y: Int) {
    operator fun plus(other: Point2) = p(x + other.x, y + other.y)
    operator fun minus(other: Point2) = p(x - other.x, y - other.y)
    operator fun times(factor: Int) = p(x * factor, y * factor)
    operator fun div(divisor: Int) = p(x / divisor, y / divisor)
    val abs get() = p(x.absoluteValue, y.absoluteValue)
    val sum get() = x + y
    override fun toString() = "($x,$y)"
}

data class Point3(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Point3) = p(x + other.x, y + other.y, z + other.z)
    fun getNeighbors(): Sequence<Point3> {
        return sequence {
            val p = this@Point3
            yield(p + p(1, 0, 0))
            yield(p + p(-1, 0, 0))
            yield(p + p(0, 1, 0))
            yield(p + p(0, -1, 0))
            yield(p + p(0, 0, 1))
            yield(p + p(0, 0, -1))

        }
    }

    override fun toString() = "($x,$y,$z)"
}

fun <T> Map<Point2, T>.getAdjacent(origin: Point2): List<Pair<Point2, T>> = listOf(
    origin + p(-1, -1),
    origin + p(0, -1),
    origin + p(1, -1),
    origin + p(-1, 0),
    origin + p(1, 0),
    origin + p(-1, 1),
    origin + p(0, 1),
    origin + p(1, 1)
).mapNotNull {
    val value = this[it] ?: return@mapNotNull null
    it to value
}

fun <T> MutableCollection<T>.addNotNull(element: T?) {
    element ?: return
    add(element)
}

fun Int.ceilDiv(other: Int): Int {
    return ceil(this.toFloat() / other).toInt()
}
