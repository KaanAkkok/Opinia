package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepository @Inject constructor(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    private val collectionName = "students"
    private val TAG = "StudentRepository"

    //oturum açmış olan kullanıcının id sini getir
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    //öğrenci yaratır (auth id ile aynı olacak şekilde)
    suspend fun createStudent(student: Student): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val finalStudent = student.copy(studentId = uid)
            firestore.collection(collectionName).document(uid).set(finalStudent).await()
            Log.d(TAG, "Student created or updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating student", e)
            Result.failure(e)
        }
    }

    suspend fun checkIfStudentExists(studentId: String): Result<Boolean> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(studentId).get().await()
            val exists = documentSnapshot.exists()
            Log.d(TAG, "Student check result: $exists")
            Result.success(exists)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking student existence", e)
            Result.failure(e)
        }
    }

    //oturum açmış olan kullanıcının öğrenci bilgilerini verir
    suspend fun getStudentProfile(): Result<Student?> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(uid).get().await()
            if (documentSnapshot.exists()) {
                val student = documentSnapshot.toObject(Student::class.java)
                Result.success(student)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //id ye göre tek öğrenci verir
    suspend fun getStudentById(studentId: String): Result<Student?> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(studentId).get().await()
            if (documentSnapshot.exists()) {
                val student = documentSnapshot.toObject(Student::class.java)
                Log.d(TAG, "Student retrieved successfully")
                Result.success(student)
            } else {
                Log.d(TAG, "Student not found")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving student", e)
            Result.failure(e)
        }
    }

    //öğrenci profil fotoğrafını günceller
    suspend fun updateProfileAvatar(avatarKey: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("studentProfileAvatar", avatarKey).await()
            Log.d(TAG, "Profile avatar updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile avatar", e)
            Result.failure(e)
        }
    }

    //öğrencinin adını günceller
    suspend fun updateStudentName(studentName: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("studentName", studentName).await()
            Log.d(TAG, "Student name updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student name", e)
            Result.failure(e)
        }
    }

    //öğrencinin soyadını günceller
    suspend fun updateStudentSurname(studentSurname: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("studentSurname", studentSurname).await()
            Log.d(TAG, "Student surname updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student surname", e)
            Result.failure(e)
        }
    }

    //öğrencinin dönemini günceller
    suspend fun updateStudentYear(studentYear: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("studentYear", studentYear).await()
            Log.d(TAG, "Student year updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student year", e)
            Result.failure(e)
        }
    }

    //öğrencinin fakültesini günceller
    suspend fun updateStudentFaculty(facultyId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("facultyID", facultyId).await()
            Log.d(TAG, "Student faculty updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student faculty", e)
            Result.failure(e)
        }
    }

    //öğrencinin departmanını günceller
    suspend fun updateStudentDepartment(departmentId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("departmentID", departmentId).await()
            Log.d(TAG, "Student department updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student department", e)
            Result.failure(e)
        }
    }

    //öğrenciyi yeni derse kaydeder
    suspend fun enrollStudentToCourse(courseId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("enrolledCourseIds", FieldValue.arrayUnion(courseId)).await()
            Log.d(TAG, "Student enrolled to course successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error enrolling student to course", e)
            Result.failure(e)
        }
    }

    //öğrenciyi dersden çıkarır
    suspend fun dropStudentFromCourse(courseId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("enrolledCourseIds", FieldValue.arrayRemove(courseId)).await()
            Log.d(TAG, "Student dropped from course successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error dropping student from course", e)
            Result.failure(e)
        }
    }

    // Öğrencinin bir dersi favorilere/kaydedilenlere eklemesi
    // (Önceki saveCommentReview yerine geldi)
    suspend fun saveCourse(courseId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("savedCourseIds", FieldValue.arrayUnion(courseId)).await()
            Log.d(TAG, "Course saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving course", e)
            Result.failure(e)
        }
    }

    // Öğrencinin bir dersi favorilerden/kaydedilenlerden çıkarması
    // (Önceki unsaveCommentReview yerine geldi)
    suspend fun unsaveCourse(courseId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection(collectionName).document(uid).update("savedCourseIds", FieldValue.arrayRemove(courseId)).await()
            Log.d(TAG, "Course unsaved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error unsaving course", e)
            Result.failure(e)
        }
    }

    //öğrencinin kaydettiği tüm dersleri verir
    suspend fun getEnrolledCoursesIds(studentId: String): Result<List<String>> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(studentId).get().await()
            val enrolledCourseIds = documentSnapshot.get("enrolledCourseIds") as? List<String> ?: emptyList()
            Log.d(TAG, "Enrolled courses retrieved successfully")
            Result.success(enrolledCourseIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving enrolled courses", e)
            Result.failure(e)
        }
    }

    // Öğrencinin favorilediği/takip ettiği derslerin ID'lerini getirir
    // (Önceki getSavedCommentReviewIds yerine geldi)
    suspend fun getSavedCourseIds(): Result<List<String>> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(uid).get().await()
            val savedCourseIds = documentSnapshot.get("savedCourseIds") as? List<String> ?: emptyList()
            Log.d(TAG, "Saved course IDs retrieved successfully")
            Result.success(savedCourseIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving saved course IDs", e)
            Result.failure(e)
        }
    }

}