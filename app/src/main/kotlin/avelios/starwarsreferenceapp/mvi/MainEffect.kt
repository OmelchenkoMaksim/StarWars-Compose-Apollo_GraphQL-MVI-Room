package avelios.starwarsreferenceapp.mvi

/**
 * Sealed class representing the various side effects that can occur in the app.
 */
sealed class MainEffect {
    data class ShowToast(val message: String) : MainEffect()
    data class NavigateToDetails(val id: String, val type: String) : MainEffect()
    data class ThemeChanged(val isDarkTheme: Boolean) : MainEffect()
}
