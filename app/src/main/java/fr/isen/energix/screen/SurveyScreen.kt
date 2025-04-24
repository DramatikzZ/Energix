package fr.isen.energix.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.energix.R
import fr.isen.energix.utils.TopBar

@Composable
fun SurveyScreen(modifier: Modifier = Modifier, navController: NavController) {
    var housingType by remember { mutableStateOf("") }
    var residentsCount by remember { mutableIntStateOf(1) }
    var objectives by remember { mutableStateOf<List<String>>(emptyList()) }

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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo et titre ENERGIX

        TopBar(modifier)

        Spacer(modifier = Modifier.height(20.dp))

        // Titre du formulaire
        Text(
            text = "Créer votre profil énergétique",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Section Type de logement
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Quel est votre type de logement?",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = housingType == "Appartement",
                            onClick = { housingType = "Appartement" }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = housingType == "Appartement",
                        onClick = { housingType = "Appartement" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Appartement")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = housingType == "Maison",
                            onClick = { housingType = "Maison" }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = housingType == "Maison",
                        onClick = { housingType = "Maison" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Maison")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section Nombre d'habitants
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Quel est le nombre d'habitants ?",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (residentsCount > 1) residentsCount-- },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("-")
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = residentsCount.toString(),
                        style = TextStyle(fontSize = 20.sp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { residentsCount++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("+")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section Objectifs
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Quels sont vos objectifs?",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                val options = listOf(
                    "Réduire ma facture",
                    "Avoir un impact moindre sur la planète",
                    "Autre"
                )

                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = objectives.contains(option),
                                onClick = {
                                    objectives = if (objectives.contains(option)) {
                                        objectives - option
                                    } else {
                                        objectives + option
                                    }
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = objectives.contains(option),
                            onClick = {
                                objectives = if (objectives.contains(option)) {
                                    objectives - option
                                } else {
                                    objectives + option
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Bouton Suivant
        OutlinedButton(
            onClick = {
                // Envoyer d'abord les données à Firebase
                // Puis naviguer vers l'écran de chargement
                navController.navigate("pieces") {
                    popUpTo("survey") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Suivant",
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}