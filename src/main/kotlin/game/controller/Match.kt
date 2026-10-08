package game.controller

import game.model.Repository
import game.model.State
import game.model.Status
import game.view.Command
import game.view.Screen

class Match(
    private val screen: Screen,
    private val repository: Repository,
) {
    private var state: State = State()

    fun run() {
        try {
            offerRestore()

            while (true) {
                screen.draw(state)
                when (screen.readCommand(state)) {
                    Command.CONFIRM -> confirmMove()
                    Command.RESTART -> if (screen.confirmAction("Deseja reiniciar?")) restartGame()
                    Command.QUIT -> if (screen.confirmAction("Deseja sair?")) break
                    else -> Unit
                }
            }
        } finally {
            screen.close()
        }
    }

    // --

    fun confirmMove(): Boolean {
        if (!Rules.tryMove(state, screen.cursor)) return false
        saveGame()
        return true
    }

    fun restartGame(): Boolean {
        state.reset()
        screen.cursor.reset()
        saveGame()
        return true
    }

    // --

    private fun offerRestore() {
        val saved = repository.load() ?: return

        if (saved.status != Status.IN_PROGRESS) {
            repository.delete()
            return
        }

        screen.draw(saved)
        if (screen.confirmAction("Deseja restaurar o jogo salvo?")) state = saved
        else repository.delete()
    }

    private fun saveGame() {
        val started = state.board.any { row -> row.any { it != null } }

        if (state.status == Status.IN_PROGRESS && started) repository.save(state)
        else repository.delete()
    }
}
