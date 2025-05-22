package fr.isen.energix.screen.pieces

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.*
import fr.isen.energix.utils.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().getReference()

    var voitures by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // (marque, marque - modele)
    var otherTransports by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val selections = remember { mutableStateMapOf<String, String>() }

    var isLoading by remember { mutableStateOf(true) }

    var voitureExpanded by remember { mutableStateOf(false) }
    var selectedVoiture by remember { mutableStateOf("") }
    var marqueQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        database.child("transports").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allVoitures = mutableListOf<Pair<String, String>>() // (marque, label)
                val autres = mutableMapOf<String, MutableList<String>>()

                for (typeSnapshot in snapshot.children) {
                    val type = typeSnapshot.key ?: continue

                    for (item in typeSnapshot.children) {
                        val marque = item.child("Marque").getValue(String::class.java) ?: continue
                        val modele = item.child("Modele").getValue(String::class.java) ?: continue
                        val label = "$marque - $modele"

                        if (type.equals("Voiture", ignoreCase = true)) {
                            allVoitures.add(marque to label)
                        } else {
                            autres.getOrPut(type) { mutableListOf() }.add(label)
                        }
                    }
                }

                voitures = allVoitures
                otherTransports = autres
                selections.clear()
                autres.keys.forEach { selections[it] = "" }

                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Erreur Firebase: ${error.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF089bac), Color(0xFF76d55d)),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(modifier)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Moyens de transport",
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // Onglet VOITURE
            Text(
                text = "Voiture",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = voitureExpanded,
                onExpandedChange = { voitureExpanded = !voitureExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedVoiture,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sélectionnez votre voiture", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                ExposedDropdownMenu(
                    expanded = voitureExpanded,
                    onDismissRequest = { voitureExpanded = false }
                ) {
                    // Champ de recherche en haut du menu
                    OutlinedTextField(
                        value = marqueQuery,
                        onValueChange = { marqueQuery = it },
                        label = { Text("Quelle est votre marque ?") },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Liste filtrée
                    voitures
                        .filter { it.first.contains(marqueQuery, ignoreCase = true) }
                        .forEach { (_, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedVoiture = label
                                    voitureExpanded = false
                                }
                            )
                        }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Autres moyens de transport (hors voiture)
            otherTransports.forEach { (type, modeles) ->
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    TextField(
                        value = selections[type] ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(type, fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        modeles.forEach { modele ->
                            DropdownMenuItem(
                                text = { Text(modele) },
                                onClick = {
                                    selections[type] = modele
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate("nextPage")
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
                    text = "Valider",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

