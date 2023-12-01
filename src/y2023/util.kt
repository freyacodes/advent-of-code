package y2023

import java.io.File

fun getInput(day: Int) = File("src/y2023/input/${day}.txt").readText().trim().lines()
fun getInputString(day: Int) = File("src/y2023/input/${day}.txt").readText().trim()
