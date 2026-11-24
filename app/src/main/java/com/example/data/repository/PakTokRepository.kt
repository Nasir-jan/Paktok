package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.PakTokDao
import com.example.data.local.PakTokSeedData
import com.example.data.local.WalletDataStore
import com.example.data.model.CommentEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.VideoEntity
import com.example.data.model.WalletSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PakTokRepository(
    private val context: Context,
    private val dao: PakTokDao,
    private val walletDataStore: WalletDataStore
) {
    val allVideos: Flow<List<VideoEntity>> = dao.observeAllVideos()
    val userVideos: Flow<List<VideoEntity>> = dao.observeUserVideos()
    val likedVideos: Flow<List<VideoEntity>> = dao.observeLikedVideos()
    val payouts: Flow<List<PayoutEntity>> = dao.observePayouts()
    val wallet: Flow<WalletSnapshot> = walletDataStore.walletFlow

    suspend fun ensureSeeded() {
        if (dao.getVideoCount() == 0) {
            val seeds = PakTokSeedData.initialVideos()
            val insertedIds = seeds.map { dao.insertVideo(it) }
            dao.insertComments(PakTokSeedData.initialComments(insertedIds))
        } else {
            dao.migrateLegacySeedUrl("@lahore_vibes", "raw://reel_lahore")
            dao.migrateLegacySeedUrl("@karachi_cricket", "raw://reel_cricket")
            dao.migrateLegacySeedUrl("@hunza_nomad", "raw://reel_hunza")
            dao.migrateLegacySeedUrl("@islamabad_nights", "raw://reel_islamabad")
            dao.migrateLegacySeedUrl("@multan_beats", "raw://reel_cricket")
        }
    }

    fun observeComments(videoId: Long): Flow<List<CommentEntity>> =
        dao.observeCommentsForVideo(videoId)

    suspend fun toggleLike(video: VideoEntity) {
        val nextLiked = !video.isLiked
        val delta = if (nextLiked) 1 else -1
        dao.updateLikeState(video.id, nextLiked, delta)
    }

    suspend fun toggleFollow(video: VideoEntity) {
        dao.updateFollowForCreator(video.creatorHandle, !video.isFollowed)
    }

    suspend fun recordShare(videoId: Long) {
        dao.incrementShareCount(videoId)
    }

    suspend fun postComment(videoId: Long, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val profile = wallet.first()
        dao.addCommentAndIncrementCount(
            CommentEntity(
                videoId = videoId,
                authorHandle = profile.profileHandle,
                text = trimmed
            )
        )
    }

    suspend fun saveWatchProgress(
        watchElapsedMs: Long,
        coinsEarnedDelta: Int,
        watchedSecondsDelta: Long
    ) {
        walletDataStore.saveWatchProgress(
            watchElapsedMs = watchElapsedMs,
            coinsEarnedDelta = coinsEarnedDelta,
            watchedSecondsDelta = watchedSecondsDelta
        )
    }

    suspend fun updateProfile(name: String, handle: String) {
        walletDataStore.updateProfile(name, handle)
    }

    suspend fun uploadVideoFromUri(
        sourceUri: Uri,
        caption: String,
        musicTitle: String
    ): Result<VideoEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val uploadsDir = File(context.filesDir, "uploads").apply { mkdirs() }
            val destFile = File(uploadsDir, "paktok_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.mp4")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: error("Unable to open selected video file.")

            val profile = wallet.first()
            val initials = profile.profileName
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercaseChar().toString() }
                .ifBlank { "PK" }

            val newVideo = VideoEntity(
                creatorHandle = profile.profileHandle,
                creatorName = profile.profileName,
                creatorInitials = initials,
                caption = caption.trim().ifBlank { "Fresh PakTok upload ✨ #PakTok #Pakistan" },
                musicTitle = musicTitle.trim().ifBlank { "Original Sound - ${profile.profileHandle}" },
                videoUrl = Uri.fromFile(destFile).toString(),
                posterKey = "lahore",
                likesCount = 0,
                commentsCount = 0,
                sharesCount = 0,
                isLiked = false,
                isFollowed = false,
                isUserUploaded = true
            )
            val newId = dao.insertVideo(newVideo)
            newVideo.copy(id = newId)
        }
    }

    suspend fun redeemCoinsToEasypaisa(
        coinsToRedeem: Int,
        easypaisaNumber: String,
        accountName: String
    ): Result<PayoutEntity> {
        if (coinsToRedeem < COINS_PER_TEN_RS || coinsToRedeem % COINS_PER_TEN_RS != 0) {
            return Result.failure(IllegalArgumentException("Redeem in multiples of 1,000 coins (1,000 coins = Rs. 10)."))
        }
        val cleanedPhone = easypaisaNumber.replace("-", "").replace(" ", "")
        if (!cleanedPhone.matches(Regex("^03\\d{9}$"))) {
            return Result.failure(IllegalArgumentException("Enter a valid 11-digit Easypaisa number (03XXXXXXXXX)."))
        }
        val cleanName = accountName.trim()
        if (cleanName.length < 2) {
            return Result.failure(IllegalArgumentException("Enter the Easypaisa account holder name."))
        }

        val deducted = walletDataStore.deductCoins(coinsToRedeem)
        if (!deducted) {
            return Result.failure(IllegalStateException("Insufficient coins for this withdrawal."))
        }

        val pkrAmount = (coinsToRedeem / COINS_PER_TEN_RS) * PKR_PER_THOUSAND_COINS
        val refCode = "EP-${UUID.randomUUID().toString().take(8).uppercase()}"
        val payout = PayoutEntity(
            easypaisaNumber = cleanedPhone,
            accountName = cleanName,
            coinsRedeemed = coinsToRedeem,
            pkrAmount = pkrAmount,
            referenceCode = refCode,
            status = "Processed"
        )
        dao.insertPayout(payout)
        return Result.success(payout)
    }

    companion object {
        const val WATCH_REWARD_INTERVAL_MS = 30_000L
        const val COINS_PER_INTERVAL = 10
        const val COINS_PER_TEN_RS = 1000
        const val PKR_PER_THOUSAND_COINS = 10
        const val LIVE_FOLLOWERS_REQUIRED = 500
    }
}
