fun main() {
    val input = mutableSetOf<Point4>()
    getInput(17, 2020).forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            if(c == '#') input.add(Point4(x, y, 0, 0))
        }
    }

    var current: Set<Point4> = input
    println(current.size)
    repeat(6) {
        current = doCycle(current)
        println(current.size)
    }
}

private fun doCycle(lastState: Set<Point4>): Set<Point4> {
    val newState = mutableSetOf<Point4>()
    val neighbors = getNeighborMap(lastState)

    neighbors.forEach { (p, n) ->
        val active = lastState.contains(p)
        if (active && n in (2..3)) newState.add(p)
        else if (!active && n == 3) newState.add(p)
    }

    return newState
}

private fun getNeighborMap(active: Set<Point4>): Map<Point4, Int> {
    val neighbors = mutableMapOf<Point4, Int>()
    active.forEach { origin ->
        getNeighborPoints(origin).forEach { neighbor ->
            neighbors.compute(neighbor) { _, i -> i?.inc() ?: 1 }
        }
    }
    return neighbors
}

private fun getNeighborPoints(origin: Point3): List<Point3> {
    val range = (-1..1)
    return range.flatMap { x ->
        range.flatMap { y ->
            range.mapNotNull { z ->
                if (x == 0 && y == 0 && z == 0) return@mapNotNull null
                Point3(origin.x + x, origin.y + y, origin.z + z)
            }
        }
    }
}

private fun getNeighborPoints(origin: Point4): List<Point4> {
    val range = (-1..1)
    return range.flatMap { x ->
        range.flatMap { y ->
            range.flatMap { z ->
                range.mapNotNull { w ->
                    if (x == 0 && y == 0 && z == 0 && w == 0) return@mapNotNull null
                    Point4(origin.x + x, origin.y + y, origin.z + z, origin.w + w)
                }
            }
        }
    }
}

private data class Point3(val x: Int, val y: Int, val z: Int)
private data class Point4(val x: Int, val y: Int, val z: Int, val w: Int)
