package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.BannerEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.ui.components.NivoraArtwork
import com.example.ui.components.OrderVerticalTimeline
import com.example.ui.theme.NivoraErrorRed
import com.example.ui.theme.NivoraSuccessGreen
import com.example.ui.viewmodel.CartItem
import com.example.ui.viewmodel.NivoraUiState
import com.example.ui.viewmodel.OrderStatusStep
import com.example.ui.viewmodel.formatBdt
import com.example.ui.viewmodel.formatTimestamp

private enum class CustomerTab(
    val labelBn: String
) {
    HOME("হোম"),
    CART("কার্ট"),
    ORDERS("অর্ডার ট্র্যাক")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerStorefrontScreen(
    state: NivoraUiState,
    onAddToCart: (ProductEntity, String, String, Int) -> Unit,
    onUpdateCartQty: (String, Int) -> Unit,
    onPlaceOrder: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(CustomerTab.HOME) }
    var selectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedProductForDetail by remember { mutableStateOf<ProductEntity?>(null) }
    var showCheckoutSheet by remember { mutableStateOf(false) }

    if (selectedTab != CustomerTab.HOME) {
        BackHandler { selectedTab = CustomerTab.HOME }
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
                    selected = selectedTab == CustomerTab.HOME,
                    onClick = { selectedTab = CustomerTab.HOME },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == CustomerTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = CustomerTab.HOME.labelBn
                        )
                    },
                    label = { Text(CustomerTab.HOME.labelBn) },
                    modifier = Modifier.testTag("customer_tab_home")
                )
                NavigationBarItem(
                    selected = selectedTab == CustomerTab.CART,
                    onClick = { selectedTab = CustomerTab.CART },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (state.cartItemCount > 0) {
                                    Badge { Text("${state.cartItemCount}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == CustomerTab.CART) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                                contentDescription = CustomerTab.CART.labelBn
                            )
                        }
                    },
                    label = { Text(CustomerTab.CART.labelBn) },
                    modifier = Modifier.testTag("customer_tab_cart")
                )
                NavigationBarItem(
                    selected = selectedTab == CustomerTab.ORDERS,
                    onClick = { selectedTab = CustomerTab.ORDERS },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (state.orders.isNotEmpty()) {
                                    Badge { Text("${state.orders.size}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == CustomerTab.ORDERS) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                                contentDescription = CustomerTab.ORDERS.labelBn
                            )
                        }
                    },
                    label = { Text(CustomerTab.ORDERS.labelBn) },
                    modifier = Modifier.testTag("customer_tab_orders")
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
                CustomerTab.HOME -> {
                    val filteredProducts = remember(state.products, selectedCategoryId, searchQuery) {
                        state.products.filter { product ->
                            val matchesCat = selectedCategoryId == null || product.categoryId == selectedCategoryId
                            val matchesQuery = searchQuery.isBlank() ||
                                product.title.contains(searchQuery, ignoreCase = true) ||
                                product.description.contains(searchQuery, ignoreCase = true)
                            matchesCat && matchesQuery
                        }
                    }

                    CustomerHomeFeed(
                        banners = state.activeBanners,
                        categories = state.categories,
                        products = filteredProducts,
                        selectedCategoryId = selectedCategoryId,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onCategorySelect = { selectedCategoryId = it },
                        onBannerClick = { banner ->
                            if (banner.linkedCategoryId != 0L) {
                                selectedCategoryId = banner.linkedCategoryId
                            }
                        },
                        onProductClick = { selectedProductForDetail = it }
                    )
                }

                CustomerTab.CART -> {
                    CustomerCartScreen(
                        cartItems = state.cartItems,
                        subtotal = state.cartSubtotal,
                        deliveryFee = state.cartDeliveryFee,
                        total = state.cartTotal,
                        onUpdateQty = onUpdateCartQty,
                        onProceedCheckout = { showCheckoutSheet = true },
                        onBrowseShop = { selectedTab = CustomerTab.HOME }
                    )
                }

                CustomerTab.ORDERS -> {
                    CustomerOrdersScreen(
                        orders = state.orders,
                        onBrowseShop = { selectedTab = CustomerTab.HOME }
                    )
                }
            }
        }
    }

    selectedProductForDetail?.let { product ->
        val categoryName = state.categories.firstOrNull { it.id == product.categoryId }?.nameBn ?: "সংগ্রহ"
        ProductDetailBottomSheet(
            product = product,
            categoryNameBn = categoryName,
            onDismiss = { selectedProductForDetail = null },
            onAddToCart = { size, color, qty ->
                onAddToCart(product, size, color, qty)
                selectedProductForDetail = null
            }
        )
    }

    if (showCheckoutSheet) {
        CheckoutBottomSheet(
            totalAmount = state.cartTotal,
            onDismiss = { showCheckoutSheet = false },
            onConfirmOrder = { name, phone, address, method ->
                onPlaceOrder(name, phone, address, method)
                showCheckoutSheet = false
                selectedTab = CustomerTab.ORDERS
            }
        )
    }
}

@Composable
private fun CustomerHomeFeed(
    banners: List<BannerEntity>,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    selectedCategoryId: Long?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (Long?) -> Unit,
    onBannerClick: (BannerEntity) -> Unit,
    onProductClick: (ProductEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 165.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (banners.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PromotionalBannerCarousel(
                    banners = banners,
                    onBannerClick = onBannerClick
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("পছন্দের পোশাক, হিজাব বা স্কিনকেয়ার খুঁজুন...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "অনুসন্ধান"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Outlined.Close, contentDescription = "মুছুন")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_search_input")
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ক্যাটাগরি",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { onCategorySelect(null) },
                            label = { Text("সবগুলো") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("category_chip_all")
                        )
                    }
                    items(categories, key = { it.id }) { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = {
                                onCategorySelect(
                                    if (selectedCategoryId == category.id) null else category.id
                                )
                            },
                            label = { Text(category.nameBn) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("category_chip_${category.id}")
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "নতুন কালেকশন",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${products.size} টি পণ্য",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (products.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyCatalogState(
                    message = "এই ক্যাটাগরিতে কোনো পণ্য পাওয়া যায়নি।",
                    actionLabel = "সব পণ্য দেখুন",
                    onAction = {
                        onCategorySelect(null)
                        onSearchQueryChange("")
                    }
                )
            }
        } else {
            items(products, key = { it.id }) { product ->
                CustomerProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
private fun PromotionalBannerCarousel(
    banners: List<BannerEntity>,
    onBannerClick: (BannerEntity) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) { page ->
            val banner = banners[page]
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(banner) }
                    .testTag("promo_banner_${banner.id}")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NivoraArtwork(
                        imageKey = banner.imageKey,
                        contentDescription = banner.title,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xD9092B2B),
                                        Color(0x99092B2B),
                                        Color(0x26092B2B)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "NIVORA এক্সক্লুসিভ",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = banner.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = banner.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = banner.ctaText.ifBlank { "এখনই দেখুন" },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (banners.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { idx ->
                    val active = pagerState.currentPage == idx
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(if (active) 20.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (active) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerProductCard(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_card_${product.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
            ) {
                NivoraArtwork(
                    imageKey = product.imageKey,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize()
                )
                if (product.discountPercent > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${product.discountPercent}% ছাড়",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (product.inStock) NivoraSuccessGreen else NivoraErrorRed,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (product.inStock) "স্টকে আছে" else "স্টক শেষ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formatBdt(product.finalPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (product.discountPercent > 0) {
                        Text(
                            text = formatBdt(product.price),
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "সাইজ: ${product.sizesCsv}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ProductDetailBottomSheet(
    product: ProductEntity,
    categoryNameBn: String,
    onDismiss: () -> Unit,
    onAddToCart: (String, String, Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSize by remember(product.id) {
        mutableStateOf(product.sizesList.firstOrNull() ?: "Standard")
    }
    var selectedColor by remember(product.id) {
        mutableStateOf(product.colorsList.firstOrNull() ?: "অরিজিনাল")
    }
    var quantity by remember(product.id) { mutableIntStateOf(1) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                NivoraArtwork(
                    imageKey = product.imageKey,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = categoryNameBn,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatBdt(product.finalPrice),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (product.discountPercent > 0) {
                    Text(
                        text = formatBdt(product.price),
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "${product.discountPercent}% ছাড়",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (product.sizesList.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("সাইজ নির্বাচন করুন:", style = MaterialTheme.typography.titleSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.sizesList.forEach { size ->
                            FilterChip(
                                selected = selectedSize == size,
                                onClick = { selectedSize = size },
                                label = { Text(size) },
                                leadingIcon = if (selectedSize == size) {
                                    { Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }

            if (product.colorsList.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("রঙ নির্বাচন করুন:", style = MaterialTheme.typography.titleSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.colorsList.forEach { color ->
                            FilterChip(
                                selected = selectedColor == color,
                                onClick = { selectedColor = color },
                                label = { Text(color) },
                                leadingIcon = if (selectedColor == color) {
                                    { Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("পরিমাণ:", style = MaterialTheme.typography.titleSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "কমান")
                    }
                    Text(
                        text = "$quantity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Filled.Add, contentDescription = "বাড়ান")
                    }
                }
            }

            Button(
                onClick = { onAddToCart(selectedSize, selectedColor, quantity) },
                enabled = product.inStock,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("add_to_cart_button")
            ) {
                Icon(Icons.Filled.ShoppingBag, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (product.inStock) {
                        "কার্টে যোগ করুন • ${formatBdt(product.finalPrice * quantity)}"
                    } else {
                        "বর্তমানে স্টক শেষ"
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomerCartScreen(
    cartItems: List<CartItem>,
    subtotal: Double,
    deliveryFee: Double,
    total: Double,
    onUpdateQty: (String, Int) -> Unit,
    onProceedCheckout: () -> Unit,
    onBrowseShop: () -> Unit
) {
    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyCatalogState(
                message = "আপনার শপিং কার্ট বর্তমানে খালি।",
                actionLabel = "শপিং শুরু করুন",
                onAction = onBrowseShop
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "আপনার শপিং ব্যাগ (${cartItems.sumOf { it.quantity }} টি)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            items(cartItems, key = { it.uniqueKey }) { item ->
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
                            imageKey = item.product.imageKey,
                            contentDescription = item.product.title,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.product.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2
                            )
                            Text(
                                text = "সাইজ: ${item.selectedSize} • রঙ: ${item.selectedColor}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatBdt(item.lineTotal),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onUpdateQty(item.uniqueKey, -1) }) {
                                Icon(Icons.Filled.Remove, contentDescription = "কমান")
                            }
                            Text(
                                text = "${item.quantity}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { onUpdateQty(item.uniqueKey, 1) }) {
                                Icon(Icons.Filled.Add, contentDescription = "বাড়ান")
                            }
                        }
                    }
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("সাবটোটাল", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatBdt(subtotal), fontWeight = FontWeight.SemiBold)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ডেলিভারি চার্জ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatBdt(deliveryFee), fontWeight = FontWeight.SemiBold)
                }
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("সর্বমোট বিল", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        formatBdt(total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                    onClick = onProceedCheckout,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("checkout_button")
                ) {
                    Text("চেকআউট করুন")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutBottomSheet(
    totalAmount: Double,
    onDismiss: () -> Unit,
    onConfirmOrder: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("নুসরাত জাহান") }
    var phone by remember { mutableStateOf("01712-445566") }
    var address by remember { mutableStateOf("বাড়ি ১৪, রোড ৬, ধানমন্ডি, ঢাকা") }
    var paymentMethod by remember { mutableStateOf("ক্যাশ অন ডেলিভারি") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ডেলিভারি তথ্য দিন",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("গ্রাহকের নাম") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("মোবাইল নম্বর") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("সম্পূর্ণ ডেলিভারি ঠিকানা") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ক্যাশ অন ডেলিভারি", "বিকাশ", "নগদ").forEach { method ->
                    FilterChip(
                        selected = paymentMethod == method,
                        onClick = { paymentMethod = method },
                        label = { Text(method) }
                    )
                }
            }
            Button(
                onClick = { onConfirmOrder(name, phone, address, paymentMethod) },
                enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_order_button")
            ) {
                Text("অর্ডার নিশ্চিত করুন (${formatBdt(totalAmount)})")
            }
        }
    }
}

@Composable
private fun CustomerOrdersScreen(
    orders: List<OrderEntity>,
    onBrowseShop: () -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyCatalogState(
                message = "এখনো কোনো অর্ডার করা হয়নি।",
                actionLabel = "পণ্য দেখুন",
                onAction = onBrowseShop
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "অর্ডার ট্র্যাকিং ও ইতিহাস",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        items(orders, key = { it.id }) { order ->
            val step = OrderStatusStep.fromKey(order.status)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "অর্ডার #${order.orderCode}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatTimestamp(order.createdAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = step.labelBn,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${order.customerName} • ${order.paymentMethod}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatBdt(order.totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    OrderVerticalTimeline(currentStatusKey = order.status)
                }
            }
        }
    }
}

@Composable
private fun EmptyCatalogState(
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onAction) {
            Text(actionLabel)
        }
    }
}
