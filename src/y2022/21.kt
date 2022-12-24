package y2022

import kotlin.system.exitProcess

private interface RiddleMonkey {
    val value: Long
}

private class ConstantMonkey(override var value: Long) : RiddleMonkey

private class ArithmeticMonkey(
    val map: Map<String, RiddleMonkey>,
    val m1Name: String,
    val m2Name: String,
    val func: (Long, Long) -> Long
) : RiddleMonkey {
    override val value: Long get() = func(map[m1Name]!!.value, map[m2Name]!!.value)
}


private fun parse(): MutableMap<String, RiddleMonkey> {
    val regexOne = """(\w{4}): (\d+)""".toRegex()
    val regexTwo = """(\w{4}): (\w{4}) ([+-/*]) (\w{4})""".toRegex()

    val map = mutableMapOf<String, RiddleMonkey>()

    getInput(21).associateTo(map) { it ->
        var groups = regexOne.matchEntire(it)?.groups?.toList()?.map { it!!.value }
        if (groups != null) {
            groups[1] to ConstantMonkey(groups[2].toLong())
        } else {
            groups = regexTwo.matchEntire(it)?.groups!!.toList().map { it!!.value }
            val func: (Long, Long) -> Long = when (groups[3]) {
                "+" -> Long::plus
                "-" -> Long::minus
                "*" -> Long::times
                "/" -> Long::div
                else -> error("Unexpected")
            }
            groups[1] to ArithmeticMonkey(map, groups[2], groups[4], func)
        }
    }

    return map
}

private fun partOne() {
    println("Part one: " + parse()["root"]!!.value)
}

private fun partTwo() {
    val map = parse()
    val root = map["root"] as ArithmeticMonkey
    val m1 = map[root.m1Name]!!
    val m2 = map[root.m2Name]!!
    val human = map["humn"] as ConstantMonkey // Neither constant nor monkey

    // Searched manually
    (3272260900000..3272261000000 step 1).forEach {
        human.value = it
        //println("$it ${m1.value - m2.value}")
        if (m1.value == m2.value) {
            println("Part two: $it")
            exitProcess(0)
        }
    }
}

fun main() {
    partOne()
    partTwo()
}