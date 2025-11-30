package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.Instructor
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InstructorRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    private val collectionName = "instructors"
    private val TAG = "InstructorRepository"

    suspend fun getInstructorById(instructorId: String): Result<Instructor?> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(instructorId).get().await()
            if (documentSnapshot.exists()) {
                val instructor = documentSnapshot.toObject(Instructor::class.java)
                Log.d(TAG, "Instructor retrieved successfully")
                Result.success(instructor)
            } else {
                Log.d(TAG, "Instructor not found")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving instructor", e)
            Result.failure(e)
        }
    }

    //search için query'i küçültüp searchName'e kaydeder
    //seed işlemi için kullanılacak
    suspend fun createInstructor(instructor: Instructor): Result<Unit> {
        val lowerCaseName = instructor.instructorName.lowercase()
        val finalInstructor = instructor.copy(searchName = lowerCaseName)
        return try {
            firestore.collection(collectionName).document(finalInstructor.instructorId).set(finalInstructor).await()
            Log.d(TAG, "Instructor created successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating instructor", e)
            Result.failure(e)
        }
    }

    suspend fun getFacultyIdByInstructorId(instructorId: String): Result<String?> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(instructorId).get().await()
            if (documentSnapshot.exists()) {
                val facultyId = documentSnapshot.getString("facultyId")
                Log.d(TAG, "Faculty ID retrieved successfully")
                return Result.success(facultyId)
            } else {
                Log.d(TAG, "Instructor not found")
                return Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving faculty ID", e)
            Result.failure(e)
        }
    }

    suspend fun getAllInstructors(): Result<List<Instructor>> {
        return try {
            val snapshot = firestore.collection(collectionName).get().await()
            val instructors = snapshot.toObjects(Instructor::class.java)
            val sortedInstructor = instructors.sortedBy { it.instructorName }
            Log.d(TAG, "All instructors retrieved")
            Result.success(sortedInstructor)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving all instructors", e)
            Result.failure(e)
        }
    }

    //course ta bulunan id listesi için kullanılır
    suspend fun getInstructorsByIds(instructorIds: List<String>): Result<List<Instructor>> {
        if (instructorIds.isEmpty()) {
            return Result.success(emptyList())
        }
        return try {
            val snapshot = firestore.collection(collectionName).whereIn(FieldPath.documentId(), instructorIds).get().await() //maks 30 id
            val instructors = snapshot.toObjects(Instructor::class.java)
            val sortedInstructor = instructors.sortedBy { it.instructorName }
            Log.d(TAG, "Instructors retrieved by IDs")
            Result.success(sortedInstructor)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving instructors by IDs", e)
            Result.failure(e)
        }
    }

    suspend fun searchInstructors(query: String): Result<List<Instructor>> {
        return try {
            val normalizedQuery = query.lowercase()
            val snapshot = firestore.collection(collectionName).orderBy("searchName").startAt(normalizedQuery).endAt(normalizedQuery + "\uf8ff").get().await()
            val instructors = snapshot.toObjects(Instructor::class.java)
            Log.d(TAG, "Instructors searched successfully")
            Result.success(instructors)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching instructors", e)
            Result.failure(e)
        }
    }

}
