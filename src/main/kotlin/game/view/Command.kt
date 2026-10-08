package game.view

enum class Command(
    val rowDelta: Int = 0,
    val colDelta: Int = 0,
) {
    UP(rowDelta = -1),
    DOWN(rowDelta = 1),
    LEFT(colDelta = -1),
    RIGHT(colDelta = 1),
    CONFIRM,
    RESTART,
    QUIT,
    IGNORED,
}
