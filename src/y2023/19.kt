package y2023

private data class PartValues(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int
) {
    operator fun get(c: Char) = when(c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("Panic! $c")
    }

    val sum = x+m+a+s
}

private data class Workflow(val name: String, val conditions: List<Condition>, val elseThen: String)

private data class Condition(val target: Char, val lessThan: Boolean, val threshold: Int, val ifTrue: String) {
    fun check(part: PartValues): String? {
        val value = part[target]
        val result = if (lessThan) value < threshold
        else value > threshold
        return if (result) ifTrue else null
    }
}

private fun parse(): Pair<Map<String, Workflow>, List<PartValues>> {
    val raw = getInputString(19)
    val (workflowLines, partLines) = raw.split("\n\n").map { it.lines() }

    val workflows = workflowLines.map { line ->
        val name = line.takeWhile { it.isLetter() }
        val fragments = line.drop(name.length + 1).dropLast(1).split(',').toMutableList()
        val elseThen = fragments.removeLast()
        val conditions = fragments.map { f ->
            Condition(
                f[0],
                f[1] == '<',
                f.filter { it.isDigit() }.toInt(),
                f.takeLastWhile { it.isLetter() }
            )
        }
        Workflow(name, conditions, elseThen)
    }.associateBy { it.name }

    val parts = partLines.map { line ->
        val seq = "\\d+".toRegex().findAll(line).map { it.value.toInt() }.iterator()
        PartValues(seq.next(), seq.next(), seq.next(), seq.next())
    }

    return workflows to parts
}

private fun resolve(workflow: Workflow, part: PartValues): String {
    workflow.conditions.forEach { condition ->
        val result = condition.check(part)
        if (result != null) return result
    }
    return workflow.elseThen
}

private fun partOne(): Int {
    val (workflows, parts) = parse()

    return parts.filter { part ->
        println(part)
        var nextWorkflow = "in"
        while (nextWorkflow.length != 1) {
            nextWorkflow = resolve(workflows[nextWorkflow]!!, part)
            println("-> $nextWorkflow")
        }
        nextWorkflow == "A"
    }.sumOf { it.sum }
}

fun main() {
    println("Part one: " + partOne())
}