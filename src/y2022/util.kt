package y2022

import java.io.File

fun getInput(day: Int) = File("src/y2022/input/${day}.txt").readLines()
fun getInputString(day: Int) = File("src/y2022/input/${day}.txt").readText()

fun p(x: Int, y: Int) = Point2(x, y)
data class Point2(val x: Int, val y: Int) {
    operator fun plus(other: Point2) = p(x + other.x, y + other.y)
    operator fun minus(other: Point2) = p(x - other.x, y - other.y)
    operator fun times(factor: Int) = p(x * factor, y * factor)
    override fun toString() = "($x,$y)"
}