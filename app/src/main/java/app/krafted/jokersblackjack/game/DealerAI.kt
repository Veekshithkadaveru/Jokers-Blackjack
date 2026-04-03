package app.krafted.jokersblackjack.game

sealed class DealerAction {
    data object Hit : DealerAction()
    data object Stand : DealerAction()
}

class DealerAI {

    fun dealerDecision(
        dealerHand: List<Card>,
        playerTotal: Int,
        difficulty: Difficulty,
        deck: MutableList<Card>
    ): DealerAction {
        val dealerTotal = handTotal(dealerHand)
        val nextCard = deck.firstOrNull()

        return when (difficulty) {
            Difficulty.EASY -> {
                if (dealerTotal <= 16) DealerAction.Hit else DealerAction.Stand
            }

            Difficulty.MEDIUM -> {
                if (dealerTotal <= 16) {
                    if (nextCard != null && dealerTotal + nextCard.value > 21) {
                        DealerAction.Stand
                    } else {
                        DealerAction.Hit
                    }
                } else {
                    DealerAction.Stand
                }
            }

            Difficulty.HARD -> {
                if (dealerTotal >= 17) {
                    DealerAction.Stand
                } else if (nextCard != null && dealerTotal + nextCard.value > 21) {
                    DealerAction.Stand
                } else if (playerTotal <= 16) {
                    DealerAction.Stand
                } else {
                    DealerAction.Hit
                }
            }
        }
    }
}
