package fr.isen.energix.screen.pieces

import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import fr.isen.energix.model.EquipementSelection
import fr.isen.energix.utils.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuisineScreen(modifier: Modifier, number: Int, onNext: () -> Unit) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().getReference("cuisine")

    var allData by remember { mutableStateOf<Map<String, List<Map<String, String>>>>(emptyMap()) }
    var selectedType by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf<Map<String, String>?>(null) }
    var selectedTime by remember { mutableStateOf("") }

    var availableBrands by remember { mutableStateOf<List<String>>(emptyList()) }
    var availableModels by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val selections = remember { mutableStateListOf<Map<String, Any>>() }

    val timeOptions = listOf(
        "Je ne sais pas", "5 min", "10 min", "20 min", "30 min",
        "1h", "1h30", "2h", "3h", "5h", "7h", "10h", "15h", "24h"
    )

    fun convertToHours(time: String): Double? {
        return when {
            time == "Je ne sais pas" -> null
            time.contains("min") -> time.replace(" min", "").toIntOrNull()?.div(60.0)
            time.contains("h") -> {
                val parts = time.split("h")
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
                hours + (minutes / 60.0)
            }
            else -> null
        }
    }

    LaunchedEffect(Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableMapOf<String, MutableList<Map<String, String>>>()
                for (typeSnapshot in snapshot.children) {
                    val type = typeSnapshot.key ?: continue
                    val items = mutableListOf<Map<String, String>>()
                    for (item in typeSnapshot.children) {
                        val map = item.children.associate { it.key!! to it.value.toString() }
                        items.add(map)
                    }
                    result[type] = items
                }
                allData = result
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Erreur Firebase : ${error.message}", Toast.LENGTH_LONG).show()
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
            text = "Cuisine n°$number - Ajouter un équipement",
            style = TextStyle(
                fontSize = 22.sp,
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
            // TYPE
            var expandedType by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = !expandedType }) {
                TextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type d’équipement") },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                    allData.keys.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                selectedBrand = ""
                                selectedModel = null
                                selectedTime = ""
                                availableBrands = allData[type]?.map { it["Marque"] ?: "" }?.distinct() ?: emptyList()
                                expandedType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // MARQUE
            if (selectedType.isNotEmpty()) {
                var expandedBrand by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandedBrand, onExpandedChange = { expandedBrand = !expandedBrand }) {
                    TextField(
                        value = selectedBrand,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Marque") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(expanded = expandedBrand, onDismissRequest = { expandedBrand = false }) {
                        availableBrands.forEach { brand ->
                            DropdownMenuItem(
                                text = { Text(brand) },
                                onClick = {
                                    selectedBrand = brand
                                    selectedModel = null
                                    selectedTime = ""
                                    availableModels = allData[selectedType]?.filter { it["Marque"] == brand } ?: emptyList()
                                    expandedBrand = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // MODÈLE
            if (selectedBrand.isNotEmpty()) {
                var expandedModel by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandedModel, onExpandedChange = { expandedModel = !expandedModel }) {
                    TextField(
                        value = selectedModel?.get("Modele") ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Modèle") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(expanded = expandedModel, onDismissRequest = { expandedModel = false }) {
                        availableModels.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model["Modele"] ?: "") },
                                onClick = {
                                    selectedModel = model
                                    expandedModel = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TEMPS
            if (selectedModel != null && selectedModel?.any { it.key.contains("W", true) } == true) {
                var expandedTime by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandedTime, onExpandedChange = { expandedTime = !expandedTime }) {
                    TextField(
                        value = selectedTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Temps d'utilisation") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(expanded = expandedTime, onDismissRequest = { expandedTime = false }) {
                        timeOptions.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    selectedTime = t
                                    expandedTime = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val duree = selectedTime
                    val heure = convertToHours(duree)

                    val puissanceStr = selectedModel?.entries?.find {
                        it.key.contains("puissance", ignoreCase = true) && it.key.contains("w", ignoreCase = true)
                    }?.value

                    val kwhStr = selectedModel?.entries?.find {
                        it.key.contains("kWh", ignoreCase = true) && it.key.contains("annuelle", ignoreCase = true)
                    }?.value

                    val puissance = puissanceStr?.replace(",", ".")?.toDoubleOrNull()
                    val kwhAn = kwhStr?.replace(",", ".")?.toDoubleOrNull()

                    val consommation = if (puissance != null && heure != null) {
                        (puissance * heure * 365) / 1000
                    } else {
                        kwhAn ?: 0.0
                    }


                    selections.add(
                        mapOf(
                            "type" to selectedType,
                            "marque" to selectedBrand,
                            "modele" to (selectedModel?.get("Modele") ?: "Inconnu"),
                            "duree" to duree,
                            "consommation" to String.format("%.2f", consommation).replace(",", ".").toDouble()
                        )
                    )

                    selectedType = ""
                    selectedBrand = ""
                    selectedModel = null
                    selectedTime = ""
                    availableBrands = emptyList()
                    availableModels = emptyList()
                },
                enabled = selectedType.isNotEmpty() && selectedBrand.isNotEmpty() && selectedModel != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajouter un appareil", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selections.isNotEmpty()) {
                Text("Équipements ajoutés :", style = TextStyle(fontWeight = FontWeight.Bold))
                selections.forEach {
                    Text("- ${it["type"]} : ${it["marque"]} - ${it["modele"]} (${it["duree"]})", modifier = Modifier.padding(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@OutlinedButton
                    FirebaseFirestore.getInstance().collection("users")
                        .whereEqualTo("uid", uid)
                        .get().addOnSuccessListener { snap ->
                            if (!snap.isEmpty) {
                                val doc = snap.documents[0].reference
                                doc.set(
                                    mapOf("materielsSelectionnes" to mapOf("Cuisines" to selections)),
                                    SetOptions.merge()
                                ).addOnSuccessListener {
                                    Toast.makeText(context, "Appareils enregistrés", Toast.LENGTH_SHORT).show()
                                    onNext()
                                }
                            }
                        }
                },
                enabled = selections.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Yellow,
                    contentColor = Color.Black
                )
            ) {
                Text("Valider", fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
