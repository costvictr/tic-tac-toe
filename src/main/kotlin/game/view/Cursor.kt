package game.view

class Cursor {
    var row: Int = 0
        private set
    var col: Int = 0
        private set

    fun move(rowDelta: Int, colDelta: Int): Boolean {
        val nextRow = (row + rowDelta).coerceIn(0, 2)
        val nextCol = (col + colDelta).coerceIn(0, 2)

        if (nextRow == row && nextCol == col) return false

        row = nextRow
        col = nextCol
        return true
    }

    fun reset() {
        row = 0
        col = 0
    }
}
