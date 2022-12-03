import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder

fun main() {
    getInput(18, 2020)
        .sumOf {
            try {
                evaluateV2(tokenize(it)).value
            } catch (e: Exception) {
                throw RuntimeException("Problem with $it", e)
            }
        }
        .let { println(it) }
}

private fun evaluateV2(tokens: List<Token>) : Num {
    println("Evaluating $tokens")
    val newList = mutableListOf<Token>()
    val innerParenthesis = mutableListOf<Token>()
    var depth = 0
    tokens.forEach { t ->
        if (depth > 0) {
            if (t == ParenOpen) depth++
            if (t == ParenClose) depth--

            if (depth == 0) {
                newList.add(evaluateV2(innerParenthesis))
                innerParenthesis.clear()
            } else {
                innerParenthesis.add(t)
            }
        } else if (t == ParenOpen) depth++
        else newList.add(t)
    }
    return evaluateMultiplication(evaluateAddition(newList))
}

private fun evaluateAddition(tokens: List<Token>): List<Token> {
    val newList = mutableListOf<Token>()
    var lastToken: Token? = null
    tokens.forEach { t ->
        if (lastToken == Plus && t is Num) {
            newList.removeLast()
            val num = newList.removeLast() as Num
            newList.add(Num(num.value + t.value))
        } else {
            newList.add(t)
        }
        lastToken = t
    }
    return newList
}

private fun evaluateMultiplication(tokens: List<Token>) = tokens.mapNotNull { (it as? Num)?.value }
    .reduce { acc, t -> acc*t }
    .let { Num(it) }

private fun evaluateV1(tokens: List<Token>): Long {
    val subsection = mutableListOf<Token>()
    var depth = 0
    var accumulator = 0L
    var lastOperator: Token = Plus
    tokens.forEach { t ->
        if (depth > 0) {
            if (t == ParenOpen) depth++
            if (t == ParenClose) depth--

            if (depth == 0) {
                val value = evaluateV1(subsection)
                subsection.clear()
                accumulator = when (lastOperator) {
                    is Plus -> accumulator + value
                    is Times -> accumulator * value
                    else -> error("Invalid $lastOperator")
                }
            } else {
                subsection.add(t)
            }
            return@forEach
        }

        when (t) {
            is Num -> accumulator = when (lastOperator) {
                is Plus -> accumulator + t.value
                is Times -> accumulator * t.value
                else -> error("Invalid $lastOperator")
            }
            is Plus, Times -> lastOperator = t
            is ParenOpen -> depth = 1
            else -> error("Unexpected $t")
        }
    }
    return accumulator
}

private fun tokenize(string: String): List<Token> {
    val tokens = mutableListOf<Token>()
    val currentInt = StringBuilder()
    string.replace(" ", "").asSequence().forEach { c ->
        if (c.isDigit()) {
            currentInt.append(c)
            return@forEach
        } else if (currentInt.isNotEmpty()) {
            tokens.add(Num(currentInt.toString().toLong()))
            currentInt.clear()
        }

        when (c) {
            '+' -> Plus
            '*' -> Times
            '(' -> ParenOpen
            ')' -> ParenClose
            else -> error("Unknown $c")
        }.let { tokens.add(it) }
    }

    if (currentInt.isNotEmpty()) tokens.add(Num(currentInt.toString().toLong()))
    return tokens
}

private sealed class Token {
    override fun toString(): String = javaClass.name
}
private class Num(val value: Long) : Token() {
    override fun toString() = value.toString()
}
private object Plus : Token()
private object Times : Token()
private object ParenOpen : Token()
private object ParenClose : Token()
