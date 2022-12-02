lateinit var ruleStrings: MutableMap<Int, String>
fun main() {
    val lines = getInput(19, 2020).lines()
    ruleStrings = lines.takeWhile { it.isNotBlank() }.associate { line ->
        val k = line.takeWhile { it.isDigit() }.toInt()
        val v = line.dropWhile { it.isDigit() || it == ':' }.trim()
        k to v
    }.toMutableMap()

    //ruleStrings[8] = "42 | 42 8"
    //ruleStrings[11] = "42 31 | 42 11 31"

    val inputLines = lines.filter { it.firstOrNull() == 'a' || it.firstOrNull() == 'b' }
    val ruleZero = ListRule(ruleStrings[0]!!)
    println(inputLines.count { ruleZero.match(it).any { s -> s == "" } })
}

private sealed interface Rule {
    fun match(string: String): List<String>
}

private class EitherRule(declaration: String) : Rule {
    val left: ListRule
    val right: ListRule

    init {
        val (lStr, rStr) = declaration.split("|").map { it.trim() }
        left = ListRule(lStr)
        right = ListRule(rStr)
    }

    override fun match(string: String): List<String> {
        val a = left.match(string)
        val b = right.match(string)
        if (a.isNotEmpty() && b.isNotEmpty()) println("Double match!")
        return a + b
    }
}

private class ListRule(declaration: String) : Rule {
    val children = declaration.split(" ")
        .map { it.toInt() }
        .map { ruleNum ->
            val line = ruleStrings[ruleNum]!!
            when {
                line.contains("|") -> EitherRule(line)
                line.startsWith("\"") -> CharacterRule(line)
                else -> ListRule(line)
            }
        }

    override fun match(string: String): List<String> {
        return children.fold(listOf(string)) { acc, rule ->
            acc.flatMap { rule.match(it) }
        }
    }
}

private class CharacterRule(declaration: String) : Rule {
    private val character = declaration[1]
    override fun match(string: String) = if (string.firstOrNull() == character) listOf(string.drop(1)) else emptyList()
}