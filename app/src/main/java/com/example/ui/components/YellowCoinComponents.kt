package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PakTokRepository
import com.example.ui.CoinRewardBurst
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneMuted
import com.example.ui.theme.CoinAmberRim
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.CoinYellowLight
import com.example.ui.theme.Gold24K
import com.example.ui.theme.IvoryText
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBlack
import kotlinx.coroutines.delay

/**
 * Custom metallic yellow coin icon drawn with Canvas so it remains vivid yellow
 * across all dark & gold surfaces.
 */
@Composable
fun YellowCoinIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // Outer amber rim
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(CoinYellowLight, CoinYellow, CoinAmberRim),
                start = Offset(0f, 0f),
                end = Offset(this.size.width, this.size.height)
            ),
            radius = radius,
            center = center
        )

        // Inner recessed bright yellow face
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CoinYellowLight, CoinYellow, Color(0xFFFFC107)),
                center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
                radius = radius * 1.1f
            ),
            radius = radius * 0.78f,
            center = center
        )

        // Crisp milled inner ring
        drawCircle(
            color = CoinAmberRim.copy(alpha = 0.65f),
            radius = radius * 0.76f,
            center = center,
            style = Stroke(width = radius * 0.08f)
        )

        // Inner diamond/star emblem
        val starRadius = radius * 0.36f
        val innerRadius = radius * 0.16f
        val path = Path().apply {
            moveTo(center.x, center.y - starRadius)
            lineTo(center.x + innerRadius, center.y - innerRadius)
            lineTo(center.x + starRadius, center.y)
            lineTo(center.x + innerRadius, center.y + innerRadius)
            lineTo(center.x, center.y + starRadius)
            lineTo(center.x - innerRadius, center.y + innerRadius)
            lineTo(center.x - starRadius, center.y)
            lineTo(center.x - innerRadius, center.y - innerRadius)
            close()
        }
        drawPath(path = path, color = Color(0xFFFFFDE7))
    }
}

@Composable
fun WatchCoinHud(
    totalCoins: Int,
    watchElapsedMs: Long,
    isPaused: Boolean,
    coinBurst: CoinRewardBurst?,
    onBurstFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressTarget = (watchElapsedMs.toFloat() / PakTokRepository.WATCH_REWARD_INTERVAL_MS.toFloat())
        .coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "coin_progress_ring"
    )

    val secondsElapsed = (watchElapsedMs / 1000L).coerceIn(0L, 30L)

    LaunchedEffect(coinBurst?.id) {
        if (coinBurst != null) {
            delay(1700L)
            onBurstFinished()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier
                .testTag("top_coin_hud")
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            ObsidianBlack.copy(alpha = 0.88f),
                            CarbonBlack.copy(alpha = 0.92f)
                        )
                    )
                )
                .border(1.dp, Gold24K.copy(alpha = 0.75f), RoundedCornerShape(28.dp))
                .padding(start = 6.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Yellow Coin Icon with 30-second countdown ring
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(34.dp)) {
                    val strokePx = 2.6.dp.toPx()
                    drawArc(
                        color = Gold24K.copy(alpha = 0.25f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokePx / 2f, strokePx / 2f),
                        size = Size(size.width - strokePx, size.height - strokePx),
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(CoinYellowLight, CoinYellow, ChampagneGold, CoinYellowLight)
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = Offset(strokePx / 2f, strokePx / 2f),
                        size = Size(size.width - strokePx, size.height - strokePx),
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
                YellowCoinIcon(size = 22.dp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatNumberWithCommas(totalCoins),
                        color = CoinYellow,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.testTag("top_coin_balance")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "COINS",
                        color = ChampagneGold,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                }
                Text(
                    text = if (isPaused) "Paused • ${secondsElapsed}s/30s" else "${secondsElapsed}s / 30s • +10",
                    color = ChampagneMuted,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp
                )
            }
        }

        AnimatedVisibility(
            visible = coinBurst != null,
            enter = fadeIn(tween(200)) + scaleIn(initialScale = 0.8f) + slideInVertically { -it / 2 },
            exit = fadeOut(tween(300)) + slideOutVertically { -it }
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 6.dp, end = 6.dp)
                    .clip(CircleShape)
                    .background(CoinYellow)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                YellowCoinIcon(size = 14.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "+${coinBurst?.amount ?: 10} Coins!",
                    color = ObsidianBlack,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

fun formatNumberWithCommas(number: Int): String {
    return "%,d".format(number)
}

fun formatCompactCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
        count >= 1_000 -> String.format("%.1fK", count / 1_000f)
        else -> count.toString()
    }
}
