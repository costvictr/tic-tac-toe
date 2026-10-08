package game.view

import game.model.State
import game.model.Status
import utils.Color

object Board {
    fun render(state: State, cursor: Cursor): String = buildString {
        // Ex. de saída:

        // --

        // Vez do Jogador: [ X ]
        //
        //        a   b   c
        //
        //   1      | X |
        //       ---|---|---
        //   2      | O |
        //       ---|---|---
        //   3      |   |
        //
        // Setas: mover | Espaço: jogar | R: reiniciar | S: sair

        appendLine()
        appendLine(statusLine(state))
        appendLine()
        appendLine("       a   b   c")
        appendLine()

        for (row in 0..2) {
            val a = cell(state, cursor, row, 0)
            val b = cell(state, cursor, row, 1)
            val c = cell(state, cursor, row, 2)
            
            appendLine("  ${row + 1}   $a|$b|$c")
            if (row < 2) appendLine("      ---|---|---")
        }

        appendLine()
        appendLine()
        appendLine(help(state))
    }

    // --

    private fun statusLine(state: State): String =
        when (state.status) {
            Status.WIN -> "Vencedor: [ ${(state.winner?.name) ?: "- Não Identificado -"} ]"
            Status.DRAW -> "Situação: Empate"
            Status.IN_PROGRESS -> "Vez do Jogador: [ ${state.currentPlayer.name} ]"
        }

    private fun help(state: State): String =
        if (state.status == Status.IN_PROGRESS)
            "Setas: mover | Espaço: jogar | R: reiniciar | S: sair"
        else
            "R: reiniciar | S: sair"

    // --

    private fun cell(
        state: State,
        cursor: Cursor,
        row: Int,
        col: Int,
    ): String {
        val mark = state.board[row][col]
        val selected =
            state.status == Status.IN_PROGRESS &&
            cursor.row == row && cursor.col == col

        // -- Partida em andamento (com foco "[ ]")

        // Pode escolher
        if (selected && mark == null) {
            val preview = Color.apply(state.currentPlayer.name, Color.DIM)
            return "[$preview]"
        }

        if (mark == null) return "   "

        // -- Quando vitória, empate ou marca comum

        val codes = when {
            (row to col) in state.winningCells -> intArrayOf(Color.BOLD, Color.GREEN)
            state.status == Status.DRAW -> intArrayOf(Color.BOLD, Color.YELLOW)
            else -> intArrayOf(Color.BOLD)
        }
        val text = Color.apply(mark.name, *codes)

        // -- Qualquer cenário (fora de foco)

        if (!selected) return " $text "

        // Não pode escolher
        val openSelector = Color.apply("[", Color.RED)
        val closeSelector = Color.apply("]", Color.RED)
        return "$openSelector$text$closeSelector"
    }
}
