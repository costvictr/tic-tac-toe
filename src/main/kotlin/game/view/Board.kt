package game.view

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
        appendLine(help(state))
    }

    // --

    private fun statusLine(state: State): String =
        when (state.status) {
            Status.WIN -> "Vencedor: [ ${state.winner?.name ?: "- Não Identificado -"} ]"
            Status.DRAW -> "Empate"
            Status.IN_PROGRESS -> "Vez do Jogador: [ ${state.currentPlayer.name} ]"
        }

    private fun help(state: State): String =
        if (state.status == Status.IN_PROGRESS)
            "Setas: mover | Espaço: jogar | R: reiniciar | S: sair"
        else
            "R: reiniciar | S: sair"

    // --

    private fun cell(state: State, row: Int, col: Int): String {
        val mark = state.board[row][col]
        val selected =
            state.status == Status.IN_PROGRESS &&
            state.cursorRow == row && state.cursorCol == col

        // -- Quando vitória

        val winning = (row to col) in state.winningCells
        
        if (winning && mark != null) {
            val colored = Color.apply(mark.name, Color.BOLD, Color.GREEN)
            return " $colored "
        }

        // -- Partida em andamento (com foco "[ ]")

        // Pode escolher
        if (selected && mark == null) {
            val preview = Color.apply(state.currentPlayer.name, Color.DIM)
            return "[$preview]"
        }

        // Não pode escolher
        if (selected && mark != null) {
            val openSelector = Color.apply("[", Color.DIM, Color.RED)
            val closeSelector = Color.apply("]", Color.DIM, Color.RED)
            return "$openSelector${mark.name}$closeSelector"
        }

        // -- Qualquer cenário (fora de foco)

        return if (mark == null) "   " else " ${mark.name} "
    }
}
