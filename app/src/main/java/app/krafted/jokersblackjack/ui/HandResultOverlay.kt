package app.krafted.jokersblackjack.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersblackjack.R
import app.krafted.jokersblackjack.game.BlackjackEngine
import app.krafted.jokersblackjack.game.Card
import app.krafted.jokersblackjack.game.Difficulty
import app.krafted.jokersblackjack.game.HandResult
import app.krafted.jokersblackjack.game.Suit
import app.krafted.jokersblackjack.game.handTotal
import kotlinx.coroutines.delay
import kotlin.math.sin

private val engine = BlackjackEngine()
private val ParchmentBg = Color(0xFFF0E8D0)
private val PurpleBorder = Color(0xFF5A1870)
private val DarkText = Color(0xFF1A0C04)
private val WinGreen = Color(0xFF4CAF50)
private val GoldAccent = Color(0xFFFFD700)
private val LossRed = Color(0xFFEF5350)

@Composable
fun HandResultOverlay(
    result: HandResult,
    playerHand: List<Card>,
    dealerHand: List<Card>,
    jokerQuote: String,
    handNumber: Int,
    difficulty: Difficulty,
    sessionResults: List<HandResult>,
    sessionScore: Int,
    onNextHand: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var visible by remember { mutableStateOf(false) }
    val slideOffset by animateFloatAsState(
        targetValue = if (visible) 0f else screenHeight.value,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.8f),
        label = "slide"
    )

    LaunchedEffect(Unit) { visible = true }

    val (icon, label, accentColor, points) = resultDisplay(result)

    val backgroundRes = when (difficulty) {
        Difficulty.EASY -> R.drawable.jok014_back_1
        Difficulty.MEDIUM -> R.drawable.jok014_back_2
        Difficulty.HARD -> R.drawable.jok014_back_3
    }

    val iconScale = remember { Animatable(0f) }
    val iconRotation = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0f) }
    val pointsAlpha = remember { Animatable(0f) }
    val panelAlpha = remember { Animatable(0f) }
    val quoteAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }
    val dotsAlpha = remember { Animatable(0f) }

    var animatedPoints by remember { mutableIntStateOf(0) }
    var showQuoteText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(250)
        iconScale.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 350f))
        iconRotation.animateTo(360f, tween(400))
        titleScale.animateTo(1f, tween(350, easing = EaseOutBack))
        pointsAlpha.animateTo(1f, tween(250))

        for (i in 0..points) {
            animatedPoints = i
            if (i < points) delay(100)
        }

        panelAlpha.animateTo(1f, tween(400))
        delay(100)
        quoteAlpha.animateTo(1f, tween(350))
        showQuoteText = true
        dotsAlpha.animateTo(1f, tween(300))
        delay(200)
        buttonAlpha.animateTo(1f, tween(350))
    }

    val bustShakeX = remember { Animatable(0f) }
    LaunchedEffect(result) {
        if (result == HandResult.BUST || result == HandResult.LOSS) {
            delay(350)
            repeat(4) {
                bustShakeX.animateTo(20f, tween(35))
                bustShakeX.animateTo(-20f, tween(35))
            }
            bustShakeX.animateTo(0f, spring(stiffness = Spring.StiffnessHigh))
        }
    }

    val isBlackjack = result == HandResult.BLACKJACK
    val isWin = result == HandResult.WIN || isBlackjack
    val shimmerTransition = rememberInfiniteTransition(label = "bj_shimmer")
    val shimmerAlpha by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isBlackjack) 0.7f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    val shimmerScale by shimmerTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = if (isBlackjack) 1.3f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_scale"
    )

    val tintColor = when (result) {
        HandResult.WIN -> WinGreen.copy(alpha = 0.08f)
        HandResult.BLACKJACK -> GoldAccent.copy(alpha = 0.1f)
        HandResult.BUST, HandResult.LOSS -> LossRed.copy(alpha = 0.06f)
        HandResult.DRAW -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = slideOffset.dp)
    ) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tintColor)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        Brush.radialGradient(
                            0f to accentColor.copy(alpha = 0.08f),
                            0.5f to Color.Transparent,
                            1f to Color.Transparent,
                            center = Offset(size.width / 2f, size.height * 0.25f)
                        )
                    )
                }
        )

        if (isBlackjack) {
            BlackjackParticles()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(contentAlignment = Alignment.Center) {
                if (isBlackjack) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .graphicsLayer {
                                alpha = shimmerAlpha
                                scaleX = shimmerScale
                                scaleY = shimmerScale
                            }
                            .blur(24.dp)
                            .background(GoldAccent, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .graphicsLayer { alpha = shimmerAlpha * 0.5f }
                            .blur(12.dp)
                            .background(Color.White, CircleShape)
                    )
                }
                if (isWin && !isBlackjack) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .graphicsLayer { alpha = 0.3f }
                            .blur(18.dp)
                            .background(WinGreen, CircleShape)
                    )
                }

                Text(
                    text = icon,
                    fontSize = 60.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = iconScale.value
                        scaleY = iconScale.value
                        rotationZ = iconRotation.value
                        translationX = bustShakeX.value
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = label,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                letterSpacing = 4.sp,
                color = accentColor,
                modifier = Modifier.graphicsLayer {
                    scaleX = titleScale.value
                    scaleY = titleScale.value
                    alpha = titleScale.value
                }
            )

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer { alpha = pointsAlpha.value }
            ) {
                OrnamentLine(accentColor.copy(alpha = 0.5f))
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "+$animatedPoints pts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.width(14.dp))
                OrnamentLine(accentColor.copy(alpha = 0.5f))
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = panelAlpha.value
                        translationY = (1f - panelAlpha.value) * 40f
                    }
                    .shadow(8.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(ParchmentBg)
                    .border(3.dp, PurpleBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                HandSummaryRow(
                    "YOUR HAND", playerHand, DarkText, accentColor = when (result) {
                        HandResult.WIN, HandResult.BLACKJACK -> WinGreen
                        else -> LossRed
                    }
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(PurpleBorder.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(6.dp)
                            .rotate(45f)
                            .background(PurpleBorder.copy(alpha = 0.4f))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(PurpleBorder.copy(alpha = 0.2f))
                    )
                }
                Spacer(Modifier.height(10.dp))
                HandSummaryRow(
                    "JOKER'S HAND", dealerHand, DarkText, accentColor = when (result) {
                        HandResult.WIN, HandResult.BLACKJACK -> LossRed.copy(alpha = 0.6f)
                        else -> WinGreen.copy(alpha = 0.8f)
                    }
                )
            }

            Spacer(Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .size(width = 26.dp, height = 14.dp)
                    .graphicsLayer { alpha = quoteAlpha.value }
            ) {
                val path = Path().apply {
                    moveTo(size.width / 2f, size.height)
                    lineTo(size.width, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                drawPath(path, color = PurpleBorder)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = quoteAlpha.value
                        translationY = (1f - quoteAlpha.value) * 25f
                    }
                    .shadow(
                        4.dp,
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .background(ParchmentBg)
                    .border(
                        3.dp, PurpleBorder,
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 14.dp,
                            bottomEnd = 14.dp
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OverlayTypewriterQuote(quote = jokerQuote, startTyping = showQuoteText)
            }

            Spacer(Modifier.height(18.dp))

            SessionDotsRow(
                sessionResults = sessionResults,
                sessionScore = sessionScore,
                modifier = Modifier.graphicsLayer { alpha = dotsAlpha.value }
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onNextHand,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .graphicsLayer {
                        alpha = buttonAlpha.value
                        scaleX = 0.85f + buttonAlpha.value * 0.15f
                        scaleY = 0.85f + buttonAlpha.value * 0.15f
                    }
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = Color.White,
                        spotColor = Color.White
                    ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF5D1040), Color(0xFF2C0820)),
                                radius = 300f
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.82f), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (handNumber >= 5) "VIEW RESULTS" else "NEXT HAND",
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp,
                        letterSpacing = 4.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BlackjackParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particles = remember {
        List(12) {
            ParticleData(
                startX = (0.05f + Math.random().toFloat() * 0.9f),
                startY = (Math.random().toFloat()),
                speed = 0.3f + Math.random().toFloat() * 0.7f,
                size = 6f + Math.random().toFloat() * 10f,
                rotSpeed = 0.5f + Math.random().toFloat() * 1.5f
            )
        }
    }

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val y = ((p.startY + time * p.speed) % 1.1f) * size.height
            val x = p.startX * size.width + sin((time + p.startY) * 6.28).toFloat() * 30f
            val rotation = time * 360f * p.rotSpeed
            val alpha = 0.15f + sin((time + p.startX) * 3.14).toFloat() * 0.25f

            val diamond = Path().apply {
                moveTo(x, y - p.size)
                lineTo(x + p.size * 0.6f, y)
                lineTo(x, y + p.size)
                lineTo(x - p.size * 0.6f, y)
                close()
            }
            drawPath(diamond, GoldAccent.copy(alpha = alpha.coerceIn(0f, 0.4f)))
        }
    }
}

private data class ParticleData(
    val startX: Float,
    val startY: Float,
    val speed: Float,
    val size: Float,
    val rotSpeed: Float
)

@Composable
private fun OverlayTypewriterQuote(quote: String, startTyping: Boolean) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(startTyping, quote) {
        if (startTyping) {
            displayedText = ""
            for (i in quote.indices) {
                displayedText += quote[i]
                delay(18)
            }
        }
    }

    Text(
        text = "\u201c$displayedText\u201d",
        fontFamily = FontFamily.Serif,
        fontSize = 17.sp,
        lineHeight = 25.sp,
        color = DarkText,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun OrnamentLine(color: Color) {
    Canvas(modifier = Modifier.size(width = 44.dp, height = 6.dp)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 1.5f
        )
        drawCircle(color = color, radius = 3f, center = Offset(0f, size.height / 2))
        drawCircle(color = color, radius = 3f, center = Offset(size.width, size.height / 2))
        val diamond = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(size.width / 2f + 4f, size.height / 2f)
            lineTo(size.width / 2f, size.height)
            lineTo(size.width / 2f - 4f, size.height / 2f)
            close()
        }
        drawPath(diamond, color)
    }
}

@Composable
private fun SessionDotsRow(
    sessionResults: List<HandResult>,
    sessionScore: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        for (i in 1..5) {
            when {
                i <= sessionResults.size -> {
                    val dotColor = when (sessionResults[i - 1]) {
                        HandResult.WIN -> WinGreen
                        HandResult.BLACKJACK -> GoldAccent
                        HandResult.DRAW -> Color(0xFFBDBDBD)
                        HandResult.LOSS, HandResult.BUST -> LossRed
                    }
                    val dotIcon = when (sessionResults[i - 1]) {
                        HandResult.WIN -> "\u2713"
                        HandResult.BLACKJACK -> "\u2605"
                        HandResult.DRAW -> "\u2014"
                        HandResult.LOSS -> "\u2717"
                        HandResult.BUST -> "!"
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(dotColor.copy(alpha = 0.2f))
                            .border(1.5.dp, dotColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dotIcon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = dotColor
                        )
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    )
                }
            }
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$sessionScore / 15",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 1.sp,
            color = Color.White.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun HandSummaryRow(label: String, hand: List<Card>, textColor: Color, accentColor: Color) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 3.sp,
            color = PurpleBorder
        )
        Spacer(Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                hand.forEach { card ->
                    val isRed = card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS
                    val suitSymbol = when (card.suit) {
                        Suit.HEARTS -> "\u2665"
                        Suit.DIAMONDS -> "\u2666"
                        Suit.CLUBS -> "\u2663"
                        Suit.SPADES -> "\u2660"
                    }
                    Text(
                        text = "${card.rank.displayName}$suitSymbol",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isRed) Color(0xFFCC1111) else textColor
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${handTotal(hand)}",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
            }
        }
    }
}

private data class ResultDisplayInfo(
    val icon: String,
    val label: String,
    val color: Color,
    val points: Int
)

private fun resultDisplay(result: HandResult): ResultDisplayInfo = when (result) {
    HandResult.WIN -> ResultDisplayInfo("\u2705", "YOU WIN!", WinGreen, 2)
    HandResult.BLACKJACK -> ResultDisplayInfo("\uD83C\uDCCF", "BLACKJACK!", GoldAccent, 3)
    HandResult.BUST -> ResultDisplayInfo("\uD83D\uDCA5", "BUST!", LossRed, 0)
    HandResult.LOSS -> ResultDisplayInfo("\u274C", "YOU LOSE", LossRed, 0)
    HandResult.DRAW -> ResultDisplayInfo("\uD83E\uDD1D", "DRAW", Color(0xFFBDBDBD), 1)
}
