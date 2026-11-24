package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.PakTokTab
import com.example.ui.theme.BrightGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneMuted
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.Gold24K
import com.example.ui.theme.GoldHairline
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateObsidian

@Composable
fun PakTokBottomBar(
    currentTab: PakTokTab,
    totalCoins: Int,
    onSelectTab: (PakTokTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianBlack.copy(alpha = 0.96f))
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = GoldHairline, thickness = 0.8.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home Tab
            val isHome = currentTab == PakTokTab.HOME
            Column(
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectTab(PakTokTab.HOME) }
                    .padding(vertical = 6.dp)
                    .testTag("nav_home"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = stringResource(R.string.nav_home),
                    tint = if (isHome) ChampagneGold else ChampagneMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.nav_home),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isHome) FontWeight.Bold else FontWeight.Medium,
                    color = if (isHome) ChampagneGold else ChampagneMuted,
                    fontSize = 11.sp
                )
            }

            // 2. + Upload Tab (Elevated Gold Pill)
            val isUpload = currentTab == PakTokTab.UPLOAD
            Column(
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectTab(PakTokTab.UPLOAD) }
                    .padding(vertical = 4.dp)
                    .testTag("nav_upload"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (isUpload) {
                                    listOf(BrightGold, ChampagneGold)
                                } else {
                                    listOf(Gold24K, ChampagneGold)
                                }
                            )
                        )
                        .border(1.dp, BrightGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.nav_upload),
                        tint = ObsidianBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "+ ${stringResource(R.string.nav_upload)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUpload) BrightGold else ChampagneGold,
                    fontSize = 11.sp
                )
            }

            // 3. Profile Tab (Shows Total Coins + Yellow Coin Icon)
            val isProfile = currentTab == PakTokTab.PROFILE
            Column(
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectTab(PakTokTab.PROFILE) }
                    .padding(vertical = 4.dp)
                    .testTag("nav_profile"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isProfile) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = stringResource(R.string.nav_profile),
                        tint = if (isProfile) ChampagneGold else ChampagneMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlateObsidian)
                            .border(0.8.dp, CoinYellow.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        YellowCoinIcon(size = 11.dp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = formatCompactCount(totalCoins),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = CoinYellow
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.nav_profile),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Medium,
                    color = if (isProfile) ChampagneGold else ChampagneMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
