package game.model

import java.io.File
import kotlin.collections.any

class Repository {
    val filename = "game.txt"
    private val file = File(filename)

    fun save(state: State) {

        // Percorre linhas e salva o estado das células nas colunas.
        // Ex.: rows = "O-X,-O-,-XX"

        val rows = state.board.joinToString(",")
        { row ->
            row.joinToString("")
            { cell -> symbolOf(cell) }
        }


        // Salva o TXT. Ex.:
        // player=X
        // status=IN_PROGRESS
        // cursor=0,0
        // board=O-X,-O-,-XX

        file.writeText(
            listOf(
                "player=${state.currentPlayer}",
                "status=${state.status}",
                "cursor=${state.cursorRow},${state.cursorCol}",
                "board=${rows}"
            ).joinToString("\n") + "\n"
        )
    }

    fun load(): State? {
        if (!file.exists()) return null

        return try {
            parse(file.readLines())
        } catch (_: Exception) {
            null
        }
    }

    // --

    private fun symbolOf(cell: Player?): String =
        when (cell) {
            Player.X -> "X"
            Player.O -> "O"
            null -> "-"
        }

    private fun cellOf(symbol: Char): Player? =
        when (symbol) {
            'X' -> Player.X
            'O' -> Player.O
            else -> null
        }

    // --

    private fun parse(lines: List<String>): State? {
        if (lines.size < 4) return null

        // Coleta os dados no arquivo. Qualquer inconsistência
        // fará o arquivo ser tratado como "corrompido" (return null).


        // player=X
        val player = Player.entries
            .find { it.name == lines[0].substringAfter("=") }
            ?: return null

        // status=IN_PROGRESS
        val status = Status.entries
            .find { it.name == lines[1].substringAfter("=") }
            ?: return null

        // cursor=0,0
        val cursor = lines[2].substringAfter("=").split(",")
        if (cursor.size != 2) return null

        val row = cursor[0].toIntOrNull() ?: return null
        val col = cursor[2].toIntOrNull() ?: return null
        if (row !in 0..2 || col !in 0..2) return null

        // board=O-X,-O-,-XX
        val boardRows = lines[3].substringAfter("=").split(",")
        if (boardRows.size != 3 || boardRows.any { it.length != 3 }) return null

        val state = State()
        state.currentPlayer = player
        state.cursorRow = row
        state.cursorCol = col
        state.status = status

        for ((boardRow, rowText) in boardRows.withIndex()) {
            for ((boardCol, symbol) in rowText.withIndex()) {
                if (symbol !in "XO-") return null
                state.board[boardRow][boardCol] = cellOf(symbol)
            }
        }

        return state
    }
}
