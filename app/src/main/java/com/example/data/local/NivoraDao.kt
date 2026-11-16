package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NivoraDao {

    // --- App Config ---
    @Query("SELECT * FROM app_config WHERE id = 1")
    fun observeAppConfig(): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config WHERE id = 1")
    suspend fun getAppConfigOnce(): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAppConfig(config: AppConfigEntity)

    // --- Banners ---
    @Query("SELECT * FROM banners ORDER BY sortOrder ASC, id DESC")
    fun observeAllBanners(): Flow<List<BannerEntity>>

    @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY sortOrder ASC, id DESC")
    fun observeActiveBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBanner(banner: BannerEntity): Long

    @Delete
    suspend fun deleteBanner(banner: BannerEntity)

    // --- Categories ---
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, id ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: CategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY createdAt DESC, id DESC")
    fun observeProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(product: ProductEntity): Long

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // --- Users (Customers & Resellers) ---
    @Query("SELECT * FROM users WHERE role = 'CUSTOMER' ORDER BY joinedAt DESC")
    fun observeCustomers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'RESELLER' ORDER BY joinedAt DESC")
    fun observeResellers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    // --- Withdrawals ---
    @Query("SELECT * FROM withdrawal_requests ORDER BY createdAt DESC")
    fun observeWithdrawals(): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawal_requests WHERE id = :id")
    suspend fun getWithdrawalById(id: Long): WithdrawalRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(request: WithdrawalRequestEntity): Long

    @Update
    suspend fun updateWithdrawal(request: WithdrawalRequestEntity)

    // --- Atomic Transactions ---
    @Transaction
    suspend fun placeOrderAtomic(order: OrderEntity): Long {
        val insertedId = insertOrder(order)
        if (order.resellerId != null && order.resellerCommissionEarned > 0.0) {
            val reseller = getUserById(order.resellerId)
            if (reseller != null) {
                updateUser(
                    reseller.copy(
                        commissionBalance = reseller.commissionBalance + order.resellerCommissionEarned,
                        totalOrdersCount = reseller.totalOrdersCount + 1
                    )
                )
            }
        }
        if (order.customerId != null) {
            val customer = getUserById(order.customerId)
            if (customer != null) {
                updateUser(customer.copy(totalOrdersCount = customer.totalOrdersCount + 1))
            }
        }
        return insertedId
    }

    @Transaction
    suspend fun submitWithdrawalAtomic(
        resellerId: Long,
        amount: Double,
        method: String,
        accountNumber: String
    ): Boolean {
        val reseller = getUserById(resellerId) ?: return false
        if (amount <= 0.0 || amount > reseller.commissionBalance) return false

        updateUser(reseller.copy(commissionBalance = reseller.commissionBalance - amount))
        insertWithdrawal(
            WithdrawalRequestEntity(
                resellerId = reseller.id,
                resellerName = reseller.name,
                amount = amount,
                method = method,
                accountNumber = accountNumber,
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
        )
        return true
    }

    @Transaction
    suspend fun approveWithdrawalAtomic(requestId: Long) {
        val req = getWithdrawalById(requestId) ?: return
        if (req.status != "PENDING") return
        val reseller = getUserById(req.resellerId)
        if (reseller != null) {
            updateUser(
                reseller.copy(
                    totalPaidCommission = reseller.totalPaidCommission + req.amount
                )
            )
        }
        updateWithdrawal(
            req.copy(
                status = "APPROVED",
                processedAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    suspend fun rejectWithdrawalAtomic(requestId: Long) {
        val req = getWithdrawalById(requestId) ?: return
        if (req.status != "PENDING") return
        val reseller = getUserById(req.resellerId)
        if (reseller != null) {
            updateUser(
                reseller.copy(
                    commissionBalance = reseller.commissionBalance + req.amount
                )
            )
        }
        updateWithdrawal(
            req.copy(
                status = "REJECTED",
                processedAt = System.currentTimeMillis()
            )
        )
    }
}
