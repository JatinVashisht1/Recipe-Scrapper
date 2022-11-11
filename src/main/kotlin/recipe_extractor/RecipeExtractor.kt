package recipe_extractor

import data.Ingredient
import data.Recipe
import org.openqa.selenium.By
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.support.FindBy
import utility.notFirst

interface RecipeExtractor {
    fun getIngredientList(driver: EdgeDriver): List<String>
    fun stringListToIngredientList(ingredientList: List<String>): List<Ingredient>
    fun getRecipeMethod(driver: EdgeDriver): List<String>
    fun getRecipeTitle(driver: EdgeDriver): String
    fun getRecipe(ingredient: List<Ingredient>, method: List<String>, title: String, tag: String, imageUrl: String): Recipe

    fun getRecipeImage(driver: EdgeDriver): String
}

fun RecipeExtractor(): RecipeExtractor = RecipeExtractorImpl()

private class RecipeExtractorImpl : RecipeExtractor {
    override fun getIngredientList(driver: EdgeDriver): List<String> {
        val ingredientsAsListOfWebElement = driver.findElements(By.className("RcpIngd-tp_li"))
        val ingredientList = mutableListOf<String>()
        ingredientsAsListOfWebElement.forEach {
            ingredientList.add(it.text)
        }
        return ingredientList
    }

    override fun getRecipeImage(driver: EdgeDriver): String {
        val imageContainer = driver.findElement(By.id("story_image_main"))
        val url = imageContainer.getAttribute("src")
        return url
    }

    override fun stringListToIngredientList(ingredientList: List<String>): List<Ingredient> {
        val ingredients = mutableListOf<Ingredient>()
        ingredientList.forEach { element ->
            val splitElement = element.split(" ")
            try {
                splitElement.first().toFloat()
                ingredients.add(
                    Ingredient(quantity = splitElement.first(), description = splitElement.notFirst())
                )
            } catch (e: Exception) {
                println("RecipeExtractor: error message ${e.localizedMessage} \n========\nerror: $e")
                try {
                    val regexString = "/".toRegex()
                    if (regexString.containsMatchIn(splitElement.first())) {
                        val quantityString = splitElement.first()
                        val numerator = quantityString.first().digitToInt()
                        val denominator = quantityString.last().digitToInt()

                        val quantity = numerator.toFloat() / denominator.toFloat()
                        ingredients.add(
                            Ingredient(
                                quantity = quantity.toString(),
                                description = splitElement.notFirst().trim()
                            )
                        )
                    } else {
                        ingredients.add(
                            Ingredient(
                                quantity = "",
                                description = (splitElement.first() + splitElement.notFirst()).trim()
                            )
                        )
                    }
                }
                catch(exc: Exception){
                    println("unable to parse quantity")
                    ingredients.add(
                        Ingredient(
                            quantity = "",
                            description = (splitElement.first() + splitElement.notFirst()).trim()
                        )
                    )
                }
            }
        }
        return ingredients.toList()
    }

    override fun getRecipeMethod(driver: EdgeDriver): List<String> {
        val methodList = mutableListOf<String>()
        driver.findElements(By.className("RcHTM_li")).forEach { method ->
            methodList.add(method.text)
        }
        return methodList.toList()
    }

    override fun getRecipe(ingredient: List<Ingredient>, method: List<String>, title: String, tag: String, imageUrl: String): Recipe {
        return Recipe(ingredient = ingredient, method = method, title = title, tag = tag, imageUrl = imageUrl)
    }

    override fun getRecipeTitle(driver: EdgeDriver): String {
        val title = driver.findElement(By.className("sp-ttl")).text
        return return title
    }
}