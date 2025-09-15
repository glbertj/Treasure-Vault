package edu.bluejack24_1.treasurevault.repository

import android.net.Uri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack24_1.treasurevault.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object UserRepository {
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val storageRef = FirebaseStorage.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): String = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                val snapshot = usersRef.child(userId).get().await()
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    User.userId = userId
                    User.email = user.email
                    User.profilePictureUrl = user.profilePictureUrl
                    ""
                } else {
                    "User not found in database"
                }
            } else {
                "Login failed: User ID is null"
            }
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> "No account found with this email address."
                else -> "Login failed: ${e.localizedMessage}"
            }
        } catch (e: Exception) {
            "Login failed: ${e.localizedMessage}"
        }
    }

    suspend fun register(email: String, password: String): String = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                val userId = user.uid
                val userData = mapOf(
                    "email" to email,
                    "id" to userId
                )

                usersRef.child(userId).setValue(userData).await()
                ""
            } else {
                "User creation failed"
            }
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already in use."
                "ERROR_WEAK_PASSWORD" -> "Password is too weak."
                "ERROR_INVALID_EMAIL" -> "Invalid email address."
                else -> "Registration failed: ${e.localizedMessage}"
            }
        } catch (e: Exception) {
            "Registration failed: ${e.localizedMessage}"
        }
    }

    fun logout() {
        auth.signOut()
        User.clearSession()
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): String = withContext(Dispatchers.IO) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            try {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                ""
            } catch (e: FirebaseAuthException) {
                when (e.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> "Incorrect current password"
                    else -> "Password change failed: ${e.localizedMessage}"
                }
            } catch (e: Exception) {
                "Password change failed: ${e.localizedMessage}"
            }
        } else {
            "No authenticated user found"
        }
    }

    fun uploadProfilePicture(userId: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageReference = storageRef.child("profile_pictures/$userId")
        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { e ->
                    onFailure(e)
                }
            }.addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun updateUserProfile(userId: String, profilePictureUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")
        userRef.child("profilePictureUrl").setValue(profilePictureUrl)
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                onFailure(e)
            }
    }
}