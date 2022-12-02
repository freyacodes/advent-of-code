fun main() {
    var score = 0
    getInput(2).lines().forEach { line ->
        val characters = line.split(" ")
        //val (them, us) = characters.map { fromLetter(it.first()) }
        val them = fromLetter(characters.first().first())
        val us = when(val targetOutcome = characters.last().first()) {
            'X' -> them.winsAgainst
            'Y' -> them
            'Z' -> them.losesAgainst
            else -> error("Unknown $targetOutcome")
        }

        val roundScore = us.playAgainst(them)
        score += roundScore
        println(line)
        println("${us.toString().padEnd(7)} ${them.toString().padEnd(7)} " + roundScore)
    }
    println(score)
}

fun fromLetter(letter: Char): Hand {
    return when (letter) {
        'A', 'X' -> Hand.ROCK
        'B', 'Y' -> Hand.PAPER
        'C', 'Z' -> Hand.SCISSOR
        else -> error("Invalid $letter")
    }
}

enum class Hand(
    private val value: Int,
    private val winsAgainstOrdinal: Int,
    private val losesAgainstOrdinal: Int
) {
    ROCK(1, 2, 1),
    PAPER(2, 0, 2),
    SCISSOR(3, 1, 0);

    val winsAgainst by lazy { Hand.values()[winsAgainstOrdinal] }
    val losesAgainst by lazy { Hand.values()[losesAgainstOrdinal] }

    fun playAgainst(other: Hand): Int {
        val winScore = when {
            this.winsAgainst == other -> 6
            this == other -> 3
            else -> 0
        }
        println("$value $winScore")
        return winScore + value
    }
}