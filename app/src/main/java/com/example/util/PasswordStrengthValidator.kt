package com.example.util

/**
 * Reusable password strength and validation utility for Arena Chat registration
 * and password reset flows.
 *
 * Enforces checks for:
 * - Minimum length (>= 8 characters)
 * - Presence of numbers (0-9)
 * - Presence of special characters (!@#$%^&* etc.)
 * - Presence of uppercase and lowercase letters
 */
enum class PasswordStrengthLevel(val segments: Int, val label: String) {
    NONE(0, "Use at least 8 characters"),
    WEAK(1, "Weak • Add numbers & special characters"),
    MEDIUM(2, "Good • Add a special character for maximum strength"),
    STRONG(3, "Strong password ✓")
}

data class PasswordValidationResult(
    val hasMinLength: Boolean,
    val hasNumber: Boolean,
    val hasSpecialChar: Boolean,
    val hasLetter: Boolean,
    val hasMixedCase: Boolean,
    val strengthScore: Int, // 0..3 matching the 3-segment strength bar
    val strengthLevel: PasswordStrengthLevel,
    val isValidForSubmission: Boolean,
    val errorMessage: String?
)

object PasswordStrengthValidator {

    const val MIN_PASSWORD_LENGTH = 8
    private val SPECIAL_CHAR_REGEX = Regex("[^A-Za-z0-9\\s]")

    /**
     * Evaluates a password string against length, numeric, letter, and special character criteria.
     */
    fun evaluate(password: String): PasswordValidationResult {
        if (password.isEmpty()) {
            return PasswordValidationResult(
                hasMinLength = false,
                hasNumber = false,
                hasSpecialChar = false,
                hasLetter = false,
                hasMixedCase = false,
                strengthScore = 0,
                strengthLevel = PasswordStrengthLevel.NONE,
                isValidForSubmission = false,
                errorMessage = "Password is required."
            )
        }

        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = SPECIAL_CHAR_REGEX.containsMatchIn(password)
        val hasLetter = password.any { it.isLetter() }
        val hasMixedCase = password.any { it.isUpperCase() } && password.any { it.isLowerCase() }

        val criteriaMet = listOf(hasMinLength, hasNumber, hasSpecialChar, hasMixedCase).count { it }

        val score = when {
            !hasMinLength -> 1
            hasMinLength && hasNumber && hasSpecialChar && (hasLetter || hasMixedCase) -> 3
            hasMinLength && (hasNumber || hasSpecialChar || hasMixedCase) -> 2
            else -> 1
        }

        val level = when (score) {
            3 -> PasswordStrengthLevel.STRONG
            2 -> PasswordStrengthLevel.MEDIUM
            1 -> PasswordStrengthLevel.WEAK
            else -> PasswordStrengthLevel.NONE
        }

        val errorMessage = when {
            !hasMinLength -> "Password must be at least $MIN_PASSWORD_LENGTH characters."
            !hasLetter -> "Password must include at least one letter."
            !hasNumber -> "Password must include at least one number (0-9)."
            !hasSpecialChar -> "Password must include at least one special character (e.g. !@#\$%)."
            else -> null
        }

        val isValid = hasMinLength && hasLetter && hasNumber && hasSpecialChar && criteriaMet >= 3

        return PasswordValidationResult(
            hasMinLength = hasMinLength,
            hasNumber = hasNumber,
            hasSpecialChar = hasSpecialChar,
            hasLetter = hasLetter,
            hasMixedCase = hasMixedCase,
            strengthScore = score,
            strengthLevel = level,
            isValidForSubmission = isValid,
            errorMessage = errorMessage
        )
    }

    /**
     * Validates password confirmation matching.
     */
    fun validateConfirmation(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Please confirm your password."
            password != confirmPassword -> "Passwords do not match."
            else -> null
        }
    }
}
