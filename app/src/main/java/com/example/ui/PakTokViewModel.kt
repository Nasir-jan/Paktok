package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PakTokDatabase
import com.example.data.local.WalletDataStore
import com.example.data.model.CommentEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.VideoEntity
import com.example.data.model.WalletSnapshot
import com.example.data.repository.PakTokRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PakTokTab(val route: String) {
    HOME("home"),
    UPLOAD("upload"),
    PROFILE("profile")
}

sealed interface RedemptionStatus {
    data object Idle : RedemptionStatus
    data object Processing : RedemptionStatus
    data class Success(val payout: PayoutEntity) : RedemptionStatus
    data class Error(val message: String) : RedemptionStatus
}

sealed interface UploadStatus {
    data object Idle : UploadStatus
    data object Publishing : UploadStatus
    data object Published : UploadStatus
    data class Error(val message: String) : UploadStatus
}

data class CoinRewardBurst(
    val id: Long,
    val amount: Int
)

class PakTokViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PakTokDatabase.getInstance(application)
    private val walletDataStore = WalletDataStore(application)
    private val repository = PakTokRepository(
        context = application,
        dao = database.pakTokDao(),
        walletDataStore = walletDataStore
    )

    val videos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userVideos: StateFlow<List<VideoEntity>> = repository.userVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedVideos: StateFlow<List<VideoEntity>> = repository.likedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payouts: StateFlow<List<PayoutEntity>> = repository.payouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallet: StateFlow<WalletSnapshot> = repository.wallet
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WalletSnapshot())

    private val _currentTab = MutableStateFlow(PakTokTab.HOME)
    val currentTab: StateFlow<PakTokTab> = _currentTab.asStateFlow()

    private val _isFeedPausedByUser = MutableStateFlow(false)
    val isFeedPausedByUser: StateFlow<Boolean> = _isFeedPausedByUser.asStateFlow()

    private val _isAppForeground = MutableStateFlow(true)
    val isAppForeground: StateFlow<Boolean> = _isAppForeground.asStateFlow()

    private val _liveWatchElapsedMs = MutableStateFlow(0L)
    val liveWatchElapsedMs: StateFlow<Long> = _liveWatchElapsedMs.asStateFlow()

    private val _coinBurst = MutableStateFlow<CoinRewardBurst?>(null)
    val coinBurst: StateFlow<CoinRewardBurst?> = _coinBurst.asStateFlow()

    private val _activeCommentVideoId = MutableStateFlow<Long?>(null)
    val activeCommentVideoId: StateFlow<Long?> = _activeCommentVideoId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeComments: StateFlow<List<CommentEntity>> = _activeCommentVideoId
        .flatMapLatest { videoId ->
            if (videoId == null) flowOf(emptyList())
            else repository.observeComments(videoId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCommentVideo: StateFlow<VideoEntity?> = combine(videos, _activeCommentVideoId) { list, id ->
        list.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _redemptionStatus = MutableStateFlow<RedemptionStatus>(RedemptionStatus.Idle)
    val redemptionStatus: StateFlow<RedemptionStatus> = _redemptionStatus.asStateFlow()

    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus.asStateFlow()

    private val _selectedFeedIndex = MutableStateFlow(0)
    val selectedFeedIndex: StateFlow<Int> = _selectedFeedIndex.asStateFlow()

    private var unsavedSecondsAccumulator = 0L
    private var initialElapsedLoaded = false

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
        viewModelScope.launch {
            repository.wallet.collect { snap ->
                if (!initialElapsedLoaded) {
                    _liveWatchElapsedMs.value = snap.watchElapsedMs
                    initialElapsedLoaded = true
                }
            }
        }
        startWatchToEarnTicker()
    }

    private fun startWatchToEarnTicker() {
        viewModelScope.launch {
            val tickMs = 200L
            while (true) {
                delay(tickMs)
                val shouldEarn = _currentTab.value == PakTokTab.HOME &&
                    !_isFeedPausedByUser.value &&
                    _isAppForeground.value &&
                    videos.value.isNotEmpty()

                if (shouldEarn) {
                    val nextElapsed = _liveWatchElapsedMs.value + tickMs
                    if (nextElapsed >= PakTokRepository.WATCH_REWARD_INTERVAL_MS) {
                        val remainder = nextElapsed % PakTokRepository.WATCH_REWARD_INTERVAL_MS
                        _liveWatchElapsedMs.value = remainder
                        unsavedSecondsAccumulator = 0L
                        repository.saveWatchProgress(
                            watchElapsedMs = remainder,
                            coinsEarnedDelta = PakTokRepository.COINS_PER_INTERVAL,
                            watchedSecondsDelta = 30L
                        )
                        _coinBurst.value = CoinRewardBurst(
                            id = System.nanoTime(),
                            amount = PakTokRepository.COINS_PER_INTERVAL
                        )
                    } else {
                        _liveWatchElapsedMs.value = nextElapsed
                        unsavedSecondsAccumulator += tickMs
                        if (unsavedSecondsAccumulator >= 5000L) {
                            unsavedSecondsAccumulator = 0L
                            repository.saveWatchProgress(
                                watchElapsedMs = nextElapsed,
                                coinsEarnedDelta = 0,
                                watchedSecondsDelta = 5L
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectTab(tab: PakTokTab) {
        _currentTab.value = tab
    }

    fun setAppForeground(foreground: Boolean) {
        _isAppForeground.value = foreground
    }

    fun togglePlayPause() {
        _isFeedPausedByUser.value = !_isFeedPausedByUser.value
    }

    fun setFeedPage(index: Int) {
        _selectedFeedIndex.value = index
        _isFeedPausedByUser.value = false
    }

    fun toggleLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleLike(video)
        }
    }

    fun likeFromDoubleTap(video: VideoEntity) {
        if (!video.isLiked) {
            viewModelScope.launch {
                repository.toggleLike(video)
            }
        }
    }

    fun toggleFollow(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleFollow(video)
        }
    }

    fun recordShare(video: VideoEntity) {
        viewModelScope.launch {
            repository.recordShare(video.id)
        }
    }

    fun openComments(videoId: Long) {
        _activeCommentVideoId.value = videoId
    }

    fun closeComments() {
        _activeCommentVideoId.value = null
    }

    fun postComment(videoId: Long, text: String) {
        viewModelScope.launch {
            repository.postComment(videoId, text)
        }
    }

    fun publishVideo(uri: Uri, caption: String, musicTitle: String) {
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus.Publishing
            val result = repository.uploadVideoFromUri(uri, caption, musicTitle)
            _uploadStatus.value = result.fold(
                onSuccess = {
                    _selectedFeedIndex.value = 0
                    _isFeedPausedByUser.value = false
                    UploadStatus.Published
                },
                onFailure = { UploadStatus.Error(it.message ?: "Failed to publish video.") }
            )
        }
    }

    fun resetUploadStatus() {
        _uploadStatus.value = UploadStatus.Idle
    }

    fun redeemCoinsToEasypaisa(coinsToRedeem: Int, phone: String, accountName: String) {
        viewModelScope.launch {
            _redemptionStatus.value = RedemptionStatus.Processing
            val result = repository.redeemCoinsToEasypaisa(coinsToRedeem, phone, accountName)
            _redemptionStatus.value = result.fold(
                onSuccess = { RedemptionStatus.Success(it) },
                onFailure = { RedemptionStatus.Error(it.message ?: "Redemption failed.") }
            )
        }
    }

    fun resetRedemptionStatus() {
        _redemptionStatus.value = RedemptionStatus.Idle
    }

    fun updateProfile(name: String, handle: String) {
        viewModelScope.launch {
            repository.updateProfile(name, handle)
        }
    }

    fun dismissCoinBurst() {
        _coinBurst.value = null
    }
}
