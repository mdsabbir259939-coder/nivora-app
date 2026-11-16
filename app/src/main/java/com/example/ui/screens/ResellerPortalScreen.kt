package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.ui.components.NivoraArtwork
import com.example.ui.components.OrderVerticalTimeline
import com.example.ui.theme.NivoraErrorRed
import com.example.ui.theme.NivoraSuccessGreen
import com.example.ui.theme.NivoraWarningAmber
import com.example.ui.viewmodel.NivoraUiState
import com.example.ui.viewmodel.OrderStatusStep
import com.example.ui.viewmodel.formatBdt
import com.example.ui.viewmodel.formatTimestamp

private enum class ResellerTab(val labelBn: String) {
    CATALOG("ক্যাটালগ"),
    WALLET("আয় ও উত্তোলন"),
    ORDERS("অর্ডারসমূহ")
}

@Composable
fun ResellerPortalScreen(
    state: NivoraUiState,
    onSelectReseller: (Long) -> Unit,
    onPlaceResellerOrder: (ProductEntity, String, String, Int, String, String, String, Double, String) -> Unit,
    onSubmitWithdrawal: (Double, String, String) -> Unit,
    onCopySuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(ResellerTab.CATALOG) }
    var selectedProductForResell by remember { mutableStateOf<ProductEntity?>(null) }
    var showWithdrawSheet by remember { mutableStateOf(false) }

    if (selectedTab != ResellerTab.CATALOG) {
        BackHandler { selectedTab = ResellerTab.CATALOG }
    }

    val activeReseller = state.activeReseller

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = selectedTab == ResellerTab.CATALOG,
                    onClick = { selectedTab = ResellerTab.CATALOG },
                    icon = {
                        Icon(
                            if (selectedTab == ResellerTab.CATALOG) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                            contentDescription = ResellerTab.CATALOG.labelBn
                        )
                    },
                    label = { Text(ResellerTab.CATALOG.labelBn) },
                    modifier = Modifier.testTag("reseller_tab_catalog")
                )
                NavigationBarItem(
                    selected = selectedTab == ResellerTab.WALLET,
                    onClick = { selectedTab = ResellerTab.WALLET },
                    icon = {
                        Icon(
                            if (selectedTab == ResellerTab.WALLET) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = ResellerTab.WALLET.labelBn
                        )
                    },
                    label = { Text(ResellerTab.WALLET.labelBn) },
                    modifier = Modifier.testTag("reseller_tab_wallet")
                )
                NavigationBarItem(
                    selected = selectedTab == ResellerTab.ORDERS,
                    onClick = { selectedTab = ResellerTab.ORDERS },
                    icon = {
                        Icon(
                            if (selectedTab == ResellerTab.ORDERS) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                            contentDescription = ResellerTab.ORDERS.labelBn
                        )
                    },
                    label = { Text(ResellerTab.ORDERS.labelBn) },
                    modifier = Modifier.testTag("reseller_tab_orders")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.resellers.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.resellers, key = { it.id }) { reseller ->
                        val selected = activeReseller?.id == reseller.id
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectReseller(reseller.id) },
                            label = {
                                Text("${reseller.name} (${formatBdt(reseller.commissionBalance)})")
                            }
                        )
                    }
                }
            }

            when (selectedTab) {
                ResellerTab.CATALOG -> {
                    ResellerCatalogFeed(
                        state = state,
                        onResellClick = { selectedProductForResell = it },
                        onCopySuccess = onCopySuccess
                    )
                }
                ResellerTab.WALLET -> {
                    ResellerWalletView(
                        reseller = activeReseller,
                        withdrawals = state.withdrawals.filter { it.resellerId == activeReseller?.id },
                        onOpenWithdrawSheet = { showWithdrawSheet = true }
                    )
                }
                ResellerTab.ORDERS -> {
                    val resellerOrders = state.orders.filter {
                        activeReseller == null || it.resellerId == activeReseller.id
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "রিসেল অর্ডার ও ট্র্যাকিং (${resellerOrders.size})",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(resellerOrders, key = { it.id }) { order ->
                            val step = OrderStatusStep.fromKey(order.status)
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "অর্ডার #${order.orderCode}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = "কমিশন +${formatBdt(order.resellerCommissionEarned)}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "গ্রাহক: ${order.customerName} (${order.customerPhone})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${order.itemsSummary} • ${step.labelBn}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    OrderVerticalTimeline(currentStatusKey = order.status)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedProductForResell?.let { product ->
        ResellOrderBottomSheet(
            product = product,
            onDismiss = { selectedProductForResell = null },
            onSubmit = { size, color, qty, cName, cPhone, addr, sellingPrice, payMethod ->
                onPlaceResellerOrder(product, size, color, qty, cName, cPhone, addr, sellingPrice, payMethod)
                selectedProductForResell = null
                selectedTab = ResellerTab.ORDERS
            }
        )
    }

    if (showWithdrawSheet && activeReseller != null) {
        WithdrawalRequestBottomSheet(
            availableBalance = activeReseller.commissionBalance,
            onDismiss = { showWithdrawSheet = false },
            onSubmit = { amount, method, account ->
                onSubmitWithdrawal(amount, method, account)
                showWithdrawSheet = false
            }
        )
    }
}

@Composable
private fun ResellerCatalogFeed(
    state: NivoraUiState,
    onResellClick: (ProductEntity) -> Unit,
    onCopySuccess: (String) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    val filtered = remember(state.products, selectedCategoryId) {
        state.products.filter { selectedCategoryId == null || it.categoryId == selectedCategoryId }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null },
                        label = { Text("সব পণ্য") }
                    )
                }
                items(state.categories, key = { it.id }) { cat ->
                    FilterChip(
                        selected = selectedCategoryId == cat.id,
                        onClick = {
                            selectedCategoryId = if (selectedCategoryId == cat.id) null else cat.id
                        },
                        label = { Text(cat.nameBn) }
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NivoraArtwork(
                            imageKey = product.imageKey,
                            contentDescription = product.title,
                            modifier = Modifier
                                .size(92.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "হোলসেল / বিক্রয় মূল্য: ${formatBdt(product.finalPrice)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "আপনার কমিশন: ${formatBdt(product.resellerCommission)} / পিস",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = if (product.inStock) "স্টকে আছে • সাইজ: ${product.sizesCsv}" else "বর্তমানে স্টক শেষ",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (product.inStock) NivoraSuccessGreen else NivoraErrorRed
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val caption = """
                                    ✨ ${product.title}
                                    💰 মূল্য: ${formatBdt(product.finalPrice)}
                                    📏 সাইজ: ${product.sizesCsv}
                                    🎨 রঙ: ${product.colorsCsv}
                                    📝 ${product.description}
                                    📦 অর্ডার করতে ইনবক্স করুন! (NIVORA)
                                """.trimIndent()
                                clipboard.setText(AnnotatedString(caption))
                                onCopySuccess("পণ্যের বিবরণ ক্লিপবোর্ডে কপি হয়েছে!")
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.ContentCopy, null, Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("বিবরণ কপি")
                        }
                        Button(
                            onClick = { onResellClick(product) },
                            enabled = product.inStock,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("resell_order_btn_${product.id}")
                        ) {
                            Text(if (product.inStock) "রিসেল অর্ডার করুন" else "স্টক শেষ")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResellerWalletView(
    reseller: UserEntity?,
    withdrawals: List<com.example.data.local.WithdrawalRequestEntity>,
    onOpenWithdrawSheet: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "${reseller?.name ?: "রিসেলার"} • কমিশন ওয়ালেট",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        Column {
                            Text(
                                text = "উত্তোলনযোগ্য ব্যালেন্স",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                            Text(
                                text = formatBdt(reseller?.commissionBalance ?: 0.0),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "মোট পরিশোধিত আয়",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
                                )
                                Text(
                                    text = formatBdt(reseller?.totalPaidCommission ?: 0.0),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "মোট রিসেল অর্ডার",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
                                )
                                Text(
                                    text = "${reseller?.totalOrdersCount ?: 0} টি",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Button(
                            onClick = onOpenWithdrawSheet,
                            enabled = (reseller?.commissionBalance ?: 0.0) > 0.0,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("open_withdraw_sheet_button")
                        ) {
                            Text("টাকা উত্তোলনের অনুরোধ করুন")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "উত্তোলন ইতিহাস (${withdrawals.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(withdrawals, key = { it.id }) { req ->
            val (statusBn, statusColor) = when (req.status) {
                "APPROVED" -> "অনুমোদিত (পরিশোধিত)" to NivoraSuccessGreen
                "REJECTED" -> "প্রত্যাখ্যাত" to NivoraErrorRed
                else -> "অপেক্ষমান" to NivoraWarningAmber
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "${formatBdt(req.amount)} • ${req.method}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "অ্যাকাউন্ট: ${req.accountNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTimestamp(req.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = statusColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = statusBn,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ResellOrderBottomSheet(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Int, String, String, String, Double, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var size by remember { mutableStateOf(product.sizesList.firstOrNull() ?: "Standard") }
    var color by remember { mutableStateOf(product.colorsList.firstOrNull() ?: "অরিজিনাল") }
    var qty by remember { mutableIntStateOf(1) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var sellingPriceText by remember { mutableStateOf("${product.finalPrice.toInt()}") }
    var paymentMethod by remember { mutableStateOf("ক্যাশ অন ডেলিভারি") }

    val customSellingPrice = sellingPriceText.toDoubleOrNull() ?: product.finalPrice
    val extraMarkup = (customSellingPrice - product.finalPrice).coerceAtLeast(0.0)
    val projectedCommission = (product.resellerCommission + extraMarkup) * qty

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "রিসেল অর্ডার: ${product.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (product.sizesList.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.sizesList.forEach { s ->
                        FilterChip(selected = size == s, onClick = { size = s }, label = { Text(s) })
                    }
                }
            }
            if (product.colorsList.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.colorsList.forEach { c ->
                        FilterChip(selected = color == c, onClick = { color = c }, label = { Text(c) })
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("পরিমাণ:", style = MaterialTheme.typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (qty > 1) qty-- }) {
                        Icon(Icons.Filled.Remove, null)
                    }
                    Text("$qty", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { qty++ }) {
                        Icon(Icons.Filled.Add, null)
                    }
                }
            }

            OutlinedTextField(
                value = sellingPriceText,
                onValueChange = { sellingPriceText = it.filter { ch -> ch.isDigit() } },
                label = { Text("গ্রাহকের কাছে বিক্রয় মূল্য (ন্যূনতম ${formatBdt(product.finalPrice)})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "এই অর্ডারে আপনার মোট কমিশন: ${formatBdt(projectedCommission)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("গ্রাহকের নাম") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = customerPhone,
                onValueChange = { customerPhone = it },
                label = { Text("গ্রাহকের মোবাইল নম্বর") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("ডেলিভারি ঠিকানা") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ক্যাশ অন ডেলিভারি", "বিকাশ").forEach { m ->
                    FilterChip(selected = paymentMethod == m, onClick = { paymentMethod = m }, label = { Text(m) })
                }
            }

            Button(
                onClick = {
                    onSubmit(
                        size,
                        color,
                        qty,
                        customerName,
                        customerPhone,
                        address,
                        customSellingPrice,
                        paymentMethod
                    )
                },
                enabled = customerName.isNotBlank() && customerPhone.isNotBlank() && address.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("রিসেল অর্ডার সাবমিট করুন")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawalRequestBottomSheet(
    availableBalance: Double,
    onDismiss: () -> Unit,
    onSubmit: (Double, String, String) -> Unit
) {
    var amountText by remember { mutableStateOf("${availableBalance.toInt().coerceAtMost(500)}") }
    var method by remember { mutableStateOf("bKash") }
    var accountNumber by remember { mutableStateOf("01911-223344") }

    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "কমিশন উত্তোলন অনুরোধ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "উপলব্ধ ব্যালেন্স: ${formatBdt(availableBalance)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("bKash", "Nagad", "Rocket").forEach { m ->
                    FilterChip(selected = method == m, onClick = { method = m }, label = { Text(m) })
                }
            }
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                label = { Text("টাকার পরিমাণ (৳)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text("$method পার্সোনাল নম্বর") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onSubmit(parsedAmount, method, accountNumber) },
                enabled = parsedAmount > 0 && parsedAmount <= availableBalance && accountNumber.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_withdrawal_button")
            ) {
                Text("অনুরোধ পাঠান")
            }
        }
    }
}
