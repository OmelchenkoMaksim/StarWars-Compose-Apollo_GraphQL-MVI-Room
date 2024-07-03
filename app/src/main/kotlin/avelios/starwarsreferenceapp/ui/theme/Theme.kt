package avelios.starwarsreferenceapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import avelios.starwarsreferenceapp.ui.theme.DarkColorPalette.ForestShadow
import avelios.starwarsreferenceapp.ui.theme.DarkColorPalette.MidnightBlue
import avelios.starwarsreferenceapp.ui.theme.DarkColorPalette.MoonlightGold
import avelios.starwarsreferenceapp.ui.theme.DarkColorPalette.MysticNight
import avelios.starwarsreferenceapp.ui.theme.DarkColorPalette.OceanDepth
import avelios.starwarsreferenceapp.ui.theme.DarkColorPalette.SunsetEmber
import avelios.starwarsreferenceapp.ui.theme.LightColorPalette.AutumnLeaf
import avelios.starwarsreferenceapp.ui.theme.LightColorPalette.LavenderField
import avelios.starwarsreferenceapp.ui.theme.LightColorPalette.MintBreeze
import avelios.starwarsreferenceapp.ui.theme.LightColorPalette.MorningDew
import avelios.starwarsreferenceapp.ui.theme.LightColorPalette.SpringBloom
import avelios.starwarsreferenceapp.ui.theme.LightColorPalette.Sunburst
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant.AutumnSunset
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant.LavenderMidnight
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant.MintOcean
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant.MorningMystic
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant.SpringForest
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant.SunMoonlight
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant.Classic
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant.Elegant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant.Modern
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant.Playful
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant.Professional

/**
 * Applies the selected theme and typography settings to the content of the app.
 *
 * @param darkTheme A flag indicating whether the dark theme is enabled. Default is system dark theme setting.
 * @param themeVariant The selected theme variant. Default is MorningMystic.
 * @param typographyVariant The selected typography variant. Default is Classic.
 * @param content The composable content to apply the theme and typography settings to.
 */
@Composable
internal fun StarWarsReferenceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeVariant: ThemeVariant = MorningMystic,
    typographyVariant: TypographyVariant = Classic,
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = when (themeVariant) {
        MorningMystic -> if (darkTheme) MysticNight else MorningDew
        SpringForest -> if (darkTheme) ForestShadow else SpringBloom
        MintOcean -> if (darkTheme) OceanDepth else MintBreeze
        SunMoonlight -> if (darkTheme) MoonlightGold else Sunburst
        AutumnSunset -> if (darkTheme) SunsetEmber else AutumnLeaf
        LavenderMidnight -> if (darkTheme) MidnightBlue else LavenderField
    }

    val typography: Typography = when (typographyVariant) {
        Classic -> ClassicTypography.typography
        Modern -> ModernTypography.typography
        Elegant -> ElegantTypography.typography
        Playful -> PlayfulTypography.typography
        Professional -> ProfessionalTypography.typography
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = Shapes(),
        content = content
    )
}

/**
 * Enum class representing the different theme variants available in the app.
 */
internal enum class ThemeVariant {
    MorningMystic, SpringForest, MintOcean, SunMoonlight, AutumnSunset, LavenderMidnight
}
