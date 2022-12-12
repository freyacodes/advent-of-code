package y2020

private class Ring(values: List<Int>) {
    var current: Node
    val map: Map<Int, Node>

    init {
        val nodes = values.map { Node(it) }
        map = nodes.asSequence().plus(nodes.first())
            .windowed(2)
            .onEach { (before, after) ->
                before.next = after
                after.previous = before
            }.flatten()
            .associateBy { it.value }
        current = map[values.first()]!!
    }

    fun list(): List<Node> {
        val list = mutableListOf<Node>()
        var next = current
        while (!list.contains(next)) {
            list.add(next)
            next = next.next
        }
        return list
    }

    override fun toString(): String {
        val swap = current
        current = map[3]!!
        val list = list()
        current = swap
        return list.joinToString(" ") { if (it == current) "($current)" else it.value.toString() }
    }
}

private class Node(val value: Int) {
    lateinit var next: Node
    lateinit var previous: Node
    override fun toString() = value.toString()

    fun remove(): Node {
        previous.next = next
        next.previous = previous
        return this
    }

    fun append(node: Node) {
        node.next = next
        node.next.previous = node
        node.previous = this
        next = node
    }
}

private fun partOne() {
    val ring = Ring("193467258".map { it.digitToInt() }.toMutableList())

    repeat(100) {
        val removed = listOf(ring.current.next.remove(), ring.current.next.remove(), ring.current.next.remove())
        var destNum = ring.current.value - 1
        if (destNum < 1) destNum = 9
        while (removed.any { it.value == destNum }) {
            destNum--
            if (destNum < 1) destNum = 9
        }
        val destination = ring.map[destNum]!!
        removed.reversed().forEach { destination.append(it) }
        ring.current = ring.current.next
    }

    ring.current = ring.map[1]!!
    println("Part 1 = " + ring.list().drop(1).joinToString(""))
}

private fun partTwo() {
    val ring = Ring("193467258".map { it.digitToInt() }.toMutableList() + (10..1_000_000).toList())

    repeat(10_000_000) { i ->
        if (i % (10_000) - 1 == 0) print(".")
        if (i % 1_000_000 == 0) println()
        val removed = listOf(ring.current.next.remove(), ring.current.next.remove(), ring.current.next.remove())
        var destNum = ring.current.value - 1
        if (destNum < 1) destNum = 1_000_000
        while (removed.any { it.value == destNum }) {
            destNum--
            if (destNum < 1) destNum = 1_000_000
        }
        val destination = ring.map[destNum]!!
        removed.reversed().forEach { destination.append(it) }
        ring.current = ring.current.next
    }
    val product = ring.map[1]!!.next.value.toLong() * ring.map[1]!!.next.next.value.toLong()
    println("\nPart two = $product")
}

fun main() {
    partOne()
    partTwo()
}
