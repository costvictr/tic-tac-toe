package game.model

import kotlinx.serialization.Serializable

@Serializable
enum class Player {
    X,
    O;

    fun opponent(): Player = if (this == X) O else X
}
