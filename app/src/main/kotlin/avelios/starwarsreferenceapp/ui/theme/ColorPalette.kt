package avelios.starwarsreferenceapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

internal object LightColorPalette {
    val MorningDew = lightColorScheme(
        primary = LightColors.white,
        primaryContainer = LightColors.ivory,
        secondary = LightColors.beige,
        secondaryContainer = LightColors.seashell,
        background = LightColors.snow,
        surface = LightColors.floralWhite,
        error = LightColors.oldLace
    )

    val SpringBloom = lightColorScheme(
        primary = GreenColors.springGreenLight,
        primaryContainer = GreenColors.lightLimeGreen,
        secondary = GreenColors.paleGreenLight,
        secondaryContainer = GreenColors.mediumSpringGreen,
        background = GreenColors.lightMint,
        surface = GreenColors.mediumSeaGreen,
        error = GreenColors.lightGreen
    )

    val MintBreeze = lightColorScheme(
        primary = BlueColors.skyBlue,
        primaryContainer = BlueColors.lightSkyBlue,
        secondary = BlueColors.steelBlue,
        secondaryContainer = BlueColors.dodgerBlue,
        background = BlueColors.lightSkyBlue,
        surface = BlueColors.royalBlue,
        error = BlueColors.blue
    )

    val Sunburst = lightColorScheme(
        primary = YellowColors.gold,
        primaryContainer = YellowColors.yellow,
        secondary = YellowColors.moccasin,
        secondaryContainer = YellowColors.papayaWhip,
        background = YellowColors.lightYellow,
        surface = YellowColors.lemonChiffon,
        error = YellowColors.lightGoldenrodYellow
    )

    val AutumnLeaf = lightColorScheme(
        primary = OrangeColors.lightOrange,
        primaryContainer = OrangeColors.lightGold,
        secondary = OrangeColors.sunset,
        secondaryContainer = OrangeColors.apricot,
        background = OrangeColors.lightPeach,
        surface = OrangeColors.melon,
        error = OrangeColors.lightSalmon
    )

    val LavenderField = lightColorScheme(
        primary = PurpleColors.lightOrchid,
        primaryContainer = PurpleColors.mauve,
        secondary = PurpleColors.mediumOrchid,
        secondaryContainer = PurpleColors.lightViolet,
        background = PurpleColors.thistle,
        surface = PurpleColors.plum,
        error = PurpleColors.paleLavender
    )
}

internal object DarkColorPalette {
    val MysticNight = darkColorScheme(
        primary = DarkColors.black,
        primaryContainer = DarkColors.charcoal,
        secondary = DarkColors.jet,
        secondaryContainer = DarkColors.onyx,
        background = DarkColors.darkSlateGray,
        surface = DarkColors.dimGray,
        error = DarkColors.ebony
    )

    val ForestShadow = darkColorScheme(
        primary = GreenColors.forestGreen,
        primaryContainer = GreenColors.olive,
        secondary = GreenColors.seaGreen,
        secondaryContainer = GreenColors.mediumSeaGreen,
        background = GreenColors.darkOliveGreen,
        surface = GreenColors.oliveDrab,
        error = GreenColors.springGreen
    )

    val OceanDepth = darkColorScheme(
        primary = BlueColors.midnightBlue,
        primaryContainer = BlueColors.darkBlue,
        secondary = BlueColors.deepSkyBlue,
        secondaryContainer = BlueColors.lightSkyBlue,
        background = BlueColors.navy,
        surface = BlueColors.steelBlue,
        error = BlueColors.cornflowerBlue
    )

    val MoonlightGold = darkColorScheme(
        primary = YellowColors.gold,
        primaryContainer = YellowColors.yellow,
        secondary = YellowColors.moccasin,
        secondaryContainer = YellowColors.papayaWhip,
        background = YellowColors.darkGoldenrod,
        surface = YellowColors.goldenrod,
        error = YellowColors.khaki
    )

    val SunsetEmber = darkColorScheme(
        primary = RedColors.red,
        primaryContainer = RedColors.darkRed,
        secondary = RedColors.firebrick,
        secondaryContainer = RedColors.crimson,
        background = RedColors.indianRed,
        surface = RedColors.lightCoral,
        error = RedColors.salmon
    )

    val MidnightBlue = darkColorScheme(
        primary = PurpleColors.darkViolet,
        primaryContainer = PurpleColors.indigo,
        secondary = PurpleColors.amethyst,
        secondaryContainer = PurpleColors.mediumOrchid,
        background = PurpleColors.midnightPurple,
        surface = PurpleColors.plum,
        error = PurpleColors.violet
    )
}
