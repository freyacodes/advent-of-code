package y2023

private class Mirror(val topDown: List<String>) {
    val leftRight: List<String>
    var resolutionHorizontal: Int? = null
    var resolutionVertical: Int? = null

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

fun main() {
    val mirrors = parse()
    val partOne = mirrors.sumOf { mirror ->
        resolveBoth(mirror, false)
    }

    println("Part one: $partOne")

    val partTwo = mirrors.sumOf { mirror ->
        resolveBoth(mirror, true)
    }

    println("Part two: $partTwo")
}

private fun resolveBoth(mirror: Mirror, partTwo: Boolean): Int {
    val horizontal = resolve(mirror.leftRight, partTwo, mirror.resolutionHorizontal)
    val vertical = resolve(mirror.topDown, partTwo, mirror.resolutionVertical)

    if (horizontal == null && vertical == null) error("Both null")
    if (horizontal != null && vertical != null) error("Neither null")

    if (!partTwo) {
        mirror.resolutionHorizontal = horizontal
        mirror.resolutionVertical = vertical
    }

    return horizontal ?: (vertical!! * 100)
}

private fun resolve(lines: List<String>, partTwo: Boolean, ignore: Int?): Int? {
    val allowance = if (partTwo) 1 else 0
    repeat(lines.size-1) { offset ->
        if (offset + 1 == ignore) return@repeat
        val diff = diff(lines[offset], lines[offset+1])
        val valid = when (diff) {
            0 -> isValid(lines, offset, allowance)
            1 -> partTwo && isValid(lines, offset, allowance)
            else -> false
        }
        if (valid) return offset + 1
    }

    return null
}

private fun diff(a: String, b: String): Int {
    return a.zip(b).count { it.first != it.second }
}

private fun isValid(lines: List<String>, reflectsAt: Int, allowance: Int): Boolean {
    var offset = 0
    var allowanceRemaining = allowance
    while (true) {
        val a = lines.elementAtOrNull(reflectsAt - offset)
        val b = lines.elementAtOrNull(reflectsAt + offset + 1)
        if (a == null || b == null) return true
        val diff = diff(a, b)
        if (diff > 0) {
            allowanceRemaining -= diff
            if (allowanceRemaining < 0) return false
        }

        offset++
    }
}