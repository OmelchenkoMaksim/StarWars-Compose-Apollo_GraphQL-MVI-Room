package avelios.starwarsreferenceapp.util

import android.content.SharedPreferences
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant

internal class SettingsManagerImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsManager {

    override fun saveThemeVariant(themeVariant: ThemeVariant) {
        sharedPreferences.edit().putString(THEME_VARIANT, themeVariant.name).apply()
    }

    override fun loadThemeVariant(): ThemeVariant {
        val themeVariantName = sharedPreferences.getString(THEME_VARIANT, ThemeVariant.MorningMystic.name)
        return ThemeVariant.valueOf(themeVariantName ?: ThemeVariant.MorningMystic.name)
    }

    override fun saveTypographyVariant(typographyVariant: TypographyVariant) {
        sharedPreferences.edit().putString(TYPOGRAPHY_VARIANT, typographyVariant.name).apply()
    }

    override fun loadTypographyVariant(): TypographyVariant {
        val typographyVariantName = sharedPreferences.getString(TYPOGRAPHY_VARIANT, TypographyVariant.Classic.name)
        return TypographyVariant.valueOf(typographyVariantName ?: TypographyVariant.Classic.name)
    }

    override fun setDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean(IS_DARK_MODE, isDarkMode).apply()
    }

    override fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean(IS_DARK_MODE, false)
    }

    internal companion object PreferencesConstants {
        const val THEME_VARIANT = "theme_variant"
        const val TYPOGRAPHY_VARIANT = "typography_variant"
        const val IS_DARK_MODE = "isDarkMode"
    }
}