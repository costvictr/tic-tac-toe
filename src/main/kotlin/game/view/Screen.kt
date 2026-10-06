package game.view

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.io.Reader
import game.model.State

class Screen {
    private val terminal: Terminal = TerminalBuilder.builder().build()
    private val previousAttributes = terminal.enterRawMode()

    fun draw(state: State) {
        clean()

        val writer = terminal.writer()

        // No modo raw, \n não volta o cursor para a coluna 0. \r\n faz isso.
        writer.write(
            Board.render(state).replace("\n", "\r\n")
        )
        writer.flush()
        terminal.flush()
    }

    fun readCommand(): Command {
        val reader = terminal.reader()
        val code = reader.read()

        if (code == -1) return Command.QUIT

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

    fun clean() {
        terminal.puts(InfoCmp.Capability.clear_screen)
        terminal.puts(InfoCmp.Capability.cursor_home)
        terminal.flush()
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
