package com.example

import com.example.util.PasswordStrengthLevel
import com.example.util.PasswordStrengthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun passwordStrengthValidator_emptyPassword_returnsNoneAndInvalid() {
        val result = PasswordStrengthValidator.evaluate("")
        assertFalse(result.hasMinLength)
        assertFalse(result.hasNumber)
        assertFalse(result.hasSpecialChar)
        assertEquals(0, result.strengthScore)
        assertEquals(PasswordStrengthLevel.NONE, result.strengthLevel)
        assertFalse(result.isValidForSubmission)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun passwordStrengthValidator_shortPassword_failsMinimumLength() {
        val result = PasswordStrengthValidator.evaluate("Ab1!")
        assertFalse(result.hasMinLength)
        assertTrue(result.hasNumber)
        assertTrue(result.hasSpecialChar)
        assertEquals(1, result.strengthScore)
        assertEquals(PasswordStrengthLevel.WEAK, result.strengthLevel)
        assertFalse(result.isValidForSubmission)
    }

    @Test
    fun passwordStrengthValidator_missingSpecialCharacter_failsValidation() {
        val result = PasswordStrengthValidator.evaluate("ArenaChat123")
        assertTrue(result.hasMinLength)
        assertTrue(result.hasNumber)
        assertFalse(result.hasSpecialChar)
        assertEquals(2, result.strengthScore)
        assertEquals(PasswordStrengthLevel.MEDIUM, result.strengthLevel)
        assertFalse(result.isValidForSubmission)
    }

    @Test
    fun passwordStrengthValidator_missingNumber_failsValidation() {
        val result = PasswordStrengthValidator.evaluate("ArenaChat!@#")
        assertTrue(result.hasMinLength)
        assertFalse(result.hasNumber)
        assertTrue(result.hasSpecialChar)
        assertEquals(2, result.strengthScore)
        assertFalse(result.isValidForSubmission)
    }

    @Test
    fun passwordStrengthValidator_strongPasswordWithLengthNumberAndSpecial_succeeds() {
        val result = PasswordStrengthValidator.evaluate("ArenaChat#2026")
        assertTrue(result.hasMinLength)
        assertTrue(result.hasNumber)
        assertTrue(result.hasSpecialChar)
        assertTrue(result.hasLetter)
        assertEquals(3, result.strengthScore)
        assertEquals(PasswordStrengthLevel.STRONG, result.strengthLevel)
        assertTrue(result.isValidForSubmission)
        assertNull(result.errorMessage)
    }

    @Test
    fun passwordStrengthValidator_validateConfirmation_checksMatching() {
        assertNull(PasswordStrengthValidator.validateConfirmation("Arena#2026", "Arena#2026"))
        assertNotNull(PasswordStrengthValidator.validateConfirmation("Arena#2026", ""))
        assertNotNull(PasswordStrengthValidator.validateConfirmation("Arena#2026", "Mismatch#2026"))
    }
}
