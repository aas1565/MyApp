package com.example.hw_3

import BottomBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.hw_3.viewmodel.FootballViewModel
import com.example.hw_3.favorites.FavoritesViewModel
import com.example.hw_3.filter.FilterBadgeProvider
import com.example.hw_3.filter.LocalFilterBadgeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}


@Composable
fun MainApp() {
    val navController = rememberNavController()
    var buttonsVisible by remember { mutableStateOf(true) }
    val footballViewModel: FootballViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()

    // Предоставляем FilterBadgeState через CompositionLocalProvider
    val filterBadgeState = FilterBadgeProvider.provideFilterBadgeState()

    CompositionLocalProvider(LocalFilterBadgeState provides filterBadgeState) {
        Scaffold(
            bottomBar = {
                if (buttonsVisible) {
                    BottomBar(
                        navController = navController,
                        state = buttonsVisible,
                        modifier = Modifier
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavigationGraph(
                    navController = navController,
                    onBottomBarVisibilityChanged = { isVisible ->
                        buttonsVisible = isVisible
                    },
                    footballViewModel = footballViewModel,
                    favoritesViewModel = favoritesViewModel
                )
            }
        }
    }
}