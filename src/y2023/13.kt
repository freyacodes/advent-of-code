package y2023

private class Mirror(val topDown: List<String>) {
    val leftRight: List<String>

    init {
        leftRight = mutableListOf<String>()
        repeat(topDown.first().length) { i ->
            leftRight.add(buildString {
                repeat(topDown.size) { j ->
                    append(topDown[j][i])
                }
            })
        }
    }
}

private fun parse(): List<Mirror> {
    return getInputString(13).split("\n\n").map {
        Mirror(it.lines())
    }
}

private fun partOne(): Int {
    var index = -1
    return parse().sumOf { mirror ->
        index++
        val horizontal = resolve(mirror.leftRight)
        val vertical = resolve(mirror.topDown)

        if (horizontal == null && vertical == null) error("Both null $index")
        if (horizontal != null && vertical != null) error("Neither null $index")
        horizontal ?: (vertical!! * 100)
    }
}

private fun resolve(lines: List<String>): Int? {
    repeat(lines.size-1) { offset ->
        if (lines[offset] == lines[offset+1]) {
            if (isValid(lines, offset)) return offset + 1
        }
    }

    return null
}

private fun isValid(lines: List<String>, reflectsAt: Int): Boolean {
    var offset = 0
    while (true) {
        val a = lines.elementAtOrNull(reflectsAt - offset)
        val b = lines.elementAtOrNull(reflectsAt + offset + 1)
        if (a == null || b == null) return true
        if (a != b) return false
        offset++
    }
}

fun main() {
    println("Part one: " + partOne())
}