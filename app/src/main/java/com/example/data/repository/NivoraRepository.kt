package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppConfigEntity
import com.example.data.local.BannerEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.NivoraDao
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserEntity
import com.example.data.local.WithdrawalRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class NivoraRepository(
    private val dao: NivoraDao,
    private val appContext: Context
) {
    val appConfig: Flow<AppConfigEntity> = dao.observeAppConfig().map { it ?: AppConfigEntity() }
    val allBanners: Flow<List<BannerEntity>> = dao.observeAllBanners()
    val activeBanners: Flow<List<BannerEntity>> = dao.observeActiveBanners()
    val categories: Flow<List<CategoryEntity>> = dao.observeCategories()
    val products: Flow<List<ProductEntity>> = dao.observeProducts()
    val customers: Flow<List<UserEntity>> = dao.observeCustomers()
    val resellers: Flow<List<UserEntity>> = dao.observeResellers()
    val orders: Flow<List<OrderEntity>> = dao.observeOrders()
    val withdrawals: Flow<List<WithdrawalRequestEntity>> = dao.observeWithdrawals()

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        if (dao.getAppConfigOnce() == null) {
            dao.upsertAppConfig(AppConfigEntity())
        }
        if (dao.getCategoryCount() > 0) return@withContext

        val clothingId = dao.upsertCategory(
            CategoryEntity(nameBn = "পোশাক", nameEn = "Clothing", iconKey = "clothing", sortOrder = 1)
        )
        val hijabId = dao.upsertCategory(
            CategoryEntity(nameBn = "হিজাব", nameEn = "Hijab", iconKey = "hijab", sortOrder = 2)
        )
        val beautyId = dao.upsertCategory(
            CategoryEntity(nameBn = "বিউটি কেয়ার", nameEn = "Beauty Care", iconKey = "beauty", sortOrder = 3)
        )
        val accessoryId = dao.upsertCategory(
            CategoryEntity(nameBn = "এক্সেসরিজ", nameEn = "Accessories", iconKey = "accessory", sortOrder = 4)
        )

        dao.upsertBanner(
            BannerEntity(
                title = "রয়্যাল এডিট কালেকশন",
                subtitle = "প্রিমিয়াম সিল্ক ও লিনেন পোশাকে ২০% পর্যন্ত ছাড়",
                ctaText = "কালেকশন দেখুন",
                imageKey = "preset:eid",
                linkedCategoryId = clothingId,
                isActive = true,
                sortOrder = 1
            )
        )
        dao.upsertBanner(
            BannerEntity(
                title = "সিগনেচার শিফন হিজাব",
                subtitle = "শ্বাস-প্রশ্বাসযোগ্য মোলায়েম ফ্যাব্রিক — সারাদিন আরাম",
                ctaText = "হিজাব দেখুন",
                imageKey = "preset:hijab",
                linkedCategoryId = hijabId,
                isActive = true,
                sortOrder = 2
            )
        )
        dao.upsertBanner(
            BannerEntity(
                title = "বোটানিক্যাল গ্লো সিরাম",
                subtitle = "প্রাকৃতিক স্কিনকেয়ার ও অর্গানিক রূপচর্চা",
                ctaText = "স্কিনকেয়ার",
                imageKey = "preset:skincare",
                linkedCategoryId = beautyId,
                isActive = true,
                sortOrder = 3
            )
        )

        val now = System.currentTimeMillis()
        dao.upsertProduct(
            ProductEntity(
                categoryId = clothingId,
                title = "ডার্ক টিল সিল্ক কাফতান সেট",
                description = "অরিজিনাল প্রিমিয়াম সিল্ক ব্লেন্ড ফ্যাব্রিক ও গোল্ডেন জরি এমব্রয়ডারি। উৎসব ও দাওয়াতের জন্য দারুণ মানানসই।",
                price = 2850.0,
                discountPercent = 15,
                resellerCommission = 320.0,
                inStock = true,
                sizesCsv = "S, M, L, XL",
                colorsCsv = "ডার্ক টিল, অলিভ, বেইজ",
                imageKey = "preset:eid",
                createdAt = now - 1000L
            )
        )
        dao.upsertProduct(
            ProductEntity(
                categoryId = clothingId,
                title = "বেইজ লিনেন মডেস্ট আবায়া",
                description = "নরম সফট বেইজ কটন-লিনেন আবায়া, ম্যাচিং বেল্ট ও পকেট সহ।",
                price = 2200.0,
                discountPercent = 10,
                resellerCommission = 250.0,
                inStock = true,
                sizesCsv = "52, 54, 56",
                colorsCsv = "সফট বেইজ, আইভরি, মোকা",
                imageKey = "product:abaya",
                createdAt = now - 2000L
            )
        )
        dao.upsertProduct(
            ProductEntity(
                categoryId = hijabId,
                title = "লাক্সারি মালয়েশিয়ান শিফন হিজাব",
                description = "হালকা, নন-স্লিপ এবং চমৎকার ড্রেপ সহ এলিগ্যান্ট হিজাব।",
                price = 650.0,
                discountPercent = 20,
                resellerCommission = 95.0,
                inStock = true,
                sizesCsv = "Free Size",
                colorsCsv = "ডার্ক টিল, স্যান্ড বেইজ, কফি, ক্রিম",
                imageKey = "preset:hijab",
                createdAt = now - 3000L
            )
        )
        dao.upsertProduct(
            ProductEntity(
                categoryId = hijabId,
                title = "সাটিন সিল্ক পার্টি হিজাব",
                description = "সফট ম্যাট-সাটিন ফিনিশ যা যেকোনো উৎসবের সাজে আনবে রাজকীয় ভাব।",
                price = 780.0,
                discountPercent = 0,
                resellerCommission = 110.0,
                inStock = true,
                sizesCsv = "Free Size",
                colorsCsv = "শ্যাম্পেইন, এমেরাল্ড, রোজ গোল্ড",
                imageKey = "product:hijab",
                createdAt = now - 4000L
            )
        )
        dao.upsertProduct(
            ProductEntity(
                categoryId = beautyId,
                title = "নিভোরা বোটানিক্যাল গ্লো সিরাম",
                description = "নিয়াসিনামাইড, গ্রিন টি ও রোজহিপ সমৃদ্ধ ত্বক উজ্জ্বলকারী ডেইলি সিরাম (৩০ মিলি)।",
                price = 1450.0,
                discountPercent = 12,
                resellerCommission = 180.0,
                inStock = true,
                sizesCsv = "30ml, 50ml",
                colorsCsv = "অরিজিনাল",
                imageKey = "preset:skincare",
                createdAt = now - 5000L
            )
        )
        dao.upsertProduct(
            ProductEntity(
                categoryId = beautyId,
                title = "হানি ও স্যাফরন নাইট ক্রিম",
                description = "গভীর পুষ্টি ও আর্দ্রতার জন্য ১০০% প্রাকৃতিক নাইট রিপেয়ার ক্রিম।",
                price = 1100.0,
                discountPercent = 0,
                resellerCommission = 140.0,
                inStock = false,
                sizesCsv = "50g",
                colorsCsv = "গোল্ডেন জারে",
                imageKey = "product:serum",
                createdAt = now - 6000L
            )
        )
        dao.upsertProduct(
            ProductEntity(
                categoryId = accessoryId,
                title = "ক্লাসিক লেদার টোট ব্যাগ",
                description = "প্রতিদিনের ব্যবহার ও অফিসের জন্য স্টাইলিশ মিনিমালিস্ট টোট ব্যাগ।",
                price = 1950.0,
                discountPercent = 10,
                resellerCommission = 220.0,
                inStock = true,
                sizesCsv = "Standard",
                colorsCsv = "বেইজ ট্যান, ডার্ক টিল, কালো",
                imageKey = "product:bag",
                createdAt = now - 7000L
            )
        )
        dao.upsertProduct(
            ProductEntity(
                categoryId = clothingId,
                title = "এমব্রয়ডার্ড কটন কুর্তি",
                description = "গরমের জন্য আরামদায়ক পিওর কটন কুর্তি, সূক্ষ্ম সুতার কাজ সহ।",
                price = 1600.0,
                discountPercent = 5,
                resellerCommission = 160.0,
                inStock = true,
                sizesCsv = "M, L, XL, XXL",
                colorsCsv = "আইভরি, পেস্তা, টিল",
                imageKey = "product:dress",
                createdAt = now - 8000L
            )
        )

        val customer1 = dao.upsertUser(
            UserEntity(
                name = "নুসরাত জাহান",
                phone = "01712-445566",
                city = "ধানমন্ডি, ঢাকা",
                role = "CUSTOMER",
                totalOrdersCount = 2,
                joinedAt = now - 86400000L * 10
            )
        )
        val customer2 = dao.upsertUser(
            UserEntity(
                name = "ফারহানা আক্তার",
                phone = "01819-778899",
                city = "চট্টগ্রাম",
                role = "CUSTOMER",
                totalOrdersCount = 1,
                joinedAt = now - 86400000L * 5
            )
        )

        val reseller1 = dao.upsertUser(
            UserEntity(
                name = "তানিয়া ইসলাম",
                phone = "01911-223344",
                city = "উত্তরা, ঢাকা",
                role = "RESELLER",
                commissionBalance = 1480.0,
                totalPaidCommission = 3200.0,
                totalOrdersCount = 14,
                joinedAt = now - 86400000L * 25
            )
        )
        val reseller2 = dao.upsertUser(
            UserEntity(
                name = "সুমাইয়া রহমান",
                phone = "01675-990011",
                city = "সিলেট",
                role = "RESELLER",
                commissionBalance = 920.0,
                totalPaidCommission = 1850.0,
                totalOrdersCount = 9,
                joinedAt = now - 86400000L * 18
            )
        )

        dao.insertOrder(
            OrderEntity(
                orderCode = "NV-2041",
                customerId = customer1,
                customerName = "নুসরাত জাহান",
                customerPhone = "01712-445566",
                deliveryAddress = "বাড়ি ১৪, রোড ৬, ধানমন্ডি, ঢাকা",
                resellerId = null,
                resellerName = null,
                itemsSummary = "ডার্ক টিল সিল্ক কাফতান সেট (M, ডার্ক টিল) × ১",
                subtotal = 2423.0,
                deliveryFee = 60.0,
                totalAmount = 2483.0,
                resellerCommissionEarned = 0.0,
                status = "CONFIRMED",
                paymentMethod = "ক্যাশ অন ডেলিভারি",
                createdAt = now - 3600000L * 5,
                updatedAt = now - 3600000L * 2
            )
        )
        dao.insertOrder(
            OrderEntity(
                orderCode = "NV-2040",
                customerId = customer2,
                customerName = "ফারহানা আক্তার",
                customerPhone = "01819-778899",
                deliveryAddress = "নাসিরাবাদ হাউজিং সোসাইটি, চট্টগ্রাম",
                resellerId = reseller1,
                resellerName = "তানিয়া ইসলাম",
                itemsSummary = "লাক্সারি মালয়েশিয়ান শিফন হিজাব × ২, গ্লো সিরাম × ১",
                subtotal = 2316.0,
                deliveryFee = 100.0,
                totalAmount = 2416.0,
                resellerCommissionEarned = 370.0,
                status = "SHIPPED",
                paymentMethod = "বিকাশ",
                createdAt = now - 86400000L,
                updatedAt = now - 3600000L * 8
            )
        )
        dao.insertOrder(
            OrderEntity(
                orderCode = "NV-2038",
                customerId = customer1,
                customerName = "নুসরাত জাহান",
                customerPhone = "01712-445566",
                deliveryAddress = "বাড়ি ১৪, রোড ৬, ধানমন্ডি, ঢাকা",
                resellerId = reseller2,
                resellerName = "সুমাইয়া রহমান",
                itemsSummary = "বেইজ লিনেন মডেস্ট আবায়া (54, সফট বেইজ) × ১",
                subtotal = 1980.0,
                deliveryFee = 60.0,
                totalAmount = 2040.0,
                resellerCommissionEarned = 250.0,
                status = "DELIVERED",
                paymentMethod = "ক্যাশ অন ডেলিভারি",
                createdAt = now - 86400000L * 3,
                updatedAt = now - 86400000L
            )
        )

        dao.insertWithdrawal(
            WithdrawalRequestEntity(
                resellerId = reseller1,
                resellerName = "তানিয়া ইসলাম",
                amount = 800.0,
                method = "bKash",
                accountNumber = "01911-223344",
                status = "PENDING",
                createdAt = now - 3600000L * 4
            )
        )
        dao.insertWithdrawal(
            WithdrawalRequestEntity(
                resellerId = reseller2,
                resellerName = "সুমাইয়া রহমান",
                amount = 1200.0,
                method = "Nagad",
                accountNumber = "01675-990011",
                status = "APPROVED",
                createdAt = now - 86400000L * 4,
                processedAt = now - 86400000L * 3
            )
        )
    }

    suspend fun saveAppConfig(config: AppConfigEntity) = dao.upsertAppConfig(config)

    suspend fun saveBanner(banner: BannerEntity) = dao.upsertBanner(banner)
    suspend fun deleteBanner(banner: BannerEntity) = dao.deleteBanner(banner)

    suspend fun saveCategory(category: CategoryEntity) = dao.upsertCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = dao.deleteCategory(category)

    suspend fun saveProduct(product: ProductEntity) = dao.upsertProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = dao.deleteProduct(product)

    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)

    suspend fun placeOrder(order: OrderEntity): Long = dao.placeOrderAtomic(order)

    suspend fun updateOrderStatus(orderId: Long, status: String) =
        dao.updateOrderStatus(orderId, status)

    suspend fun submitWithdrawal(
        resellerId: Long,
        amount: Double,
        method: String,
        accountNumber: String
    ): Boolean = dao.submitWithdrawalAtomic(resellerId, amount, method, accountNumber)

    suspend fun approveWithdrawal(requestId: Long) = dao.approveWithdrawalAtomic(requestId)
    suspend fun rejectWithdrawal(requestId: Long) = dao.rejectWithdrawalAtomic(requestId)

    /**
     * Copies a photo selected via Android Photo Picker into app-private storage
     * so the image persists across app restarts without needing storage permissions.
     */
    suspend fun persistPickedImage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val mediaDir = File(appContext.filesDir, "nivora_media").apply { mkdirs() }
            val destFile = File(mediaDir, "img_${UUID.randomUUID()}.jpg")
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext null
            destFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }
}
