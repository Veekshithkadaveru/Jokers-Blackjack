package app.krafted.jokersblackjack.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersblackjack.R
import app.krafted.jokersblackjack.game.Card
import app.krafted.jokersblackjack.game.Suit

private val backDrawables = listOf(
    R.drawable.jok014_sym_1,
    R.drawable.jok014_sym_2,
    R.drawable.jok014_sym_3,
    R.drawable.jok014_sym_4,
    R.drawable.jok014_sym_5,
    R.drawable.jok014_sym_6,
    R.drawable.jok014_sym_7
)

@Composable
fun PlayingCard(
    card: Card,
    isRevealed: Boolean,
    modifier: Modifier = Modifier,
    backIndex: Int = 0
) {
    val flipProgress = remember { Animatable(0f) }
    var isShowingBack by remember { mutableStateOf(!isRevealed) }

    LaunchedEffect(isRevealed) {
        if (isRevealed && isShowingBack) {
            flipProgress.animateTo(0.5f, tween(150))
            isShowingBack = false
            flipProgress.animateTo(1f, tween(150))
        } else if (!isRevealed && !isShowingBack) {
            flipProgress.animateTo(0.5f, tween(150))
            isShowingBack = true
            flipProgress.animateTo(1f, tween(150))
        }
    }

    val isCurrentlyShowingBack = flipProgress.value < 0.5f || isShowingBack

    Card(
        modifier = modifier
            .aspectRatio(0.7f)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (flipProgress.value <= 0.5f) {
                        1f - flipProgress.value * 2f
                    } else {
                        (flipProgress.value - 0.5f) * 2f
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isCurrentlyShowingBack) {
                Image(
                    painter = painterResource(id = backDrawables.getOrNull(backIndex) ?: R.drawable.jok014_sym_1),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CardFace(card = card)
            }
        }
    }
}

@Composable
private fun CardFace(card: Card) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val isRed = card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS
        val suitSymbol = when (card.suit) {
            Suit.HEARTS -> "♥"
            Suit.DIAMONDS -> "♦"
            Suit.CLUBS -> "♣"
            Suit.SPADES -> "♠"
        }
        Text(
            text = "${card.rank.displayName}$suitSymbol",
            color = if (isRed) Color.Red else Color.Black,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Center)
        )
        Text(
            text = suitSymbol,
            color = if (isRed) Color.Red else Color.Black,
            fontSize = 32.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 20.dp)
        )
    }
}
