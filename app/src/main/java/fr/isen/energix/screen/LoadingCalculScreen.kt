package fr.isen.energix.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.energix.R

@Composable
fun LoadingCalculScreen(navController: NavController) {
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo et titre ENERGIX
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_energix),
                contentDescription = "logo",
                modifier = Modifier.height(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ENERGIX",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Texte "Calculer ma conso"
        Text(
            text = "Calculer ma conso",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Roue de chargement
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = Color.White,
            strokeWidth = 6.dp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Veuillez patienter",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Analyse de votre consommation en cours...",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        )
    }
}
