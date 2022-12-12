package y2022

fun main() {
    val n = getInput(3).sumOf { s ->
        val c = s.take(s.length / 2).first { s.drop(s.length / 2).contains(it) }
        if (c.isLowerCase()) c.code - 96 else c.code - 38
    }
    println(n)

    val n2 = getInput(3).chunked(3).sumOf { l ->
        val c = l.first().first { l[1].contains(it) && l[2].contains(it) }
        if (c.isLowerCase()) c.code - 96 else c.code - 38
    }
    println(n2)
}