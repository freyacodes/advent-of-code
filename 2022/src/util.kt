import java.io.File

fun getInput(day: Int, year: Int = 2022) = File("$year/input/${day}.txt").readText()