package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.utils.NetworkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
            auth.currentUser?.sendEmailVerification()?.await() // doğrulama maili gönderme (gerekli değil ise kaldıralım)
            Log.d(TAG, "Signup successful")
            return Result.success(Unit)
        } catch (e: Exception) {
            val error = when(e) {
                is FirebaseAuthUserCollisionException -> "This student email is already registered"
                is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                is FirebaseAuthWeakPasswordException -> "Password should be at least 6 characters"
                else -> "Unkown error"
            }
            Log.e(TAG, "Signup failed with error: $error")
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

}