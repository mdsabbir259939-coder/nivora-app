package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.ui.components.NivoraArtwork
import com.example.ui.components.NivoraImagePresets
import com.example.ui.theme.NivoraErrorRed
import com.example.ui.theme.NivoraSuccessGreen
import com.example.ui.theme.NivoraWarningAmber
import com.example.ui.viewmodel.NivoraUiState
import com.example.ui.viewmodel.OrderStatusStep
import com.example.ui.viewmodel.formatBdt
import com.example.ui.viewmodel.formatTimestamp
import kotlinx.coroutines.launch

@Composable
fun AdminCatalogTab(
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    onSaveCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onSaveProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onToggleProductStock: (ProductEntity) -> Unit,
    onPersistPickedImage: suspend (Uri) -> String?
) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showProductSheet by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Categories (${categories.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create, rename, or remove categories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = {
                        editingCategory = null
                        showCategoryDialog = true
                    },
                    modifier = Modifier.testTag("admin_add_category_button")
                ) {
                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Category")
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(categories, key = { it.id }) { cat ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Column {
                                Text(
                                    text = cat.nameBn,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = cat.nameEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = {
                                    editingCategory = cat
                                    showCategoryDialog = true
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Rename", modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onDeleteCategory(cat) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = NivoraErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Products (${products.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage prices, discounts, stock & variations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = {
                        editingProduct = null
                        showProductSheet = true
                    },
                    modifier = Modifier.testTag("admin_add_product_button")
                ) {
                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Product")
                }
            }
        }

        items(products, key = { it.id }) { product ->
            val catName = categories.firstOrNull { it.id == product.categoryId }?.nameEn ?: "Uncategorized"
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NivoraArtwork(
                            imageKey = product.imageKey,
                            contentDescription = product.title,
                            modifier = Modifier
                                .size(78.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "$catName • ${formatBdt(product.price)} (${product.discountPercent}% off)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Sizes: ${product.sizesCsv} | Colors: ${product.colorsCsv}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Switch(
                                checked = product.inStock,
                                onCheckedChange = { onToggleProductStock(product) }
                            )
                            Text(
                                text = if (product.inStock) "In Stock" else "Out of Stock",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (product.inStock) NivoraSuccessGreen else NivoraErrorRed,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                editingProduct = product
                                showProductSheet = true
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit product")
                            }
                            IconButton(onClick = { onDeleteProduct(product) }) {
                                Icon(
                                    Icons.Filled.DeleteOutline,
                                    contentDescription = "Delete product",
                                    tint = NivoraErrorRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCategoryDialog) {
        CategoryEditorDialog(
            initial = editingCategory,
            onDismiss = { showCategoryDialog = false },
            onSave = { cat ->
                onSaveCategory(cat)
                showCategoryDialog = false
            }
        )
    }

    if (showProductSheet) {
        ProductEditorBottomSheet(
            initial = editingProduct,
            categories = categories,
            onDismiss = { showProductSheet = false },
            onSave = { prod ->
                onSaveProduct(prod)
                showProductSheet = false
            },
            onPersistPickedImage = onPersistPickedImage
        )
    }
}

@Composable
private fun CategoryEditorDialog(
    initial: CategoryEntity?,
    onDismiss: () -> Unit,
    onSave: (CategoryEntity) -> Unit
) {
    var nameBn by remember { mutableStateOf(initial?.nameBn ?: "") }
    var nameEn by remember { mutableStateOf(initial?.nameEn ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New Category" else "Rename Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nameBn,
                    onValueChange = { nameBn = it },
                    label = { Text("Bangla Label (e.g. শাড়ি)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text("English Label (e.g. Saree)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        CategoryEntity(
                            id = initial?.id ?: 0L,
                            nameBn = nameBn.trim().ifBlank { nameEn.trim() },
                            nameEn = nameEn.trim().ifBlank { nameBn.trim() },
                            iconKey = initial?.iconKey ?: "style",
                            sortOrder = initial?.sortOrder ?: 10
                        )
                    )
                },
                enabled = nameBn.isNotBlank() || nameEn.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductEditorBottomSheet(
    initial: ProductEntity?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit,
    onPersistPickedImage: suspend (Uri) -> String?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var categoryId by remember {
        mutableStateOf(initial?.categoryId ?: categories.firstOrNull()?.id ?: 0L)
    }
    var priceText by remember { mutableStateOf(initial?.price?.toInt()?.toString() ?: "1500") }
    var discountText by remember { mutableStateOf(initial?.discountPercent?.toString() ?: "0") }
    var commissionText by remember {
        mutableStateOf(initial?.resellerCommission?.toInt()?.toString() ?: "150")
    }
    var inStock by remember { mutableStateOf(initial?.inStock ?: true) }
    var sizesCsv by remember { mutableStateOf(initial?.sizesCsv ?: "S, M, L, XL") }
    var colorsCsv by remember { mutableStateOf(initial?.colorsCsv ?: "ডার্ক টিল, সফট বেইজ") }
    var imageKey by remember { mutableStateOf(initial?.imageKey ?: "product:dress") }

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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (initial == null) "Add New Product" else "Edit Product",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NivoraArtwork(
                    imageKey = imageKey,
                    contentDescription = "Product artwork",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                OutlinedButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Upload Product Photo")
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NivoraImagePresets) { preset ->
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
                label = { Text("Product Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.id }) { cat ->
                    FilterChip(
                        selected = categoryId == cat.id,
                        onClick = { categoryId = cat.id },
                        label = { Text("${cat.nameBn} (${cat.nameEn})") }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Price (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = discountText,
                    onValueChange = { discountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Discount %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = commissionText,
                    onValueChange = { commissionText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Commission ৳") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = sizesCsv,
                onValueChange = { sizesCsv = it },
                label = { Text("Sizes (comma separated: S, M, L, XL)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = colorsCsv,
                onValueChange = { colorsCsv = it },
                label = { Text("Colors (comma separated)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Stock Availability (In Stock / Out of Stock)")
                Switch(checked = inStock, onCheckedChange = { inStock = it })
            }

            Button(
                onClick = {
                    onSave(
                        ProductEntity(
                            id = initial?.id ?: 0L,
                            categoryId = categoryId,
                            title = title.trim(),
                            description = description.trim(),
                            price = priceText.toDoubleOrNull() ?: 0.0,
                            discountPercent = (discountText.toIntOrNull() ?: 0).coerceIn(0, 90),
                            resellerCommission = commissionText.toDoubleOrNull() ?: 0.0,
                            inStock = inStock,
                            sizesCsv = sizesCsv.trim(),
                            colorsCsv = colorsCsv.trim(),
                            imageKey = imageKey,
                            createdAt = initial?.createdAt ?: System.currentTimeMillis()
                        )
                    )
                },
                enabled = title.isNotBlank() && (priceText.toDoubleOrNull() ?: 0.0) > 0.0,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_product_button")
            ) {
                Text("Save Product")
            }
        }
    }
}

@Composable
fun AdminUsersAndPayoutsTab(
    state: NivoraUiState,
    onAdjustResellerBalance: (UserEntity, Double) -> Unit,
    onApproveWithdrawal: (Long) -> Unit,
    onRejectWithdrawal: (Long) -> Unit
) {
    var selectedSegment by remember { mutableIntStateOf(0) }
    var editingReseller by remember { mutableStateOf<UserEntity?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Paid Commissions",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = formatBdt(state.totalPaidCommissions),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Unpaid Commissions",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = formatBdt(state.totalUnpaidCommissions),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        item {
            val segments = listOf(
                "Resellers (${state.resellers.size})",
                "Payouts (${state.withdrawals.count { it.status == "PENDING" }})",
                "Customers (${state.customers.size})"
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                segments.forEachIndexed { idx, label ->
                    SegmentedButton(
                        selected = selectedSegment == idx,
                        onClick = { selectedSegment = idx },
                        shape = SegmentedButtonDefaults.itemShape(index = idx, count = segments.size)
                    ) {
                        Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        when (selectedSegment) {
            0 -> {
                items(state.resellers, key = { it.id }) { reseller ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
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
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = reseller.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${reseller.phone} • ${reseller.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Balance: ${formatBdt(reseller.commissionBalance)} | Paid: ${formatBdt(reseller.totalPaidCommission)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            OutlinedButton(
                                onClick = { editingReseller = reseller },
                                modifier = Modifier.testTag("adjust_balance_${reseller.id}")
                            ) {
                                Text("Adjust")
                            }
                        }
                    }
                }
            }

            1 -> {
                items(state.withdrawals, key = { it.id }) { req ->
                    val statusColor = when (req.status) {
                        "APPROVED" -> NivoraSuccessGreen
                        "REJECTED" -> NivoraErrorRed
                        else -> NivoraWarningAmber
                    }
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${req.resellerName} — ${formatBdt(req.amount)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${req.method}: ${req.accountNumber} • ${formatTimestamp(req.createdAt)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = statusColor.copy(alpha = 0.14f)
                                ) {
                                    Text(
                                        text = req.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            if (req.status == "PENDING") {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = { onRejectWithdrawal(req.id) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NivoraErrorRed),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Filled.Close, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Reject")
                                    }
                                    Button(
                                        onClick = { onApproveWithdrawal(req.id) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("approve_withdrawal_${req.id}")
                                    ) {
                                        Icon(Icons.Filled.Check, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Approve")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                items(state.customers, key = { it.id }) { customer ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
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
                                    text = customer.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${customer.phone} • ${customer.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${customer.totalOrdersCount} Orders",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editingReseller?.let { reseller ->
        var balanceText by remember { mutableStateOf("${reseller.commissionBalance.toInt()}") }
        AlertDialog(
            onDismissRequest = { editingReseller = null },
            title = { Text("Update Commission Balance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${reseller.name} (Current: ${formatBdt(reseller.commissionBalance)})")
                    OutlinedTextField(
                        value = balanceText,
                        onValueChange = { balanceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("New Commission Balance (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = balanceText.toDoubleOrNull() ?: reseller.commissionBalance
                        onAdjustResellerBalance(reseller, updated)
                        editingReseller = null
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingReseller = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<OrderEntity>,
    onUpdateOrderStatus: (Long, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Order Management (${orders.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Status updates reflect instantly on Customer & Reseller timelines",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(orders, key = { it.id }) { order ->
            var dropdownExpanded by remember { mutableStateOf(false) }
            val currentStep = OrderStatusStep.fromKey(order.status)

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.orderCode} • ${formatBdt(order.totalAmount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${order.customerName} (${order.customerPhone})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { dropdownExpanded = true }
                                    .testTag("order_status_dropdown_${order.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = currentStep.labelEn,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Icon(
                                        Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Change status",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                OrderStatusStep.entries.forEach { step ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${step.labelEn} (${step.labelBn})",
                                                fontWeight = if (step.key == order.status) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        trailingIcon = {
                                            if (step.key == order.status) {
                                                Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.primary)
                                            }
                                        },
                                        onClick = {
                                            dropdownExpanded = false
                                            onUpdateOrderStatus(order.id, step.key)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Address: ${order.deliveryAddress}" +
                            if (!order.resellerName.isNullOrBlank()) " • Reseller: ${order.resellerName}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
