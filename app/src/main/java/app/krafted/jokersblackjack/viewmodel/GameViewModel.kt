package app.krafted.jokersblackjack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.jokersblackjack.data.QuoteRepository
import app.krafted.jokersblackjack.data.db.AppDatabase
import app.krafted.jokersblackjack.data.db.SessionRecord
import app.krafted.jokersblackjack.game.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = BlackjackEngine()
    private val dealerAI = DealerAI()
    private val quoteRepository = QuoteRepository(application)
    private val dao = AppDatabase.getInstance(application).blackjackDao()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadBestScores()
    }

    private fun loadBestScores() {
        viewModelScope.launch {
            val scores = Difficulty.entries.associateWith { difficulty ->
                dao.getBestScore(difficulty.name) ?: 0
            }
            _uiState.update { it.copy(bestScores = scores) }
        }
    }

    fun startSession(difficulty: Difficulty) {
        viewModelScope.launch {
            val deck = buildDeck().also { it.shuffleDeck() }
            val (playerHand, dealerHand) = engine.dealInitialHands(deck)
            val playerTotal = handTotal(playerHand)
            val dealerTotal = handTotal(dealerHand)

            val initialQuote = quoteRepository.getRandomQuote("deal")

            _uiState.update {
                it.copy(
                    difficulty = difficulty,
                    sessionHandNumber = 1,
                    deck = deck,
                    playerHand = playerHand,
                    dealerHand = dealerHand,
                    playerTotal = playerTotal,
                    dealerTotal = dealerTotal,
                    isDealerRevealed = false,
                    gamePhase = GamePhase.DEALING,
                    handResult = null,
                    sessionResults = emptyList(),
                    sessionScore = 0,
                    isSessionComplete = false,
                    isNewBest = false,
                    jokerQuote = initialQuote
                )
            }

            delay(500)

            val playerBJ = engine.checkBlackjack(playerHand)
            val dealerBJ = engine.checkBlackjack(dealerHand)

            if (playerBJ || dealerBJ) {
                val result = engine.determineHandResult(playerHand, dealerHand)
                completeHand(result, playerHand, dealerHand)
            } else {
                _uiState.update { it.copy(gamePhase = GamePhase.PLAYER_TURN) }
            }
        }
    }

    fun playerHit() {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.PLAYER_TURN) return

        val newPlayerHand = engine.playerHit(state.deck, state.playerHand)
        val newPlayerTotal = handTotal(newPlayerHand)
        val quote = quoteRepository.getRandomQuote("player_hit")

        _uiState.update {
            it.copy(
                playerHand = newPlayerHand,
                playerTotal = newPlayerTotal,
                jokerQuote = quote
            )
        }

        if (engine.checkBust(newPlayerHand)) {
            completeHand(HandResult.BUST, newPlayerHand, state.dealerHand)
        }
    }

    fun playerStand() {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.PLAYER_TURN) return

        val quote = quoteRepository.getRandomQuote("player_stand")
        _uiState.update {
            it.copy(
                gamePhase = GamePhase.DEALER_TURN,
                isDealerRevealed = true,
                jokerQuote = quote
            )
        }

        runDealerTurn()
    }

    private fun runDealerTurn() {
        viewModelScope.launch {
            val state = _uiState.value
            var currentDealerHand = state.dealerHand.toList()
            var currentDeck = state.deck.toMutableList()

            while (true) {
                val dealerTotal = handTotal(currentDealerHand)
                val decision = dealerAI.dealerDecision(
                    currentDealerHand,
                    state.playerTotal,
                    state.difficulty,
                    currentDeck
                )

                if (decision == DealerAction.Stand) break

                val card = currentDeck.dealCard() ?: break
                currentDealerHand = currentDealerHand + card
                _uiState.update {
                    it.copy(
                        dealerHand = currentDealerHand,
                        dealerTotal = handTotal(currentDealerHand),
                        deck = currentDeck
                    )
                }
                delay(400)
            }

            val finalPlayerHand = state.playerHand
            val result = engine.determineHandResult(finalPlayerHand, currentDealerHand)
            completeHand(result, finalPlayerHand, currentDealerHand)
        }
    }

    private fun completeHand(
        result: HandResult,
        playerHand: List<Card>,
        dealerHand: List<Card>
    ) {
        val state = _uiState.value
        val points = engine.calculatePoints(result)
        val eventKey = when (result) {
            HandResult.BLACKJACK -> "player_blackjack"
            HandResult.WIN -> "player_win"
            HandResult.LOSS -> "player_loss"
            HandResult.BUST -> "player_bust"
            HandResult.DRAW -> "draw"
        }
        val quote = quoteRepository.getRandomQuote(eventKey)

        val newResults = state.sessionResults + result
        val newScore = state.sessionScore + points

        _uiState.update {
            it.copy(
                handResult = result,
                sessionResults = newResults,
                sessionScore = newScore,
                gamePhase = GamePhase.HAND_COMPLETE,
                isDealerRevealed = true,
                jokerQuote = quote
            )
        }
    }

    fun nextHand() {
        val state = _uiState.value

        if (state.sessionHandNumber >= 5) {
            completeSession()
            return
        }

        val nextHandNumber = state.sessionHandNumber + 1
        val deck = state.deck.toMutableList().also { if (it.size < 20) { it.addAll(buildDeck()); it.shuffleDeck() } }
        val (playerHand, dealerHand) = engine.dealInitialHands(deck)
        val playerTotal = handTotal(playerHand)
        val dealerTotal = handTotal(dealerHand)
        val quote = quoteRepository.getRandomQuote("deal")

        _uiState.update {
            it.copy(
                sessionHandNumber = nextHandNumber,
                deck = deck,
                playerHand = playerHand,
                dealerHand = dealerHand,
                playerTotal = playerTotal,
                dealerTotal = dealerTotal,
                isDealerRevealed = false,
                gamePhase = GamePhase.DEALING,
                handResult = null,
                jokerQuote = quote
            )
        }

        viewModelScope.launch {
            delay(500)
            val playerBJ = engine.checkBlackjack(playerHand)
            val dealerBJ = engine.checkBlackjack(dealerHand)
            if (playerBJ || dealerBJ) {
                val result = engine.determineHandResult(playerHand, dealerHand)
                completeHand(result, playerHand, dealerHand)
            } else {
                _uiState.update { it.copy(gamePhase = GamePhase.PLAYER_TURN) }
            }
        }
    }

    private fun completeSession() {
        viewModelScope.launch {
            val state = _uiState.value
            val currentBest = state.bestScores[state.difficulty] ?: 0
            val isNewBest = state.sessionScore > currentBest

            if (isNewBest) {
                dao.insertSession(
                    SessionRecord(
                        difficulty = state.difficulty.name,
                        score = state.sessionScore
                    )
                )
            }

            val eventKey = if (state.sessionScore > 5) "session_complete_win" else "session_complete_loss"
            val quote = quoteRepository.getRandomQuote(eventKey)

            _uiState.update {
                it.copy(
                    isSessionComplete = true,
                    isNewBest = isNewBest,
                    jokerQuote = quote
                )
            }

            loadBestScores()
        }
    }

    fun getBestScore(difficulty: Difficulty): Int {
        return _uiState.value.bestScores[difficulty] ?: 0
    }
}
