package y2023

private fun partOne(): Int {
    return getInputString(15)
        .split(",")
        .sumOf(::hash)
}

private fun partTwo(): Int {
    val map = mutableListOf<MutableList<Pair<String, Int>>>()
    repeat(256) {
        map.add(mutableListOf())
    }
    getInputString(15).split(",").forEach { part ->
        val label = part.takeWhile { it.isLetter() }
        val hash = hash(label)
        val operator = part[label.length]

        when (operator) {
            '=' -> {
                val focalLength = part.takeLast(1).toInt()
                val list = map[hash]
                val index = list.indexOfFirst { it.first == label }
                if (index == -1) {
                    list.add(label to focalLength)
                } else {
                    list[index] = label to focalLength
                }
            }
            '-' -> map[hash].removeIf { it.first == label }
            else -> error("Panic!")
        }
    }

    var sum = 0
    map.forEachIndexed { box, list ->
        list.forEachIndexed { slot, (label, focalLength) ->
            println("$label ${box.inc()} ${slot.inc()} $focalLength")
            sum += box.inc() * slot.inc() * focalLength
        }
    }

    return sum
}

private fun hash(string: String): Int {
    return string.fold(0) { acc, c ->
        ((acc + c.code) * 17) % 256
    }
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}