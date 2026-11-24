package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val creatorHandle: String,
    val creatorName: String,
    val creatorInitials: String,
    val caption: String,
    val musicTitle: String,
    val videoUrl: String,
    val posterKey: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean = false,
    val isFollowed: Boolean = false,
    val isUserUploaded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoId: Long,
    val authorHandle: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val easypaisaNumber: String,
    val accountName: String,
    val coinsRedeemed: Int,
    val pkrAmount: Int,
    val referenceCode: String,
    val status: String = "Completed",
    val createdAt: Long = System.currentTimeMillis()
)

data class WalletSnapshot(
    val totalCoins: Int = 0,
    val watchElapsedMs: Long = 0L,
    val totalWatchSeconds: Long = 0L,
    val followersCount: Int = 0,
    val profileHandle: String = "@PakCreator",
    val profileName: String = "PakTok Star"
)
