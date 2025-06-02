package fr.isen.energix

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import fr.isen.energix.model.EquipementSelection

fun enregistrerEquipements(
    piece: String,
    equipements: List<EquipementSelection>, // ton modèle déjà défini
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    if (userId != null) {
        val docRef = db.collection("users").document(userId)

        val dataList = equipements.map {
            mapOf(
                "type" to it.type,
                "marque" to it.marque,
                "modele" to it.modele,
                "duree" to "⏱️ Temps d'utilisation", // Placeholder à remplacer plus tard
                "consommation" to Double.NaN          // Placeholder à calculer plus tard
            )
        }

        val fieldPath = "materielsSelectionnes.${piece}"

        docRef.update(fieldPath, dataList)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                // Si le champ n'existe pas encore, on le crée
                docRef.set(mapOf(fieldPath to dataList), SetOptions.merge())
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
    } else {
        onFailure(Exception("Utilisateur non connecté"))
    }
}
