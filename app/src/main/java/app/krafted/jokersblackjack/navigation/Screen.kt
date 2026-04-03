package app.krafted.jokersblackjack.navigation

object Screen {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val LEADERBOARD = "leaderboard"
    const val GAME = "game/{difficulty}"
    const val SESSION_RESULT = "session_result/{difficulty}"

    fun gameRoute(difficulty: String) = "game/$difficulty"
    fun sessionResultRoute(difficulty: String) = "session_result/$difficulty"
}
