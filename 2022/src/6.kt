fun search(n: Int): Int {
    getInput(6).first().windowed(n).forEachIndexed { i,s->
        if(s.toList().distinct().size == n) return i + n
    }
    error("End of file")
}

fun main() {
    println("Part one = " + search(4))
    println("Part two = " + search(14))
}