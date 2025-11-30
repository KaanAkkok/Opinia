package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FacultyDepartmentRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    private val collectionFacultyName = "faculties"
    private val collectionDepartmentName = "departments"
    private val TAG = "FacultyDepartmentRepository"

    suspend fun getFacultyById(facultyId: String): Result<Faculty?> {
        return try {
            val documentSnapshot = firestore.collection(collectionFacultyName).document(facultyId).get().await()
            if (documentSnapshot.exists()) {
                val faculty = documentSnapshot.toObject(Faculty::class.java)
                Log.d(TAG, "Faculty retrieved successfully")
                Result.success(faculty)
            } else {
                Log.d(TAG, "Faculty not found")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving faculty", e)
            Result.failure(e)
        }
    }

    suspend fun getDepartmentById(departmentId: String): Result<Department?> {
        return try {
            val documentSnapshot = firestore.collection(collectionDepartmentName).document(departmentId).get().await()
            if (documentSnapshot.exists()) {
                val department = documentSnapshot.toObject(Department::class.java)
                Log.d(TAG, "Department retrieved successfully")
                Result.success(department)
            } else {
                Log.d(TAG, "Department not found")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving department", e)
            Result.failure(e)
        }
    }

    suspend fun getAllFaculties(): Result<List<Faculty>> {
        return try {
            val snapshot = firestore.collection(collectionFacultyName).get().await()
            val faculties = snapshot.toObjects(Faculty::class.java)
            val sortedFaculties = faculties.sortedBy { it.facultyName }
            Log.d(TAG, "Faculties retrieved successfully")
            Result.success(sortedFaculties)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving faculties", e)
            Result.failure(e)
        }
    }

    suspend fun getDepartmentsByFaculty(facultyId: String): Result<List<Department>> {
        return try {
            val snapshot = firestore.collection(collectionDepartmentName).whereEqualTo("facultyId", facultyId).get().await()
            val departments = snapshot.toObjects(Department::class.java)
            val sortedDepartments = departments.sortedBy { it.departmentName }
            Log.d(TAG, "Departments retrieved successfully")
            Result.success(sortedDepartments)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving departments", e)
            Result.failure(e)
        }
    }

    suspend fun getAllDepartments(): Result<List<Department>> {
        return try {
            val snapshot = firestore.collection(collectionDepartmentName).get().await()
            val departments = snapshot.toObjects(Department::class.java)
            val sortedDepartments = departments.sortedBy { it.departmentName }
            Log.d(TAG, "Departments retrieved successfully")
            Result.success(sortedDepartments)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving departments", e)
            Result.failure(e)
        }

    }

}