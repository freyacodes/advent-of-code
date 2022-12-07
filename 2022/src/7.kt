private sealed interface Entry {
    val size: Long
}

private class File(override val size: Long) : Entry
private class Directory : Entry {
    val children = mutableMapOf<String, Entry>()
    override val size get() = children.values.sumOf { it.size }
    fun getAllChildren(): List<Entry> = children.flatMap { (_, e) ->
        if (e is Directory) return@flatMap e.getAllChildren() + e
        else listOf(e)
    }
}

private val root = Directory()

private fun parse(cwd: Directory, remaining: MutableList<String>) {
    while (remaining.isNotEmpty()) {
        val next = remaining.removeFirst()
        when {
            next == "$ cd .." -> return
            next == "$ cd /" -> parse(root, remaining)
            next.startsWith("$ cd ") -> {
                parse(cwd.children[next.drop(5)] as Directory, remaining)
            }
            next == "$ ls" -> {}
            next.first().isDigit() -> {
                val (size, name) = next.split(" ")
                cwd.children[name] = File(size.toLong())
            }
            next.startsWith("dir ") -> {
                cwd.children[next.drop(4)] = Directory()
            }
            else -> error("Can't parse $next")
        }

    }
}

fun main() {
    parse(root, getInput(7).toMutableList())
    val directories = root.getAllChildren().filterIsInstance<Directory>()
    println("Part one = " + directories.filter { it.size <= 100000 }.sumOf { it.size })
    val available = 70000000 - root.size
    val requiredSpace = 30000000 - available
    println("$requiredSpace space required")
    val deleted = directories.filter { it.size >= requiredSpace }.minOf { it.size }
    println("Part two = $deleted")

}