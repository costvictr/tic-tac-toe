package utils

object Color {
    const val RESET = 0
    const val BOLD = 1
    const val DIM = 2

    const val YELLOW = 33
    const val CYAN = 36

    fun apply(text: String, vararg codes: Int): String {
        val prefix = codes.joinToString(";")
        return "\u001b[${prefix}m$text\u001b[${RESET}m"
    }
}
