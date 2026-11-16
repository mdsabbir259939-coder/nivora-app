package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.AppConfigEntity
import com.example.data.local.BannerEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.ui.components.NivoraArtwork
import com.example.ui.components.NivoraImagePresets
import com.example.ui.theme.NivoraErrorRed
import com.example.ui.theme.NivoraPaletteKey
import com.example.ui.theme.NivoraSuccessGreen
import com.example.ui.viewmodel.NivoraUiState
import kotlinx.coroutines.launch

private enum class AdminTab(val label: String) {
    UI_THEME("UI & Banners"),
    CATALOG("Catalog"),
    USERS("Users & Payouts"),
    ORDERS("Orders")
}

@Composable
fun AdminDashboardScreen(
    state: NivoraUiState,
    onUpdateTheme: (String, Boolean) -> Unit,
    onUpdateNoticeBoard: (String, String, String, String, Boolean) -> Unit,
    onToggleMaintenance: (Boolean, String) -> Unit,
    onSaveBanner: (BannerEntity) -> Unit,
    onDeleteBanner: (BannerEntity) -> Unit,
    onSaveCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onSaveProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onToggleProductStock: (ProductEntity) -> Unit,
    onAdjustResellerBalance: (UserEntity, Double) -> Unit,
    onApproveWithdrawal: (Long) -> Unit,
    onRejectWithdrawal: (Long) -> Unit,
    onUpdateOrderStatus: (Long, String) -> Unit,
    onPersistPickedImage: suspend (Uri) -> String?,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(AdminTab.UI_THEME) }

    if (selectedTab != AdminTab.UI_THEME) {
        BackHandler { selectedTab = AdminTab.UI_THEME }
    }

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
                    selected = selectedTab == AdminTab.UI_THEME,
                    onClick = { selectedTab = AdminTab.UI_THEME },
                    icon = {
                        Icon(
                            if (selectedTab == AdminTab.UI_THEME) Icons.Filled.Palette else Icons.Outlined.Palette,
                            contentDescription = AdminTab.UI_THEME.label
                        )
                    },
                    label = { Text("UI & Theme") },
                    modifier = Modifier.testTag("admin_tab_ui")
                )
                NavigationBarItem(
                    selected = selectedTab == AdminTab.CATALOG,
                    onClick = { selectedTab = AdminTab.CATALOG },
                    icon = {
                        Icon(
                            if (selectedTab == AdminTab.CATALOG) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                            contentDescription = AdminTab.CATALOG.label
                        )
                    },
                    label = { Text("Catalog") },
                    modifier = Modifier.testTag("admin_tab_catalog")
                )
                NavigationBarItem(
                    selected = selectedTab == AdminTab.USERS,
                    onClick = { selectedTab = AdminTab.USERS },
                    icon = {
                        Icon(
                            if (selectedTab == AdminTab.USERS) Icons.Filled.Group else Icons.Outlined.Group,
                            contentDescription = AdminTab.USERS.label
                        )
                    },
                    label = { Text("Users") },
                    modifier = Modifier.testTag("admin_tab_users")
                )
                NavigationBarItem(
                    selected = selectedTab == AdminTab.ORDERS,
                    onClick = { selectedTab = AdminTab.ORDERS },
                    icon = {
                        Icon(
                            if (selectedTab == AdminTab.ORDERS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = AdminTab.ORDERS.label
                        )
                    },
                    label = { Text("Orders") },
                    modifier = Modifier.testTag("admin_tab_orders")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AdminTab.UI_THEME -> AdminUiAndBannerTab(
                    config = state.appConfig,
                    banners = state.allBanners,
                    categories = state.categories,
                    onUpdateTheme = onUpdateTheme,
                    onUpdateNoticeBoard = onUpdateNoticeBoard,
                    onToggleMaintenance = onToggleMaintenance,
                    onSaveBanner = onSaveBanner,
                    onDeleteBanner = onDeleteBanner,
                    onPersistPickedImage = onPersistPickedImage
                )

                AdminTab.CATALOG -> AdminCatalogTab(
                    categories = state.categories,
                    products = state.products,
                    onSaveCategory = onSaveCategory,
                    onDeleteCategory = onDeleteCategory,
                    onSaveProduct = onSaveProduct,
                    onDeleteProduct = onDeleteProduct,
                    onToggleProductStock = onToggleProductStock,
                    onPersistPickedImage = onPersistPickedImage
                )

                AdminTab.USERS -> AdminUsersAndPayoutsTab(
                    state = state,
                    onAdjustResellerBalance = onAdjustResellerBalance,
                    onApproveWithdrawal = onApproveWithdrawal,
                    onRejectWithdrawal = onRejectWithdrawal
                )

                AdminTab.ORDERS -> AdminOrdersTab(
                    orders = state.orders,
                    onUpdateOrderStatus = onUpdateOrderStatus
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminUiAndBannerTab(
    config: AppConfigEntity,
    banners: List<BannerEntity>,
    categories: List<CategoryEntity>,
    onUpdateTheme: (String, Boolean) -> Unit,
    onUpdateNoticeBoard: (String, String, String, String, Boolean) -> Unit,
    onToggleMaintenance: (Boolean, String) -> Unit,
    onSaveBanner: (BannerEntity) -> Unit,
    onDeleteBanner: (BannerEntity) -> Unit,
    onPersistPickedImage: suspend (Uri) -> String?
) {
    var editingBanner by remember { mutableStateOf<BannerEntity?>(null) }
    var showNewBannerSheet by remember { mutableStateOf(false) }

    var maintenanceMessage by remember(config.maintenanceMessage) {
        mutableStateOf(config.maintenanceMessage)
    }
    var noticeTitle by remember(config.noticeTitle) { mutableStateOf(config.noticeTitle) }
    var noticeMessage by remember(config.noticeMessage) { mutableStateOf(config.noticeMessage) }
    var appVersion by remember(config.appVersion) { mutableStateOf(config.appVersion) }
    var noticeMode by remember(config.noticeDisplayMode) { mutableStateOf(config.noticeDisplayMode) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Maintenance Mode Switch
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (config.isMaintenanceMode) {
                        NivoraErrorRed.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (config.isMaintenanceMode) NivoraErrorRed.copy(alpha = 0.45f)
                    else MaterialTheme.colorScheme.outlineVariant
                )
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BuildCircle,
                                contentDescription = null,
                                tint = if (config.isMaintenanceMode) NivoraErrorRed else MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "System Maintenance Mode",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (config.isMaintenanceMode) "ON — Customers & Resellers blocked"
                                    else "OFF — Storefront accessible",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (config.isMaintenanceMode) NivoraErrorRed else NivoraSuccessGreen
                                )
                            }
                        }
                        Switch(
                            checked = config.isMaintenanceMode,
                            onCheckedChange = { enabled ->
                                onToggleMaintenance(enabled, maintenanceMessage)
                            },
                            modifier = Modifier.testTag("maintenance_mode_switch")
                        )
                    }
                    OutlinedTextField(
                        value = maintenanceMessage,
                        onValueChange = { maintenanceMessage = it },
                        label = { Text("Maintenance screen message") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (maintenanceMessage != config.maintenanceMessage) {
                        OutlinedButton(
                            onClick = { onToggleMaintenance(config.isMaintenanceMode, maintenanceMessage) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save Message")
                        }
                    }
                }
            }
        }

        // 2. Theme & Color Palette Customization
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "App Theme & Color Palette",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Changes entire Customer & Reseller UI instantly",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Dark", style = MaterialTheme.typography.labelMedium)
                            Switch(
                                checked = config.isDarkMode,
                                onCheckedChange = { dark ->
                                    onUpdateTheme(config.themePaletteKey, dark)
                                },
                                modifier = Modifier.testTag("admin_dark_mode_switch")
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NivoraPaletteKey.entries.forEach { palette ->
                            val isSelected = config.themePaletteKey == palette.key
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onUpdateTheme(palette.key, config.isDarkMode) }
                                    .testTag("theme_palette_${palette.key}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(palette.swatchPrimary)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(palette.swatchBackground)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(palette.swatchSecondary)
                                        )
                                    }
                                    Text(
                                        text = palette.labelEn,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (isSelected) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Notice Board / App Update System
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Notice Board & App Version",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = noticeTitle,
                            onValueChange = { noticeTitle = it },
                            label = { Text("Announcement Title") },
                            singleLine = true,
                            modifier = Modifier.weight(2f)
                        )
                        OutlinedTextField(
                            value = appVersion,
                            onValueChange = { appVersion = it },
                            label = { Text("Version") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = noticeMessage,
                        onValueChange = { noticeMessage = it },
                        label = { Text("Announcement Message") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "POPUP" to "Popup Alert",
                            "BANNER" to "Top Banner",
                            "NONE" to "Hidden"
                        ).forEach { (modeKey, label) ->
                            FilterChip(
                                selected = noticeMode == modeKey,
                                onClick = { noticeMode = modeKey },
                                label = { Text(label) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onUpdateNoticeBoard(noticeTitle, noticeMessage, appVersion, noticeMode, false)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                        Button(
                            onClick = {
                                onUpdateNoticeBoard(noticeTitle, noticeMessage, appVersion, noticeMode, true)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("broadcast_notice_button")
                        ) {
                            Text("Broadcast Now")
                        }
                    }
                }
            }
        }

        // 4. Homepage Promotional Banners
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Promotional Banners (${banners.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Add, edit, toggle, or upload custom photos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { showNewBannerSheet = true },
                    modifier = Modifier.testTag("admin_add_banner_button")
                ) {
                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Banner")
                }
            }
        }

        items(banners, key = { it.id }) { banner ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NivoraArtwork(
                        imageKey = banner.imageKey,
                        contentDescription = banner.title,
                        modifier = Modifier
                            .size(width = 104.dp, height = 66.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = banner.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = banner.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = banner.isActive,
                            onCheckedChange = { active ->
                                onSaveBanner(banner.copy(isActive = active))
                            }
                        )
                        Row {
                            IconButton(onClick = { editingBanner = banner }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit banner")
                            }
                            IconButton(onClick = { onDeleteBanner(banner) }) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "Delete banner",
                                    tint = NivoraErrorRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewBannerSheet || editingBanner != null) {
        BannerEditorBottomSheet(
            initial = editingBanner,
            categories = categories,
            onDismiss = {
                showNewBannerSheet = false
                editingBanner = null
            },
            onSave = { banner ->
                onSaveBanner(banner)
                showNewBannerSheet = false
                editingBanner = null
            },
            onPersistPickedImage = onPersistPickedImage
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BannerEditorBottomSheet(
    initial: BannerEntity?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (BannerEntity) -> Unit,
    onPersistPickedImage: suspend (Uri) -> String?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var subtitle by remember { mutableStateOf(initial?.subtitle ?: "") }
    var ctaText by remember { mutableStateOf(initial?.ctaText ?: "কালেকশন দেখুন") }
    var imageKey by remember { mutableStateOf(initial?.imageKey ?: "preset:eid") }
    var linkedCatId by remember { mutableStateOf(initial?.linkedCategoryId ?: 0L) }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val saved = onPersistPickedImage(uri)
                if (saved != null) imageKey = saved
            }
        }
    }

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
                text = if (initial == null) "New Promotional Banner" else "Edit Banner",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                NivoraArtwork(
                    imageKey = imageKey,
                    contentDescription = "Banner preview",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Upload Photo")
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NivoraImagePresets.take(3)) { preset ->
                    FilterChip(
                        selected = imageKey == preset.key,
                        onClick = { imageKey = preset.key },
                        label = { Text(preset.label) }
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Banner Headline (Bangla/English)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Subtitle") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ctaText,
                onValueChange = { ctaText = it },
                label = { Text("Button Text (e.g. কালেকশন দেখুন)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = linkedCatId == 0L,
                        onClick = { linkedCatId = 0L },
                        label = { Text("All Categories") }
                    )
                }
                items(categories, key = { it.id }) { cat ->
                    FilterChip(
                        selected = linkedCatId == cat.id,
                        onClick = { linkedCatId = cat.id },
                        label = { Text(cat.nameBn) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Banner Active on Storefront")
                Switch(checked = isActive, onCheckedChange = { isActive = it })
            }

            Button(
                onClick = {
                    onSave(
                        BannerEntity(
                            id = initial?.id ?: 0L,
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            ctaText = ctaText.trim().ifBlank { "দেখুন" },
                            imageKey = imageKey,
                            linkedCategoryId = linkedCatId,
                            isActive = isActive,
                            sortOrder = initial?.sortOrder ?: 0
                        )
                    )
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Banner")
            }
        }
    }
}
