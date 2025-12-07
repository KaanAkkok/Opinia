package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.Course
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CourseRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    private val collectionName = "courses"
    private val TAG = "CourseRepository"

    //tüm dersleri verir
    suspend fun getAllCourses(): Result<List<Course>> {
        return try {
            val snapshot = firestore.collection(collectionName).get().await()
            val courses = snapshot.toObjects(Course::class.java)
            val sortedCourses = courses.sortedBy { it.courseCode }
            Log.d(TAG, "All courses retrieved")
            Result.success(sortedCourses)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving all courses", e)
            Result.failure(e)
        }
    }

    //id ye göre tek ders verir
    suspend fun getCourseById(courseId: String): Result<Course?> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(courseId).get().await()
            if (documentSnapshot.exists()) {
                val course = documentSnapshot.toObject(Course::class.java)
                Log.d(TAG, "Course retrieved successfully")
                Result.success(course)
            } else {
                Log.d(TAG, "Course not found")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving course", e)
            Result.failure(e)
        }
    }

    //student veya instructor ta bulunan enrolled veya given course için kullanılır
    suspend fun getCoursesByIds(courseIds: List<String>): Result<List<Course>> {
        if (courseIds.isEmpty()) {
            Log.d(TAG, "No course IDs provided or empty")
            return Result.success(emptyList())
        }
        return try {
            val snapshot = firestore.collection(collectionName).whereIn(FieldPath.documentId(), courseIds).get().await()
            val courses = snapshot.toObjects(Course::class.java)
            val sortedCourses = courses.sortedBy { it.courseCode }
            Log.d(TAG, "Courses retrieved successfully")
            Result.success(sortedCourses)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving courses", e)
            Result.failure(e)
        }
    }

    //seed işlemi için kullanılır
    //course yaratır
    suspend fun createCourse(course: Course): Result<Unit> {
        val lowerCaseCode = course.courseCode.trim().lowercase()
        val finalCourse = course.copy(searchCode = lowerCaseCode)
        return try {
            firestore.collection(collectionName).document(finalCourse.courseId).set(finalCourse).await()
            Log.d(TAG, "Course created successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating course", e)
            Result.failure(e)
        }
    }

    //dersin kodunu günceller
    suspend fun updateCourseCode(courseId: String, courseCode: String): Result<Unit> {
        val lowerCaseCode = courseCode.trim().lowercase()
        return try {
            firestore.collection(collectionName).document(courseId).update("courseCode", courseCode, "searchCode", lowerCaseCode).await()
            Log.d(TAG, "Course code updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating course code", e)
            Result.failure(e)
        }
    }

    //dersin adını günceller
    suspend fun updateCourseName(courseId: String, courseName: String): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(courseId).update("courseName", courseName).await()
            Log.d(TAG, "Course name updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating course name", e)
            Result.failure(e)
        }
    }

    //dersin akts değerini günceller
    suspend fun updateCourseAkts(courseId: String, akts: Int): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(courseId).update("akts", akts).await()
            Log.d(TAG, "Course akts updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating course akts", e)
            Result.failure(e)
        }
    }

    //dersin kredi değerini günceller
    suspend fun updateCourseCredits(courseId: String, credits: Int): Result<Unit> {
        return try {
            firestore.collection(collectionName).document(courseId).update("credits", credits).await()
            Log.d(TAG, "Course credits updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating course credits", e)
            Result.failure(e)
        }
    }

    //fakülteye bağlı dersleri çeker
    suspend fun getCoursesByFacultyId(facultyId: String): Result<List<Course>> {
        return try {
            val snapshot = firestore.collection(collectionName).whereEqualTo("facultyId", facultyId).get().await()
            val courses = snapshot.toObjects(Course::class.java)
            val sortedCourses = courses.sortedBy { it.courseCode }
            Log.d(TAG, "Courses retrieved successfully by faculty")
            Result.success(sortedCourses)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving courses by faculty", e)
            Result.failure(e)
        }
    }

    //departmana bağlı dersleri çeker
    suspend fun getCoursesByDepartmentId(departmentId: String): Result<List<Course>> {
        return try {
            val snapshot = firestore.collection(collectionName).whereArrayContains("departmentIds", departmentId).get().await()
            val courses = snapshot.toObjects(Course::class.java)
            val sortedCourses = courses.sortedBy { it.courseCode }
            Log.d(TAG, "Courses retrieved successfully by department")
            Result.success(sortedCourses)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving courses by department", e)
            Result.failure(e)
        }
    }

    //hocaya bağlı dersleri çeker
    suspend fun getCoursesByInstructorId(instructorId: String): Result<List<Course>> {
        return try {
            val snapshot = firestore.collection(collectionName).whereArrayContains("instructorIds", instructorId).get().await()
            val courses = snapshot.toObjects(Course::class.java)
            val sortedCourses = courses.sortedBy { it.courseCode }
            Log.d(TAG, "Courses retrieved successfully by department")
            Result.success(sortedCourses)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving courses by department", e)
            Result.failure(e)
        }
    }

    //dersin bağlı olduğu departmanları verir
    suspend fun getDepartmentIdsByCourseId(courseId: String): Result<List<String>> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(courseId).get().await()
            val departmentIds = documentSnapshot.get("departmentIds") as? List<String> ?: emptyList()
            Log.d(TAG, "Department IDs retrieved successfully")
            Result.success(departmentIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving department IDs", e)
            Result.failure(e)
        }
    }

    //dersin bağlı olduğu hocaları verir
    suspend fun getInstructorIdsByCourseId(courseId: String): Result<List<String>> {
        return try {
            val documentSnapshot = firestore.collection(collectionName).document(courseId).get().await()
            val instructorIds = documentSnapshot.get("instructorIds") as? List<String> ?: emptyList()
            Log.d(TAG, "Instructor IDs retrieved successfully")
            Result.success(instructorIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving instructor IDs", e)
            Result.failure(e)
        }
    }

    //ders arama
    suspend fun searchCourses(query: String): Result<List<Course>> {
        return try {
            val normalizedQuery = query.trim().lowercase()
            val snapshot = firestore.collection(collectionName).orderBy("searchCode").startAt(normalizedQuery).endAt(normalizedQuery + "\uf8ff").get().await()
            val courses = snapshot.toObjects(Course::class.java)
            Log.d(TAG, "Courses searched successfully")
            Result.success(courses)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching courses", e)
            Result.failure(e)
        }
    }
}