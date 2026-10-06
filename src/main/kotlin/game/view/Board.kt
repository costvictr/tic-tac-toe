package game.view

import game.model.Player
import game.model.State
import game.model.Status
import utils.Color

object Board {
    fun render(state: State): String = buildString {
        // Ex. de saída:

        // --

        // Vez do Jogador: [ X ]
        //
        //    a   b   c
        // 1    | X |
        //   ---|---|---
        // 2    | O |
        //   ---|---|---
        // 3    |   |
        //
        // Setas: mover | Espaço: jogar | R: reiniciar | S: sair


        appendLine(statusLine(state))
        appendLine()
        appendLine("   a   b   c")

        for (row in 0..2) {
            appendLine(
                "${row + 1} ${cell(state, row, 0)}|${cell(state, row, 1)}|${cell(state, row, 2)}"
            )
            if (row < 2) appendLine("  ---|---|---")
        }

        appendLine()
        appendLine("Setas: mover | Espaço: jogar | R: reiniciar | S: sair")
    }

    // --

    private fun statusLine(state: State): String =
        when (state.status) {
            Status.WIN -> "Vencedor: [ ${state.winner?.name ?: "- Não Identificado -"} ]"
            Status.DRAW -> "Empate"
            Status.IN_PROGRESS -> "Vez do Jogador: [ ${state.currentPlayer.name} ]"
        }

    private fun cell(state: State, row: Int, column: Int): String {
        val mark = state.board[row][column]
        val selected = state.cursorRow == row && state.cursorCol == column
        val winning = (row to column) in state.winningCells

        // -- Quando vitória

        if (winning && mark != null) {
            val colored = Color.apply(mark.name, Color.BOLD, colorOf(mark))
            return if (selected) "[$colored]" else " $colored "
        }

        // -- Partida em andamento

        if (selected && mark == null && state.status == Status.IN_PROGRESS) {
            val preview = Color.apply(state.currentPlayer.name, Color.DIM, colorOf(state.currentPlayer))
            return "[$preview]"
        }

        if (selected && mark != null) {
            return "[${mark.name}]"
        }

        // -- Qualquer cenário

        return if (mark == null) "   " else " ${mark.name} "
    }

    // --

    private fun colorOf(player: Player): Int =
        if (player == Player.X) Color.CYAN else Color.YELLOW
}
