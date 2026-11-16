package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.NivoraDatabase
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.repository.NivoraRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NivoraRepositoryTest {

    private lateinit var db: NivoraDatabase
    private lateinit var repository: NivoraRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NivoraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = NivoraRepository(db.nivoraDao(), context)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `seed populates dynamic catalog banners users and orders`() = runBlocking {
        repository.ensureSeeded()

        val categories = repository.categories.first()
        val products = repository.products.first()
        val banners = repository.activeBanners.first()
        val resellers = repository.resellers.first()
        val orders = repository.orders.first()

        assertTrue(categories.size >= 4)
        assertTrue(products.size >= 8)
        assertTrue(banners.size >= 3)
        assertTrue(resellers.isNotEmpty())
        assertTrue(orders.isNotEmpty())
    }

    @Test
    fun `admin config updates theme maintenance and notice board in real time`() = runBlocking {
        repository.ensureSeeded()
        val initial = repository.appConfig.first()
        assertFalse(initial.isMaintenanceMode)

        repository.saveAppConfig(
            initial.copy(
                themePaletteKey = "emerald_ivory",
                isDarkMode = true,
                isMaintenanceMode = true,
                noticeRevision = initial.noticeRevision + 1,
                appVersion = "v2.5.0"
            )
        )

        val updated = repository.appConfig.first()
        assertEquals("emerald_ivory", updated.themePaletteKey)
        assertTrue(updated.isDarkMode)
        assertTrue(updated.isMaintenanceMode)
        assertEquals("v2.5.0", updated.appVersion)
    }

    @Test
    fun `admin order status update and product crud reflect immediately`() = runBlocking {
        repository.ensureSeeded()
        val categories = repository.categories.first()
        val prodId = repository.saveProduct(
            ProductEntity(
                categoryId = categories.first().id,
                title = "নতুন সিল্ক শাড়ি",
                description = "প্রিমিয়াম কালেকশন",
                price = 3200.0,
                discountPercent = 10,
                resellerCommission = 300.0,
                inStock = true,
                sizesCsv = "12 Haat",
                colorsCsv = "ডার্ক টিল"
            )
        )
        val savedProduct = repository.products.first().first { it.id == prodId }
        assertEquals(2880.0, savedProduct.finalPrice, 0.01)

        val orderId = repository.placeOrder(
            OrderEntity(
                orderCode = "NV-9001",
                customerName = "আয়েশা",
                customerPhone = "01700-000000",
                deliveryAddress = "ঢাকা",
                itemsSummary = "নতুন সিল্ক শাড়ি × ১",
                subtotal = 2880.0,
                totalAmount = 2940.0,
                status = "ORDERED"
            )
        )
        repository.updateOrderStatus(orderId, "SHIPPED")
        val updatedOrder = repository.orders.first().first { it.id == orderId }
        assertEquals("SHIPPED", updatedOrder.status)
    }

    @Test
    fun `reseller commission and withdrawal approval lifecycle stays consistent`() = runBlocking {
        repository.ensureSeeded()
        val reseller = repository.resellers.first().first()
        val initialBalance = reseller.commissionBalance
        val initialPaid = reseller.totalPaidCommission

        val submitted = repository.submitWithdrawal(
            resellerId = reseller.id,
            amount = 500.0,
            method = "bKash",
            accountNumber = "01900-112233"
        )
        assertTrue(submitted)

        val afterSubmit = repository.resellers.first().first { it.id == reseller.id }
        assertEquals(initialBalance - 500.0, afterSubmit.commissionBalance, 0.01)

        val pendingReq = repository.withdrawals.first().first { it.status == "PENDING" && it.accountNumber == "01900-112233" }
        repository.approveWithdrawal(pendingReq.id)

        val afterApprove = repository.resellers.first().first { it.id == reseller.id }
        assertEquals(initialPaid + 500.0, afterApprove.totalPaidCommission, 0.01)
    }
}
