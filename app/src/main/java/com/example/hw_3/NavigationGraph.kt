package com.example.hw_3

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import com.example.hw_3.favorites.FavoritesViewModel
import com.example.hw_3.data.Match
import com.example.hw_3.screens.Screen1
import com.example.hw_3.viewmodel.FootballViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    onBottomBarVisibilityChanged: (Boolean) -> Unit,
    footballViewModel: FootballViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    NavHost(navController = navController, startDestination = Routes.Welcome.route) {
        composable(Routes.Welcome.route) {
            onBottomBarVisibilityChanged(false)
            Welcome(navController = navController)
        }
        composable(Routes.Screen1.route) {
            onBottomBarVisibilityChanged(true)
            Screen1(viewModel = footballViewModel, navController = navController)
        }
        composable(Routes.Screen2.route) {
            onBottomBarVisibilityChanged(true)
            Screen2(favoritesViewModel = favoritesViewModel)
        }
        composable(Routes.Screen3.route) {
            onBottomBarVisibilityChanged(true)
            Screen3()
        }
        composable(
            route = Routes.MatchDetail.route,
            arguments = listOf(navArgument("matchId") { type = NavType.IntType })
        ) { backStackEntry ->
            onBottomBarVisibilityChanged(true)
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: 0
            val match: Match? = footballViewModel.matches.value.firstOrNull { it.id == matchId }
            if (match != null) {
                MatchDetailScreen(match = match, navController = navController, favoritesViewModel = favoritesViewModel)
            } else {
                // Fallback to old screen if not found
                com.example.hw_3.MatchDetailScreen(matchId = matchId, navController = navController, viewModel = footballViewModel)
            }
        }
        composable(Routes.Filter.route) {
            onBottomBarVisibilityChanged(false)
            com.example.hw_3.screens.FilterScreen(
                navController = navController,
                viewModel = footballViewModel
            )
        }
    }
}