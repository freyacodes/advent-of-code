package y2020

import java.io.File

fun getInput(day: Int) = File("src/y2020/input/${day}.txt").readLines()
fun getInputString(day: Int) = File("src/y2020/input/${day}.txt").readText()
