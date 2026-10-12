package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AviatorScreen
import com.example.ui.screens.BacBoScreen
import com.example.ui.screens.BookmakersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.theme.AngolaPredictTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AngolaPredictTheme {
                AngolaPredictApp()
            }
        }
    }
}

@Composable
fun AngolaPredictApp(viewModel: MainViewModel = viewModel()) {
    val screenStack by viewModel.screenStack.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedBookmaker by viewModel.selectedBookmaker.collectAsState()
    val bacBoSampleSize by viewModel.bacBoSampleSize.collectAsState()
    val aviatorSampleSize by viewModel.aviatorSampleSize.collectAsState()
    val bacBoStats by viewModel.bacBoStats.collectAsState()
    val aviatorStats by viewModel.aviatorStats.collectAsState()
    val latestBacBoOutput by viewModel.latestBacBoOutput.collectAsState()
    val latestAviatorOutput by viewModel.latestAviatorOutput.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val dailyUsed by viewModel.dailyAnalysesUsed.collectAsState()
    val dailyRemaining by viewModel.dailyAnalysesRemaining.collectAsState()
    val userAnalyses by viewModel.userAnalyses.collectAsState()
    val adminMetrics by viewModel.adminMetrics.collectAsState()
    val adminUsers by viewModel.adminUsers.collectAsState()
    val adminAuditLogs by viewModel.adminAuditLogs.collectAsState()
    val systemConfigs by viewModel.systemConfigs.collectAsState()
    val feedbackMessage by viewModel.userFeedback.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    val currentScreen = screenStack.lastOrNull() ?: Screen.Landing

    // Intercept hardware Back button
    BackHandler(enabled = screenStack.size > 1) {
        viewModel.handleBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            is Screen.Landing -> {
                LandingScreen(
                    onTryFreeClick = {
                        if (currentUser != null) {
                            viewModel.navigateTo(Screen.Dashboard)
                        } else {
                            viewModel.navigateTo(Screen.Register)
                        }
                    },
                    onExplorePremiumClick = {
                        viewModel.navigateTo(Screen.Premium)
                    },
                    onLoginClick = {
                        viewModel.navigateTo(Screen.Login)
                    },
                    modifier = screenModifier
                )
            }

            is Screen.Login -> {
                LoginScreen(
                    onLoginClick = { email, pass ->
                        viewModel.login(email, pass) { success, msg ->
                            if (!success) {
                                // show error in toast
                            }
                        }
                    },
                    onNavigateToRegister = { viewModel.navigateTo(Screen.Register) },
                    onNavigateToForgotPassword = { viewModel.navigateTo(Screen.ForgotPassword) },
                    onBackClick = { viewModel.handleBack() },
                    errorMessage = null,
                    modifier = screenModifier
                )
            }

            is Screen.Register -> {
                RegisterScreen(
                    onRegisterClick = { name, email, pass, phone ->
                        viewModel.register(name, email, pass, phone) { _, _ -> }
                    },
                    onNavigateToLogin = { viewModel.navigateTo(Screen.Login) },
                    onBackClick = { viewModel.handleBack() },
                    errorMessage = null,
                    modifier = screenModifier
                )
            }

            is Screen.ForgotPassword -> {
                ForgotPasswordScreen(
                    onRequestResetClick = { email ->
                        viewModel.requestPasswordReset(email) { _, _ -> }
                    },
                    onBackClick = { viewModel.handleBack() },
                    modifier = screenModifier
                )
            }

            is Screen.Dashboard -> {
                val user = currentUser
                if (user != null) {
                    DashboardScreen(
                        currentUser = user,
                        selectedBookmaker = selectedBookmaker,
                        onSelectBookmaker = { viewModel.selectBookmaker(it) },
                        bacBoStats = bacBoStats,
                        aviatorStats = aviatorStats,
                        dailyUsed = dailyUsed,
                        dailyRemaining = dailyRemaining,
                        onNavigateToBacBo = { viewModel.navigateTo(Screen.BacBo) },
                        onNavigateToAviator = { viewModel.navigateTo(Screen.Aviator) },
                        onNavigateToBookmakers = { viewModel.navigateTo(Screen.Bookmakers) },
                        onNavigateToHistory = { viewModel.navigateTo(Screen.History) },
                        onNavigateToPremium = { viewModel.navigateTo(Screen.Premium) },
                        onNavigateToAdmin = { viewModel.navigateTo(Screen.Admin) },
                        onLogoutClick = { viewModel.logout() },
                        modifier = screenModifier
                    )
                } else {
                    // Fallback to Login
                    LoginScreen(
                        onLoginClick = { email, pass -> viewModel.login(email, pass) { _, _ -> } },
                        onNavigateToRegister = { viewModel.navigateTo(Screen.Register) },
                        onNavigateToForgotPassword = { viewModel.navigateTo(Screen.ForgotPassword) },
                        onBackClick = { viewModel.handleBack() },
                        modifier = screenModifier
                    )
                }
            }

            is Screen.BacBo -> {
                val user = currentUser
                if (user != null) {
                    BacBoScreen(
                        currentUser = user,
                        selectedBookmaker = selectedBookmaker,
                        sampleSize = bacBoSampleSize,
                        onSelectSampleSize = { viewModel.setBacBoSampleSize(it) },
                        stats = bacBoStats,
                        engineOutput = latestBacBoOutput,
                        isAnalyzing = isAnalyzing,
                        onRunFastAnalysis = { viewModel.runFastBacBoAnalysis() },
                        onAddRound = { winner, d, t -> viewModel.addManualBacBoResult(winner, d, t) },
                        onBackClick = { viewModel.handleBack() },
                        modifier = screenModifier
                    )
                }
            }

            is Screen.Aviator -> {
                val user = currentUser
                if (user != null) {
                    AviatorScreen(
                        currentUser = user,
                        selectedBookmaker = selectedBookmaker,
                        sampleSize = aviatorSampleSize,
                        onSelectSampleSize = { viewModel.setAviatorSampleSize(it) },
                        stats = aviatorStats,
                        engineOutput = latestAviatorOutput,
                        isAnalyzing = isAnalyzing,
                        onRunFastAnalysis = { viewModel.runFastAviatorAnalysis() },
                        onAddMultiplier = { viewModel.addManualAviatorResult(it) },
                        onBackClick = { viewModel.handleBack() },
                        modifier = screenModifier
                    )
                }
            }

            is Screen.Bookmakers -> {
                val user = currentUser
                if (user != null) {
                    BookmakersScreen(
                        currentUser = user,
                        selectedBookmaker = selectedBookmaker,
                        onSelectBookmaker = { viewModel.selectBookmaker(it) },
                        onNavigateToBacBo = { viewModel.navigateTo(Screen.BacBo) },
                        onNavigateToAviator = { viewModel.navigateTo(Screen.Aviator) },
                        onBackClick = { viewModel.handleBack() },
                        modifier = screenModifier
                    )
                }
            }

            is Screen.History -> {
                val user = currentUser
                if (user != null) {
                    HistoryScreen(
                        currentUser = user,
                        analyses = userAnalyses,
                        onBackClick = { viewModel.handleBack() },
                        onNavigateToPremium = { viewModel.navigateTo(Screen.Premium) },
                        modifier = screenModifier
                    )
                }
            }

            is Screen.Premium -> {
                PremiumScreen(
                    currentUser = currentUser,
                    onBackClick = { viewModel.handleBack() },
                    modifier = screenModifier
                )
            }

            is Screen.Admin -> {
                val user = currentUser
                if (user != null) {
                    AdminScreen(
                        currentUser = user,
                        metrics = adminMetrics,
                        users = adminUsers,
                        auditLogs = adminAuditLogs,
                        configs = systemConfigs,
                        onSearchUsers = { viewModel.searchAdminUsers(it) },
                        onGrantPremium = { id, opt -> viewModel.grantPremium(id, opt) },
                        onRevokePremium = { id -> viewModel.revokePremium(id) },
                        onToggleBlockUser = { id -> viewModel.toggleBlockUser(id) },
                        onUpdateConfig = { k, v -> viewModel.updateConfig(k, v) },
                        onRefresh = { viewModel.refreshAdminData() },
                        onBackClick = { viewModel.handleBack() },
                        modifier = screenModifier
                    )
                }
            }
        }
    }
}
