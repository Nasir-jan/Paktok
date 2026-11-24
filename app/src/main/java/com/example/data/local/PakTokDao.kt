package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.CommentEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PakTokDao {

    @Query("SELECT * FROM videos ORDER BY isUserUploaded DESC, createdAt DESC")
    fun observeAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isUserUploaded = 1 ORDER BY createdAt DESC")
    fun observeUserVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun observeLikedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getVideoCount(): Int

    @Query("UPDATE videos SET videoUrl = :newUrl WHERE creatorHandle = :creatorHandle AND videoUrl LIKE '%commondatastorage.googleapis.com%'")
    suspend fun migrateLegacySeedUrl(creatorHandle: String, newUrl: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity): Long

    @Query("UPDATE videos SET isLiked = :liked, likesCount = likesCount + :delta WHERE id = :videoId")
    suspend fun updateLikeState(videoId: Long, liked: Boolean, delta: Int)

    @Query("UPDATE videos SET isFollowed = :followed WHERE creatorHandle = :creatorHandle")
    suspend fun updateFollowForCreator(creatorHandle: String, followed: Boolean)

    @Query("UPDATE videos SET sharesCount = sharesCount + 1 WHERE id = :videoId")
    suspend fun incrementShareCount(videoId: Long)

    @Query("UPDATE videos SET commentsCount = commentsCount + 1 WHERE id = :videoId")
    suspend fun incrementCommentCount(videoId: Long)

    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY createdAt DESC")
    fun observeCommentsForVideo(videoId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Transaction
    suspend fun addCommentAndIncrementCount(comment: CommentEntity) {
        insertComment(comment)
        incrementCommentCount(comment.videoId)
    }

    @Query("SELECT * FROM payouts ORDER BY createdAt DESC")
    fun observePayouts(): Flow<List<PayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity)
}
