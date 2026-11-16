package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class NivoraPaletteKey(
    val key: String,
    val labelEn: String,
    val labelBn: String,
    val swatchPrimary: Color,
    val swatchSecondary: Color,
    val swatchBackground: Color
) {
    TEAL_BEIGE(
        key = "teal_beige",
        labelEn = "Teal & Soft Beige",
        labelBn = "ডার্ক টিল ও বেইজ",
        swatchPrimary = Color(0xFF0F4C4C),
        swatchSecondary = Color(0xFFC59B5F),
        swatchBackground = Color(0xFFF6F1E7)
    ),
    EMERALD_IVORY(
        key = "emerald_ivory",
        labelEn = "Emerald & Ivory",
        labelBn = "এমেরাল্ড ও আইভরি",
        swatchPrimary = Color(0xFF145341),
        swatchSecondary = Color(0xFFB38B59),
        swatchBackground = Color(0xFFFAF7F0)
    ),
    OCEAN_SAND(
        key = "ocean_sand",
        labelEn = "Deep Petrol & Sand",
        labelBn = "পেট্রোল টিল ও স্যান্ড",
        swatchPrimary = Color(0xFF124158),
        swatchSecondary = Color(0xFFC89D66),
        swatchBackground = Color(0xFFF3EFE6)
    ),
    TERRACOTTA_CREAM(
        key = "terracotta_cream",
        labelEn = "Terracotta & Cream",
        labelBn = "টেরাকোটা ও ক্রিম",
        swatchPrimary = Color(0xFF8C3B2B),
        swatchSecondary = Color(0xFF145353),
        swatchBackground = Color(0xFFFBF5EC)
    );

    companion object {
        fun fromKey(key: String): NivoraPaletteKey =
            entries.firstOrNull { it.key == key } ?: TEAL_BEIGE
    }
}

// Signature Soft Beige & Dark Teal Defaults
val NivoraDarkTeal = Color(0xFF0F4C4C)
val NivoraTealDeep = Color(0xFF093333)
val NivoraTealContainer = Color(0xFFD2E8E5)
val NivoraSoftBeige = Color(0xFFF6F1E7)
val NivoraWarmSand = Color(0xFFEDE4D3)
val NivoraCardCream = Color(0xFFFCF9F3)
val NivoraGoldAccent = Color(0xFFC59B5F)
val NivoraGoldContainer = Color(0xFFF5E6CE)
val NivoraInkDark = Color(0xFF182222)
val NivoraMutedInk = Color(0xFF566362)
val NivoraOutline = Color(0xFFD8CEBC)
val NivoraSuccessGreen = Color(0xFF1E6B52)
val NivoraWarningAmber = Color(0xFFB86B11)
val NivoraErrorRed = Color(0xFFB3261E)

fun lightSchemeFor(palette: NivoraPaletteKey): ColorScheme = when (palette) {
    NivoraPaletteKey.TEAL_BEIGE -> lightColorScheme(
        primary = Color(0xFF0F4C4C),
        onPrimary = Color(0xFFFCF9F3),
        primaryContainer = Color(0xFFD0E7E4),
        onPrimaryContainer = Color(0xFF062929),
        secondary = Color(0xFF8B6B3E),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFF3E4CC),
        onSecondaryContainer = Color(0xFF2D200B),
        tertiary = Color(0xFF246A68),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFD8EFEA),
        onTertiaryContainer = Color(0xFF082D2B),
        background = Color(0xFFF6F1E7),
        onBackground = Color(0xFF182222),
        surface = Color(0xFFFCF9F3),
        onSurface = Color(0xFF182222),
        surfaceVariant = Color(0xFFEDE4D3),
        onSurfaceVariant = Color(0xFF4D5958),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFAF5EC),
        surfaceContainer = Color(0xFFF2EBDD),
        surfaceContainerHigh = Color(0xFFEBE2D1),
        surfaceContainerHighest = Color(0xFFE3D8C4),
        outline = Color(0xFFD4C9B6),
        outlineVariant = Color(0xFFE6DEC9),
        error = Color(0xFFB3261E),
        onError = Color.White
    )

    NivoraPaletteKey.EMERALD_IVORY -> lightColorScheme(
        primary = Color(0xFF145341),
        onPrimary = Color(0xFFFAF7F0),
        primaryContainer = Color(0xFFD3ECE2),
        onPrimaryContainer = Color(0xFF072B20),
        secondary = Color(0xFF9C7242),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF6E6D2),
        onSecondaryContainer = Color(0xFF33200B),
        tertiary = Color(0xFF286B56),
        onTertiary = Color.White,
        background = Color(0xFFFAF7F0),
        onBackground = Color(0xFF17221E),
        surface = Color(0xFFFFFDF8),
        onSurface = Color(0xFF17221E),
        surfaceVariant = Color(0xFFEEE8DA),
        onSurfaceVariant = Color(0xFF4C5953),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF8F4EB),
        surfaceContainer = Color(0xFFF1ECE0),
        surfaceContainerHigh = Color(0xFFE9E2D4),
        surfaceContainerHighest = Color(0xFFE1D8C7),
        outline = Color(0xFFD6CEC0),
        outlineVariant = Color(0xFFE8E1D3)
    )

    NivoraPaletteKey.OCEAN_SAND -> lightColorScheme(
        primary = Color(0xFF124158),
        onPrimary = Color(0xFFF8F5EE),
        primaryContainer = Color(0xFFD2E5EF),
        onPrimaryContainer = Color(0xFF072230),
        secondary = Color(0xFF997043),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF4E4CE),
        onSecondaryContainer = Color(0xFF311F0D),
        tertiary = Color(0xFF1F6373),
        onTertiary = Color.White,
        background = Color(0xFFF3EFE6),
        onBackground = Color(0xFF162026),
        surface = Color(0xFFFAF7F0),
        onSurface = Color(0xFF162026),
        surfaceVariant = Color(0xFFE9E2D4),
        onSurfaceVariant = Color(0xFF4C575E),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF7F3EA),
        surfaceContainer = Color(0xFFEFEAE0),
        surfaceContainerHigh = Color(0xFFE7E1D5),
        surfaceContainerHighest = Color(0xFFDFD7C8),
        outline = Color(0xFFD2C9B9),
        outlineVariant = Color(0xFFE4DDD0)
    )

    NivoraPaletteKey.TERRACOTTA_CREAM -> lightColorScheme(
        primary = Color(0xFF8C3B2B),
        onPrimary = Color(0xFFFBF6EE),
        primaryContainer = Color(0xFFF6DBD5),
        onPrimaryContainer = Color(0xFF3E130B),
        secondary = Color(0xFF0F4C4C),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD2E7E5),
        onSecondaryContainer = Color(0xFF072828),
        tertiary = Color(0xFFA86434),
        onTertiary = Color.White,
        background = Color(0xFFFBF5EC),
        onBackground = Color(0xFF261C18),
        surface = Color(0xFFFFFBF5),
        onSurface = Color(0xFF261C18),
        surfaceVariant = Color(0xFFF0E4D4),
        onSurfaceVariant = Color(0xFF5D4E47),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF9F1E6),
        surfaceContainer = Color(0xFFF3E9DA),
        surfaceContainerHigh = Color(0xFFECE0CE),
        surfaceContainerHighest = Color(0xFFE4D5C0),
        outline = Color(0xFFD8C6B2),
        outlineVariant = Color(0xFFEADCCE)
    )
}

fun darkSchemeFor(palette: NivoraPaletteKey): ColorScheme = when (palette) {
    NivoraPaletteKey.TEAL_BEIGE -> darkColorScheme(
        primary = Color(0xFF7FD1CC),
        onPrimary = Color(0xFF043030),
        primaryContainer = Color(0xFF104E4E),
        onPrimaryContainer = Color(0xFFD0E7E4),
        secondary = Color(0xFFDAB988),
        onSecondary = Color(0xFF3B280D),
        secondaryContainer = Color(0xFF543C1B),
        onSecondaryContainer = Color(0xFFF5E6CE),
        background = Color(0xFF0E1717),
        onBackground = Color(0xFFEDE7DC),
        surface = Color(0xFF142020),
        onSurface = Color(0xFFEDE7DC),
        surfaceVariant = Color(0xFF213130),
        onSurfaceVariant = Color(0xFFC2CECC),
        surfaceContainerLow = Color(0xFF182626),
        surfaceContainer = Color(0xFF1C2C2C),
        surfaceContainerHigh = Color(0xFF243636),
        outline = Color(0xFF394C4B),
        outlineVariant = Color(0xFF293A39)
    )

    NivoraPaletteKey.EMERALD_IVORY -> darkColorScheme(
        primary = Color(0xFF83D4B7),
        onPrimary = Color(0xFF063223),
        primaryContainer = Color(0xFF145341),
        onPrimaryContainer = Color(0xFFD3ECE2),
        secondary = Color(0xFFE0BC89),
        onSecondary = Color(0xFF3A250C),
        background = Color(0xFF0E1714),
        onBackground = Color(0xFFEEE9DE),
        surface = Color(0xFF14201B),
        onSurface = Color(0xFFEEE9DE),
        surfaceVariant = Color(0xFF22312B),
        onSurfaceVariant = Color(0xFFC2CEC8),
        surfaceContainerLow = Color(0xFF182520),
        surfaceContainer = Color(0xFF1C2C26),
        surfaceContainerHigh = Color(0xFF24362F),
        outline = Color(0xFF3A4D45)
    )

    NivoraPaletteKey.OCEAN_SAND -> darkColorScheme(
        primary = Color(0xFF88CBE8),
        onPrimary = Color(0xFF062A3B),
        primaryContainer = Color(0xFF124158),
        onPrimaryContainer = Color(0xFFD2E5EF),
        secondary = Color(0xFFDFBC8E),
        onSecondary = Color(0xFF39250E),
        background = Color(0xFF0D151B),
        onBackground = Color(0xFFEDE8DF),
        surface = Color(0xFF131E26),
        onSurface = Color(0xFFEDE8DF),
        surfaceVariant = Color(0xFF202E38),
        onSurfaceVariant = Color(0xFFC0CBD2),
        surfaceContainerLow = Color(0xFF17232C),
        surfaceContainer = Color(0xFF1C2A34),
        surfaceContainerHigh = Color(0xFF233440),
        outline = Color(0xFF394955)
    )

    NivoraPaletteKey.TERRACOTTA_CREAM -> darkColorScheme(
        primary = Color(0xFFF0A291),
        onPrimary = Color(0xFF4A180E),
        primaryContainer = Color(0xFF702C1E),
        onPrimaryContainer = Color(0xFFF6DBD5),
        secondary = Color(0xFF7FD1CC),
        onSecondary = Color(0xFF043030),
        background = Color(0xFF1A1311),
        onBackground = Color(0xFFF2E9DF),
        surface = Color(0xFF231A17),
        onSurface = Color(0xFFF2E9DF),
        surfaceVariant = Color(0xFF352823),
        onSurfaceVariant = Color(0xFFD5C4BC),
        surfaceContainerLow = Color(0xFF281E1B),
        surfaceContainer = Color(0xFF2F231F),
        surfaceContainerHigh = Color(0xFF392B26),
        outline = Color(0xFF534039)
    )
}
