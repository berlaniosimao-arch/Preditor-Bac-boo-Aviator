package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AnalysisHistoryEntity
import com.example.data.model.AviatorRoundEntity
import com.example.data.model.AviatorStatsSummary
import com.example.data.model.BacBoRoundEntity
import com.example.data.model.BacBoStatsSummary
import com.example.data.model.BacBoWinner
import com.example.data.model.BookmakerId
import com.example.data.model.FastEngineOutput
import com.example.data.model.PlanType
import com.example.data.model.SystemConfigEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.AdminDashboardMetrics
import com.example.data.repository.AdminRepository
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.PremiumDurationOption
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val authRepo = AuthRepository(db.userDao(), db.systemConfigDao())
    private val analyticsRepo = AnalyticsRepository(db.bacBoDao(), db.aviatorDao(), db.analysisDao())
    private val adminRepo = AdminRepository(db.userDao(), db.analysisDao(), db.adminAuditDao(), db.systemConfigDao())

    // Navigation state
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Landing))
    val screenStack: StateFlow<List<Screen>> = _screenStack.asStateFlow()

    val currentScreen: Screen get() = _screenStack.value.lastOrNull() ?: Screen.Landing

    // Current User Session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Selected Bookmaker
    private val _selectedBookmaker = MutableStateFlow(BookmakerId.PREMIER_BET)
    val selectedBookmaker: StateFlow<BookmakerId> = _selectedBookmaker.asStateFlow()

    // Sample limits (10, 25, 50, 100, 500)
    private val _bacBoSampleSize = MutableStateFlow(50)
    val bacBoSampleSize: StateFlow<Int> = _bacBoSampleSize.asStateFlow()

    private val _aviatorSampleSize = MutableStateFlow(50)
    val aviatorSampleSize: StateFlow<Int> = _aviatorSampleSize.asStateFlow()

    // Game stats
    private val _bacBoStats = MutableStateFlow<BacBoStatsSummary?>(null)
    val bacBoStats: StateFlow<BacBoStatsSummary?> = _bacBoStats.asStateFlow()

    private val _aviatorStats = MutableStateFlow<AviatorStatsSummary?>(null)
    val aviatorStats: StateFlow<AviatorStatsSummary?> = _aviatorStats.asStateFlow()

    // Fast engine outputs
    private val _latestBacBoOutput = MutableStateFlow<FastEngineOutput?>(null)
    val latestBacBoOutput: StateFlow<FastEngineOutput?> = _latestBacBoOutput.asStateFlow()

    private val _latestAviatorOutput = MutableStateFlow<FastEngineOutput?>(null)
    val latestAviatorOutput: StateFlow<FastEngineOutput?> = _latestAviatorOutput.asStateFlow()

    // Loading & limits
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _dailyAnalysesUsed = MutableStateFlow(0)
    val dailyAnalysesUsed: StateFlow<Int> = _dailyAnalysesUsed.asStateFlow()

    private val _dailyAnalysesRemaining = MutableStateFlow(5)
    val dailyAnalysesRemaining: StateFlow<Int> = _dailyAnalysesRemaining.asStateFlow()

    // History
    private val _userAnalyses = MutableStateFlow<List<AnalysisHistoryEntity>>(emptyList())
    val userAnalyses: StateFlow<List<AnalysisHistoryEntity>> = _userAnalyses.asStateFlow()

    // Admin state
    private val _adminMetrics = MutableStateFlow<AdminDashboardMetrics?>(null)
    val adminMetrics: StateFlow<AdminDashboardMetrics?> = _adminMetrics.asStateFlow()

    private val _adminUsers = MutableStateFlow<List<UserEntity>>(emptyList())
    val adminUsers: StateFlow<List<UserEntity>> = _adminUsers.asStateFlow()

    private val _adminAuditLogs = MutableStateFlow<List<AdminAuditLogEntity>>(emptyList())
    val adminAuditLogs: StateFlow<List<AdminAuditLogEntity>> = _adminAuditLogs.asStateFlow()

    private val _systemConfigs = MutableStateFlow<List<SystemConfigEntity>>(emptyList())
    val systemConfigs: StateFlow<List<SystemConfigEntity>> = _systemConfigs.asStateFlow()

    // Notification toast
    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    init {
        // Load initial stats for selected bookmaker
        refreshStats()
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    // ----------------------------------------------------
    // Navigation
    // ----------------------------------------------------
    fun navigateTo(screen: Screen) {
        val currentList = _screenStack.value.toMutableList()
        currentList.add(screen)
        _screenStack.value = currentList
        if (screen == Screen.Admin) {
            refreshAdminData()
        } else if (screen == Screen.History) {
            refreshHistory()
        }
    }

    fun handleBack(): Boolean {
        val currentList = _screenStack.value.toMutableList()
        if (currentList.size > 1) {
            currentList.removeAt(currentList.size - 1)
            _screenStack.value = currentList
            return true
        }
        return false
    }

    // ----------------------------------------------------
    // Auth & Session
    // ----------------------------------------------------
    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val res = authRepo.login(email, pass)) {
                is AuthResult.Success -> {
                    _currentUser.value = res.user
                    updateDailyInfo(res.user)
                    refreshStats()
                    navigateTo(Screen.Dashboard)
                    onResult(true, "Bem-vindo, ${res.user.fullName}!")
                }
                is AuthResult.Error -> {
                    onResult(false, res.message)
                }
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        pass: String,
        phone: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            when (val res = authRepo.register(fullName, email, pass, phone)) {
                is AuthResult.Success -> {
                    _currentUser.value = res.user
                    updateDailyInfo(res.user)
                    refreshStats()
                    navigateTo(Screen.Dashboard)
                    onResult(true, "Conta criada com sucesso!")
                }
                is AuthResult.Error -> {
                    onResult(false, res.message)
                }
            }
        }
    }

    fun requestPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authRepo.requestPasswordReset(email)
            if (res.isSuccess) {
                onResult(true, res.getOrNull() ?: "Instruções enviadas.")
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Falha ao recuperar senha.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _screenStack.value = listOf(Screen.Landing)
    }

    private suspend fun updateDailyInfo(user: UserEntity) {
        val (used, remaining) = authRepo.getDailyAnalysesInfo(user)
        _dailyAnalysesUsed.value = used
        _dailyAnalysesRemaining.value = remaining
    }

    // ----------------------------------------------------
    // Bookmaker & Sample Selection
    // ----------------------------------------------------
    fun selectBookmaker(bookmaker: BookmakerId) {
        _selectedBookmaker.value = bookmaker
        refreshStats()
    }

    fun setBacBoSampleSize(size: Int) {
        _bacBoSampleSize.value = size
        refreshStats()
    }

    fun setAviatorSampleSize(size: Int) {
        _aviatorSampleSize.value = size
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            val bm = _selectedBookmaker.value.name
            _bacBoStats.value = analyticsRepo.getBacBoStats(bm, _bacBoSampleSize.value)
            _aviatorStats.value = analyticsRepo.getAviatorStats(bm, _aviatorSampleSize.value)
        }
    }

    // ----------------------------------------------------
    // Fast Analysis Engine Execution (<= 5 Seconds)
    // ----------------------------------------------------
    fun runFastBacBoAnalysis() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isAnalyzing.value = true
            val (canAnalyze, limitMsg) = authRepo.checkAndConsumeDailyAnalysis(user)
            if (!canAnalyze) {
                _isAnalyzing.value = false
                _userFeedback.value = limitMsg
                return@launch
            }

            updateDailyInfo(user)
            val bm = _selectedBookmaker.value.name
            val output = analyticsRepo.runFastBacBoBacBoAnalysisSafe(user, bm, _bacBoSampleSize.value)
            _latestBacBoOutput.value = output
            _isAnalyzing.value = false
            refreshStats()
        }
    }

    fun runFastAviatorAnalysis() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isAnalyzing.value = true
            val (canAnalyze, limitMsg) = authRepo.checkAndConsumeDailyAnalysis(user)
            if (!canAnalyze) {
                _isAnalyzing.value = false
                _userFeedback.value = limitMsg
                return@launch
            }

            updateDailyInfo(user)
            val bm = _selectedBookmaker.value.name
            val output = analyticsRepo.runFastAviatorAnalysis(user, bm, _aviatorSampleSize.value)
            _latestAviatorOutput.value = output
            _isAnalyzing.value = false
            refreshStats()
        }
    }

    // Manual Data Entry (Authorized / Verified)
    fun addManualBacBoResult(winner: BacBoWinner, dTotal: Int, tTotal: Int) {
        viewModelScope.launch {
            val user = _currentUser.value
            val bm = _selectedBookmaker.value.name
            analyticsRepo.addBacBoRound(bm, winner, dTotal, tTotal, user?.id)
            refreshStats()
            _userFeedback.value = "Ronda registada com sucesso no Bac Bo ($bm)."
        }
    }

    fun addManualAviatorResult(multiplier: Double) {
        viewModelScope.launch {
            val user = _currentUser.value
            val bm = _selectedBookmaker.value.name
            analyticsRepo.addAviatorRound(bm, multiplier, user?.id)
            refreshStats()
            _userFeedback.value = "Multiplicador ${"%.2f".format(multiplier)}x registado no Aviator ($bm)."
        }
    }

    private fun refreshHistory() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _userAnalyses.value = analyticsRepo.getUserAnalyses(user.id)
        }
    }

    // ----------------------------------------------------
    // Admin Module Operations
    // ----------------------------------------------------
    fun refreshAdminData() {
        val user = _currentUser.value ?: return
        if (user.role != UserRole.ADMIN.name) return

        viewModelScope.launch {
            _adminMetrics.value = adminRepo.getMetrics()
            _adminUsers.value = adminRepo.getAllUsers()
            _adminAuditLogs.value = adminRepo.getAuditLogs()
            _systemConfigs.value = adminRepo.getConfigs()
        }
    }

    fun searchAdminUsers(query: String) {
        val user = _currentUser.value ?: return
        if (user.role != UserRole.ADMIN.name) return
        viewModelScope.launch {
            _adminUsers.value = adminRepo.searchUsers(query)
        }
    }

    fun grantPremium(userId: Long, option: PremiumDurationOption) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = adminRepo.grantPremium(userId, option, admin)
            if (res.isSuccess) {
                _userFeedback.value = "Plano Premium (${option.label}) concedido com sucesso!"
                refreshAdminData()
                // If admin granted to themselves or current active user, refresh session
                if (userId == admin.id) {
                    _currentUser.value = authRepo.refreshUser(userId)
                }
            } else {
                _userFeedback.value = "Erro: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun revokePremium(userId: Long) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = adminRepo.revokePremium(userId, admin)
            if (res.isSuccess) {
                _userFeedback.value = "Plano Premium revogado."
                refreshAdminData()
                if (userId == admin.id) {
                    _currentUser.value = authRepo.refreshUser(userId)
                }
            } else {
                _userFeedback.value = "Erro: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun toggleBlockUser(userId: Long) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = adminRepo.toggleBlockUser(userId, admin)
            if (res.isSuccess) {
                val blocked = res.getOrNull() == true
                _userFeedback.value = if (blocked) "Utilizador bloqueado." else "Utilizador desbloqueado."
                refreshAdminData()
            } else {
                _userFeedback.value = "Erro: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    fun updateConfig(key: String, value: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = adminRepo.updateConfig(key, value, admin)
            if (res.isSuccess) {
                _userFeedback.value = "Configuração atualizada com sucesso!"
                refreshAdminData()
            }
        }
    }
}

// Extension helper for safe analysis call
private suspend fun AnalyticsRepository.runFastBacBoBacBoAnalysisSafe(
    user: UserEntity,
    bookmaker: String,
    sample: Int
): FastEngineOutput {
    return runFastBacBoAnalysis(user, bookmaker, sample)
}
