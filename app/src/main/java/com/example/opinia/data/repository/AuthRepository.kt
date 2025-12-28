package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.utils.NetworkManager
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthState {
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
}

@Singleton
class AuthRepository @Inject constructor(private val auth: FirebaseAuth, private val networkManager: NetworkManager) {

    private val TAG = "AuthRepository"

    val authState: Flow<AuthState> = callbackFlow {
        trySend(AuthState.Loading)
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                trySend(AuthState.Unauthenticated)
            }
            else {
                trySend(AuthState.Authenticated)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            if (!networkManager.isInternetAvailable()) {
                return Result.failure(Exception("No internet connection"))
            }
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d(TAG, "Login successful")
            return Result.success(Unit)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthInvalidUserException -> "User not found with this email"
                is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                else -> "Unkown error"
            }
            Log.e(TAG, "Login failed: $error")
            Result.failure(Exception(error))
        }
    }

    suspend fun signup(email: String, password: String): Result<Unit> {
        return try {
            if (!networkManager.isInternetAvailable()) {
                return Result.failure(Exception("No internet connection"))
            }
            auth.createUserWithEmailAndPassword(email, password).await()
            Log.d(TAG, "Signup successful")
            return Result.success(Unit)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthUserCollisionException -> "This student email is already registered"
                is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                is FirebaseAuthWeakPasswordException -> "Password should be at least 8 characters"
                else -> "Unkown error"
            }
            Log.e(TAG, "Signup failed with error: $error")
            Result.failure(Exception(error))
        }
    }

    suspend fun sendVerificationEmail(email: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("User not authenticated"))
            if (!networkManager.isInternetAvailable()) {
                return Result.failure(Exception("No internet connection"))
            }
            user.sendEmailVerification().await()
            Log.d(TAG, "Verification email sent")
            Result.success(Unit)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthInvalidUserException -> "User not found with this email"
                is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                is FirebaseAuthUserCollisionException -> "This student email is already registered"
                is FirebaseTooManyRequestsException -> "Too many requests. Try again later"
                else -> "Unkown error"
            }
            Log.e(TAG, "Verification email failed: $error")
            Result.failure(Exception(error))
        }
    }

    suspend fun checkEmailVerification(): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("User not authenticated"))
            if (!networkManager.isInternetAvailable()) {
                return Result.failure(Exception("No internet connection"))
            }
            user.reload().await()
            val isVerified = user.isEmailVerified
            Log.d(TAG, "Email verification checked: $isVerified")
            Result.success(isVerified)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthInvalidUserException -> "User not found with this email"
                else -> "Unkown error"
            }
            Log.e(TAG, "Email verification check failed: $error")
            Result.failure(Exception(error))
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            if (!networkManager.isInternetAvailable()) {
                return Result.failure(Exception("No internet connection"))
            }
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent")
            Result.success(Unit)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthInvalidUserException -> "User not found with this email"
                is FirebaseAuthInvalidCredentialsException -> "This student email is not registered"
                else -> "Unkown error"
            }
            Log.e(TAG, "Password reset failed: $error")
            Result.failure(Exception(error))
        }
    }

    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            if (!networkManager.isInternetAvailable()) {
                return Result.failure(Exception("No internet connection"))
            }
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Log.d(TAG, "Password updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid current password"
                is FirebaseAuthWeakPasswordException -> "New password should be at least 8 characters"
                else -> "Unkown error"
            }
            Log.e(TAG, "Password update failed: $error")
            Result.failure(Exception(error))
        }
    }

    fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Log.d(TAG, "Logout successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Logout failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(password: String): Result<Unit> {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return Result.failure(Exception("User not authenticated"))
        val uid = user.uid

        return try {
            if (!networkManager.isInternetAvailable()) {
                return Result.failure(Exception("No internet connection"))
            }
            val userEmail = user.email ?: return Result.failure(Exception("Email not found"))
            val credential = EmailAuthProvider.getCredential(userEmail, password)
            user.reauthenticate(credential).await()
            //öğrencinin tüm yorumları
            val reviewsSnapshot = db.collection("comments_review").whereEqualTo("studentId", uid).get().await()
            db.runTransaction { transaction ->
                //tüm okumalar
                val uniqueCourseIds = reviewsSnapshot.documents.mapNotNull { it.getString("courseId") }.distinct()
                val courseMap = mutableMapOf<String, DocumentSnapshot>()
                for (cid in uniqueCourseIds) {
                    val ref = db.collection("courses").document(cid)
                    courseMap[cid] = transaction.get(ref)
                }
                //tüm yazmalar
                for (reviewDoc in reviewsSnapshot.documents) {
                    val courseId = reviewDoc.getString("courseId")
                    val ratingToRemove = reviewDoc.getDouble("rating") ?: 0.0
                    if (courseId != null && courseMap.containsKey(courseId)) {
                        val courseSnapshot = courseMap[courseId]!!
                        val courseRef = courseSnapshot.reference
                        if (courseSnapshot.exists()) {
                            val oldTotal = courseSnapshot.getLong("totalReviews") ?: 0
                            val oldAverage = courseSnapshot.getDouble("averageRating") ?: 0.0
                            if (oldTotal > 1) {
                                val newTotal = oldTotal - 1
                                val newAverage = ((oldAverage * oldTotal) - ratingToRemove) / newTotal
                                transaction.update(courseRef, "totalReviews", newTotal)
                                transaction.update(courseRef, "averageRating", newAverage)
                            } else {
                                transaction.update(courseRef, "totalReviews", 0)
                                transaction.update(courseRef, "averageRating", 0.0)
                            }
                        }
                    }
                    transaction.delete(reviewDoc.reference)
                }
                val student = db.collection("students").document(uid)
                transaction.delete(student)
            }.await()
            user.delete().await()
            Log.d(TAG, "Account deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid password"
                else -> e.message ?: "Unknown error"
            }
            Log.e(TAG, "Account deletion failed: $error")
            Result.failure(Exception(error))
        }
    }

    suspend fun deleteUnverifiedUser() {
        try {
            val user = auth.currentUser ?: return
            user.delete().await()
            Log.d(TAG, "Unverified user deleted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Unverified user deletion failed: ${e.message}")
        }
    }
}