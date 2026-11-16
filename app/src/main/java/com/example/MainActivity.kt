package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MaintenanceBlockerScreen
import com.example.ui.components.NivoraTopBar
import com.example.ui.components.NoticePopupDialog
import com.example.ui.components.NoticeTopBanner
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CustomerStorefrontScreen
import com.example.ui.screens.ResellerPortalScreen
import com.example.ui.theme.NivoraTheme
import com.example.ui.viewmodel.NivoraRole
import com.example.ui.viewmodel.NivoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NivoraAppRoot()
        }
    }
}

@Composable
fun NivoraAppRoot(
    viewModel: NivoraViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        val msg = state.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    NivoraTheme(
        paletteKey = state.appConfig.themePaletteKey,
        darkTheme = state.appConfig.isDarkMode
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { outerPadding ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(outerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.appConfig.isMaintenanceMode && state.activeRole != NivoraRole.ADMIN) {
                MaintenanceBlockerScreen(
                    config = state.appConfig,
                    onSwitchToAdmin = { viewModel.switchRole(NivoraRole.ADMIN) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    NivoraTopBar(
                        activeRole = state.activeRole,
                        appVersion = state.appConfig.appVersion,
                        onRoleSelected = viewModel::switchRole
                    )

                    NoticeTopBanner(
                        visible = state.shouldShowBannerNotice,
                        config = state.appConfig,
                        onDismiss = viewModel::dismissBannerNotice
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        Crossfade(
                            targetState = state.activeRole,
                            label = "role_crossfade"
                        ) { role ->
                            when (role) {
                                NivoraRole.CUSTOMER -> CustomerStorefrontScreen(
                                    state = state,
                                    onAddToCart = viewModel::addToCart,
                                    onUpdateCartQty = viewModel::updateCartQuantity,
                                    onPlaceOrder = viewModel::placeCustomerOrder
                                )

                                NivoraRole.RESELLER -> ResellerPortalScreen(
                                    state = state,
                                    onSelectReseller = viewModel::selectActiveReseller,
                                    onPlaceResellerOrder = viewModel::placeResellerOrder,
                                    onSubmitWithdrawal = viewModel::submitWithdrawalRequest,
                                    onCopySuccess = viewModel::showMessage
                                )

                                NivoraRole.ADMIN -> AdminDashboardScreen(
                                    state = state,
                                    onUpdateTheme = viewModel::updateTheme,
                                    onUpdateNoticeBoard = viewModel::updateNoticeBoard,
                                    onToggleMaintenance = viewModel::toggleMaintenanceMode,
                                    onSaveBanner = viewModel::saveBanner,
                                    onDeleteBanner = viewModel::deleteBanner,
                                    onSaveCategory = viewModel::saveCategory,
                                    onDeleteCategory = viewModel::deleteCategory,
                                    onSaveProduct = viewModel::saveProduct,
                                    onDeleteProduct = viewModel::deleteProduct,
                                    onToggleProductStock = viewModel::toggleProductStock,
                                    onAdjustResellerBalance = viewModel::updateResellerBalance,
                                    onApproveWithdrawal = viewModel::approveWithdrawal,
                                    onRejectWithdrawal = viewModel::rejectWithdrawal,
                                    onUpdateOrderStatus = viewModel::updateOrderStatus,
                                    onPersistPickedImage = viewModel::persistPickedImage
                                )
                            }
                        }
                    }
                }

                if (state.shouldShowPopupNotice) {
                    NoticePopupDialog(
                        config = state.appConfig,
                        onDismiss = viewModel::dismissPopupNotice
                    )
                }
            }
        }
    }
}
