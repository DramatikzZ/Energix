package fr.isen.energix.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import fr.isen.energix.model.UserModel
import fr.isen.energix.utils.AppUtil

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val firestore = Firebase.firestore

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {

        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, "Certaines cases sont vides")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.localizedMessage)
                }
            }
    }

    fun signup(context: Context, email : String, name : String, password : String, onResult: (Boolean, String?) -> Unit) {

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            onResult(false, "Certaines cases sont vides")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val userId = it.result?.user?.uid
                    val userModel = UserModel(name, email, userId!!)
                    firestore.collection("users")
                        .document()
                        .set(userModel)
                        .addOnCompleteListener { dbTask ->
                            sendEmailVerification {success, message ->
                                if (!success) {
                                    AppUtil.showToast(context, "Erreur : $message")
                                }
                            }
                            if(dbTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, "Something went wrong")
                            }
                    }
                } else {
                    onResult(false, it.exception?.localizedMessage)
                }
            }
    }

    fun changePassword(email: String, onResult: (Boolean, String?)-> Unit) {
        if (email.isEmpty()) {
            onResult(false, "Veuillez entrer une adresse e-mail")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Email de réinitialisation envoyé")
                } else {
                    onResult(false, task.exception?.localizedMessage ?: "Une erreur est survenue")
                }
            }
    }

    private fun sendEmailVerification(onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Email de vérification envoyé. Veuillez vérifier votre boîte mail.")
                } else {
                    onResult(false, task.exception?.localizedMessage ?: "Échec de l'envoi de l'email de vérification.")
                }
            }
    }

    fun checkEmailVerification(onResult: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (user.isEmailVerified) {
                    onResult(true, "✅ Email vérifié !")
                } else {
                    onResult(false, "❌ Email non vérifié. Veuillez vérifier votre boîte mail.")
                }
            } else {
                onResult(false, "⚠ Erreur lors de la vérification de l'email.")
            }
        }
    }
}