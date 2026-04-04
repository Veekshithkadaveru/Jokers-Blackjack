package app.krafted.jokersblackjack.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersblackjack.game.Card

@Composable
fun HandDisplay(
    cards: List<Card>,
    total: Int,
    isRevealed: Boolean,
    isDealer: Boolean,
    isBust: Boolean = false,
    isBlackjack: Boolean = false,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(isBust) {
        if (isBust && isRevealed) {
            repeat(3) {
                offsetX.animateTo(14f, spring(stiffness = Spring.StiffnessHigh))
                offsetX.animateTo(-14f, spring(stiffness = Spring.StiffnessHigh))
            }
            offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessHigh))
        } else {
            offsetX.snapTo(0f)
        }
    }

    Column(
        modifier = modifier.graphicsLayer { translationX = offsetX.value },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label?.let {
            Text(
                text = it,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 3.sp,
                color = if (isBust) Color(0xFFE57373) else Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val spacing = if (cards.size > 2) (-40).dp else 12.dp
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
        ) {
            cards.forEachIndexed { index, card ->
                val cardRevealed = if (isDealer && index == 0) isRevealed else true
                PlayingCard(
                    card = card,
                    isRevealed = cardRevealed,
                    isBlackjack = isBlackjack && cardRevealed,
                    modifier = Modifier.height(150.dp),
                    backIndex = index
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        val displayedTotal = if (isDealer && !isRevealed) null else total.toString()
        val totalColor = when {
            isBust -> Color(0xFFEF5350)
            isBlackjack -> Color(0xFFFFD700)
            else -> Color.White
        }
        displayedTotal?.let {
            Text(
                text = when {
                    isBlackjack -> "BLACKJACK!"
                    isBust -> "Bust! ($it)"
                    else -> "Total: $it"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                letterSpacing = 1.sp,
                color = totalColor
            )
        }
    }
}