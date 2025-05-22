package fr.isen.energix

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import fr.isen.energix.screen.pieces.BathroomScreen
import fr.isen.energix.screen.pieces.ChambreScreen
import fr.isen.energix.screen.pieces.CuisineScreen
import fr.isen.energix.screen.pieces.SalonScreen
import fr.isen.energix.viewmodel.PiecesViewModel


@Composable
fun AppNavigation(modifier : Modifier = Modifier) {

    val navController = rememberNavController()
    val isLoggedIn = Firebase.auth.currentUser!=null


    val start = if(isLoggedIn) "home" else  "auth"

    val piecesViewModel: PiecesViewModel = viewModel()


    NavHost(navController = navController, startDestination = start) {


        // Début authentification
        composable("auth") { AuthScreen(modifier, navController) }
        composable("login") { LoginScreen(modifier, navController) }
        composable("signup") { SignupScreen(modifier, navController) }
        composable("forgot") { ForgotPasswordScreen(modifier, navController) }
        // Fin authentification


        composable("home") { HomeScreen(modifier, navController) }
        composable("survey") { SurveyScreen(modifier, navController) }
        composable("loading") { LoadingCalculScreen(navController) }



        // Début Pièces Questionnaire
        composable("pieces") { PieceScreen(modifier, navController, viewModel = piecesViewModel) }
        composable("appareil/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val flatPieces = piecesViewModel.getFlatPieceListWithIndex()
            val pieceInfo = flatPieces.getOrNull(index)

            if (pieceInfo != null) {
                val (type, number) = pieceInfo
                when (type) {
                    "Cuisine" -> CuisineScreen(modifier, number, onNext = {
                        if (index + 1 < flatPieces.size) {
                            navController.navigate("appareil/${index + 1}")
                        } else {
                            navController.navigate("loading")
                        }
                    })
                    "Salon" -> SalonScreen(modifier, number, onNext = {
                        if (index + 1 < flatPieces.size) {
                            navController.navigate("appareil/${index + 1}")
                        } else {
                            navController.navigate("loading")
                        }
                    })
                    "Chambre" -> ChambreScreen(modifier, number, onNext = {
                        if (index + 1 < flatPieces.size) {
                            navController.navigate("appareil/${index + 1}")
                        } else {
                            navController.navigate("loading")
                        }
                    })
                    "Salle de bains" -> BathroomScreen(modifier, number, onNext = {
                        if (index + 1 < flatPieces.size) {
                            navController.navigate("appareil/${index + 1}")
                        } else {
                            navController.navigate("loading")
                        }
                    })
                }
            }
        }
        // Fin Pièces Questionnaire

    }
}