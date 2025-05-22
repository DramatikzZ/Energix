package fr.isen.energix.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.energix.utils.TopBar
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.energix.viewmodel.PiecesViewModel


data class PieceItem(val type: String, var count: Int = 1)

@Composable
fun PieceScreen(modifier: Modifier, navController: NavHostController, viewModel: PiecesViewModel = viewModel()) {
    val selectedPiece by remember { mutableStateOf("") }
    val selectedPieces = remember { mutableStateListOf<PieceItem>() }

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
    ) {
        TopBar(modifier)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Dropdown
            PieceDropdown(
                selectedPiece = selectedPiece,
                onPieceSelected = { piece ->
                    val index = selectedPieces.indexOfFirst { it.type == piece }
                    if (index != -1) {
                        val existing = selectedPieces[index]
                        val updated = existing.copy(count = existing.count + 1)
                        selectedPieces[index] = updated
                    } else {
                        selectedPieces.add(PieceItem(piece))
                    }

                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Liste des pièces sélectionnées
            LazyColumn {
                itemsIndexed(selectedPieces) { index, pieceItem ->
                    PieceItemRow(
                        piece = pieceItem,
                        onIncrement = {
                            val updated = pieceItem.copy(count = pieceItem.count + 1)
                            selectedPieces[index] = updated
                        },
                        onDecrement = {
                            if (pieceItem.count > 1) {
                                val updated = pieceItem.copy(count = pieceItem.count - 1)
                                selectedPieces[index] = updated
                            }
                        }
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    viewModel.setPieces(selectedPieces) // on enregistre dans le ViewModel
                    val flatPieces = viewModel.getFlatPieceListWithIndex()
                    if (flatPieces.isNotEmpty()) {
                        navController.navigate("appareil/0")
                    }


                },
                enabled = selectedPieces.isNotEmpty(), // a changer potentiellement, je vérifie seulement si il y 1 seule pièce de sélectionnée
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieceDropdown(
    selectedPiece: String,
    onPieceSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pieces = listOf("Salon", "Cuisine", "Chambre", "Salle de bains")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedPiece,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sélectionnez une pièce") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            pieces.forEach { piece ->
                DropdownMenuItem(
                    text = { Text(piece) },
                    onClick = {
                        onPieceSelected(piece)
                        expanded = false
                    },
                    colors = MenuItemColors(
                        textColor = Color.Black,
                        leadingIconColor = Color.White,
                        trailingIconColor = Color.White,
                        disabledTextColor = Color.White,
                        disabledLeadingIconColor = Color.White,
                        disabledTrailingIconColor = Color.White
                    )
                )
            }
        }
    }
}


@Composable
fun PieceItemRow(
    piece: PieceItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nom de la pièce
            Text(text = piece.type, style = MaterialTheme.typography.bodyLarge)

            // Compteur + boutons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement) {
                    Icon(Icons.Default.Remove , contentDescription = "Moins")
                }
                Text(
                    text = piece.count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = onIncrement) {
                    Icon(Icons.Default.Add, contentDescription = "Plus")
                }
            }
        }
    }
}