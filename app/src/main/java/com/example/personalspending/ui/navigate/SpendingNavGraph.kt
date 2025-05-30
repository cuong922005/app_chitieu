package com.example.personalspending.ui.navigate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.personalspending.ui.screen.account.AccountDestination
import com.example.personalspending.ui.screen.account.AccountScreen
import com.example.personalspending.ui.screen.category.CategoryDestination
import com.example.personalspending.ui.screen.category.CategoryScreen
import com.example.personalspending.ui.screen.category.CreateCategoryScreen
import com.example.personalspending.ui.screen.chart.ChartDestination
import com.example.personalspending.ui.screen.chart.ChartScreen
import com.example.personalspending.ui.screen.details.DetailsDestination
import com.example.personalspending.ui.screen.details.DetailsScreen
import com.example.personalspending.ui.screen.home.HomeDestination
import com.example.personalspending.ui.screen.home.HomeScreen
import com.example.personalspending.ui.screen.login.FormLogin
import com.example.personalspending.ui.screen.login.FormRegister
import com.example.personalspending.ui.screen.login.HomeLoginScreen
import com.example.personalspending.ui.screen.login.LoginDestination
import com.example.personalspending.ui.screen.login.LoginHomeDestination
import com.example.personalspending.ui.screen.login.RegisterDestination
import com.example.personalspending.ui.screen.notify.NotifyDestination
import com.example.personalspending.ui.screen.notify.NotifyScreen
import com.example.personalspending.ui.screen.pay.PayDestination
import com.example.personalspending.ui.screen.pay.PayScreen
import com.example.personalspending.ui.screen.plan.PlanDestination
import com.example.personalspending.ui.screen.plan.PlanScreen
import com.example.personalspending.ui.screen.transaction.TransactionDestination
import com.example.personalspending.ui.screen.transaction.TransactionScreen

@Composable
fun SpendingNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LoginHomeDestination.route,
        modifier = modifier
    ) {
        composable( route = LoginHomeDestination.route ) {
            HomeLoginScreen(
                onHome = { navController.navigate("${HomeDestination.route}/${it}") },
                navigateToLogin = { navController.navigate(LoginDestination.route) },
                navigateToRegister = { navController.navigate(RegisterDestination.route) }
            )
        }

        composable( route = RegisterDestination.route ) {
            FormRegister(
                navigateUp = { navController.navigateUp() },
                navigateToHome = { navController.navigate("${HomeDestination.route}/${it}") }
            )
        }

        composable( route = LoginDestination.route ) {
            FormLogin(
                navigateUp = { navController.navigateUp() },
                navigateToHome = { navController.navigate("${HomeDestination.route}/${it}") }
            )
        }

        composable(
            route = HomeDestination.routeWithArgs,
            arguments = listOf(navArgument(HomeDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            HomeScreen(
                onHome = { navController.navigate("${HomeDestination.route}/${it}") },
                onAccount = { navController.navigate("${AccountDestination.route}/${it}")},
                onAddTransaction = { navController.navigate("${TransactionDestination.route}/${it}")},
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") },
                onChart = { navController.navigate("${ChartDestination.route}/${it}") },
                onPay = { navController.navigate("${PayDestination.route}/${it}") },
                onNotify = { navController.navigate("${NotifyDestination.route}/${it}") },
                onPlan = { navController.navigate("${PlanDestination.route}/${it}") },
                onLogout = { navController.navigate(LoginHomeDestination.route) },
                onTransactionDetails = { firstValue, secondValue ->
                    navController.navigate("${DetailsDestination.route}/${firstValue}/${secondValue}/${0}")
                },
            )
        }

        composable(
            route = TransactionDestination.routeWithArgs,
            arguments = listOf(navArgument(TransactionDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            TransactionScreen(
                onBack = { navController.navigateUp() },
                onHome = { navController.navigate("${HomeDestination.route}/${it}") },
                onCreate = { navController.navigate("${CategoryDestination.route}/${CategoryDestination.create}/${it}") },
            )
        }

        composable(
            route = DetailsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(DetailsDestination.userIdArg) { type = NavType.IntType },
                navArgument(DetailsDestination.spendIdArg) { type = NavType.IntType },
                navArgument(DetailsDestination.backPage) { type = NavType.IntType }
            )
        ) {backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(DetailsDestination.userIdArg) ?: 0
            val backPage = backStackEntry.arguments?.getInt(DetailsDestination.backPage) ?: 0
            DetailsScreen(
                id = userId,
                backPage = backPage,
                onHome = { navController.navigate("${HomeDestination.route}/${it}") },
                onBack = { navController.navigate("${ChartDestination.route}/${it}") }
            )
        }

        composable(
            route = AccountDestination.routeWithArgs,
            arguments = listOf(navArgument(AccountDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            AccountScreen(
                onAccount = { navController.navigate("${AccountDestination.route}/${it}")},
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") },
                onChart = { navController.navigate("${ChartDestination.route}/${it}") },
                onPay = { navController.navigate("${PayDestination.route}/${it}") },
                onNotify = { navController.navigate("${NotifyDestination.route}/${it}") },
                onPlan = { navController.navigate("${PlanDestination.route}/${it}") },
                onLogout = { navController.navigate(LoginHomeDestination.route) },
                onHome = { navController.navigate("${HomeDestination.route}/${it}")},
            )
        }

        composable(
            route = CategoryDestination.routeWithArgs,
            arguments = listOf(navArgument(HomeDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            CategoryScreen(
                onAccount = { navController.navigate("${AccountDestination.route}/${it}")},
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") },
                onChart = { navController.navigate("${ChartDestination.route}/${it}") },
                onPay = { navController.navigate("${PayDestination.route}/${it}") },
                onNotify = { navController.navigate("${NotifyDestination.route}/${it}") },
                onPlan = { navController.navigate("${PlanDestination.route}/${it}") },
                onLogout = { navController.navigate(LoginHomeDestination.route) },
                onHome = { navController.navigate("${HomeDestination.route}/${it}") },
                onCreate = { navController.navigate("${CategoryDestination.route}/${CategoryDestination.create}/${it}") }
            )
        }

        composable(
            route = CategoryDestination.routeWithArgsForCreate,
            arguments = listOf(navArgument(CategoryDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            CreateCategoryScreen(
                onBack = { navController.navigateUp() },
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") }
            )
        }

        composable(
            route = ChartDestination.routeWithArgs,
            arguments = listOf(navArgument(ChartDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            ChartScreen(
                onAccount = { navController.navigate("${AccountDestination.route}/${it}")},
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") },
                onChart = { navController.navigate("${ChartDestination.route}/${it}") },
                onPay = { navController.navigate("${PayDestination.route}/${it}") },
                onNotify = { navController.navigate("${NotifyDestination.route}/${it}") },
                onPlan = { navController.navigate("${PlanDestination.route}/${it}") },
                onLogout = { navController.navigate(LoginHomeDestination.route) },
                onHome = { navController.navigate("${HomeDestination.route}/${it}")},
                onTransactionDetails = { firstValue, secondValue ->
                    navController.navigate("${DetailsDestination.route}/$firstValue/$secondValue/${1}")
                }
            )
        }

        composable(
            route = PayDestination.routeWithArgs,
            arguments = listOf(navArgument(PayDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            PayScreen(
                onHome = { navController.navigate("${HomeDestination.route}/${it}")},
                onAccount = { navController.navigate("${AccountDestination.route}/${it}")},
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") },
                onChart = { navController.navigate("${ChartDestination.route}/${it}") },
                onPay = { navController.navigate("${PayDestination.route}/${it}") },
                onNotify = { navController.navigate("${NotifyDestination.route}/${it}") },
                onPlan = { navController.navigate("${PlanDestination.route}/${it}") },
                onLogout = { navController.navigate(LoginHomeDestination.route) },
                onCreateCategory = { navController.navigate("${CategoryDestination.route}/${CategoryDestination.create}/${it}") }
            )
        }

        composable(
            route = NotifyDestination.routeWithArgs,
            arguments = listOf(navArgument(NotifyDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            NotifyScreen(
                onHome = { navController.navigate("${HomeDestination.route}/${it}")},
                onAccount = { navController.navigate("${AccountDestination.route}/${it}")},
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") },
                onChart = { navController.navigate("${ChartDestination.route}/${it}") },
                onPay = { navController.navigate("${PayDestination.route}/${it}") },
                onNotify = { navController.navigate("${NotifyDestination.route}/${it}") },
                onPlan = { navController.navigate("${PlanDestination.route}/${it}") },
                onLogout = { navController.navigate(LoginHomeDestination.route) },
            )
        }

        composable(
            route = PlanDestination.routeWithArgs,
            arguments = listOf(navArgument(PlanDestination.userIdArg) {
                type = NavType.IntType
            })
        ) {
            PlanScreen(
                onHome = { navController.navigate("${HomeDestination.route}/${it}")},
                onAccount = { navController.navigate("${AccountDestination.route}/${it}")},
                onCategory = { navController.navigate("${CategoryDestination.route}/${it}") },
                onChart = { navController.navigate("${ChartDestination.route}/${it}") },
                onPay = { navController.navigate("${PayDestination.route}/${it}") },
                onNotify = { navController.navigate("${NotifyDestination.route}/${it}") },
                onPlan = { navController.navigate("${PlanDestination.route}/${it}") },
                onLogout = { navController.navigate(LoginHomeDestination.route) },
            )
        }
    }
}