package avelios.starwarsreferenceapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun StarWarsReferenceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeVariant: ThemeVariant = ThemeVariant.MorningMystic,
    typographyVariant: TypographyVariant = TypographyVariant.Classic,
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = when (themeVariant) {
        ThemeVariant.MorningMystic -> if (darkTheme) DarkColorPalette.MysticNight else LightColorPalette.MorningDew
        ThemeVariant.SpringForest -> if (darkTheme) DarkColorPalette.ForestShadow else LightColorPalette.SpringBloom
        ThemeVariant.MintOcean -> if (darkTheme) DarkColorPalette.OceanDepth else LightColorPalette.MintBreeze
        ThemeVariant.SunMoonlight -> if (darkTheme) DarkColorPalette.MoonlightGold else LightColorPalette.Sunburst
        ThemeVariant.AutumnSunset -> if (darkTheme) DarkColorPalette.SunsetEmber else LightColorPalette.AutumnLeaf
        ThemeVariant.LavenderMidnight -> if (darkTheme) DarkColorPalette.MidnightBlue else LightColorPalette.LavenderField
    }

    val typography: Typography = when (typographyVariant) {
        TypographyVariant.Classic -> ClassicTypography.typography
        TypographyVariant.Modern -> ModernTypography.typography
        TypographyVariant.Elegant -> ElegantTypography.typography
        TypographyVariant.Playful -> PlayfulTypography.typography
        TypographyVariant.Professional -> ProfessionalTypography.typography
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = Shapes(),
        content = content
    )
}

enum class ThemeVariant {
    MorningMystic, SpringForest, MintOcean, SunMoonlight, AutumnSunset, LavenderMidnight
}
