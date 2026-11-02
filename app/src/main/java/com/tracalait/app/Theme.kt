package com.tracalait.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palette originale TraçaLait (blanc laiteux, bleu acier, vert naturel, ambre)
val MilkBg = Color(0xFFF8F9FB)
val WhiteSurface = Color(0xFFFFFFFF)
val NavyPrimary = Color(0xFF1A3A5C)
val BlueAccent = Color(0xFF2563EB)
val BlueLight = Color(0xFFDBEAFE)
val GreenSuccess = Color(0xFF16A34A)
val GreenLight = Color(0xFFDCFCE7)
val AmberWarning = Color(0xFFD97706)
val AmberLight = Color(0xFFFEF3C7)
val RedDanger = Color(0xFFDC2626)
val RedLight = Color(0xFFFEE2E2)
val Gray50 = Color(0xFFF9FAFB)
val Gray100 = Color(0xFFF3F4F6)
val Gray200 = Color(0xFFE5E7EB)
val Gray400 = Color(0xFF9CA3AF)
val Gray600 = Color(0xFF4B5563)
val Gray800 = Color(0xFF1F2937)

private val TracaLaitColorScheme = lightColorScheme(
    primary = BlueAccent,
    onPrimary = WhiteSurface,
    primaryContainer = BlueLight,
    onPrimaryContainer = NavyPrimary,
    secondary = NavyPrimary,
    onSecondary = WhiteSurface,
    secondaryContainer = Gray100,
    onSecondaryContainer = NavyPrimary,
    tertiary = GreenSuccess,
    onTertiary = WhiteSurface,
    tertiaryContainer = GreenLight,
    onTertiaryContainer = GreenSuccess,
    error = RedDanger,
    onError = WhiteSurface,
    errorContainer = RedLight,
    onErrorContainer = RedDanger,
    background = MilkBg,
    onBackground = Gray800,
    surface = WhiteSurface,
    onSurface = Gray800,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray600,
    outline = Gray200
)

@Composable
fun TracaLaitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TracaLaitColorScheme,
        content = content
    )
}
