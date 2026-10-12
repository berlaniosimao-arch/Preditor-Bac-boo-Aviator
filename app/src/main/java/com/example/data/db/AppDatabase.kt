package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AnalysisHistoryEntity
import com.example.data.model.AviatorResult
import com.example.data.model.AviatorRoundEntity
import com.example.data.model.BacBoResult
import com.example.data.model.BacBoRoundEntity
import com.example.data.model.BookmakerId
import com.example.data.model.PlanType
import com.example.data.model.SystemConfigEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.util.SecurityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        BacBoRoundEntity::class,
        AviatorRoundEntity::class,
        BacBoResult::class,
        AviatorResult::class,
        AnalysisHistoryEntity::class,
        SystemConfigEntity::class,
        AdminAuditLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bacBoDao(): BacBoDao
    abstract fun aviatorDao(): AviatorDao
    abstract fun bacBoResultDao(): BacBoResultDao
    abstract fun aviatorResultDao(): AviatorResultDao
    abstract fun analysisDao(): AnalysisDao
    abstract fun adminAuditDao(): AdminAuditDao
    abstract fun systemConfigDao(): SystemConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "angola_predict_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed initial data in IO scope
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    seedInitialData(database)
                }
            }
        }
    }
}

suspend fun seedInitialData(database: AppDatabase) {
    val userDao = database.userDao()
    val configDao = database.systemConfigDao()
    val bacBoDao = database.bacBoDao()
    val aviatorDao = database.aviatorDao()

    // 1. Initial Users
    val now = System.currentTimeMillis()
    val thirtyDaysLater = now + (30L * 24 * 60 * 60 * 1000)

    val admin = UserEntity(
        email = "admin@angolapredict.ao",
        passwordHash = SecurityUtils.hashPassword("Admin@2026"),
        fullName = "Administrador Master",
        phoneNumber = "+244 923 000 001",
        role = UserRole.ADMIN.name,
        plan = PlanType.PREMIUM_PERMANENT.name,
        createdAt = now,
        lastLoginAt = now
    )
    userDao.insertUser(admin)

    val demoUser = UserEntity(
        email = "demo@angolapredict.ao",
        passwordHash = SecurityUtils.hashPassword("User@2026"),
        fullName = "Utilizador Free Teste",
        phoneNumber = "+244 945 112 233",
        role = UserRole.USER.name,
        plan = PlanType.FREE.name,
        createdAt = now,
        lastLoginAt = now
    )
    userDao.insertUser(demoUser)

    val premiumUser = UserEntity(
        email = "premium@angolapredict.ao",
        passwordHash = SecurityUtils.hashPassword("Premium@2026"),
        fullName = "Carlos Manuel (VIP)",
        phoneNumber = "+244 912 345 678",
        role = UserRole.USER.name,
        plan = PlanType.PREMIUM.name,
        createdAt = now,
        lastLoginAt = now,
        premiumActivatedAt = now,
        premiumExpiresAt = thirtyDaysLater,
        activatedByAdminEmail = "admin@angolapredict.ao"
    )
    userDao.insertUser(premiumUser)

    // 2. Default System Configs
    val configs = listOf(
        SystemConfigEntity("FREE_DAILY_LIMIT", "5", "Limite diário de análises para contas gratuitas"),
        SystemConfigEntity("BAC_BO_ENABLED", "true", "Estado do módulo Bac Bo"),
        SystemConfigEntity("AVIATOR_ENABLED", "true", "Estado do módulo Aviator"),
        SystemConfigEntity("PREMIER_BET_ENABLED", "true", "Premier Bet Angola"),
        SystemConfigEntity("ELEPHANT_BET_ENABLED", "true", "Elephant Bet"),
        SystemConfigEntity("BANTU_BET_ENABLED", "true", "Bantu Bet"),
        SystemConfigEntity("KWANZA_BET_ENABLED", "true", "Kwanza Bet"),
        SystemConfigEntity("EBET_ENABLED", "true", "Ebet Angola")
    )
    for (config in configs) {
        configDao.setConfig(config)
    }

    // 3. Seed Realistic Verified Historical Rounds for Bookmakers
    // Bac Bo patterns: Dragon, Tiger, Tie
    val initialBacBoPattern = listOf(
        Pair("DRAGON", Pair(8, 6)),
        Pair("TIGER", Pair(5, 9)),
        Pair("DRAGON", Pair(10, 4)),
        Pair("DRAGON", Pair(7, 3)),
        Pair("TIGER", Pair(6, 8)),
        Pair("TIE", Pair(7, 7)),
        Pair("TIGER", Pair(4, 11)),
        Pair("DRAGON", Pair(9, 7)),
        Pair("DRAGON", Pair(8, 5)),
        Pair("TIGER", Pair(3, 10)),
        Pair("TIGER", Pair(6, 7)),
        Pair("DRAGON", Pair(11, 4)),
        Pair("TIE", Pair(6, 6)),
        Pair("DRAGON", Pair(9, 8)),
        Pair("TIGER", Pair(5, 7)),
        Pair("TIGER", Pair(4, 6)),
        Pair("DRAGON", Pair(12, 9)),
        Pair("DRAGON", Pair(7, 5)),
        Pair("TIGER", Pair(6, 10)),
        Pair("DRAGON", Pair(8, 2)),
        Pair("TIE", Pair(9, 9)),
        Pair("TIGER", Pair(5, 8)),
        Pair("DRAGON", Pair(10, 6)),
        Pair("TIGER", Pair(7, 8)),
        Pair("DRAGON", Pair(9, 3))
    )

    for (bm in BookmakerId.values()) {
        val bacBoList = mutableListOf<BacBoRoundEntity>()
        var roundNum = 1000L
        for (i in 0 until 2) {
            for (p in initialBacBoPattern) {
                roundNum++
                bacBoList.add(
                    BacBoRoundEntity(
                        bookmaker = bm.name,
                        roundNumber = roundNum,
                        winner = p.first,
                        dragonTotal = p.second.first,
                        tigerTotal = p.second.second,
                        timestamp = now - ((100 - bacBoList.size) * 60000L),
                        isVerified = true
                    )
                )
            }
        }
        bacBoDao.insertRounds(bacBoList)
    }

    // Aviator sample multipliers for bookmakers
    val sampleMultipliers = listOf(
        1.14, 2.45, 1.05, 1.88, 3.20, 1.30, 8.45, 1.12, 1.95, 4.10,
        1.02, 1.48, 2.05, 1.70, 15.60, 1.25, 1.35, 2.80, 1.08, 6.12,
        1.90, 1.44, 2.15, 1.18, 1.62, 5.50, 1.03, 2.90, 1.39, 12.30,
        1.75, 1.20, 3.40, 1.10, 1.82, 2.25, 1.42, 1.09, 4.80, 22.40,
        1.33, 1.91, 1.15, 2.02, 1.55, 3.10, 1.28, 1.67, 7.85, 1.04
    )

    for (bm in BookmakerId.values()) {
        val aviatorList = mutableListOf<AviatorRoundEntity>()
        var roundNum = 5000L
        for (mult in sampleMultipliers) {
            roundNum++
            aviatorList.add(
                AviatorRoundEntity(
                    bookmaker = bm.name,
                    roundNumber = roundNum,
                    multiplier = mult,
                    timestamp = now - (( sampleMultipliers.size - aviatorList.size) * 45000L),
                    isVerified = true
                )
            )
        }
        aviatorDao.insertRounds(aviatorList)
    }
}
