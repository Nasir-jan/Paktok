package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.LinearProgressIndicator
import com.example.data.repository.PakTokRepository
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PayoutEntity
import com.example.data.model.VideoEntity
import com.example.data.model.WalletSnapshot
import com.example.ui.components.YellowCoinIcon
import com.example.ui.components.formatCompactCount
import com.example.ui.components.formatNumberWithCommas
import com.example.ui.theme.BrightGold
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneMuted
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.EasypaisaEmerald
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.Gold24K
import com.example.ui.theme.GoldHairline
import com.example.ui.theme.IvoryText
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateObsidian
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    wallet: WalletSnapshot,
    userVideos: List<VideoEntity>,
    likedVideos: List<VideoEntity>,
    payouts: List<PayoutEntity>,
    onOpenCoinsToCash: () -> Unit,
    onOpenLive: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onOpenVideoInFeed: (VideoEntity) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackToHome() }

    var selectedSubTab by remember { mutableIntStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            // 1. Luxury Black & Gold Profile Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_profile_banner_1795423330482),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0f to ObsidianBlack.copy(alpha = 0.45f),
                                    0.6f to ObsidianBlack.copy(alpha = 0.72f),
                                    1f to ObsidianBlack
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PakTok Creator",
                            style = MaterialTheme.typography.titleLarge,
                            color = ChampagneGold
                        )
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ObsidianBlack.copy(alpha = 0.65f))
                                .border(1.dp, GoldHairline, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = ChampagneGold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val initials = wallet.profileName
                            .split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .joinToString("") { it.first().uppercaseChar().toString() }
                            .ifBlank { "PK" }

                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(SlateObsidian, CarbonBlack)
                                    )
                                )
                                .border(2.5.dp, ChampagneGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = wallet.profileName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = IvoryText,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = wallet.profileHandle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ChampagneGold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                StatMini(label = "Followers", value = "${wallet.followersCount}/500")
                                StatMini(label = "Videos", value = userVideos.size.toString())
                                StatMini(label = "Liked", value = likedVideos.size.toString())
                                StatMini(label = "Watch", value = "${wallet.totalWatchSeconds / 60}m ${wallet.totalWatchSeconds % 60}s")
                            }
                        }
                    }
                }
            }

            // 2. Total Coins & Easypaisa "Coins to Cash" Vault Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SlateObsidian, CarbonBlack, ElevatedCharcoal)
                            )
                        )
                        .border(1.5.dp, Gold24K.copy(alpha = 0.85f), RoundedCornerShape(22.dp))
                        .padding(18.dp)
                        .testTag("profile_coins_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.profile_total_coins).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneMuted,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                YellowCoinIcon(size = 34.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = formatNumberWithCommas(wallet.totalCoins),
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = CoinYellow,
                                    maxLines = 1,
                                    modifier = Modifier.testTag("profile_total_coins")
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val cashValueRs = (wallet.totalCoins.toFloat() / 1000f) * 10f
                            Text(
                                text = "CASH VALUE",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("Rs. %.2f", cashValueRs),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = EasypaisaEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = GoldHairline)
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.easypaisa_rate_badge),
                            style = MaterialTheme.typography.labelMedium,
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "+10 coins / 30s watch",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenCoinsToCash,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrightGold,
                            contentColor = ObsidianBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("coins_to_cash_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${stringResource(R.string.profile_coins_to_cash)}  •  Easypaisa",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2b. Live Access Milestone Card (Locked until 500 followers)
            item {
                val required = PakTokRepository.LIVE_FOLLOWERS_REQUIRED
                val remaining = (required - wallet.followersCount).coerceAtLeast(0)
                val isUnlocked = wallet.followersCount >= required
                val progress = (wallet.followersCount.toFloat() / required.toFloat()).coerceIn(0f, 1f)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(CarbonBlack)
                        .border(1.dp, GoldHairline, RoundedCornerShape(18.dp))
                        .clickable(onClick = onOpenLive)
                        .padding(16.dp)
                        .testTag("profile_live_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SlateObsidian)
                                    .border(1.dp, Gold24K.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Filled.Sensors else Icons.Filled.Lock,
                                    contentDescription = stringResource(R.string.live_label),
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isUnlocked) "Live Streaming Unlocked" else "Live Streaming Locked",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = IvoryText,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isUnlocked) "Tap to go Live now" else "$remaining followers left to unlock",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ChampagneMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = "${wallet.followersCount}/$required",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isUnlocked) EasypaisaEmerald else ChampagneGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        color = if (isUnlocked) EasypaisaEmerald else ChampagneGold,
                        trackColor = ObsidianBlack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            // 3. Profile Sub-tabs (My Videos / Liked / Payouts)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("My Videos", "Liked", "Easypaisa").forEachIndexed { index, title ->
                        val selected = selectedSubTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) ChampagneGold else CarbonBlack)
                                .border(1.dp, if (selected) BrightGold else GoldHairline, RoundedCornerShape(12.dp))
                                .clickable { selectedSubTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) ObsidianBlack else ChampagneMuted
                            )
                        }
                    }
                }
            }

            when (selectedSubTab) {
                0 -> {
                    if (userVideos.isEmpty()) {
                        item {
                            ProfileEmptyState(
                                icon = Icons.Outlined.Videocam,
                                title = "No uploaded videos yet",
                                subtitle = "Tap + Upload in the bottom bar to post your first PakTok reel."
                            )
                        }
                    } else {
                        val rows = userVideos.chunked(3)
                        items(rows) { rowVideos ->
                            VideoThumbnailGridRow(
                                rowVideos = rowVideos,
                                onVideoClick = onOpenVideoInFeed
                            )
                        }
                    }
                }
                1 -> {
                    if (likedVideos.isEmpty()) {
                        item {
                            ProfileEmptyState(
                                icon = Icons.Filled.Favorite,
                                title = "No liked videos yet",
                                subtitle = "Double-tap or press Heart on any video in the Home feed."
                            )
                        }
                    } else {
                        val rows = likedVideos.chunked(3)
                        items(rows) { rowVideos ->
                            VideoThumbnailGridRow(
                                rowVideos = rowVideos,
                                onVideoClick = onOpenVideoInFeed
                            )
                        }
                    }
                }
                2 -> {
                    if (payouts.isEmpty()) {
                        item {
                            ProfileEmptyState(
                                icon = Icons.Outlined.ReceiptLong,
                                title = "No Easypaisa payouts yet",
                                subtitle = "Watch videos to earn coins (+10 every 30s) and redeem 1,000 coins = Rs. 10 Easypaisa."
                            )
                        }
                    } else {
                        items(payouts, key = { it.id }) { payout ->
                            PayoutHistoryCard(payout = payout)
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = wallet.profileName,
            currentHandle = wallet.profileHandle,
            onDismiss = { showEditDialog = false },
            onSave = { name, handle ->
                onUpdateProfile(name, handle)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun StatMini(label: String, value: String) {
    Column {
        Text(
            text = value,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = IvoryText
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ChampagneMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun VideoThumbnailGridRow(
    rowVideos: List<VideoEntity>,
    onVideoClick: (VideoEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rowVideos.forEach { video ->
            val posterResId = when (video.posterKey) {
                "cricket" -> R.drawable.img_feed_cricket_1795423331244
                "hunza" -> R.drawable.img_feed_hunza_1795423352741
                else -> R.drawable.img_feed_lahore_1795423330760
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, GoldHairline, RoundedCornerShape(14.dp))
                    .clickable { onVideoClick(video) }
            ) {
                Image(
                    painter = painterResource(id = posterResId),
                    contentDescription = video.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.3f to Color.Transparent,
                                1f to ObsidianBlack.copy(alpha = 0.85f)
                            )
                        )
                )
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp)
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = formatCompactCount(video.likesCount),
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        color = IvoryText
                    )
                }
            }
        }
        repeat(3 - rowVideos.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PayoutHistoryCard(payout: PayoutEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CarbonBlack)
            .border(1.dp, GoldHairline, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = "Easypaisa • ${payout.easypaisaNumber}",
                style = MaterialTheme.typography.titleMedium,
                color = IvoryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${payout.referenceCode} • ${dateFormat.format(Date(payout.createdAt))}",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp,
                color = ChampagneMuted
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Rs. ${payout.pkrAmount}",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = EasypaisaEmerald
            )
            Text(
                text = "-${formatNumberWithCommas(payout.coinsRedeemed)} coins",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp,
                color = CoinYellow
            )
        }
    }
}

@Composable
private fun ProfileEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gold24K.copy(alpha = 0.7f),
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = IvoryText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = ChampagneMuted
        )
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentHandle: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var handle by remember { mutableStateOf(currentHandle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CarbonBlack,
        titleContentColor = ChampagneGold,
        textContentColor = IvoryText,
        title = { Text("Edit PakTok Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = GoldHairline,
                        focusedTextColor = IvoryText,
                        unfocusedTextColor = IvoryText
                    )
                )
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Creator @Handle") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = GoldHairline,
                        focusedTextColor = IvoryText,
                        unfocusedTextColor = IvoryText
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, handle) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBlack
                )
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ChampagneMuted)
            }
        }
    )
}
