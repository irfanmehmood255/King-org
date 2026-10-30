package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AnimatedHeroArtworkContainer
import com.example.ui.components.ArenaGoldShieldDividerFooter
import com.example.ui.components.ArenaPrimaryButton
import com.example.ui.components.ArenaTextField
import com.example.ui.components.BaseAuthScreen
import com.example.ui.components.InternationalBiometricScannerModal
import com.example.ui.components.LiveFaceScanCardOverlay
import com.example.ui.components.LiveFingerprintScanCardOverlay
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaEmeraldDeep
import com.example.ui.theme.ArenaEmeraldMid
import com.example.ui.theme.ArenaEmeraldVibrant
import com.example.ui.theme.ArenaError
import com.example.ui.theme.ArenaGoldDark
import com.example.ui.theme.ArenaGoldLight
import com.example.ui.theme.ArenaGoldPrimary
import com.example.ui.theme.ArenaInputBg
import com.example.ui.theme.ArenaMintBorder
import com.example.ui.theme.ArenaMintSoft
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont
import com.example.ui.viewmodel.ArenaOnboardingUiState
import java.util.Locale

// ============================================================================
// 4. EMAIL VERIFICATION SCREEN ("Verify your email")
// ============================================================================
@Composable
fun EmailVerificationScreen(
    uiState: ArenaOnboardingUiState,
    onBackClick: () -> Unit,
    onDigitChange: (Int, String) -> Unit,
    onAutofillCode: () -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val minutes = uiState.otpRemainingSeconds / 60
    val seconds = uiState.otpRemainingSeconds % 60
    val formattedTimer = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    BaseAuthScreen(
        testTag = "email_verification_screen",
        onBackClick = onBackClick,
        showHeaderLogo = true,
        showHeaderSubtitle = false,
        showGoldDivider = true,
        headerLogoSize = 64.dp,
        horizontalPadding = 22.dp,
        verticalPadding = 8.dp
    ) {
        AnimatedHeroArtworkContainer(
            glowSize = 152.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.illust_verify_email),
                contentDescription = "Verify Email Shield Illustration",
                modifier = Modifier.size(136.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 29.sp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                ArenaEmeraldDeep,
                                ArenaGoldDark,
                                ArenaEmeraldDark,
                                ArenaEmeraldVibrant
                            )
                        )
                    )
                ) {
                    append("Verify your email")
                }
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter the 6-digit code sent to your\nemail address.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Interactive helper banner showing the active session's verification code + autofill button
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onAutofillCode)
                .testTag("autofill_otp_banner"),
            color = ArenaMintSoft,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Active verification code: ${uiState.generatedOtpCode} (Tap to auto-fill)",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = ArenaEmeraldDark
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 6 OTP Digit Boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            for (index in 0..5) {
                val digit = uiState.verificationDigits.getOrElse(index) { "" }
                Surface(
                    modifier = Modifier
                        .size(width = 48.dp, height = 56.dp)
                        .border(
                            width = 1.3.dp,
                            color = if (digit.isNotEmpty()) ArenaEmeraldVibrant else ArenaMintBorder,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    shape = RoundedCornerShape(14.dp),
                    color = ArenaInputBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (digit.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .width(1.5.dp)
                                    .height(20.dp)
                                    .background(ArenaTextSecondary.copy(alpha = 0.55f))
                            )
                        }
                        BasicTextField(
                            value = digit,
                            onValueChange = { newVal ->
                                onDigitChange(index, newVal)
                                if (newVal.isNotEmpty() && index < 5) {
                                    runCatching { focusRequesters[index + 1].requestFocus() }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            textStyle = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = ArenaEmeraldDark,
                                textAlign = TextAlign.Center
                            ),
                            cursorBrush = SolidColor(ArenaEmeraldMid),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequesters[index])
                                .testTag("otp_digit_$index")
                        )
                    }
                }
            }
        }

        if (uiState.emailVerifyError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.emailVerifyError,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = ArenaError
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Timer Row: "Code expires in 04:32"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = ArenaEmeraldMid,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Code expires in $formattedTimer",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ArenaEmeraldDark
                )
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        ArenaPrimaryButton(
            text = "Verify email",
            onClick = onVerifyClick,
            isLoading = uiState.isLoading,
            showCircleArrow = true,
            testTag = "verify_email_submit_button"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "Didn't receive the code? Resend code"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Didn't receive the code?  ",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 13.5.sp,
                    color = ArenaTextSecondary
                )
            )
            Text(
                text = "Resend code",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = ArenaEmeraldMid,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable(onClick = onResendClick)
                    .padding(4.dp)
                    .testTag("resend_code_button")
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Security Info Box: "Your email helps keep your account secure."
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = ArenaMintBorder.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            color = ArenaMintSoft.copy(alpha = 0.7f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD3ECE2))
                        .border(1.dp, ArenaMintBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = ArenaEmeraldDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(34.dp)
                        .background(ArenaMintBorder)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Your email helps keep your\naccount secure.",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = ArenaEmeraldDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ============================================================================
// 5. HUMAN VERIFICATION SCREEN ("Human Verification")
// ============================================================================
@Composable
fun HumanVerificationScreen(
    uiState: ArenaOnboardingUiState,
    onBackClick: () -> Unit,
    onPolicyAcceptedChange: (Boolean) -> Unit,
    onStartChallenge: (String) -> Unit,
    onDismissChallenge: () -> Unit,
    onCompleteChallenge: (String) -> Unit
) {
    var showPolicyDialog by remember { mutableStateOf(false) }

    BaseAuthScreen(
        testTag = "human_verification_screen",
        onBackClick = onBackClick,
        showHeaderLogo = true,
        showHeaderSubtitle = true,
        showGoldDivider = true,
        headerLogoSize = 62.dp,
        horizontalPadding = 16.dp,
        verticalPadding = 6.dp,
        maxContentWidth = 540.dp
    ) {
        // Shield badge + Heading + Subtext
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.badge_human_shield),
                contentDescription = "Human Verification Shield",
                modifier = Modifier.size(58.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Human Verification",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 25.sp,
                        color = ArenaEmeraldDark
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "We need to confirm you're a real person\nto keep our community safe and secure.",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        color = ArenaTextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3 Trust Badges Row: Stop Bots | Real People | Safer Community
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrustFeatureBadge(icon = Icons.Outlined.Shield, text = "Stop Bots")
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(ArenaMintBorder)
            )
            TrustFeatureBadge(icon = Icons.Outlined.Groups, text = "Real People")
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(ArenaMintBorder)
            )
            TrustFeatureBadge(icon = Icons.Outlined.Lock, text = "Safer Community")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Two Side-by-Side Verification Cards with live 60fps Biometric Laser & Ultrasonic HUD Overlays
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HumanVerificationCard(
                modifier = Modifier.weight(1f),
                topImageRes = R.drawable.illust_face_scan,
                badgeIcon = Icons.Default.CropFree,
                title = "Face Verification",
                description = "Look at the camera and follow the simple steps to verify your identity.",
                buttonText = "Use Face Scan",
                testTag = "use_face_scan_button",
                isFaceScan = true,
                onClick = { onStartChallenge("face_scan") }
            )

            HumanVerificationCard(
                modifier = Modifier.weight(1f),
                topImageRes = R.drawable.illust_fingerprint_scan,
                badgeIcon = Icons.Default.Fingerprint,
                title = "Fingerprint Scan",
                description = "Place your finger on the sensor to verify your identity.",
                buttonText = "Use Fingerprint",
                testTag = "use_fingerprint_button",
                isFaceScan = false,
                onClick = { onStartChallenge("fingerprint_scan") }
            )
        }

        if (uiState.humanVerifyError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.humanVerifyError,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = ArenaError
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Policy Consent Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = ArenaMintBorder.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp)
                ),
            shape = RoundedCornerShape(14.dp),
            color = ArenaMintSoft.copy(alpha = 0.75f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = ArenaEmeraldDark,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(ArenaMintBorder)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (uiState.humanPolicyAccepted) ArenaEmeraldDark else ArenaInputBg)
                        .border(1.2.dp, ArenaEmeraldDark, RoundedCornerShape(5.dp))
                        .clickable { onPolicyAcceptedChange(!uiState.humanPolicyAccepted) }
                        .testTag("human_policy_checkbox"),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.humanPolicyAccepted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Accepted",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Please read our Human Verification ")
                        withStyle(
                            SpanStyle(
                                color = ArenaEmeraldDark,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Privacy Policy")
                        }
                        append(",\n")
                        withStyle(
                            SpanStyle(
                                color = ArenaEmeraldDark,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Terms and Conditions")
                        }
                        append(" before continuing.")
                    },
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = ArenaTextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showPolicyDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Footer: Shield icon + "REAL PEOPLE  •  REAL CONVERSATIONS"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(1.dp)
                        .background(ArenaGoldPrimary.copy(alpha = 0.7f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = ArenaGoldDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(1.dp)
                        .background(ArenaGoldPrimary.copy(alpha = 0.7f))
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "REAL PEOPLE   •   REAL CONVERSATIONS",
                style = TextStyle(
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    letterSpacing = 1.8.sp,
                    color = ArenaEmeraldDark
                )
            )
        }
    }

    if (uiState.activeVerificationModal != null) {
        InternationalBiometricScannerModal(
            method = uiState.activeVerificationModal,
            isLoading = uiState.isLoading,
            onDismiss = onDismissChallenge,
            onVerified = { onCompleteChallenge(uiState.activeVerificationModal) }
        )
    }

    if (showPolicyDialog) {
        PolicyInfoDialog(
            title = "Human Verification Privacy Policy & Terms",
            onDismiss = { showPolicyDialog = false }
        )
    }
}

@Composable
private fun TrustFeatureBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ArenaEmeraldMid,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = ArenaEmeraldDark
            )
        )
    }
}

@Composable
private fun HumanVerificationCard(
    modifier: Modifier = Modifier,
    topImageRes: Int,
    badgeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    buttonText: String,
    testTag: String,
    isFaceScan: Boolean,
    onClick: () -> Unit
) {
    val goldBorder = Brush.verticalGradient(
        colors = listOf(ArenaGoldLight, ArenaGoldPrimary, ArenaGoldDark, ArenaGoldLight)
    )

    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ArenaEmeraldMid,
                        ArenaEmeraldDark,
                        ArenaEmeraldDeep
                    )
                )
            )
            .border(1.8.dp, goldBorder, RoundedCornerShape(22.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
        ) {
            Image(
                painter = painterResource(id = topImageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Live international-standard biometric HUD animation overlay
            if (isFaceScan) {
                LiveFaceScanCardOverlay()
            } else {
                LiveFingerprintScanCardOverlay()
            }
        }

        // Overlapping Gold Circular Icon Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ArenaEmeraldDark)
                    .border(2.dp, ArenaGoldLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-12).dp)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFFD6EFE5)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.height(44.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gold-rimmed inner pill button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ArenaEmeraldDeep, ArenaEmeraldVibrant, ArenaEmeraldDark)
                        )
                    )
                    .border(1.4.dp, goldBorder, RoundedCornerShape(20.dp))
                    .clickable(onClick = onClick)
                    .padding(horizontal = 10.dp)
                    .testTag(testTag),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = buttonText,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 6. SET YOUR USERNAME SCREEN ("Set Your Username")
// ============================================================================
@Composable
fun SetUsernameScreen(
    uiState: ArenaOnboardingUiState,
    onBackClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onAvatarUriChange: (String?) -> Unit = {},
    onAvatarPresetChange: (Int) -> Unit = {},
    onContinueClick: () -> Unit
) {
    var showPolicyDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onAvatarUriChange(uri.toString())
        }
    }

    BaseAuthScreen(
        testTag = "set_username_screen",
        onBackClick = onBackClick,
        showHeaderLogo = true,
        showHeaderSubtitle = false,
        showGoldDivider = false,
        headerLogoSize = 64.dp,
        horizontalPadding = 22.dp,
        verticalPadding = 8.dp
    ) {
        AnimatedHeroArtworkContainer(
            modifier = Modifier.fillMaxWidth(),
            glowSize = 155.dp
        ) {
            Image(
                painter = painterResource(id = R.drawable.illust_set_username),
                contentDescription = "Set Your Username Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 29.sp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                ArenaEmeraldDeep,
                                ArenaEmeraldDark,
                                ArenaEmeraldMid,
                                ArenaGoldDark
                            )
                        )
                    )
                ) {
                    append("Set Your Username")
                }
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose a unique username to identify yourself\non Arena Chat. Make it simple, memorable\nand professional.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        ArenaTextField(
            label = "Username",
            value = uiState.usernameInput,
            onValueChange = onUsernameChange,
            placeholder = "e.g. john_doe",
            leadingIcon = Icons.Outlined.Person,
            trailingText = "${uiState.usernameInput.length}/20",
            errorMessage = uiState.usernameError,
            testTag = "username_input_field"
        )

        // Real-time Firestore Availability Status Indicator while typing
        if (uiState.isCheckingUsername || uiState.usernameAvailableMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("username_availability_status"),
                shape = RoundedCornerShape(12.dp),
                color = when {
                    uiState.isUsernameAvailable == true -> ArenaMintSoft
                    else -> ArenaMintSoft.copy(alpha = 0.55f)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.isCheckingUsername) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = ArenaEmeraldMid,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (uiState.isUsernameAvailable == true) {
                                Icons.Default.Check
                            } else {
                                Icons.Outlined.Info
                            },
                            contentDescription = null,
                            tint = if (uiState.isUsernameAvailable == true) {
                                ArenaEmeraldVibrant
                            } else {
                                ArenaEmeraldDark
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.usernameAvailableMessage ?: "Checking availability...",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (uiState.isUsernameAvailable == true) {
                                ArenaEmeraldVibrant
                            } else {
                                ArenaEmeraldDark
                            }
                        )
                    )
                }
            }
        }

        // Quick-select unique handle suggestions
        val emailHandle = remember(uiState.email) {
            uiState.email.substringBefore("@")
                .replace(Regex("[^a-zA-Z0-9_]"), "_")
                .take(14)
                .ifBlank { "arena_member" }
        }
        val suggestions = remember(emailHandle) {
            listOf(
                emailHandle.takeIf { it.length >= 3 } ?: "arena_pro",
                "${emailHandle}_01",
                "arena_${(100..999).random()}"
            ).distinct()
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Suggestions:",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp,
                    color = ArenaTextSecondary
                )
            )
            suggestions.forEachIndexed { idx, suggestion ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, ArenaMintBorder, RoundedCornerShape(12.dp))
                        .clickable { onUsernameChange(suggestion) }
                        .testTag("username_suggestion_$idx"),
                    shape = RoundedCornerShape(12.dp),
                    color = ArenaInputBg
                ) {
                    Text(
                        text = "@$suggestion",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = ArenaEmeraldDark
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Profile Picture & Avatar Selector (Gallery Photo Picker + Curated Emerald/Gold Presets)
        ProfileAvatarSelectorBar(
            context = context,
            profileAvatarUri = uiState.profileAvatarUri,
            selectedPreset = uiState.selectedAvatarPreset,
            onPickPhotoClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onSelectPreset = onAvatarPresetChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Info Box: "Your username can contain letters, numbers and underscores (_). Min. 3 characters | Max. 20 characters"
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = ArenaMintBorder.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
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
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(42.dp)
                        .background(ArenaMintBorder)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Your username can contain letters,\nnumbers and underscores ( _ ).",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp,
                            color = ArenaEmeraldDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Min. 3 characters   |   Max. 20 characters",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = ArenaEmeraldVibrant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        ArenaPrimaryButton(
            text = "Continue",
            onClick = onContinueClick,
            isLoading = uiState.isLoading,
            showCircleArrow = true,
            testTag = "set_username_continue_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

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
                    append("Read username Privacy Policy")
                }
                append(".")
            },
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontSize = 12.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable { showPolicyDialog = true }
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ArenaGoldShieldDividerFooter(
            text = "Secure connections. Built for everyone.",
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    if (showPolicyDialog) {
        PolicyInfoDialog(
            title = "Username Privacy Policy",
            onDismiss = { showPolicyDialog = false }
        )
    }
}

@Composable
private fun ProfileAvatarSelectorBar(
    context: android.content.Context,
    profileAvatarUri: String?,
    selectedPreset: Int,
    onPickPhotoClick: () -> Unit,
    onSelectPreset: (Int) -> Unit
) {
    val presetIcons = listOf(
        Icons.Default.Face,
        Icons.Default.WorkspacePremium,
        Icons.Default.AutoAwesome,
        Icons.Default.Public
    )
    val customBitmap = remember(profileAvatarUri) {
        profileAvatarUri?.let { uriStr ->
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
            .border(1.dp, ArenaMintBorder.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
            .testTag("profile_avatar_selector_card"),
        shape = RoundedCornerShape(16.dp),
        color = ArenaInputBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ArenaEmeraldMid, ArenaEmeraldDark)
                            )
                        )
                        .border(2.dp, ArenaGoldPrimary, CircleShape)
                        .clickable(onClick = onPickPhotoClick)
                        .testTag("pick_profile_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (customBitmap != null) {
                        Image(
                            bitmap = customBitmap,
                            contentDescription = "Selected Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = presetIcons.getOrElse(selectedPreset) { Icons.Default.Face },
                            contentDescription = "Profile Avatar",
                            tint = ArenaGoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Profile Picture",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ArenaEmeraldDark
                        )
                    )
                    Text(
                        text = if (profileAvatarUri != null) "Custom photo selected" else "Tap photo or choose badge",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = ArenaTextSecondary
                        )
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                presetIcons.forEachIndexed { index, icon ->
                    val isSelected = profileAvatarUri == null && selectedPreset == index
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ArenaEmeraldDark else ArenaMintSoft
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ArenaGoldPrimary else ArenaMintBorder,
                                shape = CircleShape
                            )
                            .clickable { onSelectPreset(index) }
                            .testTag("avatar_preset_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Avatar Preset ${index + 1}",
                            tint = if (isSelected) ArenaGoldLight else ArenaEmeraldDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ArenaEmeraldVibrant.copy(alpha = 0.14f))
                        .border(1.dp, ArenaEmeraldVibrant, CircleShape)
                        .clickable(onClick = onPickPhotoClick)
                        .testTag("upload_gallery_photo_icon"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Upload Photo from Gallery",
                        tint = ArenaEmeraldDark,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

