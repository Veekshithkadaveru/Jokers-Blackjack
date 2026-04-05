package app.krafted.jokersblackjack.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersblackjack.R
import app.krafted.jokersblackjack.ui.theme.GoldAccent
import app.krafted.jokersblackjack.ui.theme.GoldLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val iconScale = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(40f) }
    val underlineWidth = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            iconAlpha.animateTo(1f, tween(500, easing = EaseOutCubic))
        }
        launch {
            iconScale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 300f))
        }

        delay(300)

        launch { titleAlpha.animateTo(1f, tween(600, easing = EaseOutCubic)) }
        launch { titleOffset.animateTo(0f, tween(700, easing = EaseOutBack)) }

        delay(400)

        launch { underlineWidth.animateTo(1f, tween(500, easing = EaseOutCubic)) }

        delay(200)

        launch { subtitleAlpha.animateTo(1f, tween(400, easing = EaseOutCubic)) }

        delay(1000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.jok014_back_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.weight(0.3f))

            AnimatedJokerLogo(
                modifier = Modifier.size(180.dp),
                iconWidth = 180.dp,
                iconHeight = 180.dp,
                glowSize = 140.dp,
                iconScale = iconScale.value,
                iconAlpha = iconAlpha.value
            )

            Spacer(Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                    translationY = titleOffset.value
                }
            ) {
                Text(
                    text = "JOKER'S",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 8.sp
                )
                Text(
                    text = "BLACKJACK",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 10.sp
                )

                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(3.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val lineWidth = size.width * underlineWidth.value
                        val startX = (size.width - lineWidth) / 2f
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    GoldAccent,
                                    GoldLight,
                                    GoldAccent,
                                    Color.Transparent
                                ),
                                startX = startX,
                                endX = startX + lineWidth
                            ),
                            start = Offset(startX, size.height / 2),
                            end = Offset(startX + lineWidth, size.height / 2),
                            strokeWidth = size.height
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Beat the Joker in 5 hands",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif,
                letterSpacing = 2.sp,
                modifier = Modifier.graphicsLayer { alpha = subtitleAlpha.value }
            )

            Spacer(Modifier.weight(0.7f))
        }
    }
}
