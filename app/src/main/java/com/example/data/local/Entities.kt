package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val themePaletteKey: String = "teal_beige",
    val isDarkMode: Boolean = false,
    val noticeTitle: String = "ঈদ ও সিজনাল কালেকশন আপডেট!",
    val noticeMessage: String = "নতুন রয়্যাল সিল্ক ও জর্জেট হিজাব কালেকশনে পাচ্ছেন ২০% পর্যন্ত বিশেষ ছাড় এবং সারাদেশে দ্রুত হোম ডেলিভারি।",
    val appVersion: String = "v2.4.0",
    val noticeDisplayMode: String = "BANNER", // "POPUP", "BANNER", "NONE"
    val noticeRevision: Int = 1,
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "অ্যাপ আপডেট হচ্ছে, অনুগ্রহ করে কিছুক্ষণ পর আবার চেষ্টা করুন। আমরা আরও সুন্দর অভিজ্ঞতা নিয়ে ফিরে আসছি।"
)

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val imageKey: String,
    val linkedCategoryId: Long = 0L,
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String,
    val iconKey: String = "style",
    val sortOrder: Int = 0
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercent: Int = 0,
    val resellerCommission: Double = 0.0,
    val inStock: Boolean = true,
    val sizesCsv: String = "M, L, XL",
    val colorsCsv: String = "ডার্ক টিল, সফট বেইজ",
    val imageKey: String = "product:dress",
    val createdAt: Long = System.currentTimeMillis()
) {
    val finalPrice: Double
        get() = if (discountPercent > 0) {
            (price * (100 - discountPercent) / 100.0)
        } else {
            price
        }

    val sizesList: List<String>
        get() = sizesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val colorsList: List<String>
        get() = colorsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val city: String,
    val role: String, // "CUSTOMER" or "RESELLER"
    val commissionBalance: Double = 0.0,
    val totalPaidCommission: Double = 0.0,
    val totalOrdersCount: Int = 0,
    val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderCode: String,
    val customerId: Long? = null,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val resellerId: Long? = null,
    val resellerName: String? = null,
    val itemsSummary: String,
    val subtotal: Double,
    val deliveryFee: Double = 60.0,
    val totalAmount: Double,
    val resellerCommissionEarned: Double = 0.0,
    val status: String = "ORDERED", // "ORDERED", "CONFIRMED", "SHIPPED", "DELIVERED"
    val paymentMethod: String = "ক্যাশ অন ডেলিভারি",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val resellerId: Long,
    val resellerName: String,
    val amount: Double,
    val method: String, // "bKash", "Nagad", "Rocket"
    val accountNumber: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val createdAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)
