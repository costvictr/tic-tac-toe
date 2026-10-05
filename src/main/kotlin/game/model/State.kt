package game.model

class State {
    var currentPlayer: Player = Player.X

    var cursorRow: Int = 0
    var cursorCol: Int = 0


    // Board guarda os valores da partida. Ex.:
    // [
    //   [  O  , null,  X   ],
    //   [ null,  O  , null ],
    //   [ null,  X  ,  X   ]
    // ]
    var board: Array<Array<Player?>> = emptyBoard()


    var status: Status = Status.IN_PROGRESS
    var winner: Player? = null
    var winningCells: List<Pair<Int, Int>> = emptyList()

    // --

    fun reset() {
        currentPlayer = Player.X

        cursorRow = 0
        cursorCol = 0

        board = emptyBoard()

        status = Status.IN_PROGRESS
        winner = null
        winningCells = emptyList()
    }

    // --

    private fun emptyBoard() =
        Array(3) { arrayOfNulls<Player?>(3) }
}
