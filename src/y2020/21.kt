package y2020

// I wrote this quite a while ago, although I think I will go for a different solution

private class CSP(
    val variables: Set<String>,
    val values: Set<String>,
    val constraints: Map<List<String>, List<String>>,
    val exclude: List<Map<String, String>> = emptyList()
) {

    fun search(): Map<String, String> {
        return recursiveBacktracking(mutableMapOf()) ?: error("Failure")
    }

    fun recursiveBacktracking(assignment: MutableMap<String, String>): Map<String, String>? {
        if (isComplete(assignment)) return assignment

        val variable = selectUnassignedVariable(assignment) ?: return null
        orderDomainValues(variable, assignment).forEach { value ->
            println("Trying to set $variable to $value")
            if (isConsistent(variable, value, assignment)) {
                println("Consistent")
                assignment[variable] = value
                val res = recursiveBacktracking(assignment)
                if (res == null) println("Backtracking from $assignment")
                if (res != null && !exclude.contains(assignment)) return res
                else assignment.remove(variable)
            } else {
                println("Inconsistent")
            }
        }

        return null
    }

    fun selectUnassignedVariable(assignment: Map<String, String>): String? {
        return variables.firstOrNull { !assignment.containsKey(it) }
    }

    fun orderDomainValues(variable: String, assignment: Map<String, String>): List<String> {
        return values.toMutableList().apply { removeAll(assignment.values) }
        /*return domains[variable]!!.sortedBy { value ->
            domains.keys.count { var0 ->
                isConsistent(var0, value, assignment)
            }
        }*/
    }

    fun isConsistent(variable: String, value: String, assignment: Map<String, String>): Boolean {
        if ((variable == "mxmxvkd" && value == "dairy")
            || (variable == "sqjhc" && value == "fish")
            || (variable == "fvjkl" && value == "soy")
        ) {
            //println("Test: $variable $value")
        }
        if (assignment.isEmpty()) return true
        if (value.isNotEmpty() && assignment.values.contains(value)) return false
        /*val satisfiesListConstraints = constraints.all { (ingredients, allergens) ->
            if(!ingredients.contains(variable)) return@all true
            val unassignedIngredients = ingredients.toMutableList() - assignment.keys
            val unassignedAllergens = allergens.toMutableList() - assignment.values.toSet()
            println("----")
            println("Assignments = $assignment")
            println("Ingredients = $ingredients, allergens = $allergens")
            println(ingredients.size.toString() + ", " +  allergens.size)
            println(unassignedIngredients.size.toString() + ", " +  unassignedAllergens.size)
            unassignedIngredients.size >= unassignedAllergens.size
        }*/
        val satisfiesListConstraints = constraints.all { (ingredients, allergens) ->
            val notContainingAllergen = !allergens.contains(variable)
            if(notContainingAllergen) return@all true
            val containsIngredient = ingredients.contains(value)
            //println("$notContainingAllergen, $containsIngredient")
            val result = notContainingAllergen || containsIngredient
            if(!result) {
                //println(assignment)
                //println("Tried setting $variable to $value")
                //println("$ingredients, $allergens")
            }
            result
        }
        //if (!satisfiesListConstraints) println("Inconsistency found")
        return satisfiesListConstraints
    }

    fun isComplete(assignment: Map<String, String>): Boolean {
        return assignment.keys.containsAll(variables.toMutableSet())
    }
}

fun main() {
    val variables = mutableSetOf<String>()
    val values = mutableSetOf<String>()
    val ingredientsCount = mutableMapOf<String, Int>()
    val constraints = mutableMapOf<List<String>, List<String>>()
    getInput(21).forEach { line ->
        val ingredients = line.takeWhile { it != '(' }.trimEnd().split(" ")
        val allergens = line.dropWhile { it != '(' }.drop(10).dropLast(1).split(", ")
        variables.addAll(allergens)
        values.addAll(ingredients)
        ingredients.forEach { ingredient ->
            ingredientsCount.compute(ingredient) { _, count -> count?.inc() ?: 1 }
        }
        constraints[ingredients] = allergens
    }

    val csp = CSP(variables, values, constraints)
    val result = csp.search()
    println(result.toString())
    println(ingredientsCount)
    println(ingredientsCount.values.sum())
    println(ingredientsCount.filterKeys { !result.containsValue(it) })
    println(ingredientsCount.filterKeys { !result.containsValue(it) }.values.sum())
    // Wrong: cskbmx,bmhn,cjdmk,xlxknk,jrmr,tzxcmr,fmgxh,fxzh
    result.map { it.key to it.value }
        .sortedBy { it.first }
        .onEach { println(it) }
        .joinToString(",") { it.second }
        .let { println(it) }
    //println(ingredientsCount.filterKeys { !result2.containsKey(it) }.values.sum())

}

/*
{sesame=jrmr, fish=cskbmx, shellfish=tzxcmr, dairy=xlxknk, nuts=cjdmk, peanuts=bmhn, wheat=fxzh, soy=fmgxh}
xlxknk,cskbmx,cjdmk,bmhn,jrmr,tzxcmr,fmgxh,fxzh
 */
