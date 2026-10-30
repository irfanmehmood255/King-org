package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaEmeraldVibrant
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.PlusJakartaSansFont
import com.example.util.PasswordStrengthValidator

/**
 * Reusable BaseAuthScreen component providing the consistent soft-cream/white background,
 * layered emerald & gold wave decorations, safe-area & keyboard (IME) padding,
 * responsive max-width container, optional Arena Chat header logo with back navigation,
 * and scrollable layout structure for all registration and login screens.
 */
@Composable
fun BaseAuthScreen(
    testTag: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    showHeaderLogo: Boolean = true,
    showHeaderSubtitle: Boolean = false,
    showGoldDivider: Boolean = true,
    showGoldWaveAccents: Boolean = true,
    headerLogoSize: Dp = 64.dp,
    horizontalPadding: Dp = 22.dp,
    verticalPadding: Dp = 8.dp,
    maxContentWidth: Dp = 520.dp,
    scrollable: Boolean = true,
    overlayContent: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onBackClick != null) {
        BackHandler(onBack = onBackClick)
    }

    ArenaWaveBackground(
        modifier = modifier,
        showGoldAccents = showGoldWaveAccents
    ) {
        val baseColumnModifier = Modifier
            .fillMaxSize()
            .imePadding()
            .then(
                if (scrollable) {
                    Modifier.verticalScroll(rememberScrollState())
                } else {
                    Modifier
                }
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .widthIn(max = maxContentWidth)
            .align(Alignment.TopCenter)
            .testTag(testTag)

        Column(
            modifier = baseColumnModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showHeaderLogo) {
                ArenaHeaderLogo(
                    onBackClick = onBackClick,
                    showSubtitle = showHeaderSubtitle,
                    showGoldDivider = showGoldDivider,
                    logoSize = headerLogoSize
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            content()
        }

        overlayContent?.invoke(this)
    }
}

/**
 * Shared 3-segment password strength indicator bar driven by [PasswordStrengthValidator].
 * Matches the Registration reference screenshot ("Use at least 8 characters" with 3 pill segments)
 * while providing live feedback on minimum length, numbers, and special characters.
 */
@Composable
fun ArenaPasswordStrengthBar(
    password: String,
    modifier: Modifier = Modifier,
    testTag: String = "password_strength_bar"
) {
    val validation = PasswordStrengthValidator.evaluate(password)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (seg in 1..3) {
                    val active = validation.strengthScore >= seg || (seg == 1 && password.isEmpty())
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (active) ArenaEmeraldVibrant else Color(0xFFD5E2DC)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (password.isEmpty()) {
                    "Use at least 8 characters"
                } else {
                    validation.strengthLevel.label
                },
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp,
                    color = if (validation.isValidForSubmission) ArenaEmeraldDark else ArenaTextSecondary
                )
            )
        }

        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PasswordRuleChip(label = "8+ chars", met = validation.hasMinLength)
                PasswordRuleChip(label = "Number (0-9)", met = validation.hasNumber)
                PasswordRuleChip(label = "Special (!@#)", met = validation.hasSpecialChar)
            }
        }
    }
}

@Composable
private fun PasswordRuleChip(
    label: String,
    met: Boolean
) {
    Text(
        text = if (met) "✓ $label" else "• $label",
        style = TextStyle(
            fontFamily = PlusJakartaSansFont,
            fontWeight = if (met) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 10.5.sp,
            color = if (met) ArenaEmeraldVibrant else ArenaTextSecondary
        )
    )
}
