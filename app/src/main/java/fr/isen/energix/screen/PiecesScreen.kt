package fr.isen.energix.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import fr.isen.energix.utils.TopBar
import fr.isen.energix.viewmodel.PiecesViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector



data class PieceItem(val type: String, val count: Int = 0)

@Composable
fun PieceScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: PiecesViewModel = viewModel()
) {
    val pieces = listOf("Salon", "Cuisine", "Chambre", "Salle de bains", "Transports")
    val piecesCount = remember {
        mutableStateMapOf<String, Int>().apply {
            pieces.forEach { put(it, 0) }
        }
    }

    Column(
        modifier = modifier
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
            LazyColumn {
                items(pieces) { piece ->
                    val count = piecesCount[piece] ?: 0
                    PieceItemRow(
                        piece = PieceItem(piece, count),
                        onIncrement = {
                            piecesCount[piece] = count + 1
                        },
                        onDecrement = {
                            if (count > 0) {
                                piecesCount[piece] = count - 1
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    val selectedPieces = piecesCount.entries
                        .filter { it.value > 0 }
                        .map { PieceItem(it.key, it.value) }

                    viewModel.setPieces(selectedPieces)
                    val flatPieces = viewModel.getFlatPieceListWithIndex()

                    val authUid = FirebaseAuth.getInstance().currentUser?.uid
                    val db = FirebaseFirestore.getInstance()

                    if (authUid != null) {
                        db.collection("users")
                            .whereEqualTo("uid", authUid)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    val userDocId = documents.documents.first().id
                                    val piecesMap = selectedPieces.associate { it.type to it.count }

                                    db.collection("users").document(userDocId)
                                        .set(mapOf("piecesSelectionnees" to piecesMap), SetOptions.merge())
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Pièces enregistrées")
                                            if (flatPieces.isNotEmpty()) {
                                                navController.navigate("appareil/0")
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.e("Firestore", "Erreur enregistrement pièces : ${it.message}")
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Log.e("Firestore", "Erreur lors de la recherche utilisateur : ${it.message}")
                            }
                    }
                },
                enabled = piecesCount.any { it.value > 0 },
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

@Composable
fun PieceItemRow(
    piece: PieceItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val icon: ImageVector = when (piece.type) {
        "Salon" -> Icons.Default.Weekend
        "Cuisine" -> Icons.Default.Kitchen
        "Chambre" -> Icons.Default.Bed
        "Salle de bains" -> Icons.Default.Bathtub
        "Transports" -> Icons.Default.DirectionsCar
        else -> Icons.Default.Home
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    color = Color.White.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = piece.type,
                        tint = Color(0xFF089bac),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = piece.type,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                        color = Color.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = RoundedCornerShape(50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Moins",
                            tint = Color(0xFF089bac)
                        )
                    }

                    Text(
                        text = piece.count.toString(),
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                        color = Color.Black
                    )

                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = RoundedCornerShape(50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter",
                            tint = Color(0xFF76d55d)
                        )
                    }
                }
            }
        }
    }
}

