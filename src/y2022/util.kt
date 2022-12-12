package y2022

import java.io.File

fun getInput(day: Int) = File("src/y2022/input/${day}.txt").readLines()
fun getInputString(day: Int) = File("src/y2022/input/${day}.txt").readText()
