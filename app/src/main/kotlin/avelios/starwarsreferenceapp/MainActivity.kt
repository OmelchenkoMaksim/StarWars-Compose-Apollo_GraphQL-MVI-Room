package avelios.starwarsreferenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import avelios.starwarsreferenceapp.ui.theme.StarWarsReferenceAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val savedThemeVariant = mainViewModel.settingsManager.loadThemeVariant()
            val savedTypographyVariant = mainViewModel.settingsManager.loadTypographyVariant()

            val themeVariant = remember { mutableStateOf(savedThemeVariant) }
            val typographyVariant = remember { mutableStateOf(savedTypographyVariant) }
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()
            val showSettingsDialog = remember { mutableStateOf(false) }

            StarWarsReferenceAppTheme(
                themeVariant = themeVariant.value,
                typographyVariant = typographyVariant.value,
                darkTheme = isDarkTheme
            ) {
                if (showSettingsDialog.value) {
                    SettingsDialog(
                        onDismiss = { showSettingsDialog.value = false },
                        themeVariant = themeVariant,
                        typographyVariant = typographyVariant
                    ) { newThemeVariant, newTypographyVariant ->
                        mainViewModel.settingsManager.saveThemeVariant(newThemeVariant)
                        mainViewModel.settingsManager.saveTypographyVariant(newTypographyVariant)
                        themeVariant.value = newThemeVariant
                        typographyVariant.value = newTypographyVariant
                    }
                }
                MainScreen(
                    viewModel = mainViewModel,
                    showSettingsDialog = { showSettingsDialog.value = true },
                    toggleTheme = { mainViewModel.toggleTheme() }
                )
            }
        }
    }
}
