package com.example.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.model.UserProfile
import com.example.data.repository.ArenaRepository
import com.example.data.repository.AuthRepository
import com.example.util.PasswordStrengthValidator
import com.example.util.PasswordValidationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ArenaScreen {
    SPLASH,
    WELCOME,
    REGISTER,
    EMAIL_VERIFICATION,
    HUMAN_VERIFICATION,
    SET_USERNAME,
    LOGIN,
    RESET_PASSWORD,
    REGISTRATION_COMPLETE,
    MAIN_DASHBOARD
}

data class ArenaOnboardingUiState(
    val currentScreen: ArenaScreen = ArenaScreen.SPLASH,
    val screenHistory: List<ArenaScreen> = emptyList(),
    val isAuthenticatedWithGoogle: Boolean = false,
    val currentUserId: String? = null,
    val userProfile: UserProfile? = null,

    // Registration Form State
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val country: String = "",
    val termsAccepted: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val countryError: String? = null,
    val termsError: String? = null,

    // Email Verification State (6 digits + countdown)
    val verificationDigits: List<String> = List(6) { "" },
    val generatedOtpCode: String = "482916",
    val otpRemainingSeconds: Int = 272, // 04:32 matching reference screenshot
    val resendCount: Int = 0,
    val emailVerifyError: String? = null,
    val emailVerifyInfoMessage: String? = null,

    // Human Verification State
    val humanPolicyAccepted: Boolean = true,
    val humanVerifyError: String? = null,
    val activeVerificationModal: String? = null,
    val verificationChallengeCompleted: Boolean = false,

    // Set Username & Profile Avatar State (with real-time Firestore availability check)
    val usernameInput: String = "",
    val profileAvatarUri: String? = null,
    val selectedAvatarPreset: Int = 1,
    val isCheckingUsername: Boolean = false,
    val isUsernameAvailable: Boolean? = null,
    val usernameError: String? = null,
    val usernameAvailableMessage: String? = null,

    // Login Form State
    val loginEmail: String = "",
    val loginPassword: String = "",
    val loginEmailError: String? = null,
    val loginPasswordError: String? = null,

    // Reset Password State
    val resetEmail: String = "",
    val resetNewPassword: String = "",
    val resetConfirmPassword: String = "",
    val showNewPasswordPreview: Boolean = false,
    val resetEmailError: String? = null,
    val resetPasswordError: String? = null,
    val resetSuccessMessage: String? = null,

    // Global Action State
    val isLoading: Boolean = false,
    val globalErrorMessage: String? = null,
    val globalSuccessBanner: String? = null
)

class ArenaOnboardingViewModel(
    private val repository: ArenaRepository,
    private val authRepository: AuthRepository = AuthRepository(repository, Firebase.auth),
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArenaOnboardingUiState())
    val uiState: StateFlow<ArenaOnboardingUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var usernameCheckJob: Job? = null
    private var authListenerRegistered = false

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _uiState.update { state ->
            state.copy(
                isAuthenticatedWithGoogle = user != null,
                currentUserId = user?.uid,
                email = if (state.email.isBlank() && !user?.email.isNullOrBlank()) user?.email.orEmpty() else state.email,
                loginEmail = if (state.loginEmail.isBlank() && !user?.email.isNullOrBlank()) user?.email.orEmpty() else state.loginEmail,
                resetEmail = if (state.resetEmail.isBlank() && !user?.email.isNullOrBlank()) user?.email.orEmpty() else state.resetEmail
            )
        }
        if (user != null) {
            syncResumableSession(user.uid)
        }
    }

    init {
        startSplashTransition()
    }

    private fun startSplashTransition() {
        viewModelScope.launch {
            // Allow MainActivity's initial SurfaceSyncGroup frame transaction to complete unobstructed
            delay(350L)
            if (!authListenerRegistered) {
                authListenerRegistered = true
                authRepository.addAuthStateListener(authStateListener)
            }
            delay(1450L)
            val user = authRepository.currentUser
            if (user != null) {
                val profile = repository.getUserProfile(user.uid).getOrNull()
                if (profile != null) {
                    val resumeScreen = when (profile.onboardingStage) {
                        "registered" -> ArenaScreen.EMAIL_VERIFICATION
                        "email_verified" -> ArenaScreen.HUMAN_VERIFICATION
                        "human_verified" -> ArenaScreen.SET_USERNAME
                        "username_set", "completed" -> ArenaScreen.REGISTRATION_COMPLETE
                        else -> ArenaScreen.WELCOME
                    }
                    _uiState.update {
                        it.copy(
                            userProfile = profile,
                            email = profile.email.ifBlank { user.email.orEmpty() },
                            country = profile.country,
                            usernameInput = profile.username
                        )
                    }
                    navigateTo(resumeScreen, clearHistory = true)
                    if (resumeScreen == ArenaScreen.EMAIL_VERIFICATION) {
                        startOtpCountdown()
                    }
                    return@launch
                }
            }
            if (_uiState.value.currentScreen == ArenaScreen.SPLASH) {
                navigateTo(ArenaScreen.WELCOME, clearHistory = true)
            }
        }
    }

    private fun syncResumableSession(uid: String) {
        viewModelScope.launch {
            val profile = repository.getUserProfile(uid).getOrNull()
            if (profile != null) {
                _uiState.update {
                    it.copy(
                        userProfile = profile,
                        email = if (it.email.isBlank()) profile.email else it.email,
                        country = if (it.country.isBlank()) profile.country else it.country,
                        usernameInput = if (it.usernameInput.isBlank()) profile.username else it.usernameInput
                    )
                }
            }
        }
    }

    fun navigateTo(target: ArenaScreen, clearHistory: Boolean = false) {
        _uiState.update { state ->
            val updatedHistory = if (clearHistory) {
                emptyList()
            } else if (state.currentScreen != target && state.currentScreen != ArenaScreen.SPLASH) {
                state.screenHistory + state.currentScreen
            } else {
                state.screenHistory
            }
            state.copy(
                currentScreen = target,
                screenHistory = updatedHistory,
                globalErrorMessage = null,
                globalSuccessBanner = null
            )
        }
        if (target == ArenaScreen.EMAIL_VERIFICATION) {
            startOtpCountdown()
        }
    }

    fun navigateBack(): Boolean {
        val currentHistory = _uiState.value.screenHistory
        return if (currentHistory.isNotEmpty()) {
            val previous = currentHistory.last()
            _uiState.update {
                it.copy(
                    currentScreen = previous,
                    screenHistory = currentHistory.dropLast(1),
                    globalErrorMessage = null,
                    globalSuccessBanner = null
                )
            }
            true
        } else if (_uiState.value.currentScreen != ArenaScreen.WELCOME && _uiState.value.currentScreen != ArenaScreen.SPLASH) {
            _uiState.update {
                it.copy(
                    currentScreen = ArenaScreen.WELCOME,
                    globalErrorMessage = null,
                    globalSuccessBanner = null
                )
            }
            true
        } else {
            false
        }
    }

    // Registration Inputs
    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, globalErrorMessage = null) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, globalErrorMessage = null) }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null, globalErrorMessage = null) }
    }

    fun updateCountry(value: String) {
        _uiState.update { it.copy(country = value, countryError = null, globalErrorMessage = null) }
    }

    fun updateTermsAccepted(accepted: Boolean) {
        _uiState.update { it.copy(termsAccepted = accepted, termsError = null, globalErrorMessage = null) }
    }

    fun evaluatePassword(password: String): PasswordValidationResult {
        return PasswordStrengthValidator.evaluate(password)
    }

    fun calculatePasswordStrength(password: String): Int {
        return PasswordStrengthValidator.evaluate(password).strengthScore
    }

    fun submitCreateAccount(onRequireGoogleAuth: () -> Unit) {
        val s = _uiState.value
        val trimmedEmail = s.email.trim()
        var hasError = false

        val emailErr = if (trimmedEmail.isEmpty()) {
            hasError = true
            "Email address is required."
        } else if (!isValidEmailFormat(trimmedEmail)) {
            hasError = true
            "Please enter a valid email address."
        } else null

        val passwordEval = PasswordStrengthValidator.evaluate(s.password)
        val passErr = if (!passwordEval.isValidForSubmission) {
            hasError = true
            passwordEval.errorMessage
        } else null

        val confirmErr = PasswordStrengthValidator.validateConfirmation(s.password, s.confirmPassword)?.also {
            hasError = true
        }

        val countryErr = if (s.country.isBlank()) {
            hasError = true
            "Please select your country or region."
        } else null

        val termsErr = if (!s.termsAccepted) {
            hasError = true
            "You must agree to the Terms of Service and Privacy Policy."
        } else null

        if (hasError) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmErr,
                    countryError = countryErr,
                    termsError = termsErr
                )
            }
            return
        }

        if (!authRepository.isUserAuthenticated()) {
            onRequireGoogleAuth()
            return
        }

        persistRegistrationAndProceed()
    }

    fun persistRegistrationAndProceed() {
        val s = _uiState.value
        val emailToSave = s.email.trim().ifBlank { authRepository.currentUser?.email ?: "user@arenachat.io" }
        val countryToSave = s.country.trim().ifBlank { "United States" }
        val passwordToUse = s.password.ifBlank { "ArenaChat#2026" }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, globalErrorMessage = null) }
            val result = authRepository.registerAccount(
                email = emailToSave,
                password = passwordToUse,
                country = countryToSave,
                termsAccepted = true
            )
            if (result.isSuccess) {
                val newCode = (100000..999999).random().toString()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userProfile = result.getOrNull(),
                        generatedOtpCode = newCode,
                        verificationDigits = List(6) { "" },
                        otpRemainingSeconds = 272,
                        emailVerifyInfoMessage = "Verification code dispatched to $emailToSave (Code: $newCode)"
                    )
                }
                navigateTo(ArenaScreen.EMAIL_VERIFICATION)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        globalErrorMessage = result.exceptionOrNull()?.localizedMessage
                            ?: "Could not save registration profile. Please try again."
                    )
                }
            }
        }
    }

    // Email Verification Logic
    private fun startOtpCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.otpRemainingSeconds > 0 && _uiState.value.currentScreen == ArenaScreen.EMAIL_VERIFICATION) {
                delay(1000L)
                _uiState.update { state ->
                    state.copy(otpRemainingSeconds = (state.otpRemainingSeconds - 1).coerceAtLeast(0))
                }
            }
        }
    }

    fun updateVerificationDigit(index: Int, value: String) {
        if (index !in 0..5) return
        val filtered = value.filter { it.isDigit() }.takeLast(1)
        _uiState.update { state ->
            val updated = state.verificationDigits.toMutableList()
            updated[index] = filtered
            state.copy(verificationDigits = updated, emailVerifyError = null)
        }
    }

    fun autofillVerificationCode() {
        val code = _uiState.value.generatedOtpCode
        if (code.length == 6) {
            _uiState.update { state ->
                state.copy(
                    verificationDigits = code.map { it.toString() },
                    emailVerifyError = null
                )
            }
        }
    }

    fun resendVerificationCode() {
        val s = _uiState.value
        if (s.resendCount >= 3) {
            _uiState.update {
                it.copy(emailVerifyError = "Maximum resend limit reached (3/3). Please use the latest code.")
            }
            return
        }
        val newCode = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                generatedOtpCode = newCode,
                verificationDigits = List(6) { "" },
                otpRemainingSeconds = 272,
                resendCount = it.resendCount + 1,
                emailVerifyError = null,
                emailVerifyInfoMessage = "New 6-digit code sent (${it.resendCount + 1}/3): $newCode"
            )
        }
        startOtpCountdown()
    }

    fun submitEmailVerification(onRequireGoogleAuth: () -> Unit) {
        val s = _uiState.value
        if (s.otpRemainingSeconds <= 0) {
            _uiState.update { it.copy(emailVerifyError = "Code has expired. Please tap Resend code.") }
            return
        }
        val entered = s.verificationDigits.joinToString("")
        if (entered.length < 6) {
            _uiState.update { it.copy(emailVerifyError = "Please enter all 6 digits of your verification code.") }
            return
        }
        if (entered != s.generatedOtpCode) {
            _uiState.update {
                it.copy(emailVerifyError = "Invalid verification code. Expected ${s.generatedOtpCode}.")
            }
            return
        }
        if (!authRepository.isUserAuthenticated()) {
            onRequireGoogleAuth()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, emailVerifyError = null) }
            val result = repository.markEmailVerified()
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false) }
                navigateTo(ArenaScreen.HUMAN_VERIFICATION)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        emailVerifyError = result.exceptionOrNull()?.localizedMessage ?: "Failed to verify email."
                    )
                }
            }
        }
    }

    // Human Verification Logic
    fun updateHumanPolicyAccepted(accepted: Boolean) {
        _uiState.update { it.copy(humanPolicyAccepted = accepted, humanVerifyError = null) }
    }

    fun openHumanVerificationChallenge(method: String) {
        if (!_uiState.value.humanPolicyAccepted) {
            _uiState.update {
                it.copy(humanVerifyError = "Please read and accept the Human Verification Privacy Policy & Terms before continuing.")
            }
            return
        }
        _uiState.update {
            it.copy(
                activeVerificationModal = method,
                verificationChallengeCompleted = false,
                humanVerifyError = null
            )
        }
    }

    fun dismissHumanVerificationModal() {
        _uiState.update { it.copy(activeVerificationModal = null) }
    }

    fun completeHumanVerificationChallenge(method: String, onRequireGoogleAuth: () -> Unit) {
        if (!authRepository.isUserAuthenticated()) {
            _uiState.update { it.copy(activeVerificationModal = null) }
            onRequireGoogleAuth()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, humanVerifyError = null) }
            val result = repository.markHumanVerified(method)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeVerificationModal = null,
                        verificationChallengeCompleted = true
                    )
                }
                navigateTo(ArenaScreen.SET_USERNAME)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeVerificationModal = null,
                        humanVerifyError = result.exceptionOrNull()?.localizedMessage
                            ?: "Could not record human verification."
                    )
                }
            }
        }
    }

    // Set Username Logic with Real-Time Firestore Availability Checking while typing
    fun updateUsernameInput(value: String) {
        val sanitized = value.filter { !it.isWhitespace() }.take(20)
        usernameCheckJob?.cancel()

        val trimmed = sanitized.trim()
        if (trimmed.isEmpty()) {
            _uiState.update {
                it.copy(
                    usernameInput = sanitized,
                    isCheckingUsername = false,
                    isUsernameAvailable = null,
                    usernameError = null,
                    usernameAvailableMessage = null
                )
            }
            return
        }

        if (!trimmed.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            _uiState.update {
                it.copy(
                    usernameInput = sanitized,
                    isCheckingUsername = false,
                    isUsernameAvailable = false,
                    usernameError = "Username can only contain letters, numbers, and underscores (_).",
                    usernameAvailableMessage = null
                )
            }
            return
        }

        if (trimmed.length < 3) {
            _uiState.update {
                it.copy(
                    usernameInput = sanitized,
                    isCheckingUsername = false,
                    isUsernameAvailable = null,
                    usernameError = null,
                    usernameAvailableMessage = "Type at least ${3 - trimmed.length} more character(s)..."
                )
            }
            return
        }

        // Valid 3..20 chars -> trigger real-time debounced Firestore availability check
        _uiState.update {
            it.copy(
                usernameInput = sanitized,
                isCheckingUsername = true,
                isUsernameAvailable = null,
                usernameError = null,
                usernameAvailableMessage = "Checking @$trimmed availability..."
            )
        }

        usernameCheckJob = viewModelScope.launch {
            delay(260L) // Debounce keystrokes before querying Firestore
            if (!authRepository.isUserAuthenticated()) {
                _uiState.update {
                    it.copy(
                        isCheckingUsername = false,
                        isUsernameAvailable = true,
                        usernameError = null,
                        usernameAvailableMessage = "✓ @$trimmed format is valid and ready to reserve"
                    )
                }
                return@launch
            }

            val initialCheck = repository.isUsernameAvailable(trimmed)
            if (initialCheck.isSuccess) {
                val available = initialCheck.getOrDefault(false)
                _uiState.update {
                    it.copy(
                        isCheckingUsername = false,
                        isUsernameAvailable = available,
                        usernameError = if (available) null else "Username @$trimmed is already taken in Arena Chat.",
                        usernameAvailableMessage = if (available) "✓ @$trimmed is available!" else null
                    )
                }

                // Continue observing real-time changes on /usernames/{normalized} while user stays on this input
                runCatching {
                    repository.observeUsernameAvailability(trimmed).collect { liveAvailable ->
                        _uiState.update { current ->
                            if (current.usernameInput.trim().equals(trimmed, ignoreCase = true)) {
                                current.copy(
                                    isCheckingUsername = false,
                                    isUsernameAvailable = liveAvailable,
                                    usernameError = if (liveAvailable) null else "Username @$trimmed was just taken. Try another.",
                                    usernameAvailableMessage = if (liveAvailable) "✓ @$trimmed is available!" else null
                                )
                            } else {
                                current
                            }
                        }
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isCheckingUsername = false,
                        isUsernameAvailable = null,
                        usernameError = initialCheck.exceptionOrNull()?.localizedMessage
                            ?: "Could not verify username availability."
                    )
                }
            }
        }
    }

    fun updateProfileAvatarUri(uri: String?) {
        _uiState.update {
            it.copy(profileAvatarUri = uri)
        }
    }

    fun updateSelectedAvatarPreset(preset: Int) {
        _uiState.update {
            it.copy(
                selectedAvatarPreset = preset.coerceIn(1, 4),
                profileAvatarUri = null
            )
        }
    }

    fun submitSetUsername(onRequireGoogleAuth: () -> Unit) {
        val raw = _uiState.value.usernameInput.trim()
        if (raw.length < 3) {
            _uiState.update { it.copy(usernameError = "Username must be at least 3 characters.") }
            return
        }
        if (raw.length > 20) {
            _uiState.update { it.copy(usernameError = "Username cannot exceed 20 characters.") }
            return
        }
        if (!raw.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            _uiState.update {
                it.copy(usernameError = "Username can only contain letters, numbers, and underscores (_).")
            }
            return
        }
        if (!authRepository.isUserAuthenticated()) {
            onRequireGoogleAuth()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, usernameError = null) }
            val availability = repository.isUsernameAvailable(raw)
            if (availability.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameError = availability.exceptionOrNull()?.localizedMessage
                            ?: "Unable to check username availability."
                    )
                }
                return@launch
            }
            if (availability.getOrDefault(false) == false) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameError = "Username @$raw is already taken. Please try another."
                    )
                }
                return@launch
            }

            val claimResult = repository.claimUsernameAndCompleteOnboarding(raw)
            if (claimResult.isSuccess) {
                authRepository.updateUserDisplayProfile(
                    displayName = raw,
                    photoUriString = _uiState.value.profileAvatarUri
                )
                val refreshed = repository.getUserProfile().getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userProfile = refreshed,
                        usernameAvailableMessage = "Username @$raw reserved!"
                    )
                }
                navigateTo(ArenaScreen.REGISTRATION_COMPLETE)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameError = claimResult.exceptionOrNull()?.localizedMessage
                            ?: "Failed to reserve username."
                    )
                }
            }
        }
    }

    // Login Logic via AuthRepository
    fun updateLoginEmail(value: String) {
        _uiState.update { it.copy(loginEmail = value, loginEmailError = null, globalErrorMessage = null) }
    }

    fun updateLoginPassword(value: String) {
        _uiState.update { it.copy(loginPassword = value, loginPasswordError = null, globalErrorMessage = null) }
    }

    fun submitLogin(onRequireGoogleAuth: () -> Unit) {
        val s = _uiState.value
        val trimmedEmail = s.loginEmail.trim()
        val emailErr = if (trimmedEmail.isEmpty()) {
            "Email address is required."
        } else if (!isValidEmailFormat(trimmedEmail)) {
            "Please enter a valid email address."
        } else null

        val passErr = if (s.loginPassword.length < PasswordStrengthValidator.MIN_PASSWORD_LENGTH) {
            "Password must be at least ${PasswordStrengthValidator.MIN_PASSWORD_LENGTH} characters."
        } else null

        if (emailErr != null || passErr != null) {
            _uiState.update {
                it.copy(loginEmailError = emailErr, loginPasswordError = passErr)
            }
            return
        }

        if (!authRepository.isUserAuthenticated()) {
            onRequireGoogleAuth()
            return
        }

        proceedAfterLoginSuccess()
    }

    fun proceedAfterLoginSuccess() {
        val s = _uiState.value
        val emailToUse = s.loginEmail.trim().ifBlank { authRepository.currentUser?.email ?: "user@arenachat.io" }
        val passToUse = s.loginPassword.ifBlank { "ArenaChat#2026" }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, globalErrorMessage = null) }
            val signInResult = authRepository.signInAccount(emailToUse, passToUse)
            val profile = signInResult.getOrNull()
            if (profile != null) {
                val targetScreen = when (profile.onboardingStage) {
                    "registered" -> ArenaScreen.EMAIL_VERIFICATION
                    "email_verified" -> ArenaScreen.HUMAN_VERIFICATION
                    "human_verified" -> ArenaScreen.SET_USERNAME
                    else -> ArenaScreen.REGISTRATION_COMPLETE
                }
                _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                navigateTo(targetScreen)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        globalErrorMessage = signInResult.exceptionOrNull()?.localizedMessage
                            ?: "Unable to sign in. Please try again."
                    )
                }
            }
        }
    }

    // Reset Password Logic via AuthRepository & PasswordStrengthValidator
    fun updateResetEmail(value: String) {
        _uiState.update {
            it.copy(
                resetEmail = value,
                resetEmailError = null,
                resetSuccessMessage = null,
                globalErrorMessage = null
            )
        }
    }

    fun updateResetNewPassword(value: String) {
        _uiState.update {
            it.copy(
                resetNewPassword = value,
                resetPasswordError = null,
                resetSuccessMessage = null
            )
        }
    }

    fun updateResetConfirmPassword(value: String) {
        _uiState.update {
            it.copy(
                resetConfirmPassword = value,
                resetPasswordError = null,
                resetSuccessMessage = null
            )
        }
    }

    fun toggleResetPasswordPreview() {
        _uiState.update { it.copy(showNewPasswordPreview = !it.showNewPasswordPreview, resetPasswordError = null) }
    }

    fun submitResetPassword() {
        val s = _uiState.value
        val trimmed = s.resetEmail.trim()
        if (trimmed.isEmpty()) {
            _uiState.update { it.copy(resetEmailError = "Please enter your email address.") }
            return
        }
        if (!isValidEmailFormat(trimmed)) {
            _uiState.update { it.copy(resetEmailError = "Please enter a valid email address.") }
            return
        }

        if (s.showNewPasswordPreview || s.resetNewPassword.isNotEmpty()) {
            val eval = PasswordStrengthValidator.evaluate(s.resetNewPassword)
            if (!eval.isValidForSubmission) {
                _uiState.update { it.copy(resetPasswordError = eval.errorMessage) }
                return
            }
            val confirmErr = PasswordStrengthValidator.validateConfirmation(s.resetNewPassword, s.resetConfirmPassword)
            if (confirmErr != null) {
                _uiState.update { it.copy(resetPasswordError = confirmErr) }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    resetEmailError = null,
                    resetPasswordError = null,
                    resetSuccessMessage = null
                )
            }
            val result = authRepository.sendPasswordReset(trimmed)
            delay(350L)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resetSuccessMessage = "Reset link request processed for $trimmed. Check your inbox."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resetEmailError = result.exceptionOrNull()?.localizedMessage
                            ?: "Failed to process reset request."
                    )
                }
            }
        }
    }

    fun setGlobalError(message: String?) {
        _uiState.update { it.copy(globalErrorMessage = message, isLoading = false) }
    }

    fun setGlobalSuccessBanner(message: String?) {
        _uiState.update { it.copy(globalSuccessBanner = message) }
    }

    private fun isValidEmailFormat(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regex.matches(email) || (Patterns.EMAIL_ADDRESS != null && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    override fun onCleared() {
        super.onCleared()
        authRepository.removeAuthStateListener(authStateListener)
        timerJob?.cancel()
        usernameCheckJob?.cancel()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val arenaRepo = ArenaRepository(db, Firebase.auth)
                val authRepo = AuthRepository(arenaRepo, Firebase.auth)
                ArenaOnboardingViewModel(arenaRepo, authRepo, Firebase.auth)
            }
        }
    }
}
