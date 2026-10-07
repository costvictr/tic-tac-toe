import game.controller.Match
import game.model.Repository
import game.view.Screen

fun main() {
    Match(Screen(), Repository()).run()
}
