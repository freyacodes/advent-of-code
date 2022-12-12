package y2022

fun main() {
    var currentElf = 0
    val elves = mutableListOf<Int>()

    getInput(1).forEach { line ->
        if (line.isBlank()) {
            elves.add(currentElf)
            currentElf = 0
        } else currentElf += line.toInt()
    }
    elves.add(currentElf)

    elves.sortDescending()
    println(elves.take(3).sum())
}