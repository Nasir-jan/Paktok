package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.PakTokRepository
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneMuted
import com.example.ui.theme.EasypaisaEmerald
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.Gold24K
import com.example.ui.theme.GoldHairline
import com.example.ui.theme.HeartRedGold
import com.example.ui.theme.IvoryText
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateObsidian
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamSheet(
    followersCount: Int,
    profileHandle: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val required = PakTokRepository.LIVE_FOLLOWERS_REQUIRED
    val isUnlocked = followersCount >= required
    val remaining = (required - followersCount).coerceAtLeast(0)
    val progress = (followersCount.toFloat() / required.toFloat()).coerceIn(0f, 1f)

    var streamTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Chat & Q&A") }
    var isStreaming by remember { mutableStateOf(false) }
    var liveSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isStreaming) {
        if (isStreaming) {
            liveSeconds = 0
            while (isStreaming) {
                delay(1000L)
                liveSeconds += 1
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            isStreaming = false
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = CarbonBlack,
        contentColor = IvoryText
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isUnlocked) {
                // LOCKED STATE (< 500 Followers)
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Gold24K.copy(alpha = 0.25f), SlateObsidian)
                            )
                        )
                        .border(2.dp, ChampagneGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.live_locked_title),
                        tint = ChampagneGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.live_locked_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Live streaming unlocks at $required followers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChampagneMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Progress Card: e.g. 320/500 & exact remaining count
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SlateObsidian)
                        .border(1.dp, Gold24K.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                        .testTag("live_lock_progress_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FOLLOWERS PROGRESS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneMuted
                        )
                        Text(
                            text = "$followersCount/$required",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BrightGold,
                            modifier = Modifier.testTag("live_followers_ratio")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        color = ChampagneGold,
                        trackColor = ObsidianBlack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$remaining followers remaining to unlock Live",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IvoryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("live_remaining_followers")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianBlack.copy(alpha = 0.65f))
                        .border(1.dp, GoldHairline, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "How to reach 500 followers faster:",
                        style = MaterialTheme.typography.labelLarge,
                        color = ChampagneGold
                    )
                    Text(
                        text = "• Post engaging short reels using + Upload",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChampagneMuted
                    )
                    Text(
                        text = "• Use trending Pakistani tags (#PakTok #Lahore #Cricket)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChampagneMuted
                    )
                    Text(
                        text = "• Share your videos with friends to grow your audience",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChampagneMuted
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            } else {
                // UNLOCKED STATE (>= 500 Followers)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(HeartRedGold.copy(alpha = 0.18f))
                                .border(1.dp, HeartRedGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Sensors,
                                contentDescription = null,
                                tint = HeartRedGold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PakTok Live Studio",
                                style = MaterialTheme.typography.titleLarge,
                                color = ChampagneGold
                            )
                            Text(
                                text = "$followersCount followers • Unlocked",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp,
                                color = EasypaisaEmerald
                            )
                        }
                    }

                    if (isStreaming) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HeartRedGold)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            val mins = liveSeconds / 60
                            val secs = liveSeconds % 60
                            Text(
                                text = String.format("LIVE %02d:%02d", mins, secs),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = IvoryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = GoldHairline)
                Spacer(modifier = Modifier.height(16.dp))

                if (isStreaming) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(SlateObsidian, ObsidianBlack)
                                )
                            )
                            .border(1.5.dp, HeartRedGold.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Sensors,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = streamTitle.ifBlank { "$profileHandle is Live!" },
                                style = MaterialTheme.typography.titleMedium,
                                color = IvoryText,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Topic: $selectedCategory",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ChampagneGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { isStreaming = false },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = HeartRedGold
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.StopCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("End Live Broadcast", fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedTextField(
                        value = streamTitle,
                        onValueChange = { streamTitle = it },
                        label = { Text("Live Stream Title") },
                        placeholder = { Text("e.g. Late Night Chai & Q&A", color = ChampagneMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = GoldHairline,
                            focusedLabelColor = ChampagneGold,
                            unfocusedLabelColor = ChampagneMuted,
                            focusedTextColor = IvoryText,
                            unfocusedTextColor = IvoryText,
                            focusedContainerColor = SlateObsidian,
                            unfocusedContainerColor = SlateObsidian
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Chat & Q&A", "Music", "Cricket", "Gaming", "Travel").forEach { cat ->
                            val selected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selected) ChampagneGold else SlateObsidian)
                                    .border(
                                        1.dp,
                                        if (selected) BrightGold else GoldHairline,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) ObsidianBlack else ChampagneGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { isStreaming = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightGold,
                            contentColor = ObsidianBlack,
                            disabledContainerColor = ElevatedCharcoal
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_live_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sensors,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.live_go_live),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
