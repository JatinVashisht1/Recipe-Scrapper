package data

data class Ingredient(
    val quantity: String = "",
    val description: String = ""
){
    override fun toString(): String {
        return "$quantity$description"
    }
}
