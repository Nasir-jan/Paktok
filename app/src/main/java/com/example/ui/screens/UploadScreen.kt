package com.example.ui.screens

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.R
import com.example.ui.UploadStatus
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneMuted
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.Gold24K
import com.example.ui.theme.GoldHairline
import com.example.ui.theme.HeartRedGold
import com.example.ui.theme.IvoryText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateObsidian

import com.example.data.repository.PakTokRepository

@OptIn(UnstableApi::class)
@Composable
fun UploadScreen(
    followersCount: Int,
    uploadStatus: UploadStatus,
    onPublishVideo: (Uri, String, String) -> Unit,
    onOpenLive: () -> Unit,
    onUploadCompleted: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackToHome() }

    val context = LocalContext.current
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var caption by remember { mutableStateOf("") }
    var musicTitle by remember { mutableStateOf("Original Sound - PakTok Studio") }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
        }
    }

    LaunchedEffect(uploadStatus) {
        if (uploadStatus is UploadStatus.Published) {
            selectedVideoUri = null
            caption = ""
            onUploadCompleted()
        }
    }

    val previewPlayer = remember(selectedVideoUri) {
        selectedVideoUri?.let { uri ->
            ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
                playWhenReady = true
            }
        }
    }

    DisposableEffect(previewPlayer) {
        onDispose {
            previewPlayer?.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.upload_title),
                style = MaterialTheme.typography.headlineMedium,
                color = ChampagneGold
            )
            Text(
                text = "Share your moment with Pakistan in 24k Gold style",
                style = MaterialTheme.typography.bodyMedium,
                color = ChampagneMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Switcher: Upload Video vs Go Live (500 followers gate)
            val liveUnlocked = followersCount >= PakTokRepository.LIVE_FOLLOWERS_REQUIRED
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ChampagneGold)
                        .padding(vertical = 10.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.VideoLibrary,
                        contentDescription = null,
                        tint = ObsidianBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Upload Video",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack,
                        maxLines = 1
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateObsidian)
                        .border(1.dp, Gold24K.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
                        .clickable(onClick = onOpenLive)
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                        .testTag("upload_go_live_tab"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (liveUnlocked) Icons.Filled.Sensors else Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.live_go_live),
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (liveUnlocked) "Go Live" else "Live $followersCount/${PakTokRepository.LIVE_FOLLOWERS_REQUIRED}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Video Preview / Zero-Permission Photo Picker Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SlateObsidian, CarbonBlack)
                        )
                    )
                    .border(1.5.dp, Gold24K.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                    .clickable {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    }
                    .testTag("pick_video_area"),
                contentAlignment = Alignment.Center
            ) {
                if (previewPlayer != null) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                player = previewPlayer
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    OutlinedButton(
                        onClick = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ObsidianBlack.copy(alpha = 0.75f),
                            contentColor = ChampagneGold
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    ) {
                        Text(stringResource(R.string.upload_change_video))
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Gold24K.copy(alpha = 0.16f))
                                .border(1.dp, ChampagneGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VideoLibrary,
                                contentDescription = stringResource(R.string.upload_select_video),
                                tint = ChampagneGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.upload_select_video),
                            style = MaterialTheme.typography.titleMedium,
                            color = IvoryText,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to choose any short MP4 / video clip",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ChampagneMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption & Hashtags") },
                placeholder = { Text(stringResource(R.string.upload_caption_hint), color = ChampagneMuted) },
                minLines = 3,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = GoldHairline,
                    focusedLabelColor = ChampagneGold,
                    unfocusedLabelColor = ChampagneMuted,
                    focusedTextColor = IvoryText,
                    unfocusedTextColor = IvoryText,
                    focusedContainerColor = CarbonBlack,
                    unfocusedContainerColor = CarbonBlack
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_caption_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Pakistani Hashtag Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("#PakTok", "#Lahore", "#Cricket", "#DesiVibes", "#Hunza", "#Comedy").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateObsidian)
                            .border(1.dp, GoldHairline, RoundedCornerShape(16.dp))
                            .clickable {
                                caption = if (caption.isBlank()) tag else "$caption $tag"
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium,
                            color = ChampagneGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = musicTitle,
                onValueChange = { musicTitle = it },
                label = { Text(stringResource(R.string.upload_music_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = ChampagneGold
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChampagneGold,
                    unfocusedBorderColor = GoldHairline,
                    focusedLabelColor = ChampagneGold,
                    unfocusedLabelColor = ChampagneMuted,
                    focusedTextColor = IvoryText,
                    unfocusedTextColor = IvoryText,
                    focusedContainerColor = CarbonBlack,
                    unfocusedContainerColor = CarbonBlack
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_music_input")
            )

            if (uploadStatus is UploadStatus.Error) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = uploadStatus.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HeartRedGold
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    selectedVideoUri?.let { uri ->
                        onPublishVideo(uri, caption, musicTitle)
                    }
                },
                enabled = selectedVideoUri != null && uploadStatus !is UploadStatus.Publishing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightGold,
                    contentColor = ObsidianBlack,
                    disabledContainerColor = ElevatedCharcoal,
                    disabledContentColor = ChampagneMuted
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("publish_video_button")
            ) {
                if (uploadStatus is UploadStatus.Publishing) {
                    CircularProgressIndicator(
                        color = ObsidianBlack,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.upload_publish_cta),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
