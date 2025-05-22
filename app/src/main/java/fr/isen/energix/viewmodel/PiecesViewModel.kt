package fr.isen.energix.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import fr.isen.energix.screen.PieceItem

class PiecesViewModel : ViewModel() {

    private val pieces = mutableStateListOf<PieceItem>()

    fun setPieces(newPieces: List<PieceItem>) {
        pieces.clear()
        pieces.addAll(newPieces)
    }

    fun getFlatPieceListWithIndex(): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        val counts = mutableMapOf<String, Int>()

        for (piece in pieces) {
            repeat(piece.count) {
                val count = (counts[piece.type] ?: 0) + 1
                counts[piece.type] = count
                result.add(piece.type to count)
            }
        }

        return result
    }
}
