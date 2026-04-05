package app.krafted.jokersblackjack.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersblackjack.R
import app.krafted.jokersblackjack.game.Card
import app.krafted.jokersblackjack.game.Rank
import app.krafted.jokersblackjack.game.Suit

fun getCardDrawable(context: android.content.Context, card: Card): Int {
    val suitStr = card.suit.name.lowercase()
    val rankStr = when (card.rank) {
        Rank.JACK -> "j"
        Rank.QUEEN -> "q"
        Rank.KING -> "k"
        Rank.ACE -> "a"
        else -> card.rank.value.toString()
    }
    val resName = "card_${suitStr}_${rankStr}"
    return context.resources.getIdentifier(resName, "drawable", context.packageName)
}

private data class PipPos(val col: Int, val row: Int, val flipped: Boolean = false)

private val pipPositions: Map<Rank, List<PipPos>> = mapOf(
    Rank.ACE to listOf(PipPos(1, 2)),
    Rank.TWO to listOf(PipPos(1, 0), PipPos(1, 4, true)),
    Rank.THREE to listOf(PipPos(1, 0), PipPos(1, 2), PipPos(1, 4, true)),
    Rank.FOUR to listOf(PipPos(0, 0), PipPos(2, 0), PipPos(0, 4, true), PipPos(2, 4, true)),
    Rank.FIVE to listOf(
        PipPos(0, 0),
        PipPos(2, 0),
        PipPos(1, 2),
        PipPos(0, 4, true),
        PipPos(2, 4, true)
    ),
    Rank.SIX to listOf(
        PipPos(0, 0),
        PipPos(2, 0),
        PipPos(0, 2),
        PipPos(2, 2),
        PipPos(0, 4, true),
        PipPos(2, 4, true)
    ),
    Rank.SEVEN to listOf(
        PipPos(0, 0),
        PipPos(2, 0),
        PipPos(1, 1),
        PipPos(0, 2),
        PipPos(2, 2),
        PipPos(0, 4, true),
        PipPos(2, 4, true)
    ),
    Rank.EIGHT to listOf(
        PipPos(0, 0),
        PipPos(2, 0),
        PipPos(1, 1),
        PipPos(0, 2),
        PipPos(2, 2),
        PipPos(1, 3, true),
        PipPos(0, 4, true),
        PipPos(2, 4, true)
    ),
    Rank.NINE to listOf(
        PipPos(0, 0),
        PipPos(2, 0),
        PipPos(0, 1),
        PipPos(2, 1),
        PipPos(1, 2),
        PipPos(0, 3, true),
        PipPos(2, 3, true),
        PipPos(0, 4, true),
        PipPos(2, 4, true)
    ),
    Rank.TEN to listOf(
        PipPos(0, 0),
        PipPos(2, 0),
        PipPos(0, 1),
        PipPos(2, 1),
        PipPos(1, 1),
        PipPos(0, 3, true),
        PipPos(2, 3, true),
        PipPos(1, 3, true),
        PipPos(0, 4, true),
        PipPos(2, 4, true)
    )
)

@Composable
fun PlayingCard(
    card: Card,
    isRevealed: Boolean,
    modifier: Modifier = Modifier,
    backIndex: Int = 0,
    isBlackjack: Boolean = false
) {
    val flipProgress = remember { Animatable(0f) }
    var showingBack by remember { mutableStateOf(true) }

    val slideY = remember { Animatable(-800f) }
    LaunchedEffect(Unit) {
        slideY.animateTo(
            0f,
            animationSpec = spring(stiffness = 400f, dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    }

    LaunchedEffect(isRevealed) {
        if (isRevealed && showingBack) {
            flipProgress.animateTo(0.5f, tween(150))
            showingBack = false
            flipProgress.animateTo(1f, tween(150))
        } else if (!isRevealed && !showingBack) {
            flipProgress.animateTo(0.5f, tween(150))
            showingBack = true
            flipProgress.animateTo(1f, tween(150))
        }
    }

    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "blackjack_shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isBlackjack && !showingBack) 0.55f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Card(
        modifier = modifier
            .aspectRatio(0.68f)
            .graphicsLayer { translationY = slideY.value },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (flipProgress.value <= 0.5f) 1f - flipProgress.value * 2f
                    else (flipProgress.value - 0.5f) * 2f
                },
            contentAlignment = Alignment.Center
        ) {
            if (showingBack) {
                Image(
                    painter = painterResource(id = R.drawable.card_back_joker),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                val drawableId = getCardDrawable(context, card)
                if (drawableId != 0) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "Card Face",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    CardFace(card = card)
                }
            }

            if (isBlackjack && !showingBack) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFD700).copy(alpha = shimmerAlpha))
                )
            }
        }
    }
}

@Composable
private fun CardFace(card: Card) {
    val isRed = card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS
    val suitSymbol = when (card.suit) {
        Suit.HEARTS -> "♥"
        Suit.DIAMONDS -> "♦"
        Suit.CLUBS -> "♣"
        Suit.SPADES -> "♠"
    }
    val color = if (isRed) Color(0xFFCC1111) else Color(0xFF111111)
    val isFaceCard = card.rank in listOf(Rank.JACK, Rank.QUEEN, Rank.KING)

    val cardGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0F0F5)),
    )
    val borderGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFD4AF37), Color(0xFFAA7C11), Color(0xFFE2C252))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cardGradient)
            .border(1.5.dp, borderGradient, RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.displayName,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                lineHeight = 16.sp
            )
            Text(text = suitSymbol, color = color, fontSize = 13.sp, lineHeight = 13.sp)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .rotate(180f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.displayName,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                lineHeight = 16.sp
            )
            Text(text = suitSymbol, color = color, fontSize = 13.sp, lineHeight = 13.sp)
        }

        if (isFaceCard) {
            Text(
                text = suitSymbol,
                color = color.copy(alpha = 0.05f),
                fontSize = 100.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                color.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 18.dp)
            ) {
                Text(
                    text = card.rank.displayName,
                    color = color,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            PipGrid(suitSymbol = suitSymbol, color = color, rank = card.rank)
        }
    }
}

@Composable
private fun PipGrid(suitSymbol: String, color: Color, rank: Rank) {
    val pips = pipPositions[rank] ?: listOf(PipPos(1, 2))
    val grid = Array(5) { arrayOfNulls<Boolean>(3) }
    pips.forEach { grid[it.row][it.col] = it.flipped }

    val pipFontSize = when (rank) {
        Rank.ACE -> 36.sp
        Rank.TWO, Rank.THREE -> 22.sp
        else -> 18.sp
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 24.dp, bottom = 24.dp, start = 12.dp, end = 12.dp)
    ) {
        val cellWidth = maxWidth / 3f
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (row in 0..4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0..2) {
                        val cell = grid[row][col]
                        Box(
                            modifier = Modifier.size(cellWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell != null) {
                                Text(
                                    text = suitSymbol,
                                    color = color,
                                    fontSize = pipFontSize,
                                    fontFamily = FontFamily.Default,
                                    modifier = if (cell) Modifier.rotate(180f) else Modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}