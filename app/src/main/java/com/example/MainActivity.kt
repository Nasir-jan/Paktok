package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PakTokTab
import com.example.ui.PakTokViewModel
import com.example.ui.components.CoinsToCashSheet
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.LiveStreamSheet
import com.example.ui.components.PakTokBottomBar
import com.example.ui.screens.HomeFeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PakTokTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            PakTokTheme {
                PakTokApp()
            }
        }
    }
}

@Composable
fun PakTokApp(
    viewModel: PakTokViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.setAppForeground(true)
                Lifecycle.Event.ON_PAUSE -> viewModel.setAppForeground(false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val userVideos by viewModel.userVideos.collectAsStateWithLifecycle()
    val likedVideos by viewModel.likedVideos.collectAsStateWithLifecycle()
    val payouts by viewModel.payouts.collectAsStateWithLifecycle()
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val liveWatchElapsedMs by viewModel.liveWatchElapsedMs.collectAsStateWithLifecycle()
    val isFeedPausedByUser by viewModel.isFeedPausedByUser.collectAsStateWithLifecycle()
    val coinBurst by viewModel.coinBurst.collectAsStateWithLifecycle()
    val selectedFeedIndex by viewModel.selectedFeedIndex.collectAsStateWithLifecycle()

    val activeCommentVideo by viewModel.activeCommentVideo.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()
    val redemptionStatus by viewModel.redemptionStatus.collectAsStateWithLifecycle()
    val uploadStatus by viewModel.uploadStatus.collectAsStateWithLifecycle()

    var showCoinsToCashSheet by remember { mutableStateOf(false) }
    var showLiveSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            PakTokBottomBar(
                currentTab = currentTab,
                totalCoins = wallet.totalCoins,
                onSelectTab = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentTab) {
                PakTokTab.HOME -> {
                    HomeFeedScreen(
                        videos = videos,
                        initialPage = selectedFeedIndex,
                        totalCoins = wallet.totalCoins,
                        followersCount = wallet.followersCount,
                        watchElapsedMs = liveWatchElapsedMs,
                        isPausedByUser = isFeedPausedByUser,
                        coinBurst = coinBurst,
                        onPageChanged = { viewModel.setFeedPage(it) },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onToggleLike = { viewModel.toggleLike(it) },
                        onDoubleTapLike = { viewModel.likeFromDoubleTap(it) },
                        onOpenComments = { viewModel.openComments(it.id) },
                        onShareVideo = { viewModel.recordShare(it) },
                        onToggleFollow = { viewModel.toggleFollow(it) },
                        onOpenLive = { showLiveSheet = true },
                        onBurstFinished = { viewModel.dismissCoinBurst() }
                    )
                }

                PakTokTab.UPLOAD -> {
                    UploadScreen(
                        followersCount = wallet.followersCount,
                        uploadStatus = uploadStatus,
                        onPublishVideo = { uri, caption, music ->
                            viewModel.publishVideo(uri, caption, music)
                        },
                        onOpenLive = { showLiveSheet = true },
                        onUploadCompleted = {
                            viewModel.resetUploadStatus()
                            viewModel.selectTab(PakTokTab.HOME)
                        },
                        onBackToHome = {
                            viewModel.selectTab(PakTokTab.HOME)
                        }
                    )
                }

                PakTokTab.PROFILE -> {
                    ProfileScreen(
                        wallet = wallet,
                        userVideos = userVideos,
                        likedVideos = likedVideos,
                        payouts = payouts,
                        onOpenCoinsToCash = {
                            viewModel.resetRedemptionStatus()
                            showCoinsToCashSheet = true
                        },
                        onOpenLive = { showLiveSheet = true },
                        onUpdateProfile = { name, handle ->
                            viewModel.updateProfile(name, handle)
                        },
                        onOpenVideoInFeed = { video ->
                            val idx = videos.indexOfFirst { it.id == video.id }
                            if (idx >= 0) {
                                viewModel.setFeedPage(idx)
                                viewModel.selectTab(PakTokTab.HOME)
                            }
                        },
                        onBackToHome = {
                            viewModel.selectTab(PakTokTab.HOME)
                        }
                    )
                }
            }
        }
    }

    activeCommentVideo?.let { commentVideo ->
        CommentsBottomSheet(
            video = commentVideo,
            comments = activeComments,
            onDismiss = { viewModel.closeComments() },
            onSendComment = { text -> viewModel.postComment(commentVideo.id, text) }
        )
    }

    if (showCoinsToCashSheet) {
        CoinsToCashSheet(
            totalCoins = wallet.totalCoins,
            redemptionStatus = redemptionStatus,
            onDismiss = {
                showCoinsToCashSheet = false
                viewModel.resetRedemptionStatus()
            },
            onConfirmRedeem = { coins, phone, name ->
                viewModel.redeemCoinsToEasypaisa(coins, phone, name)
            }
        )
    }

    if (showLiveSheet) {
        LiveStreamSheet(
            followersCount = wallet.followersCount,
            profileHandle = wallet.profileHandle,
            onDismiss = { showLiveSheet = false }
        )
    }
}
