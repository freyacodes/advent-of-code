package y2023

private data class SpringLine(val springs: String, val groups: List<Int>)

private fun parse(): List<SpringLine> {
    return getInput(12).map { line ->
        line.split(" ").run {
            SpringLine(first(), last().split(',').map { it.toInt() })
        }
    }
}

fun resolve(springs: String, groups: List<Int>): Int {
    val firstGroup = groups.first()
    var hasFoundFirstValid = false
    var arrangements = 0


    repeat(springs.length) { offset ->
        val remainder = springs.drop(offset)
        if (remainder.count { it == '#' } > groups.sum()) return arrangements

        val valid = isValid(remainder, firstGroup, groups.sum() - remainder.count { it == '#' })
        if (valid) {
            val found = if (groups.size > 1) {
                resolve(springs.drop(firstGroup+offset+1), groups.drop(1))
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

fun isValid(string: String, broken: Int, maxHiddenBroken: Int): Boolean {
    if (broken > string.length) return false
    if (string.take(broken).count { it == '?' } > maxHiddenBroken) return false
    if (string.take(broken).any { it == '.' }) return false
    val end = string.drop(broken).firstOrNull()
    return end != '#'
}

private fun partOne(): Int {
    return parse().sumOf {
        val res = resolve(it.springs, it.groups)
        println("${it.springs} ${it.groups} $res")
        res
    }
}

fun main() {
    println("Part one ${partOne()}")
}