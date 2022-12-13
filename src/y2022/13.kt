package y2022

import y2022.Order.*
import java.lang.StringBuilder

private sealed interface Element
private class L(val list: List<Element>) : Element {
    override fun toString() = list.toString()
}

private class I(val int: Int) : Element {
    override fun toString() = int.toString()
}

private fun parse(): List<Triple<L, L, Int>> {
    return getInputString(13).split("\n\n").mapIndexed { i, twoLines ->
        val (upper, lower) = twoLines.lines()
        Triple(parseList(upper.drop(1).dropLast(1)), parseList(lower.drop(1).dropLast(1)), i + 1)
    }
}

private fun parseList(string: String): L {
    val list = mutableListOf<Element>()

    val buffer = StringBuilder()
    var depth = 0
    string.forEach { c ->
        if (depth > 0) {
            when (c) {
                ']' -> depth--
                '[' -> depth++
            }

            if (depth == 0) {
                list.add(parseList(buffer.toString()))
                buffer.clear()
            } else {
                buffer.append(c)
            }
            return@forEach
        }
        if (!c.isDigit() && buffer.isNotEmpty()) {
            list.add(I(buffer.toString().toInt()))
            buffer.clear()
        }
        when {
            c.isDigit() -> buffer.append(c)
            c == '[' -> depth++
            c == ',' -> {}
            else -> error("Unexpected: $c")
        }
    }
    if (buffer.isNotEmpty()) list.add(I(buffer.toString().toInt()))

    return L(list)
}

private enum class Order {
    ORDERED,
    UNORDERED,
    INCONCLUSIVE
}

private fun checkOrder(left: Element, right: Element): Order {
    return when {
        left is I && right is I -> {
            when {
                left.int < right.int -> ORDERED
                left.int > right.int -> UNORDERED
                else -> INCONCLUSIVE
            }
        }

        left is L && right is L -> {
            left.list.zip(right.list).forEach { (l, r) ->
                val order = checkOrder(l, r)
                if (order != INCONCLUSIVE) return order
            }
            when {
                left.list.size < right.list.size -> ORDERED
                left.list.size > right.list.size -> UNORDERED
                else -> INCONCLUSIVE
            }
        }

        left is I && right is L -> checkOrder(L(listOf(left)), right)
        left is L && right is I -> checkOrder(left, L(listOf(right)))
        else -> error("No match")
    }
}

private fun partOne() {
    val score = parse().filter {
        val order = checkOrder(it.first, it.second)
        println("${it.third} $order")
        order == ORDERED
    }.sumOf { it.third }
    println("Day one: $score")
}

private fun partTwo() {
    val div1 = L(listOf(L(listOf(I(2)))))
    val div2 = L(listOf(L(listOf(I(6)))))

    val sorted = parse().map { Pair(it.first, it.second) }
        .flatMap { it.toList() }
        .toMutableList().apply {
            add(div1)
            add(div2)
        }.sortedWith { upper, lower ->
            when (checkOrder(upper, lower)) {
                ORDERED -> -1
                UNORDERED -> 1
                INCONCLUSIVE -> error("Should not happen")
            }
        }
    sorted.forEachIndexed { i, v ->
        println("${i.plus(1).toString().padEnd(3)} $v")
    }
    println("Day two: " + sorted.indexOf(div1).plus(1) * sorted.indexOf(div2).plus(1))
}

fun main() {
    partOne()
    partTwo()
}