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
    private val state: State = repository.load() ?: State()

    fun run() {
        evaluateBoard()
        try {
            while (true) {
                screen.draw(state)
                val command = screen.readCommand()

                if (command == Command.QUIT) break

                when (command) {
                    Command.UP -> moveCursor(-1, 0)
                    Command.DOWN -> moveCursor(1, 0)
                    Command.LEFT -> moveCursor(0, -1)
                    Command.RIGHT -> moveCursor(0, 1)
                    Command.CONFIRM -> confirmMove()
                    Command.RESTART -> restart()
                    else -> Unit
                }
            }
        } finally {
            repository.save(state)
            screen.close()
        }
    }

    // --

    fun moveCursor(rowDelta: Int, colDelta: Int): Boolean {
        val nextRow = (state.cursorRow + rowDelta).coerceIn(0, 2)
        val nextCol = (state.cursorCol + colDelta).coerceIn(0, 2)

        if (nextRow == state.cursorRow && nextCol == state.cursorCol)
            return false

        state.cursorRow = nextRow
        state.cursorCol = nextCol

        return true
    }

    fun confirmMove(): Boolean {
        if (state.status != Status.IN_PROGRESS)
            return false

        if (state.board[state.cursorRow][state.cursorCol] != null)
            return false

        state.board[state.cursorRow][state.cursorCol] = state.currentPlayer
        evaluateBoard()

        // Se o jogo ainda estiver em progresso, troca o jogador atual
        if (state.status == Status.IN_PROGRESS) {
            state.currentPlayer = state.currentPlayer.opponent()
        }

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

    fun restart(): Boolean {
        state.reset()
        return true
    }

    // --

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