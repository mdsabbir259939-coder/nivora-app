package com.example.ui.viewmodel

import com.example.data.local.AppConfigEntity
import com.example.data.local.BannerEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.data.local.WithdrawalRequestEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NivoraRole(
    val labelEn: String,
    val labelBn: String
) {
    CUSTOMER(labelEn = "Customer", labelBn = "গ্রাহক"),
    RESELLER(labelEn = "Reseller", labelBn = "রিসেলার"),
    ADMIN(labelEn = "Admin", labelBn = "অ্যাডমিন")
}

enum class OrderStatusStep(
    val key: String,
    val labelEn: String,
    val labelBn: String,
    val descriptionBn: String
) {
    ORDERED("ORDERED", "Ordered", "অর্ডার সম্পন্ন", "আপনার অর্ডারটি সফলভাবে গৃহীত হয়েছে"),
    CONFIRMED("CONFIRMED", "Confirmed", "নিশ্চিত হয়েছে", "অ্যাডমিন আপনার অর্ডারটি যাচাই ও নিশ্চিত করেছেন"),
    SHIPPED("SHIPPED", "Shipped", "ডেলিভারির পথে", "কুরিয়ার পার্টনারের কাছে পার্সেল হস্তান্তর করা হয়েছে"),
    DELIVERED("DELIVERED", "Delivered", "ডেলিভারি সম্পন্ন", "পণ্যটি সফলভাবে গ্রাহকের হাতে পৌঁছেছে");

    companion object {
        fun fromKey(key: String): OrderStatusStep =
            entries.firstOrNull { it.key == key } ?: ORDERED

        fun indexOfKey(key: String): Int =
            entries.indexOfFirst { it.key == key }.coerceAtLeast(0)
    }
}

data class CartItem(
    val product: ProductEntity,
    val selectedSize: String,
    val selectedColor: String,
    val quantity: Int = 1
) {
    val lineTotal: Double get() = product.finalPrice * quantity
    val uniqueKey: String get() = "${product.id}_${selectedSize}_${selectedColor}"
}

data class NivoraUiState(
    val isLoading: Boolean = true,
    val activeRole: NivoraRole = NivoraRole.CUSTOMER,
    val activeResellerId: Long? = null,
    val appConfig: AppConfigEntity = AppConfigEntity(),
    val allBanners: List<BannerEntity> = emptyList(),
    val activeBanners: List<BannerEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(),
    val customers: List<UserEntity> = emptyList(),
    val resellers: List<UserEntity> = emptyList(),
    val orders: List<OrderEntity> = emptyList(),
    val withdrawals: List<WithdrawalRequestEntity> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val dismissedNoticeRevision: Int = 0,
    val isBannerNoticeDismissedForRevision: Int = 0,
    val snackbarMessage: String? = null
) {
    val activeReseller: UserEntity?
        get() = resellers.firstOrNull { it.id == activeResellerId } ?: resellers.firstOrNull()

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val cartSubtotal: Double
        get() = cartItems.sumOf { it.lineTotal }

    val cartDeliveryFee: Double
        get() = if (cartItems.isEmpty()) 0.0 else 60.0

    val cartTotal: Double
        get() = cartSubtotal + cartDeliveryFee

    val shouldShowPopupNotice: Boolean
        get() = activeRole != NivoraRole.ADMIN &&
            !appConfig.isMaintenanceMode &&
            appConfig.noticeDisplayMode == "POPUP" &&
            appConfig.noticeRevision > dismissedNoticeRevision &&
            appConfig.noticeMessage.isNotBlank()

    val shouldShowBannerNotice: Boolean
        get() = activeRole != NivoraRole.ADMIN &&
            !appConfig.isMaintenanceMode &&
            appConfig.noticeDisplayMode == "BANNER" &&
            appConfig.noticeRevision > isBannerNoticeDismissedForRevision &&
            appConfig.noticeMessage.isNotBlank()

    val totalPaidCommissions: Double
        get() = resellers.sumOf { it.totalPaidCommission }

    val totalUnpaidCommissions: Double
        get() = resellers.sumOf { it.commissionBalance } +
            withdrawals.filter { it.status == "PENDING" }.sumOf { it.amount }
}

fun formatBdt(amount: Double): String {
    val rounded = kotlin.math.round(amount).toLong()
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "৳${formatter.format(rounded)}"
}

fun formatTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}
