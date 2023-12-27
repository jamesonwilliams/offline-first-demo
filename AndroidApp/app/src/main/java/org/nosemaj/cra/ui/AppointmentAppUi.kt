package org.nosemaj.cra.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.nosemaj.cra.ui.details.AppointmentDetailScreen
import org.nosemaj.cra.ui.list.AppointmentListScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            AppointmentListScreen { appointmentId ->
                navController.navigate("detail/$appointmentId")
            }
        }
        composable(
            route = "detail/{appointmentId}",
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) {
            AppointmentDetailScreen {
                navController.navigateUp()
            }
        }
    }
}