package com.billbook.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.billbook.ui.screens.*

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddTransaction : Screen("add_transaction")
    data object Statistics : Screen("statistics")
    data object Budget : Screen("budget")
    data object Settings : Screen("settings")
}

@Composable
fun BillBookNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddClick = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateToStats = { navController.navigate(Screen.Statistics.route) },
                onNavigateToBudget = { navController.navigate(Screen.Budget.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Budget.route) {
            BudgetScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
