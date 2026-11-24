package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CommentEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.VideoEntity

@Database(
    entities = [VideoEntity::class, CommentEntity::class, PayoutEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PakTokDatabase : RoomDatabase() {
    abstract fun pakTokDao(): PakTokDao

    companion object {
        @Volatile
        private var INSTANCE: PakTokDatabase? = null

        fun getInstance(context: Context): PakTokDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PakTokDatabase::class.java,
                    "paktok_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object PakTokSeedData {
    fun initialVideos(): List<VideoEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            VideoEntity(
                creatorHandle = "@lahore_vibes",
                creatorName = "Bilal Ahmed",
                creatorInitials = "BA",
                caption = "Midnight chai & glowing Badshahi Mosque lights in Old Lahore ✨🕌 #Lahore #PakTok #NightVibes",
                musicTitle = "Sufi Strings - Golden Lahore Mix",
                videoUrl = "raw://reel_lahore",
                posterKey = "lahore",
                likesCount = 14820,
                commentsCount = 3,
                sharesCount = 412,
                isLiked = false,
                isFollowed = false,
                isUserUploaded = false,
                createdAt = now - 10_000L
            ),
            VideoEntity(
                creatorHandle = "@karachi_cricket",
                creatorName = "Zainab Raza",
                creatorInitials = "ZR",
                caption = "Last-over yorker under stadium floodlights! Pure goosebumps 🏏🔥 #PakistanCricket #PSL #PakTok",
                musicTitle = "Stadium Roar - Champion Anthem",
                videoUrl = "raw://reel_cricket",
                posterKey = "cricket",
                likesCount = 29400,
                commentsCount = 2,
                sharesCount = 1095,
                isLiked = false,
                isFollowed = false,
                isUserUploaded = false,
                createdAt = now - 20_000L
            ),
            VideoEntity(
                creatorHandle = "@hunza_nomad",
                creatorName = "Hamza Gilgit",
                creatorInitials = "HG",
                caption = "Golden hour hitting Rakaposhi peaks in Hunza Valley 🏔️💛 #Hunza #NorthernPakistan #Travel",
                musicTitle = "Rubab Echoes - Karakoram Dawn",
                videoUrl = "raw://reel_hunza",
                posterKey = "hunza",
                likesCount = 41250,
                commentsCount = 2,
                sharesCount = 2340,
                isLiked = false,
                isFollowed = true,
                isUserUploaded = false,
                createdAt = now - 30_000L
            ),
            VideoEntity(
                creatorHandle = "@islamabad_nights",
                creatorName = "Ayesha Khan",
                creatorInitials = "AK",
                caption = "Margalla Hills sunset drive & golden coffee stop ☕🌙 #Islamabad #DesiReels #PakTok",
                musicTitle = "Coke Studio Lofi - Velvet Gold",
                videoUrl = "raw://reel_islamabad",
                posterKey = "lahore",
                likesCount = 8750,
                commentsCount = 1,
                sharesCount = 189,
                isLiked = false,
                isFollowed = false,
                isUserUploaded = false,
                createdAt = now - 40_000L
            ),
            VideoEntity(
                creatorHandle = "@multan_beats",
                creatorName = "Usman Tariq",
                creatorInitials = "UT",
                caption = "Street rhythm & desert sunset freestyle 🎶⚡ #Multan #PakistaniTalent #ForYou",
                musicTitle = "Dhol & Bass - Gold Edition",
                videoUrl = "raw://reel_cricket",
                posterKey = "cricket",
                likesCount = 19310,
                commentsCount = 1,
                sharesCount = 640,
                isLiked = false,
                isFollowed = false,
                isUserUploaded = false,
                createdAt = now - 50_000L
            )
        )
    }

    fun initialComments(videoIds: List<Long>): List<CommentEntity> {
        val now = System.currentTimeMillis()
        val list = mutableListOf<CommentEntity>()
        if (videoIds.isNotEmpty()) {
            list += CommentEntity(videoId = videoIds[0], authorHandle = "@sana_writes", text = "Lahore ki raat aur Badshahi Masjid, mashallah!", createdAt = now - 5000)
            list += CommentEntity(videoId = videoIds[0], authorHandle = "@ali_lahori", text = "Food street vibes are unmatched 💛", createdAt = now - 4000)
            list += CommentEntity(videoId = videoIds[0], authorHandle = "@umer_pk", text = "Watch time se coins bhi mil rahe hain 🔥", createdAt = now - 3000)
        }
        if (videoIds.size > 1) {
            list += CommentEntity(videoId = videoIds[1], authorHandle = "@fawad_sports", text = "What a yorker! Stadium crowd went crazy!", createdAt = now - 4500)
            list += CommentEntity(videoId = videoIds[1], authorHandle = "@haider_11", text = "Pure cricket fever 🏏✨", createdAt = now - 2500)
        }
        if (videoIds.size > 2) {
            list += CommentEntity(videoId = videoIds[2], authorHandle = "@travel_pak", text = "Hunza is literal heaven on earth!", createdAt = now - 6000)
            list += CommentEntity(videoId = videoIds[2], authorHandle = "@maham_clicks", text = "Golden light on those peaks is unreal.", createdAt = now - 1500)
        }
        if (videoIds.size > 3) {
            list += CommentEntity(videoId = videoIds[3], authorHandle = "@isb_diaries", text = "Margalla evenings hit different!", createdAt = now - 2000)
        }
        if (videoIds.size > 4) {
            list += CommentEntity(videoId = videoIds[4], authorHandle = "@beats_pk", text = "Solid groove bro 🔥", createdAt = now - 1800)
        }
        return list
    }
}
