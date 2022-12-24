package y2022

private fun solve(factor: Long, times: Int = 1): Long {
    val input = getInput(20).map { it.toLong() * factor }.withIndex()
    val list = input.toMutableList()

    repeat(times) {
        input.forEach { iv ->
            val index = list.indexOf(iv)
            list.removeAt(index)
            list.add((index + iv.value).mod(list.size), iv)
        }
    }

    val zero = list.indexOfFirst { it.value == 0L }
    return listOf(1000, 2000, 3000).map { (zero + it) % list.size }.sumOf { list[it].value }
}

fun main() {
    println("Part one: " + solve(1))
    println("Part two: " + solve(811589153, 10))
}