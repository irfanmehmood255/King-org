package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.auth.GoogleSignInButton
import com.example.ui.components.ArenaGoldShieldDividerFooter
import com.example.ui.components.ArenaPasswordStrengthBar
import com.example.ui.components.ArenaPrimaryButton
import com.example.ui.components.ArenaTextField
import com.example.ui.components.ArenaWaveBackground
import com.example.ui.components.BaseAuthScreen
import com.example.ui.theme.ArenaCreamLight
import com.example.ui.theme.ArenaEmeraldBright
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaEmeraldMid
import com.example.ui.theme.ArenaEmeraldVibrant
import com.example.ui.theme.ArenaError
import com.example.ui.theme.ArenaGoldDark
import com.example.ui.theme.ArenaGoldPrimary
import com.example.ui.theme.ArenaInputBg
import com.example.ui.theme.ArenaMintBorder
import com.example.ui.theme.ArenaTextPlaceholder
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont
import com.example.ui.viewmodel.ArenaOnboardingUiState

// ============================================================================
// 1. SPLASH SCREEN
// ============================================================================
@Composable
fun SplashScreen(
    onManualSkip: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_loader")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    ArenaWaveBackground(showGoldAccents = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .clickable(onClick = onManualSkip)
                .testTag("splash_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.95f))

            Image(
                painter = painterResource(id = R.drawable.arena_logo_splash),
                contentDescription = "Arena Chat Splash Logo",
                modifier = Modifier
                    .size(164.dp)
                    .clip(CircleShape)
                    .testTag("splash_logo"),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "ARENA CHAT",
                style = TextStyle(
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    letterSpacing = 1.5.sp,
                    color = ArenaEmeraldDark
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Connect.  Share.  Belong.",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = ArenaEmeraldVibrant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.65f))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .testTag("splash_loading_spinner"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFFD2EFE4),
                                ArenaEmeraldBright,
                                ArenaEmeraldMid,
                                ArenaEmeraldDark
                            )
                        ),
                        startAngle = angle,
                        sweepAngle = 310f,
                        useCenter = false,
                        style = Stroke(width = 5.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Connecting your world...",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.5.sp,
                    color = ArenaEmeraldMid
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.9f))
        }
    }
}

// ============================================================================
// 2. WELCOME / CREATE ACCOUNT LANDING SCREEN
// ============================================================================
@Composable
fun WelcomeLandingScreen(
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleAuthSuccess: () -> Unit,
    onGoogleAuthError: (String) -> Unit,
    onOpenDashboardClick: () -> Unit = {}
) {
    var showPolicyDialog by remember { mutableStateOf<String?>(null) }

    BaseAuthScreen(
        testTag = "welcome_screen",
        showHeaderLogo = false,
        showGoldWaveAccents = true,
        horizontalPadding = 20.dp,
        verticalPadding = 8.dp
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.arena_logo_splash),
            contentDescription = "Arena Chat Logo",
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "ARENA CHAT",
            style = TextStyle(
                fontFamily = MontserratFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                letterSpacing = 1.6.sp,
                color = ArenaEmeraldDark
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Your World.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 31.sp,
                color = ArenaEmeraldDark
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 31.sp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                ArenaEmeraldDark,
                                ArenaEmeraldMid,
                                ArenaGoldDark,
                                ArenaGoldPrimary
                            )
                        )
                    )
                ) {
                    append("Your Conversations.")
                }
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Private conversations. Global connections.\nOne powerful place to belong.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        com.example.ui.components.AnimatedHeroArtworkContainer(
            modifier = Modifier.fillMaxWidth(),
            glowSize = 270.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_welcome_globe),
                contentDescription = "Connect with Amazing People across the Globe",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 270.dp, max = 365.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ArenaPrimaryButton(
            text = "Create account",
            onClick = onCreateAccountClick,
            showCircleArrow = true,
            testTag = "welcome_create_account_button",
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoogleSignInButton(
                modifier = Modifier.weight(1f),
                label = "Sign in with Google",
                onAuthSuccess = onGoogleAuthSuccess,
                onAuthError = onGoogleAuthError
            )
            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.testTag("welcome_login_link")
            ) {
                Text(
                    text = "Log in →",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ArenaEmeraldDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(
            onClick = onOpenDashboardClick,
            modifier = Modifier.testTag("welcome_open_dashboard_button")
        ) {
            Text(
                text = "Open Main Dashboard (Part 2.1) →",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ArenaEmeraldMid,
                    textDecoration = TextDecoration.Underline
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = buildAnnotatedString {
                append("By continuing, you agree to our ")
                withStyle(
                    SpanStyle(
                        color = ArenaEmeraldDark,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Terms of Service")
                }
                append(" and\nacknowledge our ")
                withStyle(
                    SpanStyle(
                        color = ArenaEmeraldDark,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Privacy Policy")
                }
                append(".")
            },
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable { showPolicyDialog = "Terms of Service & Privacy Policy" }
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ArenaGoldShieldDividerFooter(
            text = "Secure connections. Built for everyone.",
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (showPolicyDialog != null) {
        PolicyInfoDialog(
            title = showPolicyDialog!!,
            onDismiss = { showPolicyDialog = null }
        )
    }
}

// ============================================================================
// 3. REGISTRATION FORM SCREEN ("Create your account")
// ============================================================================
@Composable
fun RegistrationScreen(
    uiState: ArenaOnboardingUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onTermsChange: (Boolean) -> Unit,
    passwordStrength: Int,
    onCreateAccountSubmit: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleAuthSuccess: () -> Unit,
    onGoogleAuthError: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var countryDropdownExpanded by remember { mutableStateOf(false) }
    var showPolicyDialog by remember { mutableStateOf<String?>(null) }

    val countries = remember {
        listOf(
            "Pakistan",
            "United States",
            "Saudi Arabia",
            "United Kingdom",
            "United Arab Emirates",
            "Canada",
            "Germany",
            "France",
            "China",
            "Japan",
            "Australia",
            "Brazil",
            "South Korea",
            "India"
        )
    }

    BaseAuthScreen(
        testTag = "registration_screen",
        onBackClick = onBackClick,
        showHeaderLogo = true,
        showHeaderSubtitle = false,
        showGoldDivider = true,
        headerLogoSize = 64.dp,
        horizontalPadding = 22.dp,
        verticalPadding = 6.dp
    ) {
        Text(
            text = "Create your account",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 27.sp,
                color = ArenaTextPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Join a world of meaningful conversations.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Email address
        ArenaTextField(
            label = "Email address",
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "you@example.com",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorMessage = uiState.emailError,
            testTag = "register_email_input"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        ArenaTextField(
            label = "Password",
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "Create a strong password",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage = uiState.passwordError,
            testTag = "register_password_input"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password 3-segment strength bar powered by PasswordStrengthValidator
        ArenaPasswordStrengthBar(
            password = uiState.password,
            testTag = "register_password_strength_bar"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm password
        ArenaTextField(
            label = "Confirm password",
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Re-enter your password",
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
            errorMessage = uiState.confirmPasswordError,
            testTag = "register_confirm_password_input"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Country or region dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Country or region",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ArenaEmeraldDark
                ),
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(
                            width = 1.2.dp,
                            color = if (uiState.countryError != null) ArenaError else ArenaMintBorder,
                            shape = RoundedCornerShape(26.dp)
                        )
                        .clickable { countryDropdownExpanded = true }
                        .testTag("register_country_selector"),
                    shape = RoundedCornerShape(26.dp),
                    color = ArenaInputBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = null,
                            tint = ArenaEmeraldMid,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(22.dp)
                                .background(ArenaMintBorder.copy(alpha = 0.65f))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.country.ifBlank { "Choose your country" },
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (uiState.country.isBlank()) FontWeight.Normal else FontWeight.Medium,
                                fontSize = 14.5.sp,
                                color = if (uiState.country.isBlank()) ArenaTextPlaceholder else ArenaTextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select country",
                            tint = ArenaEmeraldDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = countryDropdownExpanded,
                    onDismissRequest = { countryDropdownExpanded = false },
                    modifier = Modifier
                        .background(ArenaCreamLight)
                        .width(280.dp)
                ) {
                    countries.forEach { c ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = c,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = ArenaTextPrimary
                                    )
                                )
                            },
                            onClick = {
                                onCountryChange(c)
                                countryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            if (uiState.countryError != null) {
                Text(
                    text = uiState.countryError,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = ArenaError
                    ),
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Terms checkbox row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (uiState.termsAccepted) ArenaEmeraldVibrant else ArenaInputBg)
                    .border(
                        1.5.dp,
                        if (uiState.termsAccepted) ArenaEmeraldVibrant else ArenaMintBorder,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onTermsChange(!uiState.termsAccepted) }
                    .testTag("register_terms_checkbox"),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.termsAccepted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Checked",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = buildAnnotatedString {
                    append("I agree to the ")
                    withStyle(
                        SpanStyle(
                            color = ArenaEmeraldVibrant,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Terms of Service")
                    }
                    append(" and ")
                    withStyle(
                        SpanStyle(
                            color = ArenaEmeraldVibrant,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Privacy Policy")
                    }
                },
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 13.sp,
                    color = ArenaTextPrimary
                ),
                modifier = Modifier.clickable {
                    showPolicyDialog = "Terms of Service & Privacy Policy"
                }
            )
        }

        if (uiState.termsError != null) {
            Text(
                text = uiState.termsError,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = ArenaError
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 4.dp)
            )
        }

        if (uiState.globalErrorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = uiState.globalErrorMessage,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = ArenaError
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Create account button
        ArenaPrimaryButton(
            text = "Create account",
            onClick = onCreateAccountSubmit,
            isLoading = uiState.isLoading,
            showCircleArrow = false,
            testTag = "register_submit_button"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Google Sign-In fallback button
        GoogleSignInButton(
            modifier = Modifier.fillMaxWidth(),
            label = if (uiState.isAuthenticatedWithGoogle) {
                "Google Account Linked ✓"
            } else {
                "Sign in with Google to Link Account"
            },
            onAuthSuccess = onGoogleAuthSuccess,
            onAuthError = onGoogleAuthError
        )

        Spacer(modifier = Modifier.height(14.dp))

        // "Already have an account?" divider row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(1.dp)
                    .background(ArenaGoldPrimary.copy(alpha = 0.55f))
            )
            Text(
                text = "Already have an account?",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.5.sp,
                    color = ArenaTextSecondary
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(1.dp)
                    .background(ArenaGoldPrimary.copy(alpha = 0.55f))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Gold-bordered pill "Log in ->" button
        Surface(
            modifier = Modifier
                .width(142.dp)
                .height(40.dp)
                .clickable(onClick = onLoginClick)
                .testTag("register_to_login_button"),
            shape = RoundedCornerShape(20.dp),
            color = ArenaCreamLight,
            border = BorderStroke(1.2.dp, ArenaGoldPrimary)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Log in",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ArenaEmeraldDark
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = ArenaEmeraldDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Security Note
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = ArenaEmeraldMid,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(14.dp)
                    .background(ArenaGoldPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Secure sign-up  •  Your information stays protected",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp,
                    color = ArenaTextSecondary
                )
            )
        }
    }

    if (showPolicyDialog != null) {
        PolicyInfoDialog(
            title = showPolicyDialog!!,
            onDismiss = { showPolicyDialog = null }
        )
    }
}

@Composable
fun PolicyInfoDialog(
    title: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaCreamLight,
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ArenaEmeraldDark
                )
            )
        },
        text = {
            Text(
                text = "Arena Chat protects your account with end-to-end security rules, zero-trust Firestore isolation, and verified human onboarding. Your email address and country are stored privately under your authenticated account and never shared publicly without your consent.",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = ArenaTextSecondary
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Got it",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        color = ArenaEmeraldMid
                    )
                )
            }
        }
    )
}
