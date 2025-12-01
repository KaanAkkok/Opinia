package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.Instructor
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InstructorRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    private val collectionName = "instructors"
    private val TAG = "InstructorRepository"

    //id ye göre tek hoca verir
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

    //tüm hocaları verir
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

    //search için query'i küçültüp searchName'e kaydeder
    //seed işlemi için kullanılacak
    suspend fun createInstructor(instructor: Instructor): Result<Unit> {
        val lowerCaseName = instructor.instructorName.trim().lowercase()
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

    //hocaya ders ekler
    suspend fun addCourseToGivenCourseIds(instructorId: String, courseId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(instructorId).update("givenCourseIds", FieldValue.arrayUnion(courseId)).await()
            Log.d(TAG, "Course added to givenCourseIds successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding course to givenCourseIds", e)
            Result.failure(e)
        }
    }

    //hocanın bağlı olduğu dersi çıkarır
    suspend fun removeCourseToGivenCourseIds(instructorId: String, courseId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(instructorId).update("givenCourseIds", FieldValue.arrayRemove(courseId)).await()
            Log.d(TAG, "Course removed to givenCourseIds successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing course to givenCourseIds", e)
            Result.failure(e)
        }
    }

    //hocanın isimini günceller (soyadı da dahil)
    suspend fun updateInstructorName(instructorId: String, instructorName: String): Result<Unit> {
        val lowerCaseName = instructorName.trim().lowercase()
        return try {
            firestore.collection(collectionName).document(instructorId).update("instructorName",  instructorName, "searchName", lowerCaseName).await()
            Log.d(TAG, "Instructor name updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating instructor name", e)
            Result.failure(e)
        }
    }

    //hocanın e-posta adresini günceller
    suspend fun updateInstructorEmail(instructorId: String, instructorEmail: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(instructorId).update("instructorEmail", instructorEmail).await()
            Log.d(TAG, "Instructor email updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating instructor email", e)
            Result.failure(e)
        }
    }

    //hocanın ünvanını günceller
    suspend fun updateInstructorTitle(instructorId: String, instructorTitle: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(instructorId).update("instructorTitle", instructorTitle).await()
            Log.d(TAG, "Instructor title updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating instructor title", e)
            Result.failure(e)
        }
    }

    //hocanın bağlı olduğu fakültenin id sini verir
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

    //fakülteye bağlı hocaları çeker
    suspend fun getInstructorsByFacultyId(facultyId: String): Result<List<Instructor>> {
        return try {
            val snapshot = firestore.collection(collectionName).whereEqualTo("facultyId", facultyId).get().await()
            val instructors = snapshot.toObjects(Instructor::class.java)
            val sortedInstructors = instructors.sortedBy { it.instructorName }
            Log.d(TAG, "Instructors retrieved successfully by faculty")
            Result.success(sortedInstructors)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving instructors by faculty", e)
            Result.failure(e)
        }
    }

    //course ta bulunan instructorIds listesi için kullanılır
    suspend fun getInstructorsByIds(instructorIds: List<String>): Result<List<Instructor>> {
        if (instructorIds.isEmpty()) {
            Log.d(TAG, "No instructor IDs provided or empty")
            return Result.success(emptyList())
        }
        return try {
            val snapshot = firestore.collection(collectionName).whereIn(FieldPath.documentId(), instructorIds).get().await() //maks 30 id
            val instructors = snapshot.toObjects(Instructor::class.java)
            val sortedInstructors = instructors.sortedBy { it.instructorName }
            Log.d(TAG, "Instructors retrieved by IDs")
            Result.success(sortedInstructors)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving instructors by IDs", e)
            Result.failure(e)
        }
    }

    //dersi veren hocaları çekmek için
    suspend fun getInstructorsByCourseId(courseId: String): Result<List<Instructor>> {
        return try {
            val snapshot = firestore.collection(collectionName).whereArrayContains("givenCourseIds", courseId).get().await()
            val instructors = snapshot.toObjects(Instructor::class.java)
            val sortedInstructors = instructors.sortedBy { it.instructorName }
            Log.d(TAG, "Instructors retrieved successfully by course")
            Result.success(sortedInstructors)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving instructors", e)
            Result.failure(e)
        }
    }

    //hocaların verdiği dersleri verir
    suspend fun getCourseIdsByInstructorId(instructorId: String): Result<List<String>> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(instructorId).get().await()
            val courseIds = documentSnapshot.get("givenCourseIds") as? List<String> ?: emptyList()
            Log.d(TAG, "Course IDs retrieved successfully")
            Result.success(courseIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving course IDs", e)
            Result.failure(e)
        }
    }

    //hocaları arama
    suspend fun searchInstructors(query: String): Result<List<Instructor>> {
        return try {
            val normalizedQuery = query.trim().lowercase()
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
