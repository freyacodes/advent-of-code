package y2022

private fun parse(): List<Valve> {
    val pattern = "Valve (\\w\\w) has flow rate=(\\d\\d?); tunnels? leads? to valves? (.+)".toPattern()
    val visits = mutableMapOf<String, Int>()
    var valves = getInput(16).map { line ->
        val m = pattern.matcher(line)
        m.find()
        Valve(
            m.group(1),
            m.group(2).toInt(),
            m.group(3).split(", ")
        )
    }
    valves.forEach { visits[it.name] = if (it.flowRate == 0) 1 else 0 }

    valves.forEach { valve ->
        valve.connections.addAll(valve.directConnections.map { v -> valves.first { v == it.name } to 1 })
        while (valves.size - 1 != valve.connections.size) {
            valve.connections.addAll(valve.connections.flatMap { (v, dist) -> v.directConnections.map { it to dist } }
                .distinct()
                .mapNotNull { (name, dist) ->
                    if (valve.connections.any { it.first.name == name }) return@mapNotNull null
                    if (name == valve.name) return@mapNotNull null
                    valves.first { it.name == name } to dist + 1
                }
            )
        }
    }

    valves = valves.mapNotNull { valve ->
        if (valve.flowRate == 0 && valve.name != "AA") return@mapNotNull null
        valve.connections.removeIf { it.first.flowRate == 0 }
        valve
    }

    return valves
}

private class Valve(
    val name: String,
    val flowRate: Int,
    val directConnections: List<String>
) {
    val connections = mutableListOf<Pair<Valve, Int>>()
    var open = false
}

private fun findBestPath(valve: Valve, timeLeft: Int): Int =
    valve.connections.filter { (target, distance) -> !target.open && 0 < timeLeft - distance - 1 }
        .maxOfOrNull { (target, distance) ->
            val remaining = timeLeft - distance - 1
            target.open = true
            val released = remaining * target.flowRate + findBestPath(target, remaining)
            target.open = false
            released
        } ?: 0

private fun findBestPathWithFriend(valveMe: Valve, timeLeftMe: Int, valveFriend: Valve, timeLeftFriend: Int): Int =
    valveMe.connections.filter { (target, distance) -> !target.open && 0 < timeLeftMe - distance - 1 }
        .maxOfOrNull { (targetMe, distanceMe) ->
            val remainingMe = timeLeftMe - distanceMe - 1
            targetMe.open = true
            val releasedFriend = valveFriend.connections.filter { (targetFriend, distanceFriend) ->
                !targetFriend.open && 0 < timeLeftFriend - distanceFriend - 1
            }.maxOfOrNull { (targetFriend, distanceFriend) ->
                val remainingFriend = timeLeftFriend - distanceFriend - 1
                targetFriend.open = true
                val next = findBestPathWithFriend(targetMe, remainingMe, targetFriend, remainingFriend)
                targetFriend.open = false
                remainingFriend * targetFriend.flowRate + next
            } ?: 0
            targetMe.open = false
            targetMe.flowRate * remainingMe + releasedFriend
        } ?: 0

fun main() {
    println("Part one: " + findBestPath(parse().first { it.name == "AA" }, 30))
    val start = parse().first { it.name == "AA" }
    println("Part two: " + findBestPathWithFriend(start, 26, start, 26))
}
