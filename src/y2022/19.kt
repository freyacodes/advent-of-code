package y2022

import kotlin.math.max

private data class Blueprint(
    val id: Int,
    val oreBotOre: Int,
    val clayBotOre: Int,
    val obsidianBotOre: Int,
    val obsidianBotClay: Int,
    val geodeBotOre: Int,
    val geodeBotObsidian: Int
) {
    val maxOreCost = oreBotOre.coerceAtLeast(clayBotOre).coerceAtLeast(obsidianBotOre).coerceAtLeast(geodeBotOre)
}

private fun parse(): List<Blueprint> {
    val regex = "\\d+".toRegex()
    return getInput(19).map { str ->
        val seq = regex.findAll(str).map { it.value.toInt() }.toMutableList()
        Blueprint(
            seq.removeAt(0),
            seq.removeAt(0),
            seq.removeAt(0),
            seq.removeAt(0),
            seq.removeAt(0),
            seq.removeAt(0),
            seq.removeAt(0)
        )
    }
}

private data class Node3(
    val blueprint: Blueprint,
    val timeLeft: Int,
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geodes: Int,
    val botOre: Int,
    val botClay: Int,
    val botObsidian: Int,
    val botGeode: Int
) {
    fun findChildren() = buildList {
        if (botOre < blueprint.maxOreCost) {
            addNotNull(buildOreBot((blueprint.oreBotOre - ore).ceilDiv(botOre) + 1))
        }
        if (botClay < blueprint.obsidianBotClay) {
            addNotNull(buildClayBot((blueprint.clayBotOre - ore).ceilDiv(botOre) + 1))
        }
        if (0 < botClay && botObsidian < blueprint.geodeBotObsidian) {
            val oreTime = (blueprint.obsidianBotOre - ore).ceilDiv(botOre) + 1
            val clayTime = (blueprint.obsidianBotClay - clay).ceilDiv(botClay) + 1
            addNotNull(buildObsidianBot(max(oreTime, clayTime)))
        }
        if (0 < botObsidian) {
            val oreTime = (blueprint.geodeBotOre - ore).ceilDiv(botOre) + 1
            val obsidianTime = (blueprint.geodeBotObsidian - obsidian).ceilDiv(botObsidian) + 1
            val remaining = 24 - timeLeft
            addNotNull(buildGeodeBot(max(oreTime, obsidianTime)))
        }
        1.floorDiv(2)
        if (isEmpty()) {
            add(
                copy(
                    timeLeft = 0,
                    ore = ore + botOre * timeLeft,
                    clay = clay + botClay * timeLeft,
                    obsidian = obsidian + botObsidian * timeLeft,
                    geodes = geodes + botGeode * timeLeft
                )
            )
        }
    }

    fun buildOreBot(resourceTime: Int): Node3? {
        val timeDelta = resourceTime.coerceAtLeast(1)
        val newTime = timeLeft - timeDelta
        if (newTime < 2) return null
        return copy(
            timeLeft = newTime,
            ore = ore + botOre * timeDelta - blueprint.oreBotOre,
            clay = clay + botClay * timeDelta,
            obsidian = obsidian + botObsidian * timeDelta,
            geodes = geodes + botGeode * timeDelta,
            botOre = botOre + 1
        )
    }

    fun buildClayBot(resourceTime: Int): Node3? {
        val timeDelta = resourceTime.coerceAtLeast(1)
        val newTime = timeLeft - timeDelta
        if (newTime < 2) return null
        return copy(
            timeLeft = newTime,
            ore = ore + botOre * timeDelta - blueprint.clayBotOre,
            clay = clay + botClay * timeDelta,
            obsidian = obsidian + botObsidian * timeDelta,
            geodes = geodes + botGeode * timeDelta,
            botClay = botClay + 1
        )
    }

    fun buildObsidianBot(resourceTime: Int): Node3? {
        val timeDelta = resourceTime.coerceAtLeast(1)
        val newTime = timeLeft - timeDelta
        if (newTime < 2) return null
        return copy(
            timeLeft = newTime,
            ore = ore + botOre * timeDelta - blueprint.obsidianBotOre,
            clay = clay + botClay * timeDelta - blueprint.obsidianBotClay,
            obsidian = obsidian + botObsidian * timeDelta,
            geodes = geodes + botGeode * timeDelta,
            botObsidian = botObsidian + 1
        )
    }

    fun buildGeodeBot(resourceTime: Int): Node3? {
        val timeDelta = resourceTime.coerceAtLeast(1)
        val newTime = timeLeft - timeDelta
        if (newTime < 1) return null
        return copy(
            timeLeft = newTime,
            ore = ore + botOre * timeDelta - blueprint.geodeBotOre,
            clay = clay + botClay * timeDelta,
            obsidian = obsidian + botObsidian * timeDelta - blueprint.geodeBotObsidian,
            geodes = geodes + botGeode * timeDelta,
            botGeode = botGeode + 1
        )
    }
}

private object NodeComparator : Comparator<Pair<Int, Node3>> {
    override fun compare(o1: Pair<Int, Node3>, o2: Pair<Int, Node3>) = o1.first - o2.first
}

private fun dfs(node: Node3): Pair<Int, Node3> {
    if (node.timeLeft == 0) return node.geodes to node
    return node.findChildren()
        .map { it.geodes to it }
        .maxOfWith(NodeComparator) {
            dfs(it.second)
        }
}

private fun partOne(): Int {
    val blueprints = parse()
    return blueprints.sumOf {
        val n = dfs(Node3(it, 24, 0, 0, 0, 0, 1, 0, 0, 0)).second
        println("Blueprint ${it.id}, geodes ${n.geodes}, quality ${n.geodes * it.id}")
        return@sumOf n.geodes * it.id
    }
}

private fun partTwo(): Int {
    val blueprints = parse().take(3)
    return blueprints.map {
        val n = dfs(Node3(it, 32, 0, 0, 0, 0, 1, 0, 0, 0)).second
        println("Blueprint ${it.id}, geodes ${n.geodes}")
        n.geodes
    }.fold(1) { a, b -> a*b}
}


fun main() {
    println("Day one: ${partOne()}")
    println("Day two: ${partTwo()}")
}