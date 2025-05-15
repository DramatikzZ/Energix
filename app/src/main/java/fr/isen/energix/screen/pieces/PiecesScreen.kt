package fr.isen.energix.screen.pieces

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.isen.energix.utils.TopBar

data class PieceItem(val type: String, var count: Int = 1)

@Composable
fun PieceScreen(modifier: Modifier, navController: NavHostController) {
    var selectedPiece by remember { mutableStateOf("") }
    val selectedPieces = remember { mutableStateListOf<PieceItem>() }

    val isFormValid = selectedPieces.isNotEmpty()

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
                    selectedPiece = piece
                    val index = selectedPieces.indexOfFirst { it.type == piece }
                    if (index != -1) {
                        val existing = selectedPieces[index]
                        selectedPieces[index] = existing.copy(count = existing.count + 1)
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
                            selectedPieces[index] = pieceItem.copy(count = pieceItem.count + 1)
                        },
                        onDecrement = {
                            if (pieceItem.count > 1) {
                                selectedPieces[index] = pieceItem.copy(count = pieceItem.count - 1)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate("salon")
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieceDropdown(
    selectedPiece: String,
    onPieceSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pieces = listOf("Salon", "Cuisine", "Chambre")
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
                    }
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
            Text(text = piece.type, style = MaterialTheme.typography.bodyLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement) {
                    Text("-", fontSize = 20.sp)
                }
                Text(
                    text = piece.count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = onIncrement) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter")
                }
            }
        }
    }
}
