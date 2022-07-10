package utility

fun List<String>.notFirst(): String {
    var resultantList = ""
    for(i in 1 until this.size){
        resultantList += " ${this[i]}"
    }
    return resultantList
}