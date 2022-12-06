private fun parseDeck(string: String): ArrayDeque<Int> {
    return ArrayDeque(string.lines().drop(1).map { it.toInt() })
}

private val ArrayDeque<Int>.score: Int get() {
    var i = 0
    return reversed().sumOf {
        i++
        it * i
    }
}

private fun partOne() {
    println("Part 1")
    val (player1, player2) = getInputString(22, 2020).split("\n\n").map(::parseDeck)

    while (player1.isNotEmpty() && player2.isNotEmpty()) {
        val hand1 = player1.removeFirst()
        val hand2 = player2.removeFirst()
        if (hand1 > hand2) {
            player1.addLast(hand1)
            player1.addLast(hand2)
        } else {
            player2.addLast(hand2)
            player2.addLast(hand1)
        }
    }


    if (player1.isNotEmpty()) println("Player 1 score = " + player1.score)
    else println("Player 2 score = " + player2.score)
    println("----")
}

private var gameCounter = 1

private fun game(player1: ArrayDeque<Int>, player2: ArrayDeque<Int>): Pair<Int, Int> {
    val gameCount = ++gameCounter
    println("Starting game $gameCount")
    val player1Decks = mutableListOf<List<Int>>()
    val player2Decks = mutableListOf<List<Int>>()

    while (player1.isNotEmpty() && player2.isNotEmpty()) {
        if (player1Decks.contains(player1.toList())) return 1 to player1.score
        if (player2Decks.contains(player1.toList())) return 1 to player1.score

        player1Decks.add(player1.toList())
        player2Decks.add(player2.toList())

        val hand1 = player1.removeFirst()
        val hand2 = player2.removeFirst()

        val winner = when {
            player1.size >= hand1 && player2.size >= hand2 -> {
                game(ArrayDeque(player1.toList().take(hand1)), ArrayDeque(player2.toList().take(hand2))).first
            }
            hand1 > hand2 -> 1
            else -> 2
        }

        if (winner == 1) {
            player1.addLast(hand1)
            player1.addLast(hand2)
        } else {
            player2.addLast(hand2)
            player2.addLast(hand1)
        }
    }

    return if (player1.isNotEmpty()) {
        println("Player 1 wins $gameCount")
        1 to player1.score
    } else {
        println("Player 2 wins $gameCount")
        2 to player2.score
    }
}

private fun partTwo() {
    println("Part 2")
    val (player1, player2) = getInputString(22, 2020).split("\n\n").map(::parseDeck)
    val (player, score) = game(player1, player2)
    println("Player $player wins with score $score")
}

fun main() {
    partOne()
    partTwo()
}