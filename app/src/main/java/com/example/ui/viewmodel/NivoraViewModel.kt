package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BannerEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.NivoraDatabase
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.data.repository.NivoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class NivoraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NivoraRepository

    init {
        val db = NivoraDatabase.getInstance(application)
        repository = NivoraRepository(db.nivoraDao(), application.applicationContext)
        viewModelScope.launch {
            repository.ensureSeeded()
            _isSeeded.value = true
        }
    }

    private data class LocalSessionState(
        val isSeeded: Boolean = false,
        val activeRole: NivoraRole = NivoraRole.CUSTOMER,
        val activeResellerId: Long? = null,
        val cartItems: List<CartItem> = emptyList(),
        val dismissedNoticeRevision: Int = 0,
        val dismissedBannerRevision: Int = 0,
        val snackbarMessage: String? = null
    )

    private val _isSeeded = MutableStateFlow(false)
    private val _activeRole = MutableStateFlow(NivoraRole.CUSTOMER)
    private val _activeResellerId = MutableStateFlow<Long?>(null)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private val _dismissedNoticeRevision = MutableStateFlow(0)
    private val _dismissedBannerRevision = MutableStateFlow(0)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    private val sessionFlow = combine(
        _isSeeded,
        _activeRole,
        _activeResellerId,
        _cartItems,
        _dismissedNoticeRevision
    ) { seeded, role, resellerId, cart, dismissedPopup ->
        LocalSessionState(
            isSeeded = seeded,
            activeRole = role,
            activeResellerId = resellerId,
            cartItems = cart,
            dismissedNoticeRevision = dismissedPopup
        )
    }.combine(_dismissedBannerRevision) { base, dismissedBanner ->
        base.copy(dismissedBannerRevision = dismissedBanner)
    }.combine(_snackbarMessage) { base, snack ->
        base.copy(snackbarMessage = snack)
    }

    private val catalogFlow = combine(
        repository.appConfig,
        repository.allBanners,
        repository.activeBanners,
        repository.categories,
        repository.products
    ) { config, allBanners, activeBanners, categories, products ->
        CatalogBundle(config, allBanners, activeBanners, categories, products)
    }

    private val operationsFlow = combine(
        repository.customers,
        repository.resellers,
        repository.orders,
        repository.withdrawals
    ) { customers, resellers, orders, withdrawals ->
        OperationsBundle(customers, resellers, orders, withdrawals)
    }

    val uiState: StateFlow<NivoraUiState> = combine(
        sessionFlow,
        catalogFlow,
        operationsFlow
    ) { session, catalog, ops ->
        NivoraUiState(
            isLoading = !session.isSeeded,
            activeRole = session.activeRole,
            activeResellerId = session.activeResellerId,
            appConfig = catalog.config,
            allBanners = catalog.allBanners,
            activeBanners = catalog.activeBanners,
            categories = catalog.categories,
            products = catalog.products,
            customers = ops.customers,
            resellers = ops.resellers,
            orders = ops.orders,
            withdrawals = ops.withdrawals,
            cartItems = session.cartItems,
            dismissedNoticeRevision = session.dismissedNoticeRevision,
            isBannerNoticeDismissedForRevision = session.dismissedBannerRevision,
            snackbarMessage = session.snackbarMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = NivoraUiState()
    )

    private data class CatalogBundle(
        val config: com.example.data.local.AppConfigEntity,
        val allBanners: List<BannerEntity>,
        val activeBanners: List<BannerEntity>,
        val categories: List<CategoryEntity>,
        val products: List<ProductEntity>
    )

    private data class OperationsBundle(
        val customers: List<UserEntity>,
        val resellers: List<UserEntity>,
        val orders: List<OrderEntity>,
        val withdrawals: List<com.example.data.local.WithdrawalRequestEntity>
    )

    fun switchRole(role: NivoraRole) {
        _activeRole.value = role
    }

    fun selectActiveReseller(resellerId: Long) {
        _activeResellerId.value = resellerId
    }

    fun consumeSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(message: String) {
        _snackbarMessage.value = message
    }

    fun dismissPopupNotice() {
        _dismissedNoticeRevision.value = uiState.value.appConfig.noticeRevision
    }

    fun dismissBannerNotice() {
        _dismissedBannerRevision.value = uiState.value.appConfig.noticeRevision
    }

    // --- Cart Actions ---
    fun addToCart(product: ProductEntity, size: String, color: String, quantity: Int = 1) {
        if (!product.inStock) {
            showMessage("দুঃখিত, এই পণ্যটি বর্তমানে স্টকে নেই")
            return
        }
        val safeSize = size.ifBlank { product.sizesList.firstOrNull() ?: "Standard" }
        val safeColor = color.ifBlank { product.colorsList.firstOrNull() ?: "Default" }
        _cartItems.update { current ->
            val key = "${product.id}_${safeSize}_${safeColor}"
            val existing = current.firstOrNull { it.uniqueKey == key }
            if (existing != null) {
                current.map {
                    if (it.uniqueKey == key) it.copy(quantity = it.quantity + quantity) else it
                }
            } else {
                current + CartItem(product, safeSize, safeColor, quantity)
            }
        }
        showMessage("পণ্যটি আপনার কার্টে যোগ করা হয়েছে")
    }

    fun updateCartQuantity(uniqueKey: String, delta: Int) {
        _cartItems.update { current ->
            current.mapNotNull { item ->
                if (item.uniqueKey == uniqueKey) {
                    val nextQty = item.quantity + delta
                    if (nextQty <= 0) null else item.copy(quantity = nextQty)
                } else {
                    item
                }
            }
        }
    }

    fun placeCustomerOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        paymentMethod: String
    ) {
        val items = _cartItems.value
        if (items.isEmpty()) return
        viewModelScope.launch {
            val summary = items.joinToString(", ") {
                "${it.product.title} (${it.selectedSize}, ${it.selectedColor}) × ${it.quantity}"
            }
            val subtotal = items.sumOf { it.lineTotal }
            val deliveryFee = 60.0
            val code = "NV-${Random.nextInt(3000, 9999)}"
            val matchedCustomer = uiState.value.customers.firstOrNull()
            repository.placeOrder(
                OrderEntity(
                    orderCode = code,
                    customerId = matchedCustomer?.id,
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    deliveryAddress = deliveryAddress.trim(),
                    resellerId = null,
                    resellerName = null,
                    itemsSummary = summary,
                    subtotal = subtotal,
                    deliveryFee = deliveryFee,
                    totalAmount = subtotal + deliveryFee,
                    resellerCommissionEarned = 0.0,
                    status = OrderStatusStep.ORDERED.key,
                    paymentMethod = paymentMethod
                )
            )
            _cartItems.value = emptyList()
            showMessage("অর্ডার #$code সফলভাবে সম্পন্ন হয়েছে!")
        }
    }

    // --- Reseller Actions ---
    fun placeResellerOrder(
        product: ProductEntity,
        size: String,
        color: String,
        quantity: Int,
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        customSellingPrice: Double,
        paymentMethod: String
    ) {
        val reseller = uiState.value.activeReseller ?: return
        viewModelScope.launch {
            val baseTotal = product.finalPrice * quantity
            val sellingTotal = (customSellingPrice.coerceAtLeast(product.finalPrice)) * quantity
            val markupProfit = (sellingTotal - baseTotal).coerceAtLeast(0.0)
            val totalCommission = (product.resellerCommission * quantity) + markupProfit
            val deliveryFee = 80.0
            val code = "NV-${Random.nextInt(3000, 9999)}"
            repository.placeOrder(
                OrderEntity(
                    orderCode = code,
                    customerId = null,
                    customerName = customerName.trim(),
                    customerPhone = customerPhone.trim(),
                    deliveryAddress = deliveryAddress.trim(),
                    resellerId = reseller.id,
                    resellerName = reseller.name,
                    itemsSummary = "${product.title} ($size, $color) × $quantity",
                    subtotal = sellingTotal,
                    deliveryFee = deliveryFee,
                    totalAmount = sellingTotal + deliveryFee,
                    resellerCommissionEarned = totalCommission,
                    status = OrderStatusStep.ORDERED.key,
                    paymentMethod = paymentMethod
                )
            )
            showMessage("রিসেল অর্ডার #$code সফল! কমিশন যোগ হয়েছে ${formatBdt(totalCommission)}")
        }
    }

    fun submitWithdrawalRequest(amount: Double, method: String, accountNumber: String) {
        val reseller = uiState.value.activeReseller ?: return
        viewModelScope.launch {
            val ok = repository.submitWithdrawal(
                resellerId = reseller.id,
                amount = amount,
                method = method,
                accountNumber = accountNumber.trim()
            )
            if (ok) {
                showMessage("${formatBdt(amount)} উত্তোলনের অনুরোধ পাঠানো হয়েছে")
            } else {
                showMessage("পর্যাপ্ত কমিশন ব্যালেন্স নেই বা সঠিক পরিমাণ দিন")
            }
        }
    }

    // --- Admin Control Panel Actions ---
    fun updateTheme(paletteKey: String, isDarkMode: Boolean) {
        viewModelScope.launch {
            val current = uiState.value.appConfig
            repository.saveAppConfig(
                current.copy(
                    themePaletteKey = paletteKey,
                    isDarkMode = isDarkMode
                )
            )
            showMessage("Theme updated in real-time")
        }
    }

    fun updateNoticeBoard(
        title: String,
        message: String,
        version: String,
        displayMode: String,
        broadcastNow: Boolean
    ) {
        viewModelScope.launch {
            val current = uiState.value.appConfig
            val nextRevision = if (broadcastNow) current.noticeRevision + 1 else current.noticeRevision
            repository.saveAppConfig(
                current.copy(
                    noticeTitle = title.trim(),
                    noticeMessage = message.trim(),
                    appVersion = version.trim().ifBlank { "v2.4.0" },
                    noticeDisplayMode = displayMode,
                    noticeRevision = nextRevision
                )
            )
            showMessage(
                if (broadcastNow) "Notice broadcasted to all users!" else "Notice settings saved"
            )
        }
    }

    fun toggleMaintenanceMode(enabled: Boolean, message: String) {
        viewModelScope.launch {
            val current = uiState.value.appConfig
            repository.saveAppConfig(
                current.copy(
                    isMaintenanceMode = enabled,
                    maintenanceMessage = message.trim().ifBlank { current.maintenanceMessage }
                )
            )
            showMessage(
                if (enabled) "Maintenance Mode ON — Public access blocked"
                else "Maintenance Mode OFF — Storefront live"
            )
        }
    }

    fun saveBanner(banner: BannerEntity) {
        viewModelScope.launch {
            repository.saveBanner(banner)
            showMessage("Banner saved")
        }
    }

    fun deleteBanner(banner: BannerEntity) {
        viewModelScope.launch {
            repository.deleteBanner(banner)
            showMessage("Banner removed")
        }
    }

    fun saveCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.saveCategory(category)
            showMessage("Category saved")
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            showMessage("Category deleted")
        }
    }

    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product)
            showMessage("Product saved to live catalog")
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            showMessage("Product deleted")
        }
    }

    fun toggleProductStock(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product.copy(inStock = !product.inStock))
        }
    }

    fun updateResellerBalance(reseller: UserEntity, newBalance: Double) {
        viewModelScope.launch {
            repository.updateUser(reseller.copy(commissionBalance = newBalance.coerceAtLeast(0.0)))
            showMessage("${reseller.name} balance updated to ${formatBdt(newBalance)}")
        }
    }

    fun approveWithdrawal(requestId: Long) {
        viewModelScope.launch {
            repository.approveWithdrawal(requestId)
            showMessage("Withdrawal approved & paid commission updated")
        }
    }

    fun rejectWithdrawal(requestId: Long) {
        viewModelScope.launch {
            repository.rejectWithdrawal(requestId)
            showMessage("Withdrawal rejected & balance refunded")
        }
    }

    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            showMessage("Order status updated to $newStatus")
        }
    }

    suspend fun persistPickedImage(uri: Uri): String? = repository.persistPickedImage(uri)
}
