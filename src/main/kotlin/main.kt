import com.google.gson.GsonBuilder
import data.Recipe
import org.openqa.selenium.By
import org.openqa.selenium.By.className
import org.openqa.selenium.By.id
import org.openqa.selenium.Dimension
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import recipe_extractor.RecipeExtractor
import java.io.File
import java.time.Duration


fun main() {
    System.setProperty("webdriver.edge.driver", "C:\\webdriver\\msedgedriver.exe")
    val options = EdgeOptions()
//    options.setBinary("C:\\webdriver\\msedgedriver.exe");
    val driver = EdgeDriver(options)


    // telling selenium to wait for how many millis seconds before it gets result
    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000))
    val height = ((driver.manage().window().size.height * 70).toFloat() / 100f).toInt()
    val width = ((driver.manage().window().size.width * 70).toFloat() / 100f).toInt()
    driver.manage().window().size = Dimension(width, height)

    initialSetup(driver = driver)

//    for(i in 0 .. 6){
//        clickMoreResults(driver = driver)
//    }
//
//    return;
    val recipeList = mutableListOf<Recipe>()
    var i = 0
    val cards = getRecipeCards(driver = driver)
    val size = cards.size
    println("size of card is $size")
    var card = cards[i]
    val recipeJsonList = mutableListOf<String>()
    try {
        for (j in 0 until 500) {
            card.click()
            val recipe = getRecipe(driver = driver)
            recipeList.add(recipe)
            i++
            driver.navigate().back()
            declinePopUp(driver = driver)
            card = getRecipeCards(driver = driver)[i]
        }
    } catch (e: Exception) {
        println("something went wrong in for loop: $e")
    }
    val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    recipeList.forEach { rec ->
        val jsonRecipe = gson.toJson(rec)
        recipeJsonList.add(jsonRecipe)
    }
    val fileToWrite = File("src/main/resources/snacks.json")
    fileToWrite.printWriter().use { writer ->
        writer.println(recipeJsonList)
    }
}

fun declinePopUp(driver: EdgeDriver) {
    try {
        // declining a popup
        val declineButton = driver.findElement(className("npop-btn"))
        declineButton.click()
    }catch(_: Exception){

    }
}

fun pressEscape(driver: EdgeDriver){
    try{
    driver.keyboard.pressKey(Keys.ESCAPE)

    }catch(e: Exception){
        println("Escape: $e")
    }
}

fun clickMoreResults(driver: EdgeDriver) {
    try {
        val clickMoreButton = driver.findElement(id("pagination"))
        println("ClickMore: clickMoreButton text is ${clickMoreButton.text} ")
        clickMoreButton.click()
        driver.executeScript("scrollBy(0, 0)")
    } catch (e: Exception) {
        println("unable to click more results")
    }
}

fun initialSetup(driver: EdgeDriver) {
    // specifying which url to follow
    driver.get("file:///D:/desktop/Study/SeleniumWithKotlin/snacks.mhtml")
//    driver.get("https://food.ndtv.com/recipes/snacks-recipes")

    // finding an element by id
    try {
        declinePopUp(driver = driver)

        driver.findElement(id("close_button")).click()


    } catch (e: Exception) {
        println("error occured: ${e.localizedMessage}\nerror: $e")
    }
}

fun getRecipeCards(driver: EdgeDriver): List<WebElement> {
    val cards = driver.findElements(className("SrcCrd-Rec"))
    val cardsList = mutableListOf<WebElement>()
    cards.forEach { element ->
        val card = element.findElement(className("crd_lnk"))
        if (card.text.isNotBlank()) {
            cardsList.add(card)
        }
    }
    return cardsList.toList()
}

fun getRecipe(driver: EdgeDriver): Recipe {

    try {
        // finding an element by id
        val declineButtonContainer = driver.findElement(id("__cricketsubscribe"))

        // declining a popup
        val declineButton = declineButtonContainer.findElement(className("npop-btn"))
        declineButton.click()
    } catch (e: Exception) {
        println("error occurred while declining button\n======")
        println(e.localizedMessage)
        println("======")
    }

    val recipeExtractor = RecipeExtractor()

    // getting list of ingredients used in the recipe in String data type
    val ingredientListOfString = recipeExtractor.getIngredientList(driver = driver)

    // converting list of string to instance of data.Ingredient data class
    val ingredientList = recipeExtractor.stringListToIngredientList(ingredientListOfString)

    // getting method of recipe in list of string
    val methodInString = recipeExtractor.getRecipeMethod(driver = driver)

    // getting title of recipe
    val title = recipeExtractor.getRecipeTitle(driver = driver)

    // converting method and ingredient list to type of data.Recipe data class
    val recipe = recipeExtractor.getRecipe(ingredientList, methodInString, title)

    //converting data class data.Recipe into Gson using Gson JSON handler
//    val gson = Gson()
    val gsonPretty = GsonBuilder()
        .setPrettyPrinting()
        .create()
    val jsonObj = gsonPretty.toJson(recipe)
    val jsonString = jsonObj.toString()
    println(jsonString)
    return recipe
}
