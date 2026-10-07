package game.view

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import java.io.Reader
import game.model.State

class Screen {
    private val terminal: Terminal = TerminalBuilder.builder().build()
    private val previousAttributes = terminal.enterRawMode()

    private val writer = terminal.writer()

    var closed: Boolean = false
        private set

    fun draw(state: State) {
        clean(false)

        // No modo raw, \n não volta o cursor para a coluna 0. \r\n faz isso.
        writer.write(
            Board.render(state).replace("\n", "\r\n")
        )
        writer.flush()
    }

    fun readCommand(): Command {
        val reader = terminal.reader()
        val code = reader.read()

        if (code == -1) {
            closed = true
            return Command.QUIT
        }

        // As setinhas mandam uma sequência de caracteres e
        // precisam ser tratadas (inicia com "ESC").

        return when (code) {
            SPACE -> Command.CONFIRM
            R_UPPER, R_LOWER -> Command.RESTART
            S_UPPER, S_LOWER -> Command.QUIT
            ESC -> readArrow(reader)
            else -> Command.IGNORED
        }
    }

    fun close() {
        terminal.attributes = previousAttributes
        clean()
        terminal.close()
    }

    // --

    fun clean(flush: Boolean = true) {
        writer.write(CLEAR)
        if (flush) writer.flush()
    }

    // --

    private fun readArrow(reader: Reader): Command {

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

    fun confirmAction(question: String): Boolean {
        writer.write("\r\n$question [S/N]")
        writer.flush()

        val reader = terminal.reader()
        while (true) {
            when (reader.read()) {
                -1 -> {
                    closed = true
                    return false
                }
                S_UPPER, S_LOWER -> return true
                N_UPPER, N_LOWER -> return false
            }
        }
    }

    private companion object {
        const val CLEAR = "\u001b[H\u001b[2J"

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
        const val N_UPPER = 78
        const val N_LOWER = 110
    }
}
