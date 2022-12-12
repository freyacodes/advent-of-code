package y2022

private data class Monkey(
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val divisor: Int,
    val trueMonkey: Int,
    val falseMonkey: Int
) {
    var inspections = 0L
}

private fun parse(): List<Monkey> {
    val operations = buildList<(Long) -> Long> {
        add { it * 5 }
        add { it * 11 }
        add { it + 2 }
        add { it + 5 }
        add { it * it }
        add { it + 4 }
        add { it + 6 }
        add { it + 7 }
    }

    return getInputString(11).split("\n\n").mapIndexed { i, str ->
        val lines = str.lines()
        Monkey(
            lines[1].replace(",", "")
                .split(" ")
                .mapNotNull { it.toLongOrNull() }
                .toMutableList(),
            operations[i],
            lines[3].takeLastWhile { it.isDigit() }.toInt(),
            lines[4].takeLastWhile { it.isDigit() }.toInt(),
            lines[5].takeLastWhile { it.isDigit() }.toInt()
        )
    }
}

private fun run(partTwo: Boolean): Long {
    val monkeys = parse()
    val commonDivisor = monkeys.map { it.divisor }.fold(1) { a, b -> a * b }
    repeat(if(partTwo) 10000 else 20) {
        monkeys.forEach { monkey: Monkey ->
            while (monkey.items.isNotEmpty()) {
                var item = monkey.operation(monkey.items.removeFirst())
                item = if (!partTwo) item / 3 else item % commonDivisor
                monkey.inspections++
                val throwTo = if(item % monkey.divisor == 0L) monkey.trueMonkey else monkey.falseMonkey
                monkeys[throwTo].items.add(item)
            }
        }
    }

    val top = monkeys.sortedByDescending { it.inspections }.take(2)
    return top[0].inspections * top[1].inspections
}

fun main() {
    println("Part one: " + run(false))
    println("Part two: " + run(true))
}