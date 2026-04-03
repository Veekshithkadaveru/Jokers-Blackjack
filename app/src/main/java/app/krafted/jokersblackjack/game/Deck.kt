package app.krafted.jokersblackjack.game

enum class Suit { HEARTS, DIAMONDS, CLUBS, SPADES }

enum class Rank(val displayName: String, val value: Int) {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 10),
    QUEEN("Q", 10),
    KING("K", 10),
    ACE("A", 11)
}

data class Card(val suit: Suit, val rank: Rank) {
    val value: Int get() = rank.value
}

fun handTotal(cards: List<Card>): Int {
    var total = cards.sumOf { it.value }
    var aces = cards.count { it.rank == Rank.ACE }
    while (total > 21 && aces > 0) {
        total -= 10
        aces--
    }
    return total
}

fun buildDeck(): MutableList<Card> {
    val deck = mutableListOf<Card>()
    for (suit in Suit.entries) {
        for (rank in Rank.entries) {
            deck.add(Card(suit, rank))
        }
    }
    return deck
}

fun MutableList<Card>.shuffleDeck() {
    for (i in size - 1 downTo 1) {
        val j = (0..i).random()
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }
}

fun MutableList<Card>.dealCard(): Card? = if (isNotEmpty()) removeAt(0) else null
