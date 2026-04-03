package app.krafted.jokersblackjack.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersblackjack.game.Card
import app.krafted.jokersblackjack.game.handTotal

@Composable
fun HandDisplay(
    cards: List<Card>,
    total: Int,
    isRevealed: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Row {
            cards.forEachIndexed { index, card ->
                PlayingCard(
                    card = card,
                    isRevealed = isRevealed,
                    modifier = Modifier.padding(4.dp),
                    backIndex = index
                )
            }
        }
        if (isRevealed) {
            Text(
                text = "Total: $total",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            Text(
                text = "Total: ?",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
