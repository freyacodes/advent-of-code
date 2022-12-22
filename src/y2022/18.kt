package y2022

private fun parse(): Set<Point3> {
    return getInput(18).map { s -> p3(s.split(",").map { it.toInt() }) }.toSet()
}

private fun partOne(): Int {
    val map = parse()
    return map.sumOf { origin -> origin.getNeighbors().count { !map.contains(it) } }
}

private fun partTwo(): Int {
    val map = parse()
    val air = mutableSetOf<Point3>()

    val range = (0..20)
    range.forEach { x ->
        range.forEach { y ->
            range.forEach { z ->
                air.add(p(x,y,z))
            }
        }
    }
    air.removeAll(map)

    var sidesRemoved = 0
    while (air.isNotEmpty()) {
        val currentSpace = mutableSetOf(air.first())
        while (true) {
            currentSpace.flatMap { it.getNeighbors() }
                .filterTo(currentSpace) { it !in currentSpace && it !in map }

            if (currentSpace.any { it !in air }) break

            if (currentSpace.flatMap { it.getNeighbors() }.all { it in currentSpace || it in map }) {
                val sides = currentSpace.sumOf { it.getNeighbors().count { n -> map.contains(n) } }
                println("Found closed space with ${currentSpace.size} cubes and $sides sides")
                sidesRemoved += sides
                break
            }
        }
        air.removeAll(currentSpace)
    }
    return partOne() - sidesRemoved
}

fun main() {
    println("Part one: ${partOne()}")
    println("Part two: ${partTwo()}")
}