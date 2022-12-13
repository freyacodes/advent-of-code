package y2022

private fun parse(): MutableMap<Point2, Char> {
    val map = mutableMapOf<Point2, Char>()
    getInput(12).forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            map[p(x, y)] = c
        }
    }
    return map
}

private class Node(val point: Point2, val height: Char) {
    var parent: Node? = null
    override fun toString() = (point to height).toString()
    fun countChildren(runningCount: Int): Int {
        return parent?.countChildren(runningCount + 1) ?: runningCount
    }
}

private fun getEdges(node: Node, map: Map<Point2, Char>): List<Node> {
    return listOf(
        node.point + p(1, 0),
        node.point + p(-1, 0),
        node.point + p(0, 1),
        node.point + p(0, -1)
    ).mapNotNull { map[it]?.to(it) }
        .filter { it.first <= node.height + 1 }
        .map { Node(it.second, it.first) }
}

private fun bfs(start: Point2): Node? {
    val map = parse()
    val end = map.entries.first { it.value == 'E' }.key
    map[end] = 'z'
    map.replaceAll { _, c -> if (c == 'S') 'a' else c }

    val q = mutableListOf(Node(start, 'a'))
    val explored = mutableSetOf(start)

    while (q.isNotEmpty()) {
        val v = q.removeFirst()
        if (v.point == end) return v
        getEdges(v, map)
            .filter { !explored.contains(it.point) }
            .forEach {
                explored.add(it.point)
                it.parent = v
                q.add(it)
            }
    }
    return null
}

fun main() {
    val map = parse()
    val start = map.entries.first { it.value == 'S' }.key
    println("Part one: " + bfs(start)!!.countChildren(0))
    val partTwo = map.filter { it.value == 'a' }.minOf { (start, _) ->
        bfs(start)?.countChildren(0) ?: Int.MAX_VALUE
    }
    println("Part two: $partTwo")
}