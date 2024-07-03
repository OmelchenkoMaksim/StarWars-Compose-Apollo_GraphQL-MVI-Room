package avelios.starwarsreferenceapp.util

import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant

/**
 * Interface for managing app settings such as theme and typography variants and dark mode.
 */
internal interface SettingsManager {

    /**
     * Saves the current theme variant to the shared preferences.
     *
     * @param themeVariant The theme variant to be saved.
     */
    fun saveThemeVariant(themeVariant: ThemeVariant)

    /**
     * Loads the saved theme variant from the shared preferences.
     *
     * @return The saved theme variant, or the default value if none is saved.
     */
    fun loadThemeVariant(): ThemeVariant

    /**
     * Saves the current typography variant to the shared preferences.
     *
     * @param typographyVariant The typography variant to be saved.
     */
    fun saveTypographyVariant(typographyVariant: TypographyVariant)

    /**
     * Loads the saved typography variant from the shared preferences.
     *
     * @return The saved typography variant, or the default value if none is saved.
     */
    fun loadTypographyVariant(): TypographyVariant

    /**
     * Sets the dark mode preference in the shared preferences.
     *
     * @param isDarkMode A boolean indicating whether dark mode should be enabled.
     */
    fun setDarkMode(isDarkMode: Boolean)

    /**
     * Checks if dark mode is enabled by reading the shared preferences.
     *
     * @return A boolean indicating whether dark mode is enabled.
     */
    fun isDarkMode(): Boolean
}


