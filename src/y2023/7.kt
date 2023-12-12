package y2023

private val cardLabels = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
private val cardLabelsTwo = cardLabels.toMutableList().apply {
    remove('J')
    add('J')
}
private val cardStrengthsOne = cardLabels
    .asReversed()
    .mapIndexed { i, c -> c to i }
    .associate { it }

private val cardStrengthsTwo = cardLabelsTwo
    .asReversed()
    .mapIndexed { i, c -> c to i }
    .associate { it }

private data class Hand(val cards: String, val bid: Int) {
    var strengthOne = resolve(cards) + tiebreaker(cards, cardStrengthsOne)
    var strengthTwo = resolveJoker(cards) + tiebreaker(cards, cardStrengthsTwo)
}

fun resolveJoker(cards: String): Double {
    return cardLabels.maxOf { replacing ->
        resolve(cards.replace('J', replacing))
    }
}

fun resolve(cards: String): Double {
    val cardCount = cards.groupBy { it }
        .map { it.key to it.value.size }
        .sortedByDescending { it.second }

    val type = when (cardCount.first().second) {
        5 -> 6
        4 -> 5
        3 -> if (cardCount[1].second == 2) 4 else 3
        2 -> if (cardCount[1].second == 2) 2 else 1
        1 -> 0
        else -> error("??")
    }

    return type * 10000000.0
}

fun tiebreaker(cards: String, cardStrengths: Map<Char, Int>): Double {
    return cards.reversed().fold(0.0) { acc, c ->
        acc / 200 + cardStrengths[c]!! * 100
    }
}

private fun parse(): List<Hand> {
    return getInput(7).map {
        val (cards, bid) = it.split(" ")
        Hand(cards, bid.toInt())
    }
}

private fun partOne(): Long {
    return parse()
        .sortedBy { it.strengthOne }
        .withIndex()
        .sumOf {
            println("${it.value.cards}: ${it.index.inc()} * ${it.value.bid} + ${it.value.strengthOne}")
            it.index.inc().toLong() * it.value.bid
        }
}

private fun partTwo(): Long {
    return parse()
        .sortedBy { it.strengthTwo }
        .withIndex()
        .sumOf {
            println("${it.value.cards}: ${it.index.inc()} * ${it.value.bid} + ${it.value.strengthTwo}")
            it.index.inc().toLong() * it.value.bid
        }
}

fun main() {
    println("Part one: ${partOne()}")
    println("Part two: ${partTwo()}")
}