package game.controller

import game.model.State
import game.model.Status
import game.view.Cursor

object Rules {
    fun play(state: State, cursor: Cursor ): Boolean {
        if (state.status != Status.IN_PROGRESS)
            return false

        val row = cursor.row
        val col = cursor.col

        if (state.board[row][col] != null)
            return false

        state.board[row][col] = state.currentPlayer
        evaluate(state)

        // Se o jogo ainda estiver em progresso, troca o jogador atual
        if (state.status == Status.IN_PROGRESS) {
            state.currentPlayer = state.currentPlayer.opponent()
        }

        return true
    }

    // --

    private fun evaluate(state: State) {

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

    private val WIN_LINES: List<List<Pair<Int, Int>>> = listOf(
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
