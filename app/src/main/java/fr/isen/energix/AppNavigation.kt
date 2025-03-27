package fr.isen.energix

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import fr.isen.energix.screen.*
import fr.isen.energix.screen.auth.AuthScreen
import fr.isen.energix.screen.auth.ForgotPasswordScreen
import fr.isen.energix.screen.auth.LoginScreen
import fr.isen.energix.screen.auth.SignupScreen

@Composable
fun AppNavigation(modifier : Modifier = Modifier) {

    val navController = rememberNavController()

    val isLoggedIn = Firebase.auth.currentUser!=null

    val start = if(isLoggedIn) "auth" else  "auth"

    NavHost(navController = navController, startDestination = start) {

        composable("auth") {
            AuthScreen(modifier, navController)
        }

        composable("login") {
            LoginScreen(modifier, navController)
        }

        composable("signup") {
            SignupScreen(modifier, navController)
        }

        composable("forgot") {
            ForgotPasswordScreen(modifier, navController)
        }

        composable("home") {
            HomeScreen(modifier, navController)
        }
    }
}