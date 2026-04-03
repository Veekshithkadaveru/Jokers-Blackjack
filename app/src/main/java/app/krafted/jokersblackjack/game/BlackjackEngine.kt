package app.krafted.jokersblackjack.game

enum class GamePhase { DEALING, PLAYER_TURN, DEALER_TURN, HAND_COMPLETE }

enum class HandResult { WIN, BLACKJACK, LOSS, BUST, DRAW }

enum class Difficulty { EASY, MEDIUM, HARD }

data class GameUiState(
    val sessionHandNumber: Int = 1,
    val deck: MutableList<Card> = mutableListOf(),
    val playerHand: List<Card> = emptyList(),
    val dealerHand: List<Card> = emptyList(),
    val playerTotal: Int = 0,
    val dealerTotal: Int = 0,
    val isDealerRevealed: Boolean = false,
    val gamePhase: GamePhase = GamePhase.DEALING,
    val handResult: HandResult? = null,
    val sessionResults: List<HandResult> = emptyList(),
    val sessionScore: Int = 0,
    val isSessionComplete: Boolean = false,
    val isNewBest: Boolean = false,
    val difficulty: Difficulty = Difficulty.EASY,
    val jokerQuote: String = "",
    val bestScores: Map<Difficulty, Int> = emptyMap()
)

class BlackjackEngine {

    fun dealInitialHands(deck: MutableList<Card>): Pair<List<Card>, List<Card>> {
        val playerHand = mutableListOf<Card>()
        val dealerHand = mutableListOf<Card>()
        playerHand.add(deck.dealCard()!!)
        dealerHand.add(deck.dealCard()!!)
        playerHand.add(deck.dealCard()!!)
        dealerHand.add(deck.dealCard()!!)
        return playerHand to dealerHand
    }

    fun checkBlackjack(hand: List<Card>): Boolean {
        return hand.size == 2 && handTotal(hand) == 21
    }

    fun checkBust(hand: List<Card>): Boolean {
        return handTotal(hand) > 21
    }

    fun playerHit(deck: MutableList<Card>, currentHand: List<Card>): List<Card> {
        val newHand = currentHand.toMutableList()
        deck.dealCard()?.let { newHand.add(it) }
        return newHand
    }

    fun determineHandResult(
        playerHand: List<Card>,
        dealerHand: List<Card>
    ): HandResult {
        val playerTotal = handTotal(playerHand)
        val dealerTotal = handTotal(dealerHand)

        val playerBlackjack = checkBlackjack(playerHand)
        val dealerBlackjack = checkBlackjack(dealerHand)

        if (playerBlackjack && !dealerBlackjack) return HandResult.BLACKJACK
        if (playerBlackjack && dealerBlackjack) return HandResult.DRAW

        if (playerTotal > 21) return HandResult.BUST
        if (dealerTotal > 21) return HandResult.WIN

        return when {
            playerTotal > dealerTotal -> HandResult.WIN
            playerTotal < dealerTotal -> HandResult.LOSS
            else -> HandResult.DRAW
        }
    }

    fun calculatePoints(result: HandResult): Int {
        return when (result) {
            HandResult.BLACKJACK -> 3
            HandResult.WIN -> 2
            HandResult.DRAW -> 1
            HandResult.LOSS, HandResult.BUST -> 0
        }
    }

    fun formatCard(card: Card): String {
        val suitSymbol = when (card.suit) {
            Suit.HEARTS -> "♥"
            Suit.DIAMONDS -> "♦"
            Suit.CLUBS -> "♣"
            Suit.SPADES -> "♠"
        }
        return "${card.rank.displayName}$suitSymbol"
    }

    fun formatHand(hand: List<Card>): String {
        return hand.joinToString(" ") { formatCard(it) }
    }
}
