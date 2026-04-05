package app.krafted.jokersblackjack.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.jokersblackjack.R
import app.krafted.jokersblackjack.game.Difficulty
import app.krafted.jokersblackjack.game.HandResult
import app.krafted.jokersblackjack.ui.theme.GoldAccent
import app.krafted.jokersblackjack.ui.theme.LossRed
import app.krafted.jokersblackjack.ui.theme.PurpleAccent
import app.krafted.jokersblackjack.ui.theme.PurpleDeep
import app.krafted.jokersblackjack.ui.theme.WinGreen
import app.krafted.jokersblackjack.viewmodel.GameViewModel

@Composable
fun SessionResultScreen(
    onNavigateHome: () -> Unit,
    onPlayAgain: () -> Unit,
    viewModel: GameViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as androidx.activity.ComponentActivity
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    val backgroundRes = when (uiState.difficulty) {
        Difficulty.EASY -> R.drawable.jok014_back_4
        Difficulty.MEDIUM -> R.drawable.jok014_back_5
        Difficulty.HARD -> R.drawable.jok014_back_4
    }

    val diffColor = when (uiState.difficulty) {
        Difficulty.EASY -> WinGreen
        Difficulty.MEDIUM -> Color(0xFFFF9800)
        Difficulty.HARD -> LossRed
    }

    val scaleIn = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        scaleIn.animateTo(
            1f,
            tween(500, easing = androidx.compose.animation.core.LinearOutSlowInEasing)
        )
    }

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
                .padding(horizontal = 22.dp)
                .graphicsLayer {
                    scaleX = scaleIn.value
                    scaleY = scaleIn.value
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "SESSION COMPLETE",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 4.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(diffColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = uiState.difficulty.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    color = diffColor
                )
            }

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(uiState.sessionResults) { index, result ->
                    HandResultRow(handNumber = index + 1, result = result)
                }
                val remainingHands = (uiState.sessionResults.size + 1)..5
                items(remainingHands.count()) { offset ->
                    HandResultRow(
                        handNumber = uiState.sessionResults.size + 1 + offset,
                        result = null
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            ScoreSummary(
                score = uiState.sessionScore,
                maxScore = 15,
                isNewBest = uiState.isNewBest
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SessionButton(
                    text = "PLAY AGAIN",
                    onClick = {
                        viewModel.resetSession()
                        onPlayAgain()
                    },
                    modifier = Modifier.weight(1f)
                )
                SessionButton(
                    text = "HOME",
                    onClick = {
                        viewModel.clearSession()
                        onNavigateHome()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HandResultRow(handNumber: Int, result: HandResult?) {
    val rowScale = remember { Animatable(0.9f) }
    LaunchedEffect(Unit) {
        rowScale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    val (icon, points, resultColor) = when (result) {
        HandResult.WIN -> Triple("\u2713 WIN", "+2", WinGreen)
        HandResult.BLACKJACK -> Triple("\u2605 BLACKJACK", "+3", GoldAccent)
        HandResult.DRAW -> Triple("\u2014 DRAW", "+1", Color(0xFFBDBDBD))
        HandResult.LOSS -> Triple("\u2717 LOSS", "+0", LossRed)
        HandResult.BUST -> Triple("! BUST", "+0", LossRed)
        null -> Triple("", "", Color.White.copy(alpha = 0.2f))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.3f),
                        Color.Black.copy(alpha = 0.15f)
                    )
                )
            )
            .border(1.dp, resultColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .graphicsLayer {
                scaleX = rowScale.value
                scaleY = rowScale.value
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "H$handNumber",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                color = Color.White.copy(alpha = 0.5f)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = icon,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 1.sp,
                color = resultColor
            )
        }
        Text(
            text = points,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif,
            color = resultColor
        )
    }
}

@Composable
private fun ScoreSummary(score: Int, maxScore: Int, isNewBest: Boolean) {
    val pulseTransition = rememberInfiniteTransition(label = "score_pulse")
    val glowAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isNewBest) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "best_glow"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.4f),
                        Color.Black.copy(alpha = 0.2f)
                    )
                )
            )
            .border(1.dp, PurpleAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOTAL:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "$score / $maxScore pts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                letterSpacing = 1.sp,
                color = GoldAccent
            )
        }

        if (isNewBest) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoldAccent.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "\u2B50",
                    fontSize = 18.sp,
                    modifier = Modifier.graphicsLayer { alpha = glowAlpha }
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "NEW BEST!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    color = GoldAccent
                )
            }
        }
    }
}

@Composable
private fun SessionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btn_scale"
    )

    val glowTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_glow_val"
    )

    Box(
        modifier = modifier.height(56.dp),
        contentAlignment = Alignment.Center
    ) {
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

        Button(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { scaleX = scale; scaleY = scale },
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF5D1040), PurpleDeep),
                            radius = 300f
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.82f), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 3.sp
                )
            }
        }
    }
}
