package game.model

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
private data class SavedGame(
    val currentPlayer: Player,
    val status: Status,
    val board: List<List<Player?>>,
)

// --

class Repository {

    val filename = "game.json"

    private val file = File(filename)
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun save(state: State) {
        val snapshot = SavedGame(
            currentPlayer = state.currentPlayer,
            status = state.status,
            board = state.board.map { row -> row.toList() },
        )
        file.writeText(json.encodeToString(snapshot))
    }

    fun load(): State? {
        if (!file.exists()) return null

        return try {
            val snapshot = json.decodeFromString<SavedGame>(file.readText())
            if (snapshot.board.size != 3 || snapshot.board.any { it.size != 3 }) return null

            State().apply {
                currentPlayer = snapshot.currentPlayer
                status = snapshot.status
                board = Array(3) { row -> snapshot.board[row].toTypedArray() }
            }
        } catch (_: Exception) {
            null
        }
    }
}
