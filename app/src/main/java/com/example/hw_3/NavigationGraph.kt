package com.example.hw_3

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hw_3.screens.Screen1
import com.example.hw_3.viewmodel.FootballViewModel


@Composable
fun NavigationGraph(
    navController: NavHostController,
    onBottomBarVisibilityChanged: (Boolean) -> Unit,
    footballViewModel: FootballViewModel
) {
    NavHost(navController, startDestination = Routes.Welcome.route) {
        composable(Routes.Welcome.route) {
            onBottomBarVisibilityChanged(false)
            Welcome(navController = navController)
        }
        composable(Routes.Screen1.route) {
            onBottomBarVisibilityChanged(true)
            Screen1(navController = navController, viewModel = footballViewModel) // ← ПЕРЕДАЕМ
        }
        composable(Routes.Screen2.route) {
            onBottomBarVisibilityChanged(true)
            Screen2()
        }
        composable(Routes.Screen3.route) {
            onBottomBarVisibilityChanged(true)
            Screen3(navController = navController)
        }
        // ДОБАВЬТЕ ЭТОТ МАРШРУТ
        composable("editProfile") {
            onBottomBarVisibilityChanged(false)
            EditProfileScreen(navController = navController)
        }
        composable(
            route = Routes.MatchDetail.route,
            arguments = listOf(navArgument("matchId") {
                type = androidx.navigation.NavType.IntType
            })
        ) { backStackEntry ->
            onBottomBarVisibilityChanged(false)
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: 0
            MatchDetailScreen(
                matchId = matchId,
                navController = navController,
                viewModel = footballViewModel
            )
        }
    }
}