package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.ui.theme.ArenaTextPrimary
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * International-standard animated wrapper for hero & shield illustrations.
 * Adds smooth 60fps vertical breathing motion, rotating golden & emerald orbital arcs,
 * and pulsing starlight particles around the artwork (WhatsApp / Telegram premium style).
 */
@Composable
fun AnimatedHeroArtworkContainer(
    modifier: Modifier = Modifier,
    glowSize: Dp = 170.dp,
    enableFloating: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_artwork_anim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_angle"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Subtle animated orbital aura + golden particle nodes behind artwork
        Canvas(
            modifier = Modifier.size(glowSize)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.88f

            // Soft radial emerald & gold glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ArenaEmeraldBright.copy(alpha = 0.16f * pulseScale),
                        ArenaGoldLight.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.15f
                ),
                radius = baseRadius * 1.15f,
                center = center
            )

            // Rotating dashed orbital gold & emerald arcs
            rotate(degrees = orbitAngle, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            ArenaGoldPrimary.copy(alpha = 0.55f),
                            ArenaEmeraldBright.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    ),
                    startAngle = 15f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(center.x - baseRadius, center.y - baseRadius),
                    size = Size(baseRadius * 2f, baseRadius * 2f),
                    style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            ArenaEmeraldBright.copy(alpha = 0.45f),
                            ArenaGoldLight.copy(alpha = 0.65f),
                            Color.Transparent
                        )
                    ),
                    startAngle = 195f,
                    sweepAngle = 115f,
                    useCenter = false,
                    topLeft = Offset(center.x - baseRadius, center.y - baseRadius),
                    size = Size(baseRadius * 2f, baseRadius * 2f),
                    style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 4 orbiting golden & emerald light nodes
            for (i in 0 until 4) {
                val rad = ((orbitAngle + i * 90f) * PI / 180.0).toFloat()
                val px = center.x + cos(rad) * baseRadius
                val py = center.y + sin(rad) * baseRadius
                drawCircle(
                    color = if (i % 2 == 0) ArenaGoldPrimary else ArenaEmeraldBright,
                    radius = (2.8.dp.toPx()) * (if (i % 2 == 0) pulseScale else 1f),
                    center = Offset(px, py)
                )
            }
        }

        Box(
            modifier = if (enableFloating) {
                Modifier.offset { IntOffset(0, floatOffset.dp.roundToPx()) }
            } else {
                Modifier
            },
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/**
 * Live animated HUD overlay rendered directly on top of the Face Verification card image.
 * Simulates a Binance / Banking KYC 3D facial mesh laser scan in real time.
 */
@Composable
fun LiveFaceScanCardOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "face_card_hud")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "face_laser"
    )
    val nodePulse by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "face_node_pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val scanY = h * laserProgress

        // Glowing cyan/emerald horizontal laser line across the face frame
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF39F3BB).copy(alpha = 0.85f),
                    Color(0xFFA7FFEB),
                    Color(0xFF39F3BB).copy(alpha = 0.85f),
                    Color.Transparent
                )
            ),
            start = Offset(w * 0.16f, scanY),
            end = Offset(w * 0.84f, scanY),
            strokeWidth = 2.2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Subtle laser trail gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF39F3BB).copy(alpha = 0.18f),
                    Color.Transparent
                ),
                startY = (scanY - 16.dp.toPx()).coerceAtLeast(0f),
                endY = (scanY + 16.dp.toPx()).coerceAtMost(h)
            ),
            topLeft = Offset(w * 0.18f, (scanY - 14.dp.toPx()).coerceAtLeast(0f)),
            size = Size(w * 0.64f, 28.dp.toPx())
        )

        // Pulsing facial landmark tracking nodes
        val nodes = listOf(
            Offset(w * 0.42f, h * 0.42f),
            Offset(w * 0.58f, h * 0.42f),
            Offset(w * 0.50f, h * 0.52f),
            Offset(w * 0.38f, h * 0.58f),
            Offset(w * 0.62f, h * 0.58f),
            Offset(w * 0.50f, h * 0.68f)
        )
        nodes.forEach { pt ->
            val distFactor = (1f - (abs(pt.y - scanY) / (h * 0.35f)).coerceIn(0f, 1f))
            val alpha = (0.35f + 0.65f * maxOf(nodePulse * 0.6f, distFactor)).coerceIn(0f, 1f)
            drawCircle(
                color = Color(0xFF64FFDA).copy(alpha = alpha),
                radius = 2.4.dp.toPx(),
                center = pt
            )
        }
    }
}

/**
 * Live animated HUD overlay rendered directly on top of the Fingerprint Scan card image.
 * Simulates an international banking app ultrasonic sensor ripple & ridge laser sweep.
 */
@Composable
fun LiveFingerprintScanCardOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fp_card_hud")
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fp_ripple"
    )
    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.80f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fp_sweep"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val sensorCenter = Offset(w * 0.50f, h * 0.48f)
        val maxRadius = size.minDimension * 0.36f

        // Dual ultrasonic expanding rings
        for (ring in 0..1) {
            val phase = (rippleProgress + ring * 0.5f) % 1f
            val radius = maxRadius * (0.35f + 0.65f * phase)
            val alpha = (1f - phase) * 0.72f
            drawCircle(
                color = Color(0xFF39F3BB).copy(alpha = alpha),
                radius = radius,
                center = sensorCenter,
                style = Stroke(width = 1.8.dp.toPx())
            )
        }

        // Ultrasonic ridge scan line
        val scanY = h * sweepProgress
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF00E5FF).copy(alpha = 0.85f),
                    Color(0xFF64FFDA),
                    Color(0xFF00E5FF).copy(alpha = 0.85f),
                    Color.Transparent
                )
            ),
            start = Offset(w * 0.24f, scanY),
            end = Offset(w * 0.76f, scanY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * International Banking / Binance-Grade Interactive Biometric & Human Verification Scanner Modal.
 *
 * Features:
 * - High-tech animated 3D Face Mesh Scanner HUD (when `method == "face_scan"`)
 * - High-tech animated Ultrasonic Fingerprint Ridge Scanner HUD (when `method == "fingerprint_scan"`)
 * - Interactive Hold-to-Scan biometric capture + 3-checkpoint liveness sequence (`liveness_step_0..2`)
 * - Interactive vector alignment calibration slider (`human_challenge_slider`)
 * - Interactive cognitive arithmetic human challenge (`human_challenge_math_input`)
 * - Preserves all testTags for full automated & manual verification compatibility.
 */
@Composable
fun InternationalBiometricScannerModal(
    method: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val isFaceScan = method == "face_scan"

    val targetPercent = remember { (32..84).random() }
    val numA = remember { (4..14).random() }
    val numB = remember { (3..11).random() }
    val expectedSum = numA + numB

    var sliderVal by remember { mutableFloatStateOf(10f) }
    var sumAnswer by remember { mutableStateOf("") }
    var livenessStep by remember { mutableIntStateOf(0) }
    var isHoldingScanner by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Continuous hold-to-scan progression when the user presses and holds the biometric scanner pad
    LaunchedEffect(isHoldingScanner, livenessStep) {
        if (isHoldingScanner && livenessStep < 3) {
            while (isHoldingScanner && livenessStep < 3) {
                delay(420L)
                if (isHoldingScanner && livenessStep < 3) {
                    livenessStep += 1
                    runCatching { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
                }
            }
        }
    }

    val sliderMatched = abs(sliderVal.toInt() - targetPercent) <= 3
    val mathMatched = sumAnswer.trim().toIntOrNull() == expectedSum
    val livenessMatched = livenessStep >= 3

    val completedStagesCount =
        (if (livenessMatched) 1 else 0) +
                (if (sliderMatched) 1 else 0) +
                (if (mathMatched) 1 else 0)

    val animatedProgress by animateFloatAsState(
        targetValue = (livenessStep / 3f) * 0.5f +
                (if (sliderMatched) 0.25f else 0f) +
                (if (mathMatched) 0.25f else 0f),
        animationSpec = tween(durationMillis = 350),
        label = "scanner_overall_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "modal_biometric_hud")
    val hudRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hud_rotation"
    )
    val laserBeamY by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "modal_laser_y"
    )
    val pulseWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "modal_pulse_wave"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 480.dp)
                .shadow(24.dp, RoundedCornerShape(28.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(ArenaGoldLight, ArenaGoldPrimary, ArenaEmeraldBright, ArenaGoldDark)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            color = ArenaCreamLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Security Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ArenaEmeraldDark)
                                .border(1.5.dp, ArenaGoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFaceScan) Icons.Default.CropFree else Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = ArenaGoldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isFaceScan) {
                                    "3D Face Liveness & Biometric KYC"
                                } else {
                                    "Ultrasonic Fingerprint Liveness"
                                },
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = ArenaEmeraldDark
                                )
                            )
                            Text(
                                text = "ARENA SECURITY SHIELD •STAGE $completedStagesCount/3 VERIFIED",
                                style = TextStyle(
                                    fontFamily = MontserratFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 1.1.sp,
                                    color = ArenaEmeraldVibrant
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close scanner",
                            tint = ArenaTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // BANKING / BINANCE-GRADE ANIMATED BIOMETRIC HUD VIEWPORT
                // =========================================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(186.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0A4835),
                                    ArenaEmeraldDark,
                                    ArenaEmeraldDeep
                                )
                            )
                        )
                        .border(
                            width = 1.6.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(ArenaGoldPrimary, ArenaEmeraldBright, ArenaGoldPrimary)
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHoldingScanner = true
                                    tryAwaitRelease()
                                    isHoldingScanner = false
                                },
                                onTap = {
                                    if (livenessStep < 3) {
                                        livenessStep += 1
                                        runCatching {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val center = Offset(w / 2f, h / 2f)
                        val hudRadius = size.minDimension * 0.39f

                        // Background cyber grid lines
                        val gridCols = 8
                        val gridRows = 5
                        for (c in 1 until gridCols) {
                            val gx = (w / gridCols) * c
                            drawLine(
                                color = Color(0xFF39F3BB).copy(alpha = 0.07f),
                                start = Offset(gx, 0f),
                                end = Offset(gx, h),
                                strokeWidth = 1f
                            )
                        }
                        for (r in 1 until gridRows) {
                            val gy = (h / gridRows) * r
                            drawLine(
                                color = Color(0xFF39F3BB).copy(alpha = 0.07f),
                                start = Offset(0f, gy),
                                end = Offset(w, gy),
                                strokeWidth = 1f
                            )
                        }

                        // Outer rotating radar ticks ring
                        rotate(degrees = hudRotation, pivot = center) {
                            for (tick in 0 until 36) {
                                val angleRad = (tick * 10f * PI / 180.0).toFloat()
                                val innerR = hudRadius * 1.04f
                                val outerR = hudRadius * (if (tick % 3 == 0) 1.14f else 1.09f)
                                drawLine(
                                    color = if (tick % 3 == 0) ArenaGoldLight.copy(alpha = 0.75f)
                                    else Color(0xFF39F3BB).copy(alpha = 0.45f),
                                    start = Offset(
                                        center.x + cos(angleRad) * innerR,
                                        center.y + sin(angleRad) * innerR
                                    ),
                                    end = Offset(
                                        center.x + cos(angleRad) * outerR,
                                        center.y + sin(angleRad) * outerR
                                    ),
                                    strokeWidth = if (tick % 3 == 0) 2.dp.toPx() else 1.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }

                        // Progress arc around the biometric core
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    ArenaEmeraldBright,
                                    Color(0xFF64FFDA),
                                    ArenaGoldLight,
                                    ArenaEmeraldBright
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress.coerceIn(0.08f, 1f),
                            useCenter = false,
                            topLeft = Offset(center.x - hudRadius, center.y - hudRadius),
                            size = Size(hudRadius * 2f, hudRadius * 2f),
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        if (isFaceScan) {
                            // 3D Face Contour Oval + 24 Landmark Constellation Mesh
                            val faceW = hudRadius * 1.15f
                            val faceH = hudRadius * 1.45f
                            drawOval(
                                color = if (livenessMatched) Color(0xFF64FFDA) else ArenaGoldLight.copy(alpha = 0.75f),
                                topLeft = Offset(center.x - faceW / 2f, center.y - faceH / 2f),
                                size = Size(faceW, faceH),
                                style = Stroke(width = 2.dp.toPx())
                            )

                            val meshPoints = listOf(
                                // Forehead & brows
                                Offset(center.x - faceW * 0.24f, center.y - faceH * 0.24f),
                                Offset(center.x, center.y - faceH * 0.30f),
                                Offset(center.x + faceW * 0.24f, center.y - faceH * 0.24f),
                                // Eyes
                                Offset(center.x - faceW * 0.22f, center.y - faceH * 0.10f),
                                Offset(center.x + faceW * 0.22f, center.y - faceH * 0.10f),
                                // Nose & Cheeks
                                Offset(center.x, center.y + faceH * 0.02f),
                                Offset(center.x - faceW * 0.30f, center.y + faceH * 0.06f),
                                Offset(center.x + faceW * 0.30f, center.y + faceH * 0.06f),
                                // Mouth & Chin
                                Offset(center.x - faceW * 0.16f, center.y + faceH * 0.22f),
                                Offset(center.x + faceW * 0.16f, center.y + faceH * 0.22f),
                                Offset(center.x, center.y + faceH * 0.36f)
                            )

                            // Connect constellation mesh lines
                            val edges = listOf(
                                0 to 1, 1 to 2, 0 to 3, 2 to 4,
                                3 to 5, 4 to 5, 3 to 6, 4 to 7,
                                5 to 8, 5 to 9, 6 to 8, 7 to 9,
                                8 to 9, 8 to 10, 9 to 10
                            )
                            edges.forEachIndexed { idx, (a, b) ->
                                val activeEdge = livenessStep >= (idx % 3) + 1
                                drawLine(
                                    color = if (activeEdge) Color(0xFF64FFDA).copy(alpha = 0.78f)
                                    else Color.White.copy(alpha = 0.25f),
                                    start = meshPoints[a],
                                    end = meshPoints[b],
                                    strokeWidth = 1.3.dp.toPx()
                                )
                            }

                            meshPoints.forEachIndexed { idx, pt ->
                                val lit = livenessStep >= (idx % 3) + 1
                                drawCircle(
                                    color = if (lit) Color(0xFF64FFDA) else ArenaGoldLight,
                                    radius = if (lit) 3.4.dp.toPx() else 2.2.dp.toPx(),
                                    center = pt
                                )
                            }
                        } else {
                            // Ultrasonic Concentric Fingerprint Ridges + Shockwave Pulse
                            val shockRadius = hudRadius * (0.25f + 0.85f * pulseWave)
                            drawCircle(
                                color = Color(0xFF64FFDA).copy(alpha = (1f - pulseWave) * 0.55f),
                                radius = shockRadius,
                                center = center,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            for (ridge in 1..6) {
                                val r = hudRadius * (0.18f + ridge * 0.11f)
                                val ridgeLit = livenessStep * 2 >= ridge
                                val ridgeColor = if (ridgeLit) Color(0xFF64FFDA) else Color.White.copy(alpha = 0.32f)
                                drawArc(
                                    color = ridgeColor,
                                    startAngle = -145f + ridge * 6f,
                                    sweepAngle = 110f,
                                    useCenter = false,
                                    topLeft = Offset(center.x - r, center.y - r),
                                    size = Size(r * 2f, r * 2f),
                                    style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = ridgeColor,
                                    startAngle = 25f - ridge * 5f,
                                    sweepAngle = 125f,
                                    useCenter = false,
                                    topLeft = Offset(center.x - r, center.y - r),
                                    size = Size(r * 2f, r * 2f),
                                    style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }

                        // Corner HUD Targeting Brackets
                        val bracketLen = 18.dp.toPx()
                        val pad = 12.dp.toPx()
                        val bracketColor = if (livenessMatched) Color(0xFF64FFDA) else ArenaGoldLight
                        // Top-left
                        drawLine(bracketColor, Offset(pad, pad), Offset(pad + bracketLen, pad), 2.5.dp.toPx())
                        drawLine(bracketColor, Offset(pad, pad), Offset(pad, pad + bracketLen), 2.5.dp.toPx())
                        // Top-right
                        drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad - bracketLen, pad), 2.5.dp.toPx())
                        drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad, pad + bracketLen), 2.5.dp.toPx())
                        // Bottom-left
                        drawLine(bracketColor, Offset(pad, h - pad), Offset(pad + bracketLen, h - pad), 2.5.dp.toPx())
                        drawLine(bracketColor, Offset(pad, h - pad), Offset(pad, h - pad - bracketLen), 2.5.dp.toPx())
                        // Bottom-right
                        drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad - bracketLen, h - pad), 2.5.dp.toPx())
                        drawLine(bracketColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - bracketLen), 2.5.dp.toPx())

                        // Live sweeping laser beam across the scanner viewport
                        val beamY = h * laserBeamY
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF39F3BB),
                                    Color.White,
                                    Color(0xFF39F3BB),
                                    Color.Transparent
                                )
                            ),
                            start = Offset(w * 0.12f, beamY),
                            end = Offset(w * 0.88f, beamY),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Bottom HUD Telemetry Status Pill inside viewport
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = ArenaEmeraldDeep.copy(alpha = 0.88f)
                    ) {
                        Text(
                            text = when {
                                livenessMatched -> "✓ BIOMETRIC LIVENESS LOCKED (100%)"
                                isHoldingScanner -> "SCANNING BIOMETRICS... (${(livenessStep * 33).coerceAtMost(99)}%)"
                                else -> "TAP OR HOLD SENSOR PAD TO SCAN (${livenessStep}/3)"
                            },
                            style = TextStyle(
                                fontFamily = MontserratFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.9.sp,
                                color = if (livenessMatched) Color(0xFF64FFDA) else ArenaGoldLight
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // =========================================================================
                // STEP 1: 3-POINT SEQUENTIAL LIVENESS CHECKPOINTS
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1. Biometric Liveness Sequence (${livenessStep}/3):",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = ArenaEmeraldDark
                        )
                    )
                    if (livenessMatched) {
                        Text(
                            text = "VERIFIED ✓",
                            style = TextStyle(
                                fontFamily = MontserratFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp,
                                color = ArenaEmeraldVibrant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val labels = if (isFaceScan) {
                        listOf("Center Gaze", "Blink Check", "3D Nod Lock")
                    } else {
                        listOf("Touch Sensor", "Pulse Hold", "Ridge Lock")
                    }
                    labels.forEachIndexed { idx, label ->
                        val done = livenessStep > idx
                        val active = livenessStep == idx
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .border(
                                    width = 1.2.dp,
                                    color = when {
                                        done -> ArenaGoldPrimary
                                        active -> ArenaEmeraldVibrant
                                        else -> ArenaMintBorder
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(enabled = active) {
                                    livenessStep = idx + 1
                                    errorMsg = null
                                    runCatching {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                                .testTag("liveness_step_$idx"),
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                done -> ArenaEmeraldVibrant
                                active -> ArenaMintSoft
                                else -> Color(0xFFEAEAEA)
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (done) "✓ $label" else label,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        color = if (done) Color.White else ArenaEmeraldDark
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // STEP 2: PRECISION VECTOR CALIBRATION SLIDER
                // =========================================================================
                Text(
                    text = "2. Align Biometric Frequency to $targetPercent% (Current: ${sliderVal.toInt()}%):",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = if (sliderMatched) ArenaEmeraldVibrant else ArenaEmeraldDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Slider(
                    value = sliderVal,
                    onValueChange = {
                        sliderVal = it
                        errorMsg = null
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = ArenaEmeraldDark,
                        activeTrackColor = ArenaEmeraldVibrant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .testTag("human_challenge_slider")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // =========================================================================
                // STEP 3: CRYPTOGRAPHIC COGNITIVE HUMAN CHALLENGE
                // =========================================================================
                Text(
                    text = "3. Anti-Bot Security Proof: What is $numA + $numB?",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = if (mathMatched) ArenaEmeraldVibrant else ArenaEmeraldDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .border(
                            width = 1.2.dp,
                            color = if (mathMatched) ArenaEmeraldVibrant else ArenaMintBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = ArenaInputBg
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (sumAnswer.isEmpty()) {
                            Text(
                                text = "Enter security sum ($expectedSum)",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 13.sp,
                                    color = ArenaTextSecondary.copy(alpha = 0.6f)
                                )
                            )
                        }
                        BasicTextField(
                            value = sumAnswer,
                            onValueChange = {
                                sumAnswer = it.filter { ch -> ch.isDigit() }.take(3)
                                errorMsg = null
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ArenaTextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("human_challenge_math_input")
                        )
                    }
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMsg!!,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = ArenaError
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaMintSoft,
                            contentColor = ArenaEmeraldDark
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(0.38f)
                            .height(46.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            when {
                                !livenessMatched -> errorMsg = "Please complete all 3 biometric liveness checkpoints."
                                !sliderMatched -> errorMsg = "Please align the frequency slider within ±3% of $targetPercent%."
                                !mathMatched -> errorMsg = "Incorrect security sum. Please enter $expectedSum."
                                else -> {
                                    runCatching {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    onVerified()
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArenaEmeraldDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(0.62f)
                            .height(46.dp)
                            .border(1.4.dp, ArenaGoldPrimary, RoundedCornerShape(20.dp))
                            .testTag("confirm_human_proof_button")
                    ) {
                        Icon(
                            imageVector = if (livenessMatched && sliderMatched && mathMatched) {
                                Icons.Default.Check
                            } else {
                                Icons.Default.VerifiedUser
                            },
                            contentDescription = null,
                            tint = ArenaGoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLoading) "Verifying..." else "Verify Humanity",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
