package y2023

//private var iteration = 0
private data class PartValues(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int
) {

    operator fun get(c: Char) = when (c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("Panic! $c")
    }

    val sum = x + m + a + s
}

private data class PartSequences(
    val x: List<Int>,
    val m: List<Int>,
    val a: List<Int>,
    val s: List<Int>,
    val visited: Set<Condition> = emptySet()
) {
    //val i = iteration++
    operator fun get(c: Char) = when (c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("Panic! $c")
    }

    fun copy(c: Char, seq: List<Int>, condition: Condition): PartSequences {
        return when (c) {
            'x' -> copy(x = seq, visited = visited + condition)
            'm' -> copy(m = seq, visited = visited + condition)
            'a' -> copy(a = seq, visited = visited + condition)
            's' -> copy(s = seq, visited = visited + condition)
            else -> error("Panic!")
        }
    }

    fun product() = x.count().toLong() * m.count().toLong() * a.count().toLong() * s.count().toLong()
}

private data class Workflow(val name: String, val conditions: List<Condition>, val elseThen: String)

private data class Condition(val target: Char, val lessThan: Boolean, val threshold: Int, val ifTrue: String) {
    fun check(part: PartValues): String? {
        val value = part[target]
        val result = if (lessThan) value < threshold
        else value > threshold
        return if (result) ifTrue else null
    }

    fun check(parts: PartSequences): Pair<PartSequences, PartSequences> {
        //println(parts.i)
        val original = parts[target]
        fun condition(n: Int) = if (lessThan) n < threshold else n > threshold
        val trues = original.filter { condition(it) }
        val falses = original.filter { !condition(it) }
        return parts.copy(target, trues, this) to parts.copy(target, falses, this)
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

private var i = 0
private fun resolve2(workflows: Map<String, Workflow>, workflowName: String, input: PartSequences): Long {
    i++
    if (input.product() == 0L) return 0
    val workflow: Workflow
    when (workflowName) {
        "A" -> return input.product()
        "R" -> return 0
        else -> workflow = workflows[workflowName]!!
    }

    if (workflow.conditions.first() in input.visited) return input.product()

    var sum = 0L
    var nextInput = input
    workflow.conditions.forEach { condition ->
        val (trues, falses) = condition.check(nextInput)
        nextInput = falses
        sum += resolve2(workflows, condition.ifTrue, trues)
    }

    return sum + resolve2(workflows, workflow.elseThen, nextInput)
}

private fun partOneAlt(): List<Long> {
    val (workflows, inputs) = parse()
    return inputs.map {
        it.run {
            resolve2(workflows, "in", PartSequences((x..x).toList(), (m..m).toList(), (a..a).toList(), (s..s).toList()))
        }
    }
}

private fun partTwo(): Long {
    val (workflows, _) = parse()
    val initial = PartSequences((1..4000).toList(), (1..4000).toList(),
        (1..4000).toList(), (1..4000).toList())

    return resolve2(workflows, "in", initial)
}

fun main() {
    println("Part one: " + partOne())
    println("Part two: " + partTwo())
}