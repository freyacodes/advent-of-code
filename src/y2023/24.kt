package y2023

private data class Point3Double(val x: Double, val y: Double, val z: Double)
private data class Point2Double(val x: Double, val y: Double)
private data class Hail(val position: Point3Double, val velocity: Point3Double) {
    val simplified by lazy {
        copy(
            position = position.copy(z = 0.0),
            velocity = velocity.copy(z = 0.0)
        )
    }

    override fun toString(): String {
        return "${position.x} ${position.y} ${position.z} @ ${velocity.x} ${velocity.y} ${velocity.z}"
    }
}

// https://www.omnicalculator.com/math/intersection-of-two-lines
// https://math.libretexts.org/Courses/Palo_Alto_College/College_Algebra/05%3A_Systems_of_Equations_and_Inequalities/5.04%3A_Solving_Systems_with_Gaussian_Elimination
// https://bvanderlei.github.io/jupyter-guide-to-linear-algebra/Gaussian_Elimination.html

private fun parse(): List<Hail> {
    val regex = "-?\\d+".toPattern()
    return getInput(24).map { line ->
        val sequence = regex.matcher(line).results().map { it.group().toDouble() }.iterator()
        Hail(
            Point3Double(sequence.next(), sequence.next(), sequence.next()),
            Point3Double(sequence.next(), sequence.next(), sequence.next())
        )
    }
}

private fun findIntersection(hailA: Hail, hailB: Hail): Pair<Point3Double, Pair<Double, Double>> {
    val matrix = mutableListOf(
        listOf(hailA.velocity.x, hailB.velocity.x, hailA.position.x - hailB.position.x),
        listOf(hailA.velocity.y, hailB.velocity.y, hailA.position.y - hailB.position.y),
        listOf(hailA.velocity.z, hailB.velocity.z, hailA.position.z - hailB.position.z)
    )
    //println(matrix)
    //println()
    gaussianElimination(matrix)
    //println(matrix)

    val s = matrix[1][2]
    val t = -(matrix[0][2] - s * matrix[0][1]) / matrix[0][0]
    val intersection = Point3Double(
        hailB.position.x + hailB.velocity.x * s,
        hailB.position.y + hailB.velocity.y * s,
        hailB.position.z + hailB.velocity.z * s
    )
    val intersection2 = Point3Double(
        hailA.position.x + hailA.velocity.x * t,
        hailA.position.y + hailA.velocity.y * t,
        hailA.position.z + hailA.velocity.z * t
    )
    return intersection to (s to t)
}

private fun gaussianElimination(matrix: MutableList<List<Double>>) {
    fun rowSwap(matrix: MutableList<List<Double>>, i1: Int, i2: Int) {
        val temp = matrix[i1]
        matrix[i1] = matrix[i2]
        matrix[i2] = temp
    }

    fun rowScale(matrix: MutableList<List<Double>>, i: Int, factor: Double) {
        matrix[i] = matrix[i].map { it * factor }
    }

    fun rowAdd(matrix: MutableList<List<Double>>, iFrom: Int, iTo: Int, factor: Double) {
        matrix[iTo] = matrix[iFrom].zip(matrix[iTo]).map { (from, to) -> from * factor + to }
    }


    val m = matrix.size
    val n = matrix.first().size

    // For each step of elimination, we find a suitable pivot, move it into
    // position and create zeros for all entries below.

    repeat(m) { k ->
        // Set pivot as (k,k) entry
        var pivot = matrix[k][k]
        var pivotRow = k

        while (pivot == 0.0 && pivotRow < m - 1) {
            pivotRow++
            pivot = matrix[pivotRow][k]
        }

        // Swap row if needed
        if (pivotRow != k) {
            rowSwap(matrix, k, pivotRow)
        }

        // If pivot is nonzero, carry on with elimination in column k
        if (pivot != 0.0) {
            rowScale(matrix, k, 1.0 / matrix[k][k])
            for (i in (k.inc()..<m)) {
                rowAdd(matrix, k, i, -matrix[i][k])
            }
        }
    }
}

private fun <T> pairAll(list: List<T>): List<Pair<T, T>> {
    val newList = mutableListOf<Pair<T, T>>()
    val workingList = list.toMutableList()
    while (workingList.isNotEmpty()) {
        val next = workingList.removeFirst()
        workingList.mapTo(newList) { next to it }
    }
    return newList
}

val testArea = 200000000000000.0..400000000000000.0
//val testArea = 7.0..27.0

private fun partOne(): Int {
    return pairAll(parse().map { it.simplified }).count { (a, b) ->
        val (intersection, steps) = findIntersection(a, b)
        val isInTestArea = intersection.x in testArea && intersection.y in testArea
        val isInFuture = steps.first > 0 && steps.second > 0

        isInTestArea && isInFuture
    }
}

fun main() {
    println("Part one: ${partOne()}")
}