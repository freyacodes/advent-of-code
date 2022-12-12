package y2022

fun main() {
    var register = 1
    val cycles = mutableListOf<Int>()

    getInput(10).forEach { line ->
        if (line == "noop") {
            cycles.add(register)
        } else {
            cycles.add(register)
            cycles.add(register)
            register += line.takeLastWhile { it.isDigit() || it == '-' }.toInt()
        }
    }

    val strength = (20..220 step 40).sumOf {
        it * cycles[it-1]
    }

    val image = buildString {
        cycles.forEachIndexed { i, reg ->
            val x = (i) % 40
            val c = if (reg in (x-1)..(x+1)) '#' else '.'
            append(c)
            if (x == 39) appendLine()
        }
    }

    println("Part one: $strength")
    println("Part two:\n$image")
}