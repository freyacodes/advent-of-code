import java.lang.StringBuilder

private lateinit var pieces: List<PuzzlePiece>
private val bySides = mutableMapOf<String, MutableList<PuzzlePiece>>()
private val solution = mutableMapOf<Pair<Int, Int>, PuzzlePiece>()

private val top = "..................#.".toPattern()
private val middle = "#....##....##....###".toPattern()
private val bottom = ".#..#..#..#..#..#...".toPattern()
private const val dragonFactor = 15

fun main() {
    pieces = getInputString(20, 2020).split("\n\n").map { puzzleString ->
        val lines = puzzleString.lines().toMutableList()
        val id = lines.removeFirst().drop(5).take(4).toLong()
        val bodyLines = lines.map { it.toCharArray() }.toTypedArray()
        PuzzlePiece(id, bodyLines)
    }
    println("Parsed ${pieces.size} pieces")

    val sides = pieces.flatMap { p -> (p.sides + p.sides.map { it.reversed() }).map { p to it } }
    sides.forEach { (pp, s) -> bySides.compute(s) { _, v -> v?.apply { add(pp) } ?: mutableListOf(pp) } }

    val firstPiece = pieces.first() { p -> p.sides.count { isEdgeSide(it) } == 2 }
    solution[0 to 0] = firstPiece.orientations().first() { isEdgeSide(it.top) && isEdgeSide(it.left) }
    repeat(12) {
        solveRow(it)
    }

    // Can also do by scanning the corner pieces
    println("Part 1: " + solution[0 to 0]!!.id * solution[11 to 0]!!.id * solution[0 to 11]!!.id * solution[11 to 11]!!.id)

    val stitched = (0..11).joinToString("\n") { y ->
        /*"y = $y:\n" + */mergeLines((11 downTo 0)
        .map { x ->
            //listOf("x = $x:".padEnd(10)) +
            solution[x to y]!!
                .truncate()
                .body.map { it.joinToString("") }
        })
    }

    //val rotatedPuzzle = PuzzlePiece(0, stitched.split("\n").map { it.toCharArray() }.toTypedArray()).orientations().drop(5).first()

    val dragons =
        PuzzlePiece(0, stitched.split("\n").map { it.toCharArray() }.toTypedArray()).orientations().maxOf { pp ->
            findDragons2(pp.body.map { it.concatToString() })
        }

    val squares = stitched.count { it == '#' }
    println("Part 2: $squares - $dragons * $dragonFactor = " + (squares - dragons * dragonFactor))
}

private fun solveRow(y: Int) {
    var x = if (y == 0) 1 else 0
    var leftPiece = solution[x - 1 to y]
    var topPiece: PuzzlePiece?

    while (leftPiece == null || !isEdgeSide(leftPiece.right)) {
        leftPiece = solution[x - 1 to y]
        topPiece = solution[x to y - 1]

        var newPiece = if (leftPiece == null) {
            bySides[topPiece!!.bottom]!!
                .toMutableList().single { it.id != topPiece.id }
        } else {
            bySides[leftPiece.right]!!
                .toMutableList().single { it.id != leftPiece.id }
        }

        newPiece = newPiece.orientations().first {
            val leftPass = it.left == leftPiece?.right || (leftPiece == null && isEdgeSide(it.left))
            val topPass = it.top == topPiece?.bottom || (topPiece == null && isEdgeSide(it.top))
            topPass && leftPass
        }

        solution[x to y] = newPiece
        println("Found solution for $x $y")
        if (isEdgeSide(newPiece.right)) {
            println("Completed row $y")
            return
        }
        x++
    }
}

private data class PuzzlePiece(val id: Long, @Suppress("ArrayInDataClass") val body: Array<CharArray>) {
    val top: String get() = body.first().concatToString()
    val right: String get() = body.joinToString("") { it.first().toString() }
    val bottom: String get() = body.last().concatToString()
    val left: String get() = body.joinToString("") { it.last().toString() }
    val sides: List<String> = listOf(top, right, bottom, left)

    fun rotate(): PuzzlePiece {
        val new = body.mapIndexed { x, row ->
            row.mapIndexed { y, _ ->
                body[y][x]
            }.reversed().toCharArray()
        }.toTypedArray()
        return copy(body = new)
    }

    fun flip() = copy(
        body = body.reversedArray()
    )

    fun orientations(): Sequence<PuzzlePiece> = sequence {
        var current = this@PuzzlePiece
        repeat(2) {
            repeat(4) {
                current = current.rotate()
                yield(current)
            }
            current = current.flip()
        }
    }

    fun truncate(): PuzzlePiece {
        return copy(body = body.drop(1)
            .dropLast(1)
            .map { it.drop(1).dropLast(1).toCharArray() }
            .toTypedArray())
    }

    override fun toString() = "[Piece $id: $sides]"
}

private fun mergeLines(lineSets: List<List<String>>): String {
    val newLines = lineSets.first().map { StringBuilder() }
    lineSets.forEach { block ->
        block.forEachIndexed { i, s -> newLines[i].append(s) }
    }
    return newLines.joinToString("\n") { it.toString() }
}

private fun isEdgeSide(side: String) = bySides[side]!!.size == 1

private fun findDragons2(lines: List<String>): Int {
    var dragons = 0
    lines.forEachIndexed { y, s ->
        middle.matcher(s).results().forEach { m ->
            val sub2 = lines.getOrNull(y - 1)?.subSequence(m.start() until m.end()) ?: return@forEach
            println(sub2)
            println(m.group(0))
            val m2 = top.matcher(sub2)
            if (!m2.find()) {
                println("Rejected\n")
                return@forEach
            }
            val sub3 = lines.getOrNull(y + 1)?.subSequence(m.start() until m.end()) ?: return@forEach
            val m3 = bottom.matcher(sub3)
            println(sub3)
            if (!m3.find()) {
                println("Rejected\n")
                return@forEach
            }
            println("Here be dragons!\n")
            dragons++
        }
    }

    println("Found $dragons dragons")
    return dragons
}
