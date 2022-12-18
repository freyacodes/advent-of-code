package y2022

import java.util.LinkedList

private fun parse(): Map<String, Node2> {
    val pattern = "Valve (\\w\\w) has flow rate=(\\d\\d?); tunnels? leads? to valves? (.+)".toPattern()
    val map = mutableMapOf<String, Node2>()
    val visits = mutableMapOf<String, Int>()
    getInput(16).forEach { line ->
        val m = pattern.matcher(line)
        m.find()
        val connections = m.group(3).split(", ").toSortedSet()
        val n = Node2(
            map,
            m.group(1),
            30,
            connections,
            m.group(2).toInt(),
            emptySet(),
            0,
            visits
        )
        map[n.name] = n
    }
    map.values.forEach { visits[it.name] = if (it.flowRate == 0) 1 else 0 }
    return map
}

private class Node2(
    val map: Map<String, Node2>,
    val name: String,
    val minutesLeft: Int,
    val connections: Set<String>,
    val flowRate: Int,
    val opened: Set<String>,
    val released: Int,
    val visitCounts: Map<String, Int>
) {
    var parent: Node2? = null

    override fun toString(): String {
        return "Node2(name='$name', minutesLeft=$minutesLeft, released=$released)"
    }

    fun getChildren(): List<Node2> {
        val list = mutableListOf<Node2>()

        /*when {
            minutesLeft == 25 && released < 500 -> return list
            minutesLeft == 20 && released < 1000 -> return list
            minutesLeft == 15 && released < 1200 -> return list
            minutesLeft == 10 && released < 1400 -> return list
            minutesLeft == 5 && released < 1500 -> return list
            minutesLeft == 2 && released < 1600 -> return list
            minutesLeft == 0 -> return list
        }*/

        when {
            minutesLeft == 25 && released < 500-100 -> return list
            minutesLeft == 20 && released < 1000-100 -> return list
            minutesLeft == 15 && released < 1200-200 -> return list
            minutesLeft == 10 && released < 1400-200 -> return list
            minutesLeft == 5 && released < 1500-200 -> return list
            minutesLeft == 2 && released < 1600-200 -> return list
            minutesLeft == 0 -> return list
        }

        if (name !in opened && flowRate != 0) {
            list.add(getConnection(name))
        }

        connections.forEach {
            if (visitCounts[it]!! < 4) list.add(getConnection(it))
        }

        return list
    }

    private fun getConnection(theirName: String): Node2 {
        val them = map[theirName]!!
        val isRelease = name == theirName
        return Node2(
            map,
            theirName,
            minutesLeft - 1,
            them.connections,
            them.flowRate,
            if (isRelease) opened.toSortedSet().apply { add(name) } else opened,
            if (isRelease) released + flowRate * (minutesLeft - 1) else released,
            visitCounts.toMutableMap().also { it[theirName] = visitCounts[theirName]!! + 1 }
        )//.also { it.parent = this@Node2 }
    }
}

private fun search(start: Node2): Node2 {
    lateinit var best: Node2
    var bestScore = 0

    var startingPoints = start.getChildren()
    repeat(18) {
        startingPoints = startingPoints.flatMap { it.getChildren() }
    }
    println("Starting with ${startingPoints.size} staring points")
    var n = 0
    startingPoints.forEachIndexed { i, origin ->
        val queue = LinkedList<Node2>()
        queue.add(origin)
        while (queue.isNotEmpty()) {
            n++
            if (n % 1000000 == 0) {
                val mid = /*queue.size.toString() + */("+" + (startingPoints.size - i)).toString()
                println(
                    "${n.toString().padEnd(10)} ${mid.padEnd(15)} ${Runtime.getRuntime().freeMemory()}"
                )
            }
            val node = queue.removeFirst()
            val children = node.getChildren()
            if (children.isEmpty()) {
                if (node.released <= bestScore) continue
                bestScore = node.released
                best = node
                println("New best score: $bestScore")
                continue
            }
            queue.addAll(children)
        }
    }

    return best
}

fun main() {
    val start = parse()["AA"]!!
    println(search(start))
}