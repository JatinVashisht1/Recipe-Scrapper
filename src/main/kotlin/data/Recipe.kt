package data

data class Recipe(
    val title: String = "",
    val ingredient: List<Ingredient> = emptyList(),
    val method: List<String> = emptyList(),
    val tag: String = "",
    val imageUrl: String = "",
)
