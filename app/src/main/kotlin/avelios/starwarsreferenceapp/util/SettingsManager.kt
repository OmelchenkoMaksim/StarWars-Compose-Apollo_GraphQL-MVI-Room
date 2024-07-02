package avelios.starwarsreferenceapp.util

import android.content.SharedPreferences
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant

internal class SettingsManager(private val sharedPreferences: SharedPreferences) {

    fun saveThemeVariant(themeVariant: ThemeVariant) {
        sharedPreferences.edit().putString(THEME_VARIANT, themeVariant.name).apply()
    }

    fun loadThemeVariant(): ThemeVariant {
        val themeVariantName = sharedPreferences.getString(THEME_VARIANT, ThemeVariant.MorningMystic.name)
        return ThemeVariant.valueOf(themeVariantName ?: ThemeVariant.MorningMystic.name)
    }

    fun saveTypographyVariant(typographyVariant: TypographyVariant) {
        sharedPreferences.edit().putString(TYPOGRAPHY_VARIANT, typographyVariant.name).apply()
    }

    fun loadTypographyVariant(): TypographyVariant {
        val typographyVariantName = sharedPreferences.getString(TYPOGRAPHY_VARIANT, TypographyVariant.Classic.name)
        return TypographyVariant.valueOf(typographyVariantName ?: TypographyVariant.Classic.name)
    }

    fun setDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean(IS_DARK_MODE, isDarkMode).apply()
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(IS_DARK_MODE, false)
    }

    internal companion object PreferencesConstants {
        const val THEME_VARIANT = "theme_variant"
        const val TYPOGRAPHY_VARIANT = "typography_variant"
        const val IS_DARK_MODE = "isDarkMode"
    }
}
