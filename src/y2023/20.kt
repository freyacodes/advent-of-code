package y2023

import y2022.Point2
import y2022.p
import java.util.*

private sealed class PulseNode(val name: String, val outputNames: List<String>) {
    lateinit var outputs: List<PulseNode>
    abstract fun pulse(pulse: Pulse): List<Pulse>
}
private class FlipFlop(name: String, outputNames: List<String>) : PulseNode(name, outputNames) {
    var on = false
    override fun pulse(pulse: Pulse): List<Pulse> {
        if (pulse.high) return LinkedList()
        on = !on
        return outputs.map { Pulse(name, it, on) }
    }

    override fun toString() = "%$name"
}

private class Conjunction(name: String, outputNames: List<String>) : PulseNode(name, outputNames) {
    lateinit var inputs: MutableMap<String, Boolean>
    override fun pulse(pulse: Pulse): List<Pulse> {
        inputs[pulse.from] = pulse.high
        val allHigh = inputs.all { it.value }
        return outputs.map { Pulse(name, it, !allHigh) }
    }

    override fun toString() = "&$name"
}

private class DummyNode(name: String) : PulseNode(name, emptyList()) {
    override fun pulse(pulse: Pulse): List<Pulse> {
        // noop
        return emptyList()
    }
}

private data class Pulse(val from: String, val to: PulseNode, val high: Boolean)

private fun parse(): Pair<Map<String, PulseNode>, List<String>> {
    val lines = getInput(20).toMutableList()

    val broadcasterLine = lines.single { it.startsWith("broadcaster") }
    lines.remove(broadcasterLine)
    val broadcasterOutputs = broadcasterLine.drop(15).split(", ")

    val map = lines.map { line ->
        val isFlipFlop = line.first() == '%'
        val name = line.drop(1).takeWhile { it.isLetter() }
        val outputNames = line.split(" -> ").last().split(", ")
        if (isFlipFlop) FlipFlop(name, outputNames) else Conjunction(name, outputNames)
    }.associateBy { it.name }

    map.values.forEach { node ->
        println(node)
        node.outputs = node.outputNames.map { map[it] ?: DummyNode(it) }
        if (node is Conjunction) {
            node.inputs = map.values.filter { node.name in it.outputNames }
                .map { it.name }
                .associateWith { false }
                .toMutableMap()
                .apply {
                    if (node.name in broadcasterOutputs) this["broadcaster"] = false
                }
        }
    }

    return map to broadcasterOutputs
}

private fun pushButton(nodes: Map<String, PulseNode>, broadcastOutputs: List<String>): Pair<Point2, Boolean> {
    var lows = 1
    var highs = 0
    val remainingPulses = broadcastOutputs.map { Pulse("broadcaster", nodes[it]!!, false) }
        .toMutableList()
    var lowSentToRx = false

    while (remainingPulses.isNotEmpty()) {
        val pulse = remainingPulses.removeFirst()
        if (pulse.high) highs++ else lows++
        if (pulse.to.name == "rx" && !pulse.high) lowSentToRx = true
        remainingPulses.addAll(pulse.to.pulse(pulse))
    }

    return p(lows, highs) to lowSentToRx
}

private fun partOne(): Long {
    val input = parse()
    var sum = p(0,0)
    repeat(1000) {
        sum += pushButton(input.first, input.second).first
    }
    return sum.x.toLong() * sum.y.toLong()
}

private fun partTwo(): Int {
    val input = parse()
    repeat(1000000000) {
        if (pushButton(input.first, input.second).second) {
            return it + 1
        }
    }
    error("Panic!")
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}