package game.view

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import game.model.State
import game.model.Status

class Screen {
    private val terminal: Terminal = TerminalBuilder.builder().build()
    private val previousAttributes = terminal.enterRawMode() // modo raw

    private val writer = terminal.writer()
    private val keyboard = Keyboard(terminal.reader())

    val cursor = Cursor()

    init {
        // Passa a desenhar numa tela especial separada
        terminal.puts(InfoCmp.Capability.enter_ca_mode)
        terminal.flush()
    }

    fun draw(state: State) {
        clean()

        // No modo raw, \n não volta o cursor para a coluna 0. \r\n faz isso.
        writer.write(
            Board.render(state, cursor).replace("\n", "\r\n")
        )
        writer.flush()
    }

    fun readCommand(state: State): Command {
        while (true) {
            when (val command = keyboard.readCommand()) {
                Command.UP -> if (state.status == Status.IN_PROGRESS) cursor.move(-1, 0)
                Command.DOWN -> if (state.status == Status.IN_PROGRESS) cursor.move(1, 0)
                Command.LEFT -> if (state.status == Status.IN_PROGRESS) cursor.move(0, -1)
                Command.RIGHT -> if (state.status == Status.IN_PROGRESS) cursor.move(0, 1)
                else -> return command
            }
            draw(state)
        }
    }

    fun clean() {
        writer.write(CLEAR)
        writer.flush()
    }

    fun close() {
        terminal.puts(InfoCmp.Capability.exit_ca_mode)
        terminal.flush()
        terminal.attributes = previousAttributes
        terminal.close()
    }

    // --

    fun confirmAction(question: String): Boolean {

        // O terminal retorna a sua configuração original 
        // (permite digitação livre)

        terminal.attributes = previousAttributes // modo cooked
        writer.write("\r\n$question [s/N] ")
        writer.flush()

        val answer = try {
            keyboard.readLine()
        } finally {
            terminal.enterRawMode()
        }

        return answer.equals("s", ignoreCase = true) ||
            answer.equals("sim", ignoreCase = true)
    }

    private companion object {
        const val CLEAR = "\u001b[H\u001b[2J\u001b[3J"
    }
}
