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
import com.google.firebase.database.*
import fr.isen.energix.utils.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BathroomScreen(modifier: Modifier, number: Int, onNext: () -> Unit) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().getReference()
    var equipements by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val selections = remember { mutableStateMapOf<String, String>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        database.child("salle de bain").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableMapOf<String, MutableList<String>>()

                for (typeSnapshot in snapshot.children) {
                    val type = typeSnapshot.key ?: continue
                    val modelesList = mutableListOf<String>()

                    for (item in typeSnapshot.children) {
                        val marque = item.child("Marque").getValue(String::class.java)
                        val modele = item.child("Modele").getValue(String::class.java)

                        val label = when {
                            !marque.isNullOrBlank() && !modele.isNullOrBlank() -> "$marque - $modele"
                            else -> "$type (aucun modèle)"
                        }

                        modelesList.add(label)
                    }

                    if (modelesList.isNotEmpty()) {
                        result[type] = modelesList
                    }
                }

                equipements = result
                selections.clear()
                result.keys.forEach { selections[it] = "" }
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
            text = "Salle de bain n°$number - Équipements",
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
            equipements.forEach { (type, modeles) ->
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
                onClick = onNext,
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
