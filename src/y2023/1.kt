package y2023

fun main() {
    val partOne = getInput(1).sumOf { s ->
       s.first { it.isDigit() }.digitToInt() * 10 + s.last { it.isDigit() }.digitToInt()
    }
    println("Part one: $partOne")

    val partTwo = getInput(1).map { string ->
        var s = string
        s = s.replace("one", "one1one")
        s = s.replace("two", "two2two")
        s = s.replace("three", "three3three")
        s = s.replace("four", "four4four")
        s = s.replace("five", "five5five")
        s = s.replace("six", "six6six")
        s = s.replace("seven", "seven7seven")
        s = s.replace("eight", "eight8eight")
        s = s.replace("nine", "nine9nine")
        s
    }.sumOf { s ->
        s.first { it.isDigit() }.digitToInt() * 10 + s.last { it.isDigit() }.digitToInt()
    }

    println("Part two $partTwo")
}