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
                when (screen.readCommand()) {
                    Command.UP -> if (state.status == Status.IN_PROGRESS) screen.moveCursor(-1, 0)
                    Command.DOWN -> if (state.status == Status.IN_PROGRESS) screen.moveCursor(1, 0)
                    Command.LEFT -> if (state.status == Status.IN_PROGRESS) screen.moveCursor(0, -1)
                    Command.RIGHT -> if (state.status == Status.IN_PROGRESS) screen.moveCursor(0, 1)
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
        if (state.status != Status.IN_PROGRESS)
            return false

        val row = screen.cursorRow
        val col = screen.cursorCol

        if (state.board[row][col] != null)
            return false

        state.board[row][col] = state.currentPlayer
        evaluateBoard()

        // Se o jogo ainda estiver em progresso, troca o jogador atual
        if (state.status == Status.IN_PROGRESS) {
            state.currentPlayer = state.currentPlayer.opponent()
        }

        saveGame()
        return true
    }

    fun evaluateBoard() {

        // -- Verifica se há um vencedor

        for (line in WIN_LINES) {

            // Faz, no máximo, 24 verificações

            val marks = line.map { (row, col) -> state.board[row][col] }
            val first = marks.first()

            if (first != null && marks.all { it == first }) {
                state.status = Status.WIN
                state.winner = first
                state.winningCells = line
                return
            }
        }

        // -- Verifica se houve empate

        val full = state.board.all { row -> row.all { it != null } }

        state.status = if (full) Status.DRAW else Status.IN_PROGRESS
        state.winner = null
        state.winningCells = emptyList()
    }

    fun restartGame(): Boolean {
        state.reset()
        screen.resetCursor()
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

    private companion object {
        val WIN_LINES: List<List<Pair<Int, Int>>> = listOf(
            listOf(0 to 0, 0 to 1, 0 to 2),
            listOf(1 to 0, 1 to 1, 1 to 2),
            listOf(2 to 0, 2 to 1, 2 to 2),
            listOf(0 to 0, 1 to 0, 2 to 0),
            listOf(0 to 1, 1 to 1, 2 to 1),
            listOf(0 to 2, 1 to 2, 2 to 2),
            listOf(0 to 0, 1 to 1, 2 to 2),
            listOf(0 to 2, 1 to 1, 2 to 0),
        )
    }
}
