package y2023

private data class SpringLine(val springs: String, val groups: List<Int>) {
    fun unfold(): SpringLine {
        val newSprings = listOf(springs, springs, springs, springs, springs).joinToString("?")
        val groups = groups + groups + groups + groups + groups
        return SpringLine(newSprings, groups)
    }
}

private fun parse(): List<SpringLine> {
    return getInput(12).map { line ->
        line.split(" ").run {
            SpringLine(first(), last().split(',').map { it.toInt() })
        }
    }
}

fun resolve(springs: String, groups: List<Int>): Long {
    val firstGroup = groups.first()
    var hasFoundFirstValid = false
    var arrangements = 0L

    repeat(springs.length) { offset ->
        val remainder = springs.drop(offset)
        if (remainder.count { it == '#' } > groups.sum()) return arrangements

        val valid = isValid(remainder, firstGroup, groups.sum() - remainder.count { it == '#' })
        if (valid) {
            val found = if (groups.size > 1) {
                memoize(springs.drop(firstGroup+offset+1), groups.drop(1))
            } else 1

            /*if (found > 0) {
                println("$found $remainder $groups")
            }*/

            arrangements += found
        }
        if (remainder.startsWith('#')) return arrangements
    }

    return arrangements
}

private val cache = mutableMapOf<Pair<String, List<Int>>, Long>()

private fun memoize(springs: String, groups: List<Int>): Long {
    val cached = cache[springs to groups]
    if (cached != null) return cached
    val new = resolve(springs, groups)
    cache[springs to groups] = new
    return new
}

fun isValid(string: String, broken: Int, maxHiddenBroken: Int): Boolean {
    if (broken > string.length) return false
    if (string.take(broken).count { it == '?' } > maxHiddenBroken) return false
    if (string.take(broken).any { it == '.' }) return false
    val end = string.drop(broken).firstOrNull()
    return end != '#'
}

private fun partOne(): Long {
    return parse().sumOf {
        resolve(it.springs, it.groups)
    }
}

private fun partTwo(): Long {
    var i = 0
    return parse().map { it.unfold() }.sumOf {
        memoize(it.springs, it.groups)
    }
}

fun main() {
    println("Part one ${partOne()}")
    println("Part two ${partTwo()}")
}