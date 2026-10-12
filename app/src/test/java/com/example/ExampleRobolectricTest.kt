package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.AviatorRoundEntity
import com.example.data.model.BacBoRoundEntity
import com.example.data.model.BacBoWinner
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Angola Predict AI", appName)
    }

    @Test
    fun `test Bac Bo room database storage and querying`() = runBlocking {
        val bacBoDao = db.bacBoDao()

        val sampleRounds = (1..60).map { i ->
            BacBoRoundEntity(
                bookmaker = "PREMIER_BET",
                roundNumber = i.toLong(),
                winner = when (i % 3) {
                    0 -> BacBoWinner.DRAGON.name
                    1 -> BacBoWinner.TIGER.name
                    else -> BacBoWinner.TIE.name
                },
                dragonTotal = 7 + (i % 5),
                tigerTotal = 6 + (i % 4)
            )
        }

        bacBoDao.insertRounds(sampleRounds)

        // Count total
        val count = bacBoDao.countRounds("PREMIER_BET")
        assertEquals(60, count)

        // Query last 10 rounds
        val last10 = bacBoDao.getRounds("PREMIER_BET", 10)
        assertEquals(10, last10.size)
        assertEquals(60L, last10.first().roundNumber)

        // Query last 50 rounds
        val last50 = bacBoDao.getRounds("PREMIER_BET", 50)
        assertEquals(50, last50.size)

        // Count by winner
        val dragonCount = bacBoDao.countByWinner("PREMIER_BET", BacBoWinner.DRAGON.name)
        assertEquals(20, dragonCount)

        val tieCount = bacBoDao.countByWinner("PREMIER_BET", BacBoWinner.TIE.name)
        assertEquals(20, tieCount)
    }

    @Test
    fun `test Aviator room database storage and bracket querying`() = runBlocking {
        val aviatorDao = db.aviatorDao()

        val sampleMultipliers = listOf(
            1.10, 1.25, 1.48, // Below 1.50x (3)
            1.55, 1.82, 1.95, // 1.50x to 2.00x (3)
            2.10, 3.40, 4.80, // 2.00x to 5.00x (3)
            7.50, 15.20       // Above 5.00x (2)
        )

        val rounds = sampleMultipliers.mapIndexed { idx, m ->
            AviatorRoundEntity(
                bookmaker = "ELEPHANT_BET",
                roundNumber = (idx + 1).toLong(),
                multiplier = m
            )
        }

        aviatorDao.insertRounds(rounds)

        val total = aviatorDao.countRounds("ELEPHANT_BET")
        assertEquals(11, total)

        // Test threshold queries
        val below150 = aviatorDao.countBelowThreshold("ELEPHANT_BET", 1.50, 50)
        assertEquals(3, below150)

        val between150And200 = aviatorDao.countBetweenThresholds("ELEPHANT_BET", 1.50, 2.00, 50)
        assertEquals(3, between150And200)

        val above500 = aviatorDao.countAboveThreshold("ELEPHANT_BET", 5.00, 50)
        assertEquals(2, above500)

        val maxMult = aviatorDao.getMaxMultiplier("ELEPHANT_BET", 50)
        assertEquals(15.20, maxMult ?: 0.0, 0.001)
    }

    @Test
    fun `test BacBoResult and AviatorResult specific entities`() = runBlocking {
        val bacBoResultDao = db.bacBoResultDao()
        val aviatorResultDao = db.aviatorResultDao()

        val bacBoItem = com.example.data.model.BacBoResult(
            round_id = 101L,
            winner = "DRAGON",
            timestamp = System.currentTimeMillis(),
            casa_apostas = "Premier Bet Angola"
        )
        val insertedBbId = bacBoResultDao.insert(bacBoItem)
        assertTrue(insertedBbId > 0)

        val retrievedBb = bacBoResultDao.getRecentResults("Premier Bet Angola", 10)
        assertEquals(1, retrievedBb.size)
        assertEquals(101L, retrievedBb.first().round_id)
        assertEquals("DRAGON", retrievedBb.first().winner)
        assertEquals("Premier Bet Angola", retrievedBb.first().casa_apostas)

        val aviatorItem = com.example.data.model.AviatorResult(
            round_id = 502L,
            multiplier = 3.75,
            timestamp = System.currentTimeMillis(),
            casa_apostas = "Elephant Bet"
        )
        val insertedAvId = aviatorResultDao.insert(aviatorItem)
        assertTrue(insertedAvId > 0)

        val retrievedAv = aviatorResultDao.getRecentResults("Elephant Bet", 10)
        assertEquals(1, retrievedAv.size)
        assertEquals(502L, retrievedAv.first().round_id)
        assertEquals(3.75, retrievedAv.first().multiplier, 0.001)
        assertEquals("Elephant Bet", retrievedAv.first().casa_apostas)
    }
}
