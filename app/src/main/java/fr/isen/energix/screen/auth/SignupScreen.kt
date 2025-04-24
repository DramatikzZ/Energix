package fr.isen.energix.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.energix.R
import fr.isen.energix.utils.AppUtil
import fr.isen.energix.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight


@Composable
fun SignupScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF089bac), Color(0xFF76d55d)),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                )
            )
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo et titre
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_energix),
                contentDescription = "logo",
                modifier = Modifier.height(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ENERGIX",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Bienvenue",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Créer mon compte",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Nom complet
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Nom complet") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Adresse e-mail") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            keyboardActions = KeyboardActions.Default
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Mot de passe") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Confirmer le mot de passe
        OutlinedTextField(
            value = confirmpassword,
            onValueChange = { confirmpassword = it },
            label = { Text(text = "Confirmer mot de passe") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bouton "Créer"
        OutlinedButton(
            onClick = {
                isLoading = true
                if (confirmpassword != password) {
                    isLoading = false
                    AppUtil.showToast(context, "Les mots de passe ne correspondent pas")
                } else {
                    authViewModel.signup(context, email, name, password) { success, errorMessage ->
                        if (success) {
                            isLoading = false
                            navController.navigate("login") {
                                popUpTo("auth") { inclusive = true }
                            }
                        } else {
                            isLoading = false
                            AppUtil.showToast(context, errorMessage ?: "Erreur inconnue")
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow,
                contentColor = Color.Black
            ),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(
                text = if (isLoading) "Création..." else "Créer",
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bouton de connexion
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Vous avez déjà un compte ?")
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "Cliquez ici")
            }
        }
    }
}
