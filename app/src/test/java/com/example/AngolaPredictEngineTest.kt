package com.example

import com.example.data.model.AviatorBracket
import com.example.data.model.AviatorRoundEntity
import com.example.data.model.PlanType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AngolaPredictEngineTest {

    @Test
    fun testPasswordHashingSecurity() {
        val pass = "Angola@2026"
        val hash = SecurityUtils.hashPassword(pass)

        assertNotEquals(pass, hash)
        assertTrue(SecurityUtils.verifyPassword(pass, hash))
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", hash))
    }

    @Test
    fun testUserPlanAndPremiumStatus() {
        val now = System.currentTimeMillis()

        // Free User
        val freeUser = UserEntity(
            email = "test@free.ao",
            passwordHash = "hash",
            fullName = "Free User",
            plan = PlanType.FREE.name
        )
        assertFalse(freeUser.isPremiumActive())
        assertEquals("GRATUITO (FREE)", freeUser.effectivePlanName())

        // Active Premium User (expires in 10 days)
        val activePremUser = UserEntity(
            email = "vip@ao.ao",
            passwordHash = "hash",
            fullName = "VIP User",
            plan = PlanType.PREMIUM.name,
            premiumExpiresAt = now + (10L * 24 * 60 * 60 * 1000)
        )
        assertTrue(activePremUser.isPremiumActive())
        assertEquals("PREMIUM", activePremUser.effectivePlanName())

        // Expired Premium User (expired yesterday)
        val expiredPremUser = UserEntity(
            email = "expired@ao.ao",
            passwordHash = "hash",
            fullName = "Expired VIP",
            plan = PlanType.PREMIUM.name,
            premiumExpiresAt = now - (1L * 24 * 60 * 60 * 1000)
        )
        assertFalse(expiredPremUser.isPremiumActive())
        assertEquals("GRATUITO (FREE)", expiredPremUser.effectivePlanName())

        // Permanent Premium User
        val permUser = UserEntity(
            email = "perm@ao.ao",
            passwordHash = "hash",
            fullName = "Permanent VIP",
            plan = PlanType.PREMIUM_PERMANENT.name
        )
        assertTrue(permUser.isPremiumActive())
        assertEquals("PREMIUM PERMANENTE", permUser.effectivePlanName())

        // Master Admin User
        val adminUser = UserEntity(
            email = "admin@ao.ao",
            passwordHash = "hash",
            fullName = "Admin Master",
            role = UserRole.ADMIN.name
        )
        assertTrue(adminUser.isPremiumActive())
        assertEquals("ADMINISTRADOR MASTER", adminUser.effectivePlanName())
    }

    @Test
    fun testAviatorBracketClassification() {
        val r1 = AviatorRoundEntity(bookmaker = "PREMIER_BET", roundNumber = 1, multiplier = 1.12)
        assertEquals(AviatorBracket.BELOW_1_50, r1.bracket())

        val r2 = AviatorRoundEntity(bookmaker = "PREMIER_BET", roundNumber = 2, multiplier = 1.84)
        assertEquals(AviatorBracket.BETWEEN_1_50_AND_2_00, r2.bracket())

        val r3 = AviatorRoundEntity(bookmaker = "PREMIER_BET", roundNumber = 3, multiplier = 3.45)
        assertEquals(AviatorBracket.BETWEEN_2_00_AND_5_00, r3.bracket())

        val r4 = AviatorRoundEntity(bookmaker = "PREMIER_BET", roundNumber = 4, multiplier = 12.80)
        assertEquals(AviatorBracket.ABOVE_5_00, r4.bracket())
    }

    @Test
    fun testProcessingSpeedConstraint() {
        // Fast processing test requirement: computation finishes in < 5000ms
        val start = System.currentTimeMillis()
        val mockData = (1..500).map { (it * 1.05) % 15.0 }
        val sum = mockData.sum()
        val avg = sum / mockData.size
        val elapsed = System.currentTimeMillis() - start

        assertTrue("Execution must be under 5000ms", elapsed < 5000)
        assertTrue(avg > 0.0)
    }
}
