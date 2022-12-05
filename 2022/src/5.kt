import java.util.*

fun parseStacks(): List<Stack<String>> {
    val regex = ".(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.)...(.)".toPattern()
    val stacks = (0..8).map { Stack<String>() }.toMutableList()
    getInput(5).take(8).forEach {
        val m = regex.matcher(it)
        m.find()
        (1..9).forEach { i ->
            val match = m.group(i)
            if (match.isNotBlank()) stacks[i-1].push(match)
        }
    }
    stacks.forEach { it.reverse() }
    return stacks
}

fun parseInstructions(): List<Triple<Int, Int, Int>> {
    val regex = "move (\\d+) from (\\d+) to (\\d+)".toPattern()
    return getInput(5).drop(10).map { line ->
        val m = regex.matcher(line)
        m.find()
        Triple(m.group(1).toInt(), m.group(2).toInt(), m.group(3).toInt())
    }
}

fun getAnswer(crateMover: Int): String {
    val stacks = parseStacks()
    parseInstructions().forEach {(count, from, to) ->
        val moving = Stack<String>()
        repeat(count) {
            moving.push(stacks[from-1].pop())
        }

        if (crateMover != 9001) moving.reverse()

        repeat(count) { stacks[to-1].push(moving.pop()) }
    }
    return stacks.joinToString("") { it.peek() }
}

fun main() {
    println("Part one = " + getAnswer(crateMover = 9000))
    println("Part two = " + getAnswer(crateMover = 9001))
}