package app.krafted.jokersblackjack.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersblackjack.R
import app.krafted.jokersblackjack.game.Difficulty
import app.krafted.jokersblackjack.ui.theme.GoldAccent
import app.krafted.jokersblackjack.ui.theme.GoldLight
import app.krafted.jokersblackjack.ui.theme.LossRed
import app.krafted.jokersblackjack.ui.theme.OrangeAccent
import app.krafted.jokersblackjack.ui.theme.WinGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedJokerLogo(
    modifier: Modifier = Modifier,
    iconWidth: Dp = 140.dp,
    iconHeight: Dp = 180.dp,
    glowSize: Dp = 120.dp,
    iconScale: Float = 1f,
    iconAlpha: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    val logoHover by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover"
    )
    val logoTilt by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )

    Box(
        modifier = modifier
            .height(iconHeight)
            .width(iconWidth)
            .graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
                alpha = iconAlpha
                translationY = logoHover
                rotationZ = logoTilt
                shadowElevation = 35f
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(glowSize)
                .graphicsLayer { alpha = iconAlpha * 0.5f }
                .blur(28.dp)
                .background(GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        )
        Image(
            painter = painterResource(id = R.drawable.card_back_joker),
            contentDescription = "Joker Icon",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    2.dp,
                    GoldAccent.copy(alpha = 0.8f),
                    RoundedCornerShape(6.dp)
                ),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun HomeScreen(
    onNavigateToGame: (String) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }


    val iconScale = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(40f) }
    val underlineWidth = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    val pillAlpha = remember { Animatable(0f) }
    val pillScale = remember { Animatable(0.8f) }

    val btnAlphas = remember { List(3) { Animatable(0f) } }
    val btnOffsets = remember { List(3) { Animatable(60f) } }

    val playAlpha = remember { Animatable(0f) }
    val playScale = remember { Animatable(0.7f) }

    val footerAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {

        launch {
            iconAlpha.animateTo(1f, tween(300, easing = EaseOutCubic))
        }
        launch {
            iconScale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 300f))
        }

        delay(200)


        launch { titleAlpha.animateTo(1f, tween(450, easing = EaseOutCubic)) }
        launch { titleOffset.animateTo(0f, tween(500, easing = EaseOutBack)) }

        delay(250)


        launch { underlineWidth.animateTo(1f, tween(400, easing = EaseInOutCubic)) }
        delay(150)
        launch { subtitleAlpha.animateTo(1f, tween(350)) }

        delay(200)


        launch { pillAlpha.animateTo(1f, tween(300)) }
        launch { pillScale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 400f)) }

        delay(100)


        for (i in 0..2) {
            launch {
                btnAlphas[i].animateTo(1f, tween(350, easing = EaseOutCubic))
            }
            launch {
                btnOffsets[i].animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 350f))
            }
            delay(90)
        }

        delay(150)


        launch { playAlpha.animateTo(1f, tween(300)) }
        launch { playScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 350f)) }

        delay(200)


        footerAlpha.animateTo(1f, tween(400))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        Brush.radialGradient(
                            0f to Color.Transparent,
                            0.6f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.6f)
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedJokerLogo(
                    iconScale = iconScale.value,
                    iconAlpha = iconAlpha.value
                )

                Spacer(Modifier.height(8.dp))


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
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 6.sp
                    )
                    Text(
                        text = "BLACKJACK",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 8.sp
                    )


                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(180.dp)
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

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Beat the Joker in 5 hands",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.graphicsLayer { alpha = subtitleAlpha.value }
                )
            }

            Spacer(modifier = Modifier.weight(0.2f))


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(
                    modifier = Modifier.graphicsLayer {
                        alpha = pillAlpha.value
                        scaleX = pillScale.value
                        scaleY = pillScale.value
                    }
                ) {
                    AnimatedLabelPill("SELECT DIFFICULTY")
                }


                val difficulties = listOf(
                    DifficultyInfo(
                        "EASY",
                        "Joker plays normally",
                        "\u2660",
                        Difficulty.EASY,
                        WinGreen
                    ),
                    DifficultyInfo(
                        "MEDIUM",
                        "Joker cheats to survive",
                        "\u2666",
                        Difficulty.MEDIUM,
                        OrangeAccent
                    ),
                    DifficultyInfo("HARD", "Joker plays to win", "\u2663", Difficulty.HARD, LossRed)
                )

                difficulties.forEachIndexed { i, info ->
                    Box(
                        modifier = Modifier.graphicsLayer {
                            alpha = btnAlphas[i].value
                            translationX = btnOffsets[i].value
                        }
                    ) {
                        DifficultyButton(
                            label = info.label,
                            description = info.description,
                            icon = info.icon,
                            accentColor = info.color,
                            isSelected = selectedDifficulty == info.difficulty,
                            onClick = { selectedDifficulty = info.difficulty }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))


                Box(
                    modifier = Modifier.graphicsLayer {
                        alpha = playAlpha.value
                        scaleX = playScale.value
                        scaleY = playScale.value
                    }
                ) {
                    PlayButton(
                        difficulty = selectedDifficulty,
                        onPlay = { onNavigateToGame(selectedDifficulty.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier.graphicsLayer { alpha = footerAlpha.value }) {
                AnimatedFooter()
            }
        }
    }
}

private data class DifficultyInfo(
    val label: String,
    val description: String,
    val icon: String,
    val difficulty: Difficulty,
    val color: Color
)


@Composable
private fun AnimatedLabelPill(text: String) {
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.5.dp, GoldAccent.copy(alpha = glowAlpha), RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 3.sp
        )
    }
}

@Composable
private fun DifficultyButton(
    label: String,
    description: String,
    icon: String,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isSelected -> 1.02f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "btn_scale"
    )


    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else accentColor.copy(alpha = 0.35f),
        animationSpec = tween(250, easing = EaseOutCubic),
        label = "border_color"
    )


    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            accentColor.copy(alpha = 0.22f)
        else
            Color(0xFF1A1020).copy(alpha = 0.85f),
        animationSpec = tween(250, easing = EaseOutCubic),
        label = "bg_color"
    )


    val labelColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.White,
        animationSpec = tween(200),
        label = "label_color"
    )

    val descColor by animateColorAsState(
        targetValue = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "desc_color"
    )


    val iconColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else accentColor.copy(alpha = 0.7f),
        animationSpec = tween(200),
        label = "icon_color"
    )

    val selectedGlow by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(300),
        label = "sel_glow"
    )

    val iconPulse = rememberInfiniteTransition(label = "icon_pulse")
    val iconScale by iconPulse.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {

        if (selectedGlow > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = selectedGlow * 0.4f }
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = accentColor,
                        spotColor = accentColor
                    )
            )
        }

        Button(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .border(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        fontSize = 32.sp,
                        color = iconColor,
                        modifier = Modifier.graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = label,
                            color = labelColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = description,
                            color = descColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayButton(
    difficulty: Difficulty,
    onPlay: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "btn_scale"
    )

    val accentColor by animateColorAsState(
        targetValue = when (difficulty) {
            Difficulty.EASY -> WinGreen
            Difficulty.MEDIUM -> OrangeAccent
            Difficulty.HARD -> LossRed
        },
        animationSpec = tween(350, easing = EaseOutCubic),
        label = "play_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "play_anim")


    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_val"
    )


    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )


    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_pulse"
    )

    val combinedScale = pressScale * breathScale

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = glowAlpha }
                .shadow(
                    elevation = 40.dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = accentColor,
                    spotColor = accentColor
                )
        )

        Button(
            onClick = onPlay,
            interactionSource = interactionSource,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = combinedScale
                    scaleY = combinedScale
                },
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
                        Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.6f),
                                accentColor,
                                accentColor.copy(alpha = 0.85f),
                                accentColor,
                                accentColor.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .drawWithContent {
                        drawContent()

                        drawRect(
                            Brush.verticalGradient(
                                0f to Color.White.copy(alpha = 0.15f),
                                0.4f to Color.Transparent
                            )
                        )

                        val shimmerCenter = size.width * shimmerOffset
                        drawRect(
                            Brush.radialGradient(
                                0f to Color.White.copy(alpha = 0.25f),
                                1f to Color.Transparent,
                                center = Offset(shimmerCenter, size.height / 2),
                                radius = size.height * 1.5f
                            )
                        )
                    }
                    .border(
                        2.5.dp,
                        Color.White.copy(alpha = borderAlpha),
                        RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PLAY",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 8.sp
                )
            }
        }
    }
}

@Composable
private fun AnimatedFooter() {
    val infiniteTransition = rememberInfiniteTransition(label = "footer_shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Black.copy(alpha = 0.5f),
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
            .border(1.5.dp, GoldAccent.copy(alpha = shimmerAlpha), RoundedCornerShape(14.dp))
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Text(
            text = "\u2726  5 HANDS PER SESSION  \u2726",
            color = GoldAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 2.5.sp
        )
    }
}
