package com.example.util

import java.security.MessageDigest

object SecurityUtils {
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        // Salt for Angola Predict AI
        val salted = "angola_predict_salt_2026:$password"
        val bytes = digest.digest(salted.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }
}
