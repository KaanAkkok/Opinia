package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FacultyDepartmentRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    private val collectionFacultyName = "faculties"
    private val collectionDepartmentName = "departments"
    private val TAG = "FacultyDepartmentRepository"

    //id ye göre tek fakülte verir
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

    //fakülte yaratır
    //seed işlemi için kullanılır
    suspend fun createFaculty(faculty: Faculty): Result<Unit> {
        return try {
            firestore.collection(collectionFacultyName).document(faculty.facultyId).set(faculty).await()
            Log.d(TAG, "Faculty created successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating faculty", e)
            Result.failure(e)
        }
    }

    //fakültenin adını günceller
    suspend fun updateFacultyName(facultyId: String, facultyName: String): Result<Unit> {
        return try {
            firestore.collection(collectionFacultyName).document(facultyId).update("facultyName", facultyName).await()
            Log.d(TAG, "Faculty name updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating faculty name", e)
            Result.failure(e)
        }
    }

    //id ye göre tek departman verir
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

    //departman yaratır
    //seed işlemi için kullanılır
    suspend fun createDepartment(department: Department): Result<Unit> {
        return try {
            firestore.collection(collectionDepartmentName).document(department.departmentId).set(department).await()
            Log.d(TAG, "Department created successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating department", e)
            Result.failure(e)
        }
    }

    //departmanın adını günceller
    suspend fun updateDepartmentName(departmentId: String, departmentName: String): Result<Unit> {
        return try {
            firestore.collection(collectionDepartmentName).document(departmentId).update("departmentName", departmentName).await()
            Log.d(TAG, "Department name updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating department name", e)
            Result.failure(e)
        }
    }

    //tüm fakülteleri verir
    suspend fun getAllFaculties(): Result<List<Faculty>> {
        return try {
            val documentSnapshot = firestore.collection(collectionFacultyName).get().await()
            val faculties = documentSnapshot.toObjects(Faculty::class.java)
            val sortedFaculties = faculties.sortedBy { it.facultyName }
            Log.d(TAG, "Faculties retrieved successfully")
            Result.success(sortedFaculties)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving faculties", e)
            Result.failure(e)
        }
    }

    //tüm departmanları verir
    suspend fun getAllDepartments(): Result<List<Department>> {
        return try {
            val documentSnapshot = firestore.collection(collectionDepartmentName).get().await()
            val departments = documentSnapshot.toObjects(Department::class.java)
            val sortedDepartments = departments.sortedBy { it.departmentName }
            Log.d(TAG, "Departments retrieved successfully")
            Result.success(sortedDepartments)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving departments", e)
            Result.failure(e)
        }

    }

    //fakülteye bağlı departmanları verir
    suspend fun getDepartmentsByFaculty(facultyId: String): Result<List<Department>> {
        return try {
            val documentSnapshot = firestore.collection(collectionDepartmentName).whereEqualTo("facultyId", facultyId).get().await()
            val departments = documentSnapshot.toObjects(Department::class.java)
            val sortedDepartments = departments.sortedBy { it.departmentName }
            Log.d(TAG, "Departments retrieved successfully")
            Result.success(sortedDepartments)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving departments", e)
            Result.failure(e)
        }
    }

    //course ta bulunan departmantIds listesi için kullanılır
    //instructor ta bulunan departmentIds listesi için kullanılır
    suspend fun getDepartmentsByIds(departmentIds: List<String>): Result<List<Department>> {
        if (departmentIds.isEmpty()) {
            Log.d(TAG, "No department IDs provided or empty")
            return Result.success(emptyList())
        }
        return try {
            val documentSnapshot = firestore.collection(collectionDepartmentName).whereIn(FieldPath.documentId(), departmentIds).get().await() //maks 30 id
            val departments = documentSnapshot.toObjects(Department::class.java)
            val sortedDepartments = departments.sortedBy { it.departmentName }
            Log.d(TAG, "Departments retrieved successfully")
            Result.success(sortedDepartments)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving departments by IDs", e)
            Result.failure(e)
        }
    }

}