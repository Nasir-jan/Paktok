package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.R
import com.example.data.model.VideoEntity
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.Gold24K
import com.example.ui.theme.HeartRedGold
import com.example.ui.theme.ObsidianBlack
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoFeedPlayer(
    video: VideoEntity,
    isActive: Boolean,
    isPausedByUser: Boolean,
    onSingleTap: () -> Unit,
    onDoubleTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasRenderedFirstFrame by remember(video.id) { mutableStateOf(false) }
    var showHeartBurst by remember { mutableStateOf(false) }

    val posterResId = remember(video.posterKey) {
        when (video.posterKey) {
            "cricket" -> R.drawable.img_feed_cricket_1795423331244
            "hunza" -> R.drawable.img_feed_hunza_1795423352741
            else -> R.drawable.img_feed_lahore_1795423330760
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "backdrop_ken_burns")
    val ambientScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_scale"
    )

    val player = remember(video.id) {
        val httpFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("PakTok/1.0 (Android)")
            .setAllowCrossProtocolRedirects(true)
        val dataSourceFactory = DefaultDataSource.Factory(context, httpFactory)
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = false
            }
    }

    val resolvedUri = remember(video.videoUrl, video.posterKey) {
        when {
            video.videoUrl == "raw://reel_lahore" -> RawResourceDataSource.buildRawResourceUri(R.raw.reel_lahore)
            video.videoUrl == "raw://reel_cricket" -> RawResourceDataSource.buildRawResourceUri(R.raw.reel_cricket)
            video.videoUrl == "raw://reel_hunza" -> RawResourceDataSource.buildRawResourceUri(R.raw.reel_hunza)
            video.videoUrl == "raw://reel_islamabad" -> RawResourceDataSource.buildRawResourceUri(R.raw.reel_islamabad)
            video.videoUrl.contains("commondatastorage.googleapis.com") -> {
                val resId = when (video.posterKey) {
                    "cricket" -> R.raw.reel_cricket
                    "hunza" -> R.raw.reel_hunza
                    else -> R.raw.reel_lahore
                }
                RawResourceDataSource.buildRawResourceUri(resId)
            }
            else -> Uri.parse(video.videoUrl)
        }
    }

    DisposableEffect(video.id, resolvedUri) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                hasRenderedFirstFrame = true
            }

            override fun onPlayerError(error: PlaybackException) {
                hasRenderedFirstFrame = false
            }
        }
        player.addListener(listener)
        val mediaItem = MediaItem.fromUri(resolvedUri)
        player.setMediaItem(mediaItem)
        player.prepare()

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(isActive, isPausedByUser) {
        if (isActive && !isPausedByUser) {
            player.playWhenReady = true
            player.play()
        } else {
            player.playWhenReady = false
            player.pause()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    player.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (isActive && !isPausedByUser) {
                        player.play()
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(showHeartBurst) {
        if (showHeartBurst) {
            delay(750L)
            showHeartBurst = false
        }
    }

    val videoAlpha by animateFloatAsState(
        targetValue = if (hasRenderedFirstFrame) 1f else 0f,
        animationSpec = tween(250),
        label = "video_fade_in"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .pointerInput(video.id) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onDoubleTap = {
                        showHeartBurst = true
                        onDoubleTap()
                    }
                )
            }
    ) {
        // Rich vertical fallback/loading backdrop so screen never looks blank
        Image(
            painter = painterResource(id = posterResId),
            contentDescription = video.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val scale = if (isActive && !isPausedByUser) ambientScale else 1f
                    scaleX = scale
                    scaleY = scale
                }
        )

        // ExoPlayer Video Surface
        if (isActive) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        this.player = player
                    }
                },
                update = { view ->
                    if (view.player !== player) {
                        view.player = player
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(videoAlpha)
            )
        }

        // Top & bottom black vignette gradients for crisp gold/ivory contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to ObsidianBlack.copy(alpha = 0.65f),
                        0.22f to Color.Transparent,
                        0.62f to Color.Transparent,
                        1.0f to ObsidianBlack.copy(alpha = 0.92f)
                    )
                )
        )

        // Paused center Play indicator
        AnimatedVisibility(
            visible = isPausedByUser && isActive,
            enter = fadeIn(tween(150)) + scaleIn(initialScale = 0.7f),
            exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.7f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ObsidianBlack.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Paused, tap to play",
                    tint = ChampagneGold,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Double-tap Heart Burst
        AnimatedVisibility(
            visible = showHeartBurst,
            enter = fadeIn(tween(120)) + scaleIn(initialScale = 0.4f),
            exit = fadeOut(tween(300)) + scaleOut(targetScale = 1.4f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = HeartRedGold.copy(alpha = 0.92f),
                modifier = Modifier.size(104.dp)
            )
        }
    }
}
