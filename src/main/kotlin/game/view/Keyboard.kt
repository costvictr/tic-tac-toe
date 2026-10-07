package game.view

import java.io.Reader

class Keyboard(
    private val reader: Reader
) {
    fun readCommand(): Command {
        val code = reader.read()

        // As setinhas mandam uma sequência de caracteres e
        // precisam ser tratadas (inicia com "ESC").

        return when (code) {
            ESC -> readArrow()
            SPACE -> Command.CONFIRM
            R_UPPER, R_LOWER -> Command.RESTART
            -1, S_UPPER, S_LOWER -> Command.QUIT
            else -> Command.IGNORED
        }
    }

    fun readLine(): String = buildString {
        while (true) {
            val code = reader.read()
            if (code == -1 || code == '\n'.code || code == '\r'.code) break
            append(code.toChar())
        }
    }.trim()

    // --

    private fun readArrow(): Command {

        // Ex. de representação da sequência: ESC [ A/B/C/D

        val second = reader.read()
        if (second != CSI && second != SS3) return Command.IGNORED

        return when (reader.read()) {
            UP -> Command.UP
            DOWN -> Command.DOWN
            RIGHT -> Command.RIGHT
            LEFT -> Command.LEFT
            else -> Command.IGNORED
        }
    }

    private companion object {
        const val ESC = 27
        const val CSI = 91
        const val SS3 = 79

        const val UP = 65
        const val DOWN = 66
        const val RIGHT = 67
        const val LEFT = 68

        const val SPACE = 32
        const val R_UPPER = 82
        const val R_LOWER = 114
        const val S_UPPER = 83
        const val S_LOWER = 115
    }
}
