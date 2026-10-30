package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.GoogleAuthHelper
import com.example.ui.screens.EmailVerificationScreen
import com.example.ui.screens.HumanVerificationScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RegistrationCompleteScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.ResetPasswordScreen
import com.example.ui.screens.SetUsernameScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WelcomeLandingScreen
import com.example.ui.screens.dashboard.ArenaMainDashboardScreen
import com.example.ui.theme.ArenaCreamBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ArenaOnboardingViewModel
import com.example.ui.viewmodel.ArenaScreen
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

private fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ArenaCreamBg
                ) {
                    ArenaChatPhase1App()
                }
            }
        }
    }
}

@Composable
fun ArenaChatPhase1App(
    viewModel: ArenaOnboardingViewModel = viewModel(factory = ArenaOnboardingViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val launchInteractiveGoogleSignIn: (() -> Unit) -> Unit = { onSignedIn ->
        GoogleAuthHelper.onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { onSignedIn() },
            onAuthError = { err -> viewModel.setGlobalError(err) },
            scope = scope,
            onAuthCancelled = {
                viewModel.setGlobalError("Google Sign-In is required to securely store your Arena Chat account in Firestore.")
            }
        )
    }

    when (uiState.currentScreen) {
        ArenaScreen.SPLASH -> {
            SplashScreen(
                onManualSkip = { viewModel.navigateTo(ArenaScreen.WELCOME, clearHistory = true) }
            )
        }

        ArenaScreen.WELCOME -> {
            WelcomeLandingScreen(
                onCreateAccountClick = { viewModel.navigateTo(ArenaScreen.REGISTER) },
                onLoginClick = { viewModel.navigateTo(ArenaScreen.LOGIN) },
                onGoogleAuthSuccess = {
                    viewModel.navigateTo(ArenaScreen.MAIN_DASHBOARD)
                },
                onGoogleAuthError = { err ->
                    viewModel.setGlobalError(err)
                },
                onOpenDashboardClick = {
                    viewModel.navigateTo(ArenaScreen.MAIN_DASHBOARD)
                }
            )
        }

        ArenaScreen.REGISTER -> {
            RegistrationScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateBack() },
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,
                onConfirmPasswordChange = viewModel::updateConfirmPassword,
                onCountryChange = viewModel::updateCountry,
                onTermsChange = viewModel::updateTermsAccepted,
                passwordStrength = viewModel.calculatePasswordStrength(uiState.password),
                onCreateAccountSubmit = {
                    viewModel.submitCreateAccount(
                        onRequireGoogleAuth = {
                            launchInteractiveGoogleSignIn {
                                viewModel.persistRegistrationAndProceed()
                            }
                        }
                    )
                },
                onLoginClick = { viewModel.navigateTo(ArenaScreen.LOGIN) },
                onGoogleAuthSuccess = {
                    viewModel.setGlobalError(null)
                },
                onGoogleAuthError = { err ->
                    viewModel.setGlobalError(err)
                }
            )
        }

        ArenaScreen.EMAIL_VERIFICATION -> {
            EmailVerificationScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateBack() },
                onDigitChange = viewModel::updateVerificationDigit,
                onAutofillCode = viewModel::autofillVerificationCode,
                onVerifyClick = {
                    viewModel.submitEmailVerification(
                        onRequireGoogleAuth = {
                            launchInteractiveGoogleSignIn {
                                viewModel.submitEmailVerification {}
                            }
                        }
                    )
                },
                onResendClick = viewModel::resendVerificationCode
            )
        }

        ArenaScreen.HUMAN_VERIFICATION -> {
            HumanVerificationScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateBack() },
                onPolicyAcceptedChange = viewModel::updateHumanPolicyAccepted,
                onStartChallenge = viewModel::openHumanVerificationChallenge,
                onDismissChallenge = viewModel::dismissHumanVerificationModal,
                onCompleteChallenge = { method ->
                    viewModel.completeHumanVerificationChallenge(
                        method = method,
                        onRequireGoogleAuth = {
                            launchInteractiveGoogleSignIn {
                                viewModel.completeHumanVerificationChallenge(method) {}
                            }
                        }
                    )
                }
            )
        }

        ArenaScreen.SET_USERNAME -> {
            SetUsernameScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateBack() },
                onUsernameChange = viewModel::updateUsernameInput,
                onAvatarUriChange = viewModel::updateProfileAvatarUri,
                onAvatarPresetChange = viewModel::updateSelectedAvatarPreset,
                onContinueClick = {
                    viewModel.submitSetUsername(
                        onRequireGoogleAuth = {
                            launchInteractiveGoogleSignIn {
                                viewModel.submitSetUsername {}
                            }
                        }
                    )
                }
            )
        }

        ArenaScreen.LOGIN -> {
            LoginScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateBack() },
                onEmailChange = viewModel::updateLoginEmail,
                onPasswordChange = viewModel::updateLoginPassword,
                onForgotPasswordClick = { viewModel.navigateTo(ArenaScreen.RESET_PASSWORD) },
                onLoginSubmit = {
                    viewModel.submitLogin(
                        onRequireGoogleAuth = {
                            launchInteractiveGoogleSignIn {
                                viewModel.proceedAfterLoginSuccess()
                            }
                        }
                    )
                },
                onCreateAccountClick = { viewModel.navigateTo(ArenaScreen.REGISTER) },
                onGoogleAuthSuccess = {
                    viewModel.proceedAfterLoginSuccess()
                },
                onGoogleAuthError = { err ->
                    viewModel.setGlobalError(err)
                }
            )
        }

        ArenaScreen.RESET_PASSWORD -> {
            ResetPasswordScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateBack() },
                onResetEmailChange = viewModel::updateResetEmail,
                onResetNewPasswordChange = viewModel::updateResetNewPassword,
                onResetConfirmPasswordChange = viewModel::updateResetConfirmPassword,
                onTogglePasswordPreview = viewModel::toggleResetPasswordPreview,
                onSendResetLinkClick = viewModel::submitResetPassword,
                onLoginClick = { viewModel.navigateTo(ArenaScreen.LOGIN) }
            )
        }

        ArenaScreen.REGISTRATION_COMPLETE -> {
            RegistrationCompleteScreen(
                uiState = uiState,
                onBackClick = { viewModel.navigateBack() },
                onSignOutClick = {
                    GoogleAuthHelper.signOut(
                        credentialManager = credentialManager,
                        onSignOutComplete = {
                            viewModel.navigateTo(ArenaScreen.WELCOME, clearHistory = true)
                        },
                        scope = scope
                    )
                },
                onEnterDashboardClick = {
                    viewModel.navigateTo(ArenaScreen.MAIN_DASHBOARD)
                }
            )
        }

        ArenaScreen.MAIN_DASHBOARD -> {
            ArenaMainDashboardScreen(
                onboardingState = uiState,
                onBackToOnboardingClick = {
                    viewModel.navigateTo(ArenaScreen.WELCOME)
                },
                onSignOutClick = {
                    GoogleAuthHelper.signOut(
                        credentialManager = credentialManager,
                        onSignOutComplete = {
                            viewModel.navigateTo(ArenaScreen.WELCOME, clearHistory = true)
                        },
                        scope = scope
                    )
                },
                onRequestGoogleSignIn = {
                    launchInteractiveGoogleSignIn {
                        viewModel.setGlobalError(null)
                    }
                }
            )
        }
    }
}
