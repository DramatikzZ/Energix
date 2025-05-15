package fr.isen.energix.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.energix.utils.TopBar

@Composable
fun SurveyScreen(modifier: Modifier = Modifier, navController: NavController) {
    var housingType by remember { mutableStateOf("") }
    var residentsCount by remember { mutableIntStateOf(1) }
    var objectives by remember { mutableStateOf<List<String>>(emptyList()) }

    val isFormValid = housingType.isNotBlank() && residentsCount > 0 && objectives.isNotEmpty()

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
        TopBar(modifier)

        Spacer(modifier = Modifier.height(20.dp))

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

        // Type de logement
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quel est votre type de logement?",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                listOf("Appartement", "Maison").forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = housingType == type,
                                onClick = { housingType = type }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = housingType == type, onClick = { housingType = type })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = type)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nombre d'habitants
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quel est le nombre d'habitants ?",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { if (residentsCount > 1) residentsCount-- }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = residentsCount.toString(), style = TextStyle(fontSize = 20.sp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { residentsCount++ }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                        Text("+")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Objectifs
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quels sont vos objectifs?",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
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
                navController.navigate("pieces") {
                    popUpTo("survey") { inclusive = true }
                }
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color.Yellow else Color.LightGray,
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
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
