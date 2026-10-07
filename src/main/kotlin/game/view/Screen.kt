package game.view

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import java.io.Reader
import game.model.State

class Screen {
    private val terminal: Terminal = TerminalBuilder.builder().build()
    private val previousAttributes = terminal.enterRawMode()

    private val writer = terminal.writer()

    var cursorRow: Int = 0
        private set
    var cursorCol: Int = 0
        private set

    fun draw(state: State) {
        clean()

        // No modo raw, \n não volta o cursor para a coluna 0. \r\n faz isso.
        writer.write(
            Board.render(state, cursorRow, cursorCol).replace("\n", "\r\n")
        )
        writer.flush()
    }

    fun moveCursor(rowDelta: Int, colDelta: Int): Boolean {
        val nextRow = (cursorRow + rowDelta).coerceIn(0, 2)
        val nextCol = (cursorCol + colDelta).coerceIn(0, 2)

        if (nextRow == cursorRow && nextCol == cursorCol) return false

        cursorRow = nextRow
        cursorCol = nextCol
        return true
    }

    fun resetCursor() {
        cursorRow = 0
        cursorCol = 0
    }

    fun readCommand(): Command {
        val reader = terminal.reader()
        val code = reader.read()

        // As setinhas mandam uma sequência de caracteres e
        // precisam ser tratadas (inicia com "ESC").

        return when (code) {
            ESC -> readArrow(reader)
            SPACE -> Command.CONFIRM
            R_UPPER, R_LOWER -> Command.RESTART
            -1, S_UPPER, S_LOWER -> Command.QUIT
            else -> Command.IGNORED
        }
    }

    fun clean() {
        writer.write(CLEAR)
        writer.flush()
    }

    fun close() {
        terminal.attributes = previousAttributes
        clean()
        terminal.close()
    }

    // --

    fun confirmAction(question: String): Boolean {
        writer.write("\r\n$question [S/N]")
        writer.flush()

        val reader = terminal.reader()
        while (true) {
            when (reader.read()) {
                -1, S_UPPER, S_LOWER -> return true
                N_UPPER, N_LOWER -> return false
            }
        }
    }

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
