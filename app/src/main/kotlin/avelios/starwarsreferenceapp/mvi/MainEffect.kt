package avelios.starwarsreferenceapp.mvi

sealed class MainEffect {
    data class ShowToast(val message: String) : MainEffect()
    data class NavigateToDetails(val id: String, val type: String) : MainEffect()
    data class ThemeChanged(val isDarkTheme: Boolean) : MainEffect()
}
