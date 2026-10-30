package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ArenaCreamBg
import com.example.ui.theme.ArenaCreamLight
import com.example.ui.theme.ArenaEmeraldBright
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
import com.example.ui.theme.ArenaTextPlaceholder
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont

@Composable
fun ArenaWaveBackground(
    modifier: Modifier = Modifier,
    showGoldAccents: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ArenaCreamLight,
                        ArenaCreamBg,
                        Color(0xFFF6F2E4)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top-left outer soft mint wave
            val topOuterWave = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.62f, 0f)
                cubicTo(
                    w * 0.45f, h * 0.06f,
                    w * 0.25f, h * 0.10f,
                    0f, h * 0.20f
                )
                close()
            }
            drawPath(
                path = topOuterWave,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF9DD6C3).copy(alpha = 0.55f),
                        Color(0xFFD9EFE6).copy(alpha = 0.25f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w * 0.6f, h * 0.2f)
                )
            )

            // Top-left mid emerald wave
            val topMidWave = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.50f, 0f)
                cubicTo(
                    w * 0.36f, h * 0.05f,
                    w * 0.18f, h * 0.09f,
                    0f, h * 0.155f
                )
                close()
            }
            drawPath(
                path = topMidWave,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ArenaEmeraldVibrant.copy(alpha = 0.85f),
                        ArenaEmeraldMid.copy(alpha = 0.65f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w * 0.5f, h * 0.15f)
                )
            )

            // Top-left deep emerald core wave
            val topInnerWave = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.36f, 0f)
                cubicTo(
                    w * 0.26f, h * 0.04f,
                    w * 0.12f, h * 0.065f,
                    0f, h * 0.105f
                )
                close()
            }
            drawPath(
                path = topInnerWave,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ArenaEmeraldDeep,
                        ArenaEmeraldDark
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w * 0.36f, h * 0.10f)
                )
            )

            if (showGoldAccents) {
                val topGoldCurve = Path().apply {
                    moveTo(0f, h * 0.135f)
                    cubicTo(
                        w * 0.16f, h * 0.075f,
                        w * 0.32f, h * 0.045f,
                        w * 0.48f, 0f
                    )
                }
                drawPath(
                    path = topGoldCurve,
                    brush = Brush.linearGradient(
                        colors = listOf(ArenaGoldLight, ArenaGoldPrimary, Color.Transparent),
                        start = Offset(0f, h * 0.13f),
                        end = Offset(w * 0.48f, 0f)
                    ),
                    style = Stroke(width = 3.5f)
                )
            }

            // Bottom-left and bottom-right layered waves
            val bottomSoftWave = Path().apply {
                moveTo(0f, h * 0.86f)
                cubicTo(
                    w * 0.30f, h * 0.94f,
                    w * 0.68f, h * 0.96f,
                    w, h * 0.85f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = bottomSoftWave,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFB3E0D1).copy(alpha = 0.55f),
                        Color(0xFF7AC8B0).copy(alpha = 0.65f)
                    ),
                    start = Offset(0f, h * 0.85f),
                    end = Offset(w, h)
                )
            )

            val bottomMidWave = Path().apply {
                moveTo(0f, h * 0.90f)
                cubicTo(
                    w * 0.28f, h * 0.97f,
                    w * 0.65f, h * 0.98f,
                    w, h * 0.89f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = bottomMidWave,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ArenaEmeraldMid,
                        ArenaEmeraldVibrant,
                        ArenaEmeraldDark
                    ),
                    start = Offset(0f, h * 0.90f),
                    end = Offset(w, h)
                )
            )

            val bottomDeepWave = Path().apply {
                moveTo(0f, h * 0.935f)
                cubicTo(
                    w * 0.25f, h * 0.985f,
                    w * 0.72f, h * 0.99f,
                    w, h * 0.93f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = bottomDeepWave,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ArenaEmeraldDeep,
                        ArenaEmeraldDark
                    ),
                    start = Offset(0f, h * 0.94f),
                    end = Offset(w, h)
                )
            )

            if (showGoldAccents) {
                val bottomGoldLine = Path().apply {
                    moveTo(0f, h * 0.905f)
                    cubicTo(
                        w * 0.30f, h * 0.975f,
                        w * 0.68f, h * 0.98f,
                        w, h * 0.89f
                    )
                }
                drawPath(
                    path = bottomGoldLine,
                    brush = Brush.horizontalGradient(
                        colors = listOf(ArenaGoldPrimary, ArenaGoldLight, ArenaGoldPrimary)
                    ),
                    style = Stroke(width = 3f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            content = content
        )
    }
}

@Composable
fun ArenaHeaderLogo(
    onBackClick: (() -> Unit)? = null,
    showSubtitle: Boolean = false,
    showGoldDivider: Boolean = true,
    logoSize: Dp = 66.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, start = 12.dp, end = 12.dp)
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(48.dp)
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ArenaEmeraldDark,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.arena_logo_header),
                contentDescription = "Arena Chat Logo",
                modifier = Modifier
                    .size(logoSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ARENA CHAT",
                style = TextStyle(
                    fontFamily = MontserratFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    letterSpacing = 1.6.sp,
                    color = ArenaEmeraldDark
                )
            )
            if (showSubtitle) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "GLOBAL MESSAGING POWER",
                    style = TextStyle(
                        fontFamily = MontserratFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 9.5.sp,
                        letterSpacing = 2.4.sp,
                        color = ArenaTextSecondary
                    )
                )
            }
            if (showGoldDivider) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    ArenaGoldDark.copy(alpha = 0.4f),
                                    ArenaGoldPrimary,
                                    ArenaGoldLight,
                                    ArenaGoldPrimary,
                                    ArenaGoldDark.copy(alpha = 0.4f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun ArenaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    showCircleArrow: Boolean = true,
    testTag: String = "primary_cta_button"
) {
    val goldBorderBrush = Brush.horizontalGradient(
        colors = listOf(
            ArenaGoldDark,
            ArenaGoldLight,
            ArenaGoldPrimary,
            ArenaGoldLight,
            ArenaGoldDark
        )
    )
    val greenGradientBrush = Brush.horizontalGradient(
        colors = if (enabled) {
            listOf(
                ArenaEmeraldDark,
                ArenaEmeraldVibrant,
                ArenaEmeraldBright,
                ArenaEmeraldVibrant,
                ArenaEmeraldDark
            )
        } else {
            listOf(
                ArenaEmeraldDark.copy(alpha = 0.5f),
                ArenaEmeraldMid.copy(alpha = 0.5f),
                ArenaEmeraldDark.copy(alpha = 0.5f)
            )
        }
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 2.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = ArenaEmeraldDark,
                spotColor = ArenaEmeraldVibrant
            )
            .clip(RoundedCornerShape(30.dp))
            .background(greenGradientBrush)
            .border(
                width = 1.8.dp,
                brush = goldBorderBrush,
                shape = RoundedCornerShape(30.dp)
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .padding(horizontal = 16.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = ArenaCreamLight,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )
            if (showCircleArrow) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ArenaEmeraldDark.copy(alpha = 0.55f))
                        .border(1.dp, ArenaEmeraldBright.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(22.dp)
                )
            }
        }
    }
}

@Composable
fun ArenaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    leadingIconCircleBg: Boolean = false,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    trailingText: String? = null,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    testTag: String = "arena_text_field"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ArenaEmeraldDark
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .border(
                    width = 1.2.dp,
                    color = if (errorMessage != null) ArenaError else ArenaMintBorder,
                    shape = RoundedCornerShape(26.dp)
                ),
            shape = RoundedCornerShape(26.dp),
            color = ArenaInputBg
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIconCircleBg) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ArenaMintSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = ArenaEmeraldDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = ArenaEmeraldMid,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(22.dp)
                        .background(ArenaMintBorder.copy(alpha = 0.65f))
                )
                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.5.sp,
                                color = ArenaTextPlaceholder
                            )
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.5.sp,
                            color = ArenaTextPrimary
                        ),
                        cursorBrush = SolidColor(ArenaEmeraldMid),
                        keyboardOptions = keyboardOptions,
                        visualTransformation = visualTransformation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(testTag)
                    )
                }

                if (isPassword && onTogglePasswordVisibility != null) {
                    IconButton(
                        onClick = onTogglePasswordVisibility,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("${testTag}_toggle_visibility")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (passwordVisible) ArenaMintSoft else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Outlined.VisibilityOff
                                } else {
                                    Icons.Outlined.Visibility
                                },
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = if (passwordVisible) ArenaEmeraldVibrant else ArenaEmeraldDark,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }
                } else if (trailingText != null) {
                    Text(
                        text = trailingText,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = ArenaEmeraldDark
                        )
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
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
}

@Composable
fun ArenaGoldShieldDividerFooter(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, ArenaGoldPrimary)
                        )
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.2.dp, ArenaGoldDark, RoundedCornerShape(5.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔒",
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ArenaGoldPrimary, Color.Transparent)
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}
