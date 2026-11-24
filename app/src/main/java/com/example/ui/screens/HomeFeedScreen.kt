package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.VideoEntity
import com.example.ui.CoinRewardBurst
import com.example.ui.components.VideoFeedPlayer
import com.example.ui.components.WatchCoinHud
import com.example.ui.components.formatCompactCount
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneMuted
import com.example.ui.theme.Gold24K
import com.example.ui.theme.HeartRedGold
import com.example.ui.theme.IvoryText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateObsidian

import com.example.data.repository.PakTokRepository

@Composable
fun HomeFeedScreen(
    videos: List<VideoEntity>,
    initialPage: Int,
    totalCoins: Int,
    followersCount: Int,
    watchElapsedMs: Long,
    isPausedByUser: Boolean,
    coinBurst: CoinRewardBurst?,
    onPageChanged: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleLike: (VideoEntity) -> Unit,
    onDoubleTapLike: (VideoEntity) -> Unit,
    onOpenComments: (VideoEntity) -> Unit,
    onShareVideo: (VideoEntity) -> Unit,
    onToggleFollow: (VideoEntity) -> Unit,
    onOpenLive: () -> Unit,
    onBurstFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (videos.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianBlack),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading PakTok feed...",
                style = MaterialTheme.typography.titleMedium,
                color = ChampagneGold
            )
        }
        return
    }

    val safeInitialPage = initialPage.coerceIn(0, (videos.size - 1).coerceAtLeast(0))
    val pagerState = rememberPagerState(
        initialPage = safeInitialPage,
        pageCount = { videos.size }
    )

    LaunchedEffect(initialPage, videos.size) {
        val target = initialPage.coerceIn(0, (videos.size - 1).coerceAtLeast(0))
        if (pagerState.currentPage != target) {
            pagerState.scrollToPage(target)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChanged(page)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        VerticalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            key = { index -> videos[index].id },
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val video = videos[pageIndex]
            val isActive = pagerState.currentPage == pageIndex

            Box(modifier = Modifier.fillMaxSize()) {
                VideoFeedPlayer(
                    video = video,
                    isActive = isActive,
                    isPausedByUser = isPausedByUser,
                    onSingleTap = onTogglePlayPause,
                    onDoubleTap = { onDoubleTapLike(video) }
                )

                // Right-Side Action Rail (Follow Avatar, Like, Comment, Share, Vinyl)
                RightInteractionRail(
                    video = video,
                    isSpinning = isActive && !isPausedByUser,
                    onFollowClick = { onToggleFollow(video) },
                    onLikeClick = { onToggleLike(video) },
                    onCommentClick = { onOpenComments(video) },
                    onShareClick = {
                        onShareVideo(video)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Watch ${video.creatorHandle} on PakTok: ${video.caption}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share PakTok Video"))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 18.dp)
                )

                // Bottom-Left Creator Handle + Follow Pill + Caption + Music
                BottomVideoDetails(
                    video = video,
                    onFollowClick = { onToggleFollow(video) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 92.dp, bottom = 20.dp)
                )
            }
        }

        // Top Bar Overlay: PakTok Brand + Live Pill (500-follower gate) + Watch-to-Earn Coin HUD
        val isLiveUnlocked = followersCount >= PakTokRepository.LIVE_FOLLOWERS_REQUIRED
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ObsidianBlack.copy(alpha = 0.72f))
                        .border(1.dp, Gold24K.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pak",
                        style = MaterialTheme.typography.titleMedium,
                        color = IvoryText,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Text(
                        text = "Tok",
                        style = MaterialTheme.typography.titleMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }

                // Live Option Pill with Lock when < 500 followers
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isLiveUnlocked) HeartRedGold.copy(alpha = 0.88f)
                            else ObsidianBlack.copy(alpha = 0.78f)
                        )
                        .border(
                            1.dp,
                            if (isLiveUnlocked) HeartRedGold else Gold24K.copy(alpha = 0.65f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onOpenLive)
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                        .testTag("home_live_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLiveUnlocked) Icons.Filled.Sensors else Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.live_label),
                        tint = if (isLiveUnlocked) IvoryText else ChampagneGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.live_label).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLiveUnlocked) IvoryText else ChampagneGold,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            WatchCoinHud(
                totalCoins = totalCoins,
                watchElapsedMs = watchElapsedMs,
                isPaused = isPausedByUser,
                coinBurst = coinBurst,
                onBurstFinished = onBurstFinished
            )
        }
    }
}

@Composable
private fun RightInteractionRail(
    video: VideoEntity,
    isSpinning: Boolean,
    onFollowClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Creator Avatar + Follow badge
        Box(
            modifier = Modifier.size(width = 52.dp, height = 60.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SlateObsidian, CarbonBlack)
                        )
                    )
                    .border(2.dp, ChampagneGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.creatorInitials,
                    style = MaterialTheme.typography.titleMedium,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (video.isFollowed) SlateObsidian else ChampagneGold)
                    .border(1.dp, ObsidianBlack, CircleShape)
                    .clickable(onClick = onFollowClick)
                    .testTag("avatar_follow_button_${video.id}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (video.isFollowed) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = if (video.isFollowed) {
                        stringResource(R.string.action_following)
                    } else {
                        stringResource(R.string.action_follow)
                    },
                    tint = if (video.isFollowed) ChampagneGold else ObsidianBlack,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Like Button
        RailActionButton(
            icon = Icons.Filled.Favorite,
            label = formatCompactCount(video.likesCount),
            contentDescription = stringResource(R.string.action_like),
            tint = if (video.isLiked) HeartRedGold else IvoryText,
            onClick = onLikeClick,
            testTag = "like_button_${video.id}"
        )

        // Comment Button
        RailActionButton(
            icon = Icons.Filled.ChatBubble,
            label = formatCompactCount(video.commentsCount),
            contentDescription = stringResource(R.string.action_comment),
            tint = IvoryText,
            onClick = onCommentClick,
            testTag = "comment_button_${video.id}"
        )

        // Share Button
        RailActionButton(
            icon = Icons.Filled.Share,
            label = formatCompactCount(video.sharesCount),
            contentDescription = stringResource(R.string.action_share),
            tint = ChampagneGold,
            onClick = onShareClick,
            testTag = "share_button_${video.id}"
        )

        // Spinning Gold Music Vinyl
        GoldMusicVinyl(isSpinning = isSpinning)
    }
}

@Composable
private fun RailActionButton(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .widthIn(min = 56.dp)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(ObsidianBlack.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = IvoryText,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun GoldMusicVinyl(isSpinning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_rotation"
    )

    Box(
        modifier = Modifier
            .size(42.dp)
            .graphicsLayer { rotationZ = if (isSpinning) angle else 0f }
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(SlateObsidian, ObsidianBlack)
                )
            )
            .border(2.5.dp, Gold24K, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(ChampagneGold),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
private fun BottomVideoDetails(
    video: VideoEntity,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.widthIn(max = 460.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = video.creatorHandle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = IvoryText,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (video.isFollowed) {
                            SlateObsidian.copy(alpha = 0.85f)
                        } else {
                            ChampagneGold
                        }
                    )
                    .border(
                        1.dp,
                        if (video.isFollowed) Gold24K.copy(alpha = 0.7f) else BrightGold,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(onClick = onFollowClick)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("follow_button_${video.id}")
            ) {
                Text(
                    text = if (video.isFollowed) {
                        stringResource(R.string.action_following)
                    } else {
                        "+ ${stringResource(R.string.action_follow)}"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (video.isFollowed) ChampagneGold else ObsidianBlack,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = video.caption,
            style = MaterialTheme.typography.bodyLarge,
            color = IvoryText.copy(alpha = 0.94f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = null,
                tint = ChampagneGold,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = video.musicTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = ChampagneGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
