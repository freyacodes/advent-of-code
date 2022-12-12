package y2022

fun main() {
    val pairs = getInput(4).map() { s ->
        s.split(",").map {
            val (l,r) = it.split("-").map { i -> i.toInt() }
            l..r
        }.zipWithNext().single()
    }
    val part1 = pairs.count { (l, r) ->
        l.all { it in r } || r.all { it in l }
    }
    val part2 = pairs.count { (l, r) ->
        l.any { it in r } || r.any { it in l }
    }
    println("Part 1: $part1")
    println("Part 2: $part2")
}