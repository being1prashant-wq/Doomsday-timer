package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnimationDirection
import com.example.data.DigitAnimationStyle
import com.example.data.DoomsdaySettings
import com.example.data.EasingCurveOption
import com.example.data.HorizontalAlignOption
import com.example.data.VerticalAlignOption
import com.example.domain.CountdownSnapshot
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NegativeCrimson
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val HorologicalCubicEasing = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)

private fun resolveEasing(option: EasingCurveOption): Easing = when (option) {
    EasingCurveOption.FAST_OUT_SLOW_IN -> FastOutSlowInEasing
    EasingCurveOption.EASE_IN_OUT_CUBIC -> HorologicalCubicEasing
    EasingCurveOption.SPRING_PHYSICS -> FastOutSlowInEasing
    EasingCurveOption.LINEAR -> LinearEasing
}

@Composable
fun GlassmorphicCountdownCard(
    snapshot: CountdownSnapshot,
    settings: DoomsdaySettings,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false
) {
    val preset = settings.preset
    val baseSurfaceColor = Color(preset.surfaceColorHex)
    val glassAlpha = (settings.glassOpacity * (1f - settings.backgroundTransparency * 0.5f))
        .coerceIn(0.02f, 0.96f)
    val borderColor = Color(preset.accentColorHex).copy(alpha = settings.borderOpacity.coerceIn(0f, 1f))
    val primaryTextColor = Color(settings.textColorHex).copy(alpha = settings.textOpacity.coerceIn(0.15f, 1f))
    val secondaryLabelColor = Color(preset.accentColorHex).copy(alpha = (settings.textOpacity * 0.65f).coerceIn(0.2f, 0.9f))

    val hAlignment = when (settings.horizontalAlignment) {
        HorizontalAlignOption.LEFT -> Alignment.Start
        HorizontalAlignOption.CENTER -> Alignment.CenterHorizontally
        HorizontalAlignOption.RIGHT -> Alignment.End
    }

    val vArrangement = when (settings.verticalAlignment) {
        VerticalAlignOption.TOP -> Arrangement.Top
        VerticalAlignOption.CENTER -> Arrangement.Center
        VerticalAlignOption.BOTTOM -> Arrangement.Bottom
    }

    val cornerShape = RoundedCornerShape(settings.cornerRadiusDp.dp)

    Box(
        modifier = modifier
            .clip(cornerShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        baseSurfaceColor.copy(alpha = glassAlpha),
                        Color(0xFF07080C).copy(alpha = (glassAlpha * 0.88f).coerceIn(0.02f, 0.95f))
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = (settings.borderOpacity * 0.35f).coerceIn(0f, 1f)),
                        borderColor.copy(alpha = (settings.borderOpacity * 0.8f).coerceIn(0f, 1f))
                    )
                ),
                shape = cornerShape
            )
            .padding(horizontal = if (compactMode) 14.dp else 20.dp, vertical = if (compactMode) 14.dp else 22.dp)
    ) {
        // Optional subtle silver specular top highlight bar
        if (settings.showSilverHighlights && settings.borderOpacity > 0.05f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.55f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = (settings.borderOpacity * 0.9f).coerceIn(0f, 0.6f)),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = hAlignment,
            verticalArrangement = vArrangement
        ) {
            // Header row: DOOMSDAY title + Negative Elapsed badge if passed
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Doomsday".uppercase(),
                    style = TextStyle(
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compactMode) 11.sp else 13.sp,
                        letterSpacing = (2.8f + settings.letterSpacingEm * 10f).sp,
                        color = secondaryLabelColor
                    )
                )
                if (snapshot.isNegative) {
                    Box(
                        modifier = Modifier
                            .background(
                                NegativeCrimson.copy(alpha = 0.2f),
                                RoundedCornerShape(6.dp)
                            )
                            .border(0.5.dp, NegativeCrimson.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ELAPSED",
                            style = TextStyle(
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                color = NegativeCrimson
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (compactMode) 10.dp else 16.dp))

            // If Analog Dark BG with White Sticks is selected or overlay is enabled, show the Analog White Sticks Chronometer Dial
            if (settings.animationStyle == DigitAnimationStyle.ANALOG_WHITE_STICKS || settings.showAnalogSticksOverlay) {
                AnalogDarkWhiteSticksDial(
                    snapshot = snapshot,
                    primaryColor = primaryTextColor,
                    accentColor = secondaryLabelColor,
                    dialSize = if (compactMode) 136.dp else 188.dp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Main 5-Unit Live Countdown Row: "01 : 09 : 12 : 59 : 00"
            val effectiveFontSize = (if (compactMode) settings.fontSizeSp * 0.78f else settings.fontSizeSp)
                .coerceIn(14f, 40f).sp
            val fontWeight = FontWeight(settings.fontWeightVal.coerceIn(300, 900))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(settings.numberSpacingDp.coerceIn(1f, 14f).dp)
            ) {
                if (snapshot.isNegative) {
                    Text(
                        text = "-",
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = fontWeight,
                            fontSize = effectiveFontSize,
                            color = NegativeCrimson
                        )
                    )
                }

                val units = listOf(
                    snapshot.monthsStr,
                    snapshot.daysStr,
                    snapshot.hoursStr,
                    snapshot.minutesStr,
                    snapshot.secondsStr
                )

                units.forEachIndexed { index, unitStr ->
                    AnimatedUnitBlock(
                        valueStr = unitStr,
                        settings = settings,
                        fontSize = effectiveFontSize,
                        fontWeight = fontWeight,
                        textColor = primaryTextColor
                    )

                    if (index < units.lastIndex) {
                        Text(
                            text = ":",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = fontWeight,
                                fontSize = (effectiveFontSize.value * 0.85f).sp,
                                color = secondaryLabelColor.copy(alpha = 0.65f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (compactMode) 8.dp else 14.dp))

            // Unit Labels Row: Month | Days | Hours | Minutes | Seconds
            Text(
                text = if (compactMode) {
                    "M  |  D  |  H  |  M  |  S"
                } else {
                    "Month  |  Days  |  Hours  |  Minutes  |  Seconds"
                },
                style = TextStyle(
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = if (compactMode) 10.sp else 11.sp,
                    letterSpacing = (1.2f + settings.letterSpacingEm * 8f).sp,
                    color = secondaryLabelColor
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Analog Dark Background with Crisp White Sticks Dial requested by user ("analog dark bg with white sticks")
 */
@Composable
fun AnalogDarkWhiteSticksDial(
    snapshot: CountdownSnapshot,
    primaryColor: Color,
    accentColor: Color,
    dialSize: Dp = 188.dp
) {
    // Smooth animated sweep for seconds stick
    val targetAngle = (snapshot.seconds % 60) * 6f
    val animatedSecondAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "analog_sec_angle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(dialSize)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF040507))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Subtle inner dark ring
            drawCircle(
                color = Color(0xFF0B0D13),
                radius = radius * 0.94f,
                center = center
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = radius * 0.72f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // 60 crisp white stick markers around the dark dial
            for (i in 0 until 60) {
                val angleDeg = i * 6f - 90f
                val angleRad = (angleDeg * PI / 180.0).toFloat()
                val isMajorHourStick = i % 5 == 0
                val isQuarterStick = i % 15 == 0
                val isPassedSecond = i <= snapshot.seconds

                val outerR = radius * 0.95f
                val innerR = when {
                    isQuarterStick -> radius * 0.74f
                    isMajorHourStick -> radius * 0.80f
                    else -> radius * 0.87f
                }

                val stickWidth = when {
                    isQuarterStick -> 2.8.dp.toPx()
                    isMajorHourStick -> 2.0.dp.toPx()
                    else -> 1.1.dp.toPx()
                }

                val stickAlpha = when {
                    i == snapshot.seconds -> 1.0f
                    isQuarterStick -> 0.95f
                    isMajorHourStick -> 0.82f
                    isPassedSecond -> 0.58f
                    else -> 0.24f
                }

                val start = Offset(
                    x = center.x + innerR * cos(angleRad),
                    y = center.y + innerR * sin(angleRad)
                )
                val end = Offset(
                    x = center.x + outerR * cos(angleRad),
                    y = center.y + outerR * sin(angleRad)
                )

                drawLine(
                    color = Color.White.copy(alpha = stickAlpha),
                    start = start,
                    end = end,
                    strokeWidth = stickWidth,
                    cap = StrokeCap.Round
                )
            }

            // Hours white stick hand (24h scale or 12h horological)
            val hourAngleDeg = ((snapshot.hours % 12) * 30f + snapshot.minutes * 0.5f) - 90f
            val hourRad = (hourAngleDeg * PI / 180.0).toFloat()
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = center,
                end = Offset(
                    x = center.x + radius * 0.45f * cos(hourRad),
                    y = center.y + radius * 0.45f * sin(hourRad)
                ),
                strokeWidth = 3.2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Minutes white stick hand
            val minAngleDeg = (snapshot.minutes * 6f + snapshot.seconds * 0.1f) - 90f
            val minRad = (minAngleDeg * PI / 180.0).toFloat()
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(
                    x = center.x + radius * 0.64f * cos(minRad),
                    y = center.y + radius * 0.64f * sin(minRad)
                ),
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Sweeping Seconds stick hand
            val secAngleDeg = animatedSecondAngle - 90f
            val secRad = (secAngleDeg * PI / 180.0).toFloat()
            val tailRad = ((secAngleDeg + 180f) * PI / 180.0).toFloat()
            drawLine(
                color = primaryColor,
                start = Offset(
                    x = center.x + radius * 0.16f * cos(tailRad),
                    y = center.y + radius * 0.16f * sin(tailRad)
                ),
                end = Offset(
                    x = center.x + radius * 0.78f * cos(secRad),
                    y = center.y + radius * 0.78f * sin(secRad)
                ),
                strokeWidth = 1.4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center cap pivot
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color(0xFF040507),
                radius = 1.6.dp.toPx(),
                center = center
            )
        }

        // Sub-dial digital readout at bottom of dial
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 26.dp)
        ) {
            Text(
                text = "${snapshot.monthsStr}M ${snapshot.daysStr}D",
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.88f),
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
fun AnimatedUnitBlock(
    valueStr: String,
    settings: DoomsdaySettings,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    textColor: Color
) {
    if (settings.individualDigitAnimation) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            valueStr.forEachIndexed { idx, char ->
                SingleAnimatedSymbol(
                    symbol = char.toString(),
                    keyIndex = idx,
                    settings = settings,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textColor = textColor
                )
            }
        }
    } else {
        SingleAnimatedSymbol(
            symbol = valueStr,
            keyIndex = 0,
            settings = settings,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textColor = textColor
        )
    }
}

@Composable
private fun SingleAnimatedSymbol(
    symbol: String,
    keyIndex: Int,
    settings: DoomsdaySettings,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    textColor: Color
) {
    val style = settings.animationStyle
    val effectiveDuration = (settings.animationDurationMs / settings.animationSpeedMultiplier.coerceIn(0.25f, 3.0f))
        .toInt()
        .coerceIn(60, 950)
    val intensity = settings.transitionIntensity.coerceIn(0.2f, 1.5f)
    val easing = resolveEasing(settings.easingCurve)

    var toggleDir by remember { mutableIntStateOf(1) }
    LaunchedEffect(symbol) {
        toggleDir = -toggleDir
    }

    val dirSign = when (settings.animationDirection) {
        AnimationDirection.DOWN -> 1
        AnimationDirection.UP -> -1
        AnimationDirection.ALTERNATE -> toggleDir
    }

    when (style) {
        DigitAnimationStyle.FLIP_CLOCK,
        DigitAnimationStyle.ANALOG_WHITE_STICKS -> {
            FlipClockDigitCard(
                symbol = symbol,
                durationMs = effectiveDuration,
                intensity = intensity,
                dirSign = dirSign,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textColor = textColor
            )
        }

        DigitAnimationStyle.BLUR_TRANSITION -> {
            var previousSymbol by remember { mutableStateOf(symbol) }
            val blurProgress by animateFloatAsState(
                targetValue = if (previousSymbol != symbol) 1f else 0f,
                animationSpec = tween(effectiveDuration, easing = easing),
                label = "blur_anim"
            )
            LaunchedEffect(symbol) {
                previousSymbol = symbol
            }
            AnimatedContent(
                targetState = symbol,
                transitionSpec = {
                    (fadeIn(tween(effectiveDuration, easing = easing)) +
                        scaleIn(initialScale = 1f - 0.18f * intensity, animationSpec = tween(effectiveDuration)))
                        .togetherWith(
                            fadeOut(tween(effectiveDuration, easing = easing)) +
                                scaleOut(targetScale = 1f + 0.18f * intensity, animationSpec = tween(effectiveDuration))
                        )
                },
                label = "blur_digit_$keyIndex"
            ) { target ->
                val blurDp = ((1f - kotlin.math.abs(blurProgress - 0.5f) * 2f) * 6f * intensity).dp
                DigitTextElement(
                    text = target,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textColor = textColor,
                    letterSpacingEm = settings.letterSpacingEm,
                    modifier = Modifier.blur(blurDp)
                )
            }
        }

        DigitAnimationStyle.MINIMAL_INSTANT -> {
            DigitTextElement(
                text = symbol,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textColor = textColor,
                letterSpacingEm = settings.letterSpacingEm
            )
        }

        else -> {
            AnimatedContent(
                targetState = symbol,
                transitionSpec = {
                    buildDigitTransition(
                        style = style,
                        durationMs = effectiveDuration,
                        dirSign = dirSign,
                        intensity = intensity,
                        easing = easing
                    ).using(SizeTransform(clip = true))
                },
                label = "digit_anim_$keyIndex"
            ) { target ->
                DigitTextElement(
                    text = target,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textColor = textColor,
                    letterSpacingEm = settings.letterSpacingEm
                )
            }
        }
    }
}

@Composable
private fun FlipClockDigitCard(
    symbol: String,
    durationMs: Int,
    intensity: Float,
    dirSign: Int,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    textColor: Color
) {
    var previousSymbol by remember { mutableStateOf(symbol) }
    var flipTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(symbol) {
        if (symbol != previousSymbol) {
            flipTrigger++
            previousSymbol = symbol
        }
    }

    val rotationX by animateFloatAsState(
        targetValue = flipTrigger * 360f * dirSign,
        animationSpec = spring(
            dampingRatio = (0.78f / intensity.coerceIn(0.5f, 1.4f)).coerceIn(0.45f, 0.95f),
            stiffness = (420f * (420f / durationMs.coerceAtLeast(100))).coerceIn(180f, 900f)
        ),
        label = "flip_rot_x"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.045f))
            .border(0.5.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
            .padding(horizontal = 3.dp, vertical = 2.dp)
            .graphicsLayer {
                // Normalize angle to subtle 3D mechanical split-flap tilt during transition
                val phase = (rotationX % 360f)
                val tilt = if (phase > 180f) phase - 360f else phase
                this.rotationX = (tilt * 0.25f * intensity).coerceIn(-65f, 65f)
                this.cameraDistance = 12f * density
            }
    ) {
        AnimatedContent(
            targetState = symbol,
            transitionSpec = {
                (slideInVertically(tween(durationMs)) { h -> -dirSign * (h / 2) } +
                    fadeIn(tween(durationMs)))
                    .togetherWith(
                        slideOutVertically(tween(durationMs)) { h -> dirSign * (h / 2) } +
                            fadeOut(tween(durationMs))
                    )
            },
            label = "flip_content"
        ) { target ->
            Text(
                text = target,
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = fontWeight,
                    fontSize = fontSize,
                    color = textColor,
                    fontFeatureSettings = "tnum"
                )
            )
        }
        // Horizontal split-flap mechanical crease line across the middle
        Box(
            modifier = Modifier
                .matchParentSize()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width((fontSize.value * 0.65f).dp)
                    .height(0.75.dp)
                    .background(Color.Black.copy(alpha = 0.38f))
            )
        }
    }
}

private fun buildDigitTransition(
    style: DigitAnimationStyle,
    durationMs: Int,
    dirSign: Int,
    intensity: Float,
    easing: Easing
): androidx.compose.animation.ContentTransform {
    return when (style) {
        DigitAnimationStyle.SLIDE_UP -> {
            (slideInVertically(tween(durationMs, easing = easing)) { h -> (h * intensity).toInt() } +
                fadeIn(tween(durationMs)))
                .togetherWith(
                    slideOutVertically(tween(durationMs, easing = easing)) { h -> -(h * intensity).toInt() } +
                        fadeOut(tween(durationMs))
                )
        }

        DigitAnimationStyle.SLIDE_DOWN -> {
            (slideInVertically(tween(durationMs, easing = easing)) { h -> -(h * intensity).toInt() } +
                fadeIn(tween(durationMs)))
                .togetherWith(
                    slideOutVertically(tween(durationMs, easing = easing)) { h -> (h * intensity).toInt() } +
                        fadeOut(tween(durationMs))
                )
        }

        DigitAnimationStyle.FADE -> {
            fadeIn(tween(durationMs, easing = easing))
                .togetherWith(fadeOut(tween(durationMs, easing = easing)))
        }

        DigitAnimationStyle.SCALE -> {
            (scaleIn(
                initialScale = (1f - 0.45f * intensity).coerceIn(0.2f, 0.9f),
                animationSpec = tween(durationMs, easing = easing)
            ) + fadeIn(tween(durationMs)))
                .togetherWith(
                    scaleOut(
                        targetScale = (1f + 0.35f * intensity).coerceIn(1.05f, 1.8f),
                        animationSpec = tween(durationMs, easing = easing)
                    ) + fadeOut(tween(durationMs))
                )
        }

        DigitAnimationStyle.ROLLING -> {
            (slideInVertically(tween(durationMs, easing = LinearEasing)) { h -> dirSign * h } +
                fadeIn(tween(durationMs / 2)))
                .togetherWith(
                    slideOutVertically(tween(durationMs, easing = LinearEasing)) { h -> -dirSign * h } +
                        fadeOut(tween(durationMs / 2))
                )
        }

        DigitAnimationStyle.SLOT_MACHINE -> {
            val springSpec = spring<androidx.compose.ui.unit.IntOffset>(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
            (slideInVertically(springSpec) { h -> -h * 2 } + fadeIn(tween(durationMs / 2)))
                .togetherWith(
                    slideOutVertically(tween(durationMs / 2)) { h -> h * 2 } + fadeOut(tween(durationMs / 2))
                )
        }

        DigitAnimationStyle.MECHANICAL_CLOCK -> {
            val mechSpring = spring<androidx.compose.ui.unit.IntOffset>(
                dampingRatio = 0.45f,
                stiffness = Spring.StiffnessHigh
            )
            (slideInVertically(mechSpring) { h -> dirSign * (h * 0.65f).toInt() } +
                scaleIn(initialScale = 0.92f, animationSpec = tween(120)) +
                fadeIn(tween(100)))
                .togetherWith(
                    slideOutVertically(tween(140)) { h -> -dirSign * (h * 0.65f).toInt() } +
                        fadeOut(tween(110))
                )
        }

        DigitAnimationStyle.BOUNCE -> {
            val bounceSpec = spring<Float>(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
            (scaleIn(initialScale = 0.4f, animationSpec = bounceSpec) + fadeIn(tween(durationMs / 2)))
                .togetherWith(
                    scaleOut(targetScale = 0.6f, animationSpec = tween(durationMs / 2)) +
                        fadeOut(tween(durationMs / 2))
                )
        }

        DigitAnimationStyle.TYPEWRITER -> {
            (scaleIn(initialScale = 1.15f, animationSpec = tween(90, easing = LinearEasing)) +
                fadeIn(tween(70, easing = LinearEasing)))
                .togetherWith(
                    fadeOut(tween(50, easing = LinearEasing))
                )
        }

        else -> {
            EnterTransition.None.togetherWith(ExitTransition.None)
        }
    }
}

@Composable
private fun DigitTextElement(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    textColor: Color,
    letterSpacingEm: Float,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            color = textColor,
            letterSpacing = (letterSpacingEm * 8f).sp,
            fontFeatureSettings = "tnum"
        )
    )
}
