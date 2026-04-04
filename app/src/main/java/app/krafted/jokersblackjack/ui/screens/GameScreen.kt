package app.krafted.jokersblackjack.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import app.krafted.jokersblackjack.ui.components.HandDisplay
import app.krafted.jokersblackjack.ui.components.JokerQuote
import app.krafted.jokersblackjack.viewmodel.GameViewModel

private val DarkScrim = Color(0x66000000)
private val ParchmentBorder = Color(0xFF5A1870)
private val MaroonButton = Color(0xFF3D0E28)
private val White = Color.White

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

    LaunchedEffect(uiState.isSessionComplete) {
        if (uiState.isSessionComplete) onNavigateToSessionResult()
    }

    val backgroundRes = when (uiState.difficulty) {
        Difficulty.EASY -> R.drawable.jok014_back_1
        Difficulty.MEDIUM -> R.drawable.jok014_back_2
        Difficulty.HARD -> R.drawable.jok014_back_3
    }

    val isPlayerBlackjack = uiState.playerHand.size == 2 && uiState.playerTotal == 21
    val isDealerBlackjack =
        uiState.dealerHand.size == 2 && uiState.dealerTotal == 21 && uiState.isDealerRevealed
    val isPlayerTurn = uiState.gamePhase == GamePhase.PLAYER_TURN

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(DarkScrim))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Hand ${uiState.sessionHandNumber} of 5",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                color = White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Score: ${uiState.sessionScore}/15 pts",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                color = White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))
            HandDisplay(
                cards = uiState.dealerHand,
                total = uiState.dealerTotal,
                isRevealed = uiState.isDealerRevealed,
                isDealer = true,
                isBust = uiState.dealerTotal > 21,
                isBlackjack = isDealerBlackjack,
                label = "JOKER'S HAND"
            )

            Spacer(Modifier.height(14.dp))
            Canvas(modifier = Modifier.size(width = 26.dp, height = 14.dp)) {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)          // apex (top center)
                    lineTo(size.width, size.height)       // bottom-right
                    lineTo(0f, size.height)               // bottom-left
                    close()
                }
                drawPath(path, color = Color(0xFF5A1870))
            }

            JokerQuote(
                quote = uiState.jokerQuote,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))
            HandDisplay(
                cards = uiState.playerHand,
                total = uiState.playerTotal,
                isRevealed = true,
                isDealer = false,
                isBust = uiState.playerTotal > 21,
                isBlackjack = isPlayerBlackjack,
                label = "YOUR HAND"
            )

            Spacer(Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                val hitSource = remember { MutableInteractionSource() }
                val isHitPressed by hitSource.collectIsPressedAsState()
                val hitScale by animateFloatAsState(
                    targetValue = if (isHitPressed) 0.95f else 1f,
                    animationSpec = spring(), label = "hit"
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
                    targetValue = if (isStandPressed) 0.95f else 1f,
                    animationSpec = spring(), label = "stand"
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
    val bgColor = if (enabled) MaroonButton else Color(0xFF1C1C1C).copy(alpha = 0.5f)
    val borderClr = if (enabled) White.copy(alpha = 0.82f) else White.copy(alpha = 0.10f)
    val textColor = if (enabled) White else White.copy(alpha = 0.28f)

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        enabled = enabled,
        modifier = modifier
            .height(62.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(
                elevation = if (enabled) 18.dp else 0.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color.White,
                spotColor = Color.White
            ),
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
                            colors = listOf(Color(0xFF5D1040), Color(0xFF2C0820)),
                            radius = 300f
                        )
                    else Brush.linearGradient(listOf(bgColor, bgColor))
                )
                .border(2.dp, borderClr, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                letterSpacing = 4.sp
            )
        }
    }
}
