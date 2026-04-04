package app.krafted.jokersblackjack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.jokersblackjack.R
import app.krafted.jokersblackjack.game.Difficulty
import app.krafted.jokersblackjack.game.GamePhase
import app.krafted.jokersblackjack.game.HandResult
import app.krafted.jokersblackjack.ui.HandResultOverlay
import app.krafted.jokersblackjack.ui.components.HandDisplay
import app.krafted.jokersblackjack.ui.components.JokerQuote
import app.krafted.jokersblackjack.viewmodel.GameViewModel

private val PurpleAccent = Color(0xFF5A1870)
private val PurpleDeep = Color(0xFF2C0820)
private val Parchment = Color(0xFFF0E8D0)
private val GoldAccent = Color(0xFFFFD700)
private val WinGreen = Color(0xFF4CAF50)
private val LossRed = Color(0xFFEF5350)

@Composable
fun GameScreen(
    difficulty: String,
    onNavigateToSessionResult: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        val diff = try {
            Difficulty.valueOf(difficulty.uppercase())
        } catch (e: Exception) {
            Difficulty.EASY
        }
        viewModel.startSession(diff)
    }

    val showOverlay = uiState.gamePhase == GamePhase.HAND_COMPLETE && uiState.handResult != null

    val backgroundRes = when (uiState.difficulty) {
        Difficulty.EASY -> R.drawable.jok014_back_1
        Difficulty.MEDIUM -> R.drawable.jok014_back_2
        Difficulty.HARD -> R.drawable.jok014_back_3
    }

    val isPlayerBlackjack = uiState.playerHand.size == 2 && uiState.playerTotal == 21
    val isDealerBlackjack =
        uiState.dealerHand.size == 2 && uiState.dealerTotal == 21 && uiState.isDealerRevealed
    val isPlayerTurn = uiState.gamePhase == GamePhase.PLAYER_TURN
    val isDealerTurn = uiState.gamePhase == GamePhase.DEALER_TURN

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x70000000))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.55f),
                            0.15f to Color.Transparent,
                            0.85f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.55f)
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp))
            ScoreHeader(
                handNumber = uiState.sessionHandNumber,
                score = uiState.sessionScore,
                sessionResults = uiState.sessionResults,
                difficulty = uiState.difficulty
            )

            Spacer(Modifier.height(12.dp))

            DealerSection(
                uiState = uiState,
                isDealerBlackjack = isDealerBlackjack,
                isDealerTurn = isDealerTurn
            )

            Spacer(Modifier.height(12.dp))
            Canvas(modifier = Modifier.size(width = 26.dp, height = 14.dp)) {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path, color = PurpleAccent)
            }

            JokerQuote(
                quote = uiState.jokerQuote,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            PlayerSection(
                uiState = uiState,
                isPlayerBlackjack = isPlayerBlackjack,
                isPlayerTurn = isPlayerTurn
            )

            Spacer(Modifier.weight(1f))

            PhaseIndicator(uiState.gamePhase)

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = uiState.gamePhase != GamePhase.DEALING,
                enter = fadeIn(tween(400)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = 300f)
                ),
                exit = fadeOut(tween(200))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    val hitSource = remember { MutableInteractionSource() }
                    val isHitPressed by hitSource.collectIsPressedAsState()
                    val hitScale by animateFloatAsState(
                        targetValue = if (isHitPressed) 0.91f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "hit"
                    )
                    GameButton(
                        text = "HIT",
                        enabled = isPlayerTurn,
                        scale = hitScale,
                        interactionSource = hitSource,
                        onClick = { viewModel.playerHit() },
                        modifier = Modifier.weight(1f)
                    )

                    val standSource = remember { MutableInteractionSource() }
                    val isStandPressed by standSource.collectIsPressedAsState()
                    val standScale by animateFloatAsState(
                        targetValue = if (isStandPressed) 0.91f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "stand"
                    )
                    GameButton(
                        text = "STAND",
                        enabled = isPlayerTurn,
                        scale = standScale,
                        interactionSource = standSource,
                        onClick = { viewModel.playerStand() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (showOverlay) {
            HandResultOverlay(
                result = uiState.handResult!!,
                playerHand = uiState.playerHand,
                dealerHand = uiState.dealerHand,
                jokerQuote = uiState.jokerQuote,
                handNumber = uiState.sessionHandNumber,
                difficulty = uiState.difficulty,
                sessionResults = uiState.sessionResults,
                sessionScore = uiState.sessionScore,
                onNextHand = {
                    if (uiState.sessionHandNumber >= 5) {
                        viewModel.nextHand()
                        onNavigateToSessionResult()
                    } else {
                        viewModel.nextHand()
                    }
                }
            )
        }
    }
}

@Composable
private fun DealerSection(
    uiState: app.krafted.jokersblackjack.game.GameUiState,
    isDealerBlackjack: Boolean,
    isDealerTurn: Boolean
) {
    val dealerPulse = rememberInfiniteTransition(label = "dealer_pulse")
    val dealerGlow by dealerPulse.animateFloat(
        initialValue = 0f,
        targetValue = if (isDealerTurn) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dealer_glow"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isDealerTurn) {
            Text(
                text = "JOKER'S HAND",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 3.sp,
                color = Color.White.copy(alpha = 0.5f + dealerGlow * 0.5f)
            )
            Spacer(Modifier.height(8.dp))
        }

        HandDisplay(
            cards = uiState.dealerHand,
            total = uiState.dealerTotal,
            isRevealed = uiState.isDealerRevealed,
            isDealer = true,
            isBust = uiState.dealerTotal > 21,
            isBlackjack = isDealerBlackjack,
            label = if (isDealerTurn) null else "JOKER'S HAND"
        )
    }
}

@Composable
private fun PlayerSection(
    uiState: app.krafted.jokersblackjack.game.GameUiState,
    isPlayerBlackjack: Boolean,
    isPlayerTurn: Boolean
) {
    val playerGlow = rememberInfiniteTransition(label = "player_pulse")
    val playerAlpha by playerGlow.animateFloat(
        initialValue = 0.6f,
        targetValue = if (isPlayerTurn) 1f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "player_glow"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isPlayerTurn) {
            Text(
                text = "YOUR HAND",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 3.sp,
                color = GoldAccent.copy(alpha = playerAlpha)
            )
            Spacer(Modifier.height(8.dp))
        }

        HandDisplay(
            cards = uiState.playerHand,
            total = uiState.playerTotal,
            isRevealed = true,
            isDealer = false,
            isBust = uiState.playerTotal > 21,
            isBlackjack = isPlayerBlackjack,
            label = if (isPlayerTurn) null else "YOUR HAND"
        )
    }
}

@Composable
private fun PhaseIndicator(phase: GamePhase) {
    val pulseTransition = rememberInfiniteTransition(label = "phase_pulse")
    val dotAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase_dot"
    )

    val phaseText = when (phase) {
        GamePhase.DEALING -> "Dealing..."
        GamePhase.PLAYER_TURN -> "Your turn"
        GamePhase.DEALER_TURN -> "Joker's turn..."
        GamePhase.HAND_COMPLETE -> ""
    }

    val phaseColor = when (phase) {
        GamePhase.PLAYER_TURN -> GoldAccent
        GamePhase.DEALER_TURN -> LossRed.copy(alpha = 0.8f)
        else -> Color.White.copy(alpha = 0.5f)
    }

    if (phaseText.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (phase == GamePhase.DEALING || phase == GamePhase.DEALER_TURN) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { i ->
                        val staggeredAlpha by pulseTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    600,
                                    delayMillis = i * 150,
                                    easing = LinearEasing
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "dot_$i"
                        )
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .graphicsLayer { alpha = staggeredAlpha }
                                .clip(CircleShape)
                                .background(phaseColor)
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = phaseText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 2.sp,
                color = phaseColor
            )
        }
    }
}

@Composable
private fun ScoreHeader(
    handNumber: Int,
    score: Int,
    sessionResults: List<HandResult>,
    difficulty: Difficulty
) {
    val scoreScale = remember { Animatable(1f) }
    var lastScore by remember { mutableIntStateOf(score) }

    val scoreFlashColor by animateColorAsState(
        targetValue = when {
            score > lastScore -> WinGreen
            else -> Color.White
        },
        animationSpec = tween(400),
        label = "score_flash"
    )

    LaunchedEffect(score) {
        if (score != lastScore) {
            lastScore = score
            scoreScale.animateTo(1.25f, tween(100))
            scoreScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    val diffColor = when (difficulty) {
        Difficulty.EASY -> WinGreen
        Difficulty.MEDIUM -> Color(0xFFFF9800)
        Difficulty.HARD -> LossRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.35f),
                        Color.Black.copy(alpha = 0.15f)
                    )
                )
            )
            .border(1.dp, PurpleAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(diffColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Hand $handNumber of 5",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White
                    )
                }
                Text(
                    text = "$score / 15",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.sp,
                    color = scoreFlashColor,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scoreScale.value
                        scaleY = scoreScale.value
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            SessionProgressBar(
                currentHand = handNumber,
                results = sessionResults
            )
        }
    }
}

@Composable
private fun SessionProgressBar(
    currentHand: Int,
    results: List<HandResult>
) {
    val pulseTransition = rememberInfiniteTransition(label = "dot_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)) {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.1f),
                cornerRadius = CornerRadius(2f),
                size = Size(size.width, size.height)
            )
            val filledWidth = if (results.isNotEmpty())
                size.width * (results.size / 5f) else 0f
            if (filledWidth > 0f) {
                drawRoundRect(
                    color = PurpleAccent.copy(alpha = 0.6f),
                    cornerRadius = CornerRadius(2f),
                    size = Size(filledWidth, size.height)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                when {
                    i <= results.size -> {
                        val resultColor = when (results[i - 1]) {
                            HandResult.WIN -> WinGreen
                            HandResult.BLACKJACK -> GoldAccent
                            HandResult.DRAW -> Color(0xFFBDBDBD)
                            HandResult.LOSS, HandResult.BUST -> LossRed
                        }
                        val resultIcon = when (results[i - 1]) {
                            HandResult.WIN -> "\u2713"
                            HandResult.BLACKJACK -> "\u2605"
                            HandResult.DRAW -> "\u2014"
                            HandResult.LOSS -> "\u2717"
                            HandResult.BUST -> "!"
                        }
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(resultColor.copy(alpha = 0.2f))
                                .border(1.5.dp, resultColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = resultIcon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = resultColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    i == currentHand -> {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .border(2.dp, GoldAccent.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$i",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = GoldAccent
                            )
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$i",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color.White.copy(alpha = 0.25f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameButton(
    text: String,
    enabled: Boolean,
    scale: Float,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (enabled) Color(0xFF3D0E28) else Color(0xFF1C1C1C).copy(alpha = 0.5f)
    val borderAlpha by animateFloatAsState(
        targetValue = if (enabled) 0.82f else 0.10f,
        animationSpec = tween(300),
        label = "border"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.28f,
        animationSpec = tween(300),
        label = "text"
    )

    val glowTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = if (enabled) 0.12f else 0f,
        targetValue = if (enabled) 0.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier.height(62.dp),
        contentAlignment = Alignment.Center
    ) {
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { this.alpha = glowAlpha }
                    .shadow(
                        elevation = 28.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = PurpleAccent,
                        spotColor = PurpleAccent
                    )
            )
        }

        Button(
            onClick = onClick,
            interactionSource = interactionSource,
            enabled = enabled,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { scaleX = scale; scaleY = scale },
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (enabled)
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF5D1040), PurpleDeep),
                                radius = 300f
                            )
                        else Brush.linearGradient(listOf(bgColor, bgColor))
                    )
                    .border(2.dp, Color.White.copy(alpha = borderAlpha), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = Color.White.copy(alpha = textAlpha),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    letterSpacing = 4.sp
                )
            }
        }
    }
}
