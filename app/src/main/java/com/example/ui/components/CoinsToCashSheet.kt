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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.PakTokRepository
import com.example.ui.RedemptionStatus
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneMuted
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.EasypaisaEmerald
import com.example.ui.theme.ElevatedCharcoal
import com.example.ui.theme.Gold24K
import com.example.ui.theme.GoldHairline
import com.example.ui.theme.HeartRedGold
import com.example.ui.theme.IvoryText
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SlateObsidian

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinsToCashSheet(
    totalCoins: Int,
    redemptionStatus: RedemptionStatus,
    onDismiss: () -> Unit,
    onConfirmRedeem: (coins: Int, easypaisaPhone: String, accountName: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCoins by remember(totalCoins) {
        mutableIntStateOf(
            when {
                totalCoins >= 5000 -> 5000
                totalCoins >= 2000 -> 2000
                else -> 1000
            }
        )
    }
    var phoneNumber by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf("") }

    val pkrForSelected = (selectedCoins / PakTokRepository.COINS_PER_TEN_RS) * PakTokRepository.PKR_PER_THOUSAND_COINS
    val canRedeemSelected = totalCoins >= selectedCoins && selectedCoins >= PakTokRepository.COINS_PER_TEN_RS

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                .padding(bottom = 28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EasypaisaEmerald.copy(alpha = 0.16f))
                        .border(1.dp, EasypaisaEmerald.copy(alpha = 0.55f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = EasypaisaEmerald
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.easypaisa_sheet_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = ChampagneGold
                    )
                    Text(
                        text = stringResource(R.string.easypaisa_rate_badge),
                        style = MaterialTheme.typography.labelMedium,
                        color = EasypaisaEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Available Coins Summary Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(SlateObsidian, ElevatedCharcoal)
                        )
                    )
                    .border(1.dp, Gold24K.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        YellowCoinIcon(size = 24.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${formatNumberWithCommas(totalCoins)} Coins",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = CoinYellow
                        )
                    }
                    val estimatedPkr = (totalCoins.toFloat() / 1000f) * 10f
                    Text(
                        text = String.format("≈ Rs. %.2f", estimatedPkr),
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ChampagneGold
                    )
                }

                if (totalCoins < PakTokRepository.COINS_PER_TEN_RS) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val progress = (totalCoins.toFloat() / 1000f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        color = CoinYellow,
                        trackColor = ObsidianBlack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${1000 - totalCoins} more coins needed for Rs. 10 Easypaisa (watch 30s = +10 coins).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChampagneMuted,
                        fontSize = 12.sp
                    )
                }
            }

            if (redemptionStatus is RedemptionStatus.Success) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EasypaisaEmerald.copy(alpha = 0.14f))
                        .border(1.dp, EasypaisaEmerald, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = EasypaisaEmerald,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rs. ${redemptionStatus.payout.pkrAmount} Sent to Easypaisa!",
                        style = MaterialTheme.typography.titleLarge,
                        color = IvoryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ref: ${redemptionStatus.payout.referenceCode} • ${redemptionStatus.payout.easypaisaNumber}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 12.sp,
                        color = ChampagneGold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = ObsidianBlack
                        )
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Select Redemption Pack",
                    style = MaterialTheme.typography.labelLarge,
                    color = ChampagneGold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1000 to 10, 2000 to 20, 5000 to 50).forEach { (coins, rs) ->
                        val isSelected = selectedCoins == coins
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Gold24K.copy(alpha = 0.22f) else SlateObsidian)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ChampagneGold else GoldHairline,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCoins = coins }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                YellowCoinIcon(size = 14.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatNumberWithCommas(coins),
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CoinYellow
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rs. $rs",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) IvoryText else ChampagneMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 12) phoneNumber = it },
                    label = { Text(stringResource(R.string.easypaisa_number_label)) },
                    placeholder = { Text("03001234567", color = ChampagneMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("easypaisa_phone_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text(stringResource(R.string.easypaisa_name_label)) },
                    placeholder = { Text("e.g. Muhammad Ali", color = ChampagneMuted) },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("easypaisa_name_input")
                )

                if (redemptionStatus is RedemptionStatus.Error) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = redemptionStatus.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HeartRedGold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onConfirmRedeem(selectedCoins, phoneNumber, accountName) },
                    enabled = canRedeemSelected && redemptionStatus !is RedemptionStatus.Processing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBlack,
                        disabledContainerColor = ElevatedCharcoal,
                        disabledContentColor = ChampagneMuted
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_easypaisa_button")
                ) {
                    if (redemptionStatus is RedemptionStatus.Processing) {
                        CircularProgressIndicator(
                            color = ObsidianBlack,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = if (canRedeemSelected) {
                                "Withdraw Rs. $pkrForSelected via Easypaisa"
                            } else {
                                "Need ${formatNumberWithCommas(selectedCoins)} Coins (1,000 = Rs. 10)"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
