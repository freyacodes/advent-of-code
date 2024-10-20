package y2020

class Day21(input: List<String>) {
    private val food: Map<Set<String>, Set<String>> = parseInput(input)
    private val allIngredients: Set<String> = food.keys.flatten().toSet()
    private val allAllergies: Set<String> = food.values.flatten().toSet()

    fun solvePart1(): Int {
        val safeIngredients = safeIngredients()
        return food.keys.sumOf { recipe ->
            recipe.count { it in safeIngredients }
        }
    }

    fun solvePart2(): String {
        val ingredientsByAllergy = ingredientsByAllergy()
        val found: MutableMap<String, String> = mutableMapOf()

        while (ingredientsByAllergy.isNotEmpty()) {
            val singles = ingredientsByAllergy
                .filter { it.value.size == 1 }
                .map { it.key to it.value.first() }
                .toMap()
            found.putAll(singles)
            singles.keys.forEach { ingredientsByAllergy.remove(it) }
            ingredientsByAllergy.values.forEach { it.removeAll(singles.values) }
        }

        println(found)

        return found.entries.sortedBy { it.key }.joinToString(",") { it.value }
    }

    private fun ingredientsByAllergy(): MutableMap<String, MutableSet<String>> {
        val safeIngredients = safeIngredients()

        return allAllergies.map { allergy ->
            allergy to food.entries
                .filter { allergy in it.value }
                .map { it.key - safeIngredients }
                .reduce { a, b -> a intersect b }
                .toMutableSet()
        }.toMap().toMutableMap()
    }

    private fun safeIngredients(): Set<String> =
        allIngredients subtract allAllergies.flatMap { allergy ->
            food
                .filter { allergy in it.value }
                .map { it.key }
                .reduce { carry, ingredients -> ingredients intersect carry }
        }.toSet()

    private fun parseInput(input: List<String>): Map<Set<String>, Set<String>> =
        input.map { line ->
            val ingredients = line.substringBefore(" (").split(" ").toSet()
            val allergies = line.substringAfter("(contains ").substringBefore(")").split(", ").toSet()
            ingredients to allergies
        }.toMap()
}

fun main() {
    val d = Day21(getInput(21))
    println(d.solvePart1())
    println(d.solvePart2())
}