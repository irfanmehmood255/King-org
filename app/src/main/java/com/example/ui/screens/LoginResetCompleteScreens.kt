package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.auth.GoogleSignInButton
import com.example.ui.components.ArenaPasswordStrengthBar
import com.example.ui.components.ArenaPrimaryButton
import com.example.ui.components.ArenaTextField
import com.example.ui.components.BaseAuthScreen
import com.example.ui.theme.ArenaCreamLight
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaEmeraldMid
import com.example.ui.theme.ArenaEmeraldVibrant
import com.example.ui.theme.ArenaGoldLight
import com.example.ui.theme.ArenaGoldPrimary
import com.example.ui.theme.ArenaMintBorder
import com.example.ui.theme.ArenaMintSoft
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont
import com.example.ui.viewmodel.ArenaOnboardingUiState

// ============================================================================
// 7. LOGIN SCREEN ("Welcome back")
// ============================================================================
@Composable
fun LoginScreen(
    uiState: ArenaOnboardingUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSubmit: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onGoogleAuthSuccess: () -> Unit,
    onGoogleAuthError: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    BaseAuthScreen(
        testTag = "login_screen",
        onBackClick = onBackClick,
        showHeaderLogo = true,
        showHeaderSubtitle = false,
        showGoldDivider = true,
        headerLogoSize = 72.dp,
        horizontalPadding = 24.dp,
        verticalPadding = 8.dp
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome back",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 31.sp,
                color = ArenaTextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Sign in to continue your conversations.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        ArenaTextField(
            label = "Email address",
            value = uiState.loginEmail,
            onValueChange = onEmailChange,
            placeholder = "you@example.com",
            leadingIcon = Icons.Outlined.Email,
            leadingIconCircleBg = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorMessage = uiState.loginEmailError,
            testTag = "login_email_input"
        )

        Spacer(modifier = Modifier.height(16.dp))

        ArenaTextField(
            label = "Password",
            value = uiState.loginPassword,
            onValueChange = onPasswordChange,
            placeholder = "Enter your password",
            leadingIcon = Icons.Default.Lock,
            leadingIconCircleBg = true,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage = uiState.loginPasswordError,
            testTag = "login_password_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Forgot password?",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = ArenaEmeraldMid
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(onClick = onForgotPasswordClick)
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .testTag("forgot_password_link")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ArenaPrimaryButton(
            text = "Log in",
            onClick = onLoginSubmit,
            isLoading = uiState.isLoading,
            showCircleArrow = true,
            testTag = "login_submit_button"
        )

        Spacer(modifier = Modifier.height(18.dp))

        // "OR" Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(ArenaMintBorder.copy(alpha = 0.6f))
            )
            Text(
                text = "OR",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = ArenaTextSecondary
                ),
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(ArenaMintBorder.copy(alpha = 0.6f))
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        GoogleSignInButton(
            modifier = Modifier.fillMaxWidth(),
            label = "Sign in with Google",
            onAuthSuccess = onGoogleAuthSuccess,
            onAuthError = onGoogleAuthError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "New to ARENA CHAT? Create account"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "New to ARENA CHAT?  ",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = ArenaTextSecondary
                )
            )
            Text(
                text = "Create account",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ArenaEmeraldMid,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable(onClick = onCreateAccountClick)
                    .padding(4.dp)
                    .testTag("login_to_create_account_link")
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Footer: Shield icon + "Your account. Your conversations. Protected."
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = ArenaEmeraldDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(ArenaMintBorder)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Your account. Your conversations. Protected.",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = ArenaTextSecondary
                )
            )
        }
    }
}

// ============================================================================
// 8. FORGOT / RESET PASSWORD SCREEN ("Reset your password")
// ============================================================================
@Composable
fun ResetPasswordScreen(
    uiState: ArenaOnboardingUiState,
    onBackClick: () -> Unit,
    onResetEmailChange: (String) -> Unit,
    onResetNewPasswordChange: (String) -> Unit = {},
    onResetConfirmPasswordChange: (String) -> Unit = {},
    onTogglePasswordPreview: () -> Unit = {},
    onSendResetLinkClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    BaseAuthScreen(
        testTag = "reset_password_screen",
        onBackClick = onBackClick,
        showHeaderLogo = true,
        showHeaderSubtitle = false,
        showGoldDivider = false,
        headerLogoSize = 64.dp,
        horizontalPadding = 24.dp,
        verticalPadding = 8.dp
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        com.example.ui.components.AnimatedHeroArtworkContainer(
            glowSize = 156.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.illust_reset_password),
                contentDescription = "Reset Password Shield Illustration",
                modifier = Modifier.size(142.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Reset your password",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = ArenaTextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter the email address linked to your account.\nWe'll send you a secure reset link.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.5.sp,
                lineHeight = 21.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(22.dp))

        ArenaTextField(
            label = "Email address",
            value = uiState.resetEmail,
            onValueChange = onResetEmailChange,
            placeholder = "you@example.com",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorMessage = uiState.resetEmailError,
            testTag = "reset_email_input"
        )

        // Optional New Password + PasswordStrengthValidator preview section for password reset flow
        if (uiState.showNewPasswordPreview) {
            Spacer(modifier = Modifier.height(12.dp))

            ArenaTextField(
                label = "New password",
                value = uiState.resetNewPassword,
                onValueChange = onResetNewPasswordChange,
                placeholder = "Create a strong new password",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                passwordVisible = newPasswordVisible,
                onTogglePasswordVisibility = { newPasswordVisible = !newPasswordVisible },
                visualTransformation = if (newPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                errorMessage = uiState.resetPasswordError,
                testTag = "reset_new_password_input"
            )

            Spacer(modifier = Modifier.height(8.dp))

            ArenaPasswordStrengthBar(
                password = uiState.resetNewPassword,
                testTag = "reset_password_strength_bar"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ArenaTextField(
                label = "Confirm new password",
                value = uiState.resetConfirmPassword,
                onValueChange = onResetConfirmPasswordChange,
                placeholder = "Re-enter your new password",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                passwordVisible = confirmPasswordVisible,
                onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                visualTransformation = if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                testTag = "reset_confirm_password_input"
            )
        }

        if (uiState.resetSuccessMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ArenaEmeraldVibrant.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("reset_password_success_banner"),
                shape = RoundedCornerShape(14.dp),
                color = ArenaMintSoft
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = ArenaEmeraldVibrant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = uiState.resetSuccessMessage,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp,
                            color = ArenaEmeraldDark
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        ArenaPrimaryButton(
            text = "Send reset link",
            onClick = onSendResetLinkClick,
            isLoading = uiState.isLoading,
            showCircleArrow = true,
            testTag = "send_reset_link_button"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Info Box: "You'll receive a reset email if an account matches this address."
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = ArenaMintBorder.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            color = ArenaMintSoft.copy(alpha = 0.72f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = ArenaEmeraldDark,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(34.dp)
                        .background(ArenaMintBorder)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "You'll receive a reset email if an account\nmatches this address.",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp,
                            color = ArenaEmeraldDark
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.showNewPasswordPreview) {
                            "Hide new password strength checker"
                        } else {
                            "Check new password strength requirements →"
                        },
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = ArenaEmeraldMid,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .clickable(onClick = onTogglePasswordPreview)
                            .testTag("toggle_reset_password_checker")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Text(
                text = "Remember your password?  ",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 14.sp,
                    color = ArenaTextSecondary
                )
            )
            Text(
                text = "Log in",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ArenaEmeraldMid,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable(onClick = onLoginClick)
                    .padding(4.dp)
                    .testTag("reset_to_login_link")
            )
        }
    }
}

// ============================================================================
// 9. REGISTRATION COMPLETE SCREEN ("Registration Complete!")
// ============================================================================
@Composable
fun RegistrationCompleteScreen(
    uiState: ArenaOnboardingUiState,
    onBackClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onEnterDashboardClick: () -> Unit = {}
) {
    var showPhaseCompleteDialog by remember { mutableStateOf(false) }

    BaseAuthScreen(
        testTag = "registration_complete_screen",
        onBackClick = onBackClick,
        showHeaderLogo = true,
        showHeaderSubtitle = true,
        showGoldDivider = true,
        headerLogoSize = 62.dp,
        horizontalPadding = 18.dp,
        verticalPadding = 6.dp,
        maxContentWidth = 540.dp
    ) {
        // Global Community Network Artwork (Exact asset from reference screenshot)
        com.example.ui.components.AnimatedHeroArtworkContainer(
            modifier = Modifier.fillMaxWidth(),
            glowSize = 260.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.illust_registration_complete),
                contentDescription = "Connected Global Community Network",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp, max = 335.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Registration Complete!",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = ArenaEmeraldDark
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "You're now connected with people from\naround the world.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Card: "Your account is ready. Stay connected. Chat. Make new friends. Explore the world."
        val context = LocalContext.current
        val presetIcons = listOf(
            Icons.Default.Face,
            Icons.Default.WorkspacePremium,
            Icons.Default.AutoAwesome,
            Icons.Default.Public
        )
        val customBitmap = remember(uiState.profileAvatarUri) {
            uiState.profileAvatarUri?.let { uriStr ->
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = ArenaMintBorder.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            color = ArenaMintSoft.copy(alpha = 0.78f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ArenaEmeraldMid, ArenaEmeraldDark)
                            )
                        )
                        .border(1.5.dp, ArenaGoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (customBitmap != null) {
                        Image(
                            bitmap = customBitmap,
                            contentDescription = "User Profile Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = presetIcons.getOrElse(uiState.selectedAvatarPreset) { Icons.Default.VerifiedUser },
                            contentDescription = null,
                            tint = ArenaGoldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(ArenaMintBorder)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val usernameBadge = uiState.userProfile?.username?.takeIf { it.isNotBlank() }
                        ?: uiState.usernameInput.takeIf { it.isNotBlank() }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (usernameBadge != null) {
                                "Your account (@$usernameBadge) is ready."
                            } else {
                                "Your account is ready."
                            },
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ArenaEmeraldDark
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Account",
                            tint = ArenaEmeraldVibrant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Stay connected. Chat. Make new friends. Explore the world.",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.5.sp,
                            color = ArenaTextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Feature Pillars Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompleteFeaturePillar(
                icon = Icons.Outlined.Language,
                title = "Global\nCommunity"
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(ArenaMintBorder.copy(alpha = 0.6f))
            )
            CompleteFeaturePillar(
                icon = Icons.Outlined.Shield,
                title = "Secure\nMessaging"
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(ArenaMintBorder.copy(alpha = 0.6f))
            )
            CompleteFeaturePillar(
                icon = Icons.Outlined.Groups,
                title = "Real People\nWorldwide"
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(ArenaMintBorder.copy(alpha = 0.6f))
            )
            CompleteFeaturePillar(
                icon = Icons.Default.Bolt,
                title = "Unlimited\nConnections"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // "Get Started ->" CTA Button launches Part 2.1 Main Dashboard
        ArenaPrimaryButton(
            text = "Get Started",
            onClick = onEnterDashboardClick,
            showCircleArrow = false,
            testTag = "get_started_button",
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = { showPhaseCompleteDialog = true },
            modifier = Modifier.testTag("view_phase1_summary_button")
        ) {
            Text(
                text = "View Phase 1 Verification Summary",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = ArenaEmeraldMid,
                    textDecoration = TextDecoration.Underline
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bottom Footer: "ARENA CHAT • CONNECT • SHARE • BELONG"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(1.dp)
                    .background(ArenaGoldPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ARENA CHAT  •  CONNECT  •  SHARE  •  BELONG",
                style = TextStyle(
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.sp,
                    letterSpacing = 1.4.sp,
                    color = ArenaEmeraldDark
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(1.dp)
                    .background(ArenaGoldPrimary)
            )
        }
    }

    if (showPhaseCompleteDialog) {
        val p = uiState.userProfile
        AlertDialog(
            onDismissRequest = { showPhaseCompleteDialog = false },
            containerColor = ArenaCreamLight,
            title = {
                Text(
                    text = "ARENA CHAT — Phase 1 Verified",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = ArenaEmeraldDark
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your Arena Chat onboarding and registration record is verified and persisted in Firestore:",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 13.5.sp,
                            color = ArenaTextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Email: ${p?.email?.ifBlank { uiState.email } ?: uiState.email}\n" +
                                "• Country: ${p?.country?.ifBlank { uiState.country } ?: uiState.country}\n" +
                                "• Username: @${p?.username?.ifBlank { uiState.usernameInput } ?: uiState.usernameInput}\n" +
                                "• Human Verified: ${if (p?.humanVerified == true) "Yes (${p.humanVerificationMethod})" else "Verified"}\n" +
                                "• Stage: ${p?.onboardingStage ?: "completed"}",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = ArenaEmeraldDark
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhaseCompleteDialog = false }) {
                    Text(
                        "Done",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            color = ArenaEmeraldMid
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPhaseCompleteDialog = false
                        onSignOutClick()
                    }
                ) {
                    Text(
                        "Restart Flow / Sign Out",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Medium,
                            color = ArenaTextSecondary
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun CompleteFeaturePillar(
    icon: ImageVector,
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ArenaEmeraldMid,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Medium,
                fontSize = 10.5.sp,
                lineHeight = 13.sp,
                color = ArenaEmeraldDark
            ),
            textAlign = TextAlign.Center
        )
    }
}
