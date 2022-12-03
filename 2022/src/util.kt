import java.io.File

fun getInput(day: Int, year: Int = 2022) = File("$year/input/${day}.txt").readLines()
fun getInputString(day: Int, year: Int = 2022) = File("$year/input/${day}.txt").readText()
