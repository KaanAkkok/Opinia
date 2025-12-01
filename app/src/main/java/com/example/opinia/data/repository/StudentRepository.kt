package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.Student
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    private val collectionName = "students"
    private val TAG = "StudentRepository"

    //öğrenci yaratır (auth id ile aynı olacak şekilde)
    suspend fun createStudent(student: Student, studentAuthUid: String): Result<Unit> {
        return try {
            val finalStudent = student.copy(studentId = studentAuthUid)
            firestore.collection(collectionName).document(finalStudent.studentId).set(student).await()
            Log.d(TAG, "Student created or updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating or updating student", e)
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
    suspend fun updateProfileAvatar(studentId: String, avatarKey: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("studentProfileAvatar", avatarKey).await()
            Log.d(TAG, "Profile avatar updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile avatar", e)
            Result.failure(e)
        }
    }

    //öğrencinin adını günceller
    suspend fun updateStudentName(studentId: String, studentName: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("studentName", studentName).await()
            Log.d(TAG, "Student name updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student name", e)
            Result.failure(e)
        }
    }

    //öğrencinin soyadını günceller
    suspend fun updateStudentSurname(studentId: String, studentSurname: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("studentSurname", studentSurname).await()
            Log.d(TAG, "Student surname updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student surname", e)
            Result.failure(e)
        }
    }

    //öğrencinin dönemini günceller
    suspend fun updateStudentYear(studentId: String, studentYear: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("studentYear", studentYear).await()
            Log.d(TAG, "Student year updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student year", e)
            Result.failure(e)
        }
    }

    //öğrencinin fakültesini günceller
    suspend fun updateStudentFaculty(studentId: String, facultyId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("facultyID", facultyId).await()
            Log.d(TAG, "Student faculty updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student faculty", e)
            Result.failure(e)
        }
    }

    //öğrencinin departmanını günceller
    suspend fun updateStudentDepartment(studentId: String, departmentId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("departmentID", departmentId).await()
            Log.d(TAG, "Student department updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student department", e)
            Result.failure(e)
        }
    }

    //öğrenciyi yeni derse kaydeder
    suspend fun enrollStudentToCourse(studentId: String, courseId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("enrolledCourseIds", FieldValue.arrayUnion(courseId)).await()
            Log.d(TAG, "Student enrolled to course successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error enrolling student to course", e)
            Result.failure(e)
        }
    }

    //öğrenciyi dersden çıkarır
    suspend fun dropStudentFromCourse(studentId: String, courseId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("enrolledCourseIds", FieldValue.arrayRemove(courseId)).await()
            Log.d(TAG, "Student dropped from course successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error dropping student from course", e)
            Result.failure(e)
        }
    }

    //öğrencinin yorumlarını kaydeder
    suspend fun saveCommentReview(studentId: String, commentReviewId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("savedCommentReviewIds", FieldValue.arrayUnion(commentReviewId)).await()
            Log.d(TAG, "Comment review saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving comment review", e)
            Result.failure(e)
        }
    }

    //öğrencinin kaydettiği yorumları siler
    suspend fun unsaveCommentReview(studentId: String, commentReviewId: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(studentId).update("savedCommentReviewIds", FieldValue.arrayRemove(commentReviewId)).await()
            Log.d(TAG, "Comment review unsaved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving comment review", e)
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

    //öğrencinin kaydettiği tüm yorumları verir
    suspend fun getSavedCommentReviewIds(studentId: String): Result<List<String>> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(studentId).get().await()
            val savedCommentReviewIds = documentSnapshot.get("savedCommentReviewIds") as? List<String> ?: emptyList()
            Log.d(TAG, "Saved comment reviews retrieved successfully")
            Result.success(savedCommentReviewIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving saved comment reviews", e)
            Result.failure(e)
        }
    }

}