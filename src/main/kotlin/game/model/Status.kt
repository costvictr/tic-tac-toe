package game.model

import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    IN_PROGRESS,
    WIN,
    DRAW;
}
