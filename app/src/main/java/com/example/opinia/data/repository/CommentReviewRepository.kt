package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.CommentReview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentReviewRepository @Inject constructor(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    private val collectionName = "comments_review"
    private val TAG = "CommentReviewRepository"

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    //yorum ve puan oluşturur ve ekler
    suspend fun createCommentReview(commentReview: CommentReview): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        val existingReviewQuery = firestore.collection(collectionName)
            .whereEqualTo("studentId", uid)
            .whereEqualTo("courseId", commentReview.courseId)
            .limit(1)
            .get()
            .await()
        if (!existingReviewQuery.isEmpty) {
            return Result.failure(Exception("User has already reviewed this course"))
        }
        return try {
            firestore.runTransaction { transaction ->
                val newCommentRef = firestore.collection(collectionName).document()
                val courseRef = firestore.collection("courses").document(commentReview.courseId)
                val courseSnapshot = transaction.get(courseRef)
                val currentAverage = courseSnapshot.getDouble("averageRating") ?: 0.0
                val currentTotal = courseSnapshot.getLong("totalReviews") ?: 0
                val newTotal = currentTotal + 1
                val newAverage = ((currentAverage * currentTotal) + commentReview.rating) / newTotal
                val finalComment = commentReview.copy(
                    commentId = newCommentRef.id,
                    studentId = uid,
                    timestamp = System.currentTimeMillis()
                )
                transaction.set(newCommentRef, finalComment)
                transaction.update(courseRef, "averageRating", newAverage)
                transaction.update(courseRef, "totalReviews", newTotal)
            }.await()
            Log.d(TAG, "Comment review created and course stats updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating comment review", e)
            Result.failure(e)
        }
    }

    //derslerin yorumlarını çeker
    suspend fun getCommentsByCourseId(courseId: String): Result<List<CommentReview>> {
        return try {
            val snapshot = firestore.collection(collectionName).whereEqualTo("courseId", courseId).get().await()
            val comments = snapshot.toObjects(CommentReview::class.java)
            val sortedComments = comments.sortedByDescending { it.timestamp }
            Log.d(TAG, "Comments retrieved and sorted successfully")
            Result.success(sortedComments)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving comments", e)
            Result.failure(e)
        }
    }

    //öğrencinin yaptığı yorumları çeker
    suspend fun getCommentsByStudentId(studentId: String): Result<List<CommentReview>> {
        return try {
            val snapshot = firestore.collection(collectionName).whereEqualTo("studentId", studentId).get().await()
            val comments = snapshot.toObjects(CommentReview::class.java)
            val sortedComments = comments.sortedByDescending { it.timestamp }
            Log.d(TAG, "Comments retrieved and sorted successfully")
            Result.success(sortedComments)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving comments", e)
            Result.failure(e)
        }
    }

    //(gerekli olursa) yorumu siler
    suspend fun deleteComment(commentId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.runTransaction { transaction ->
                val commentRef = firestore.collection(collectionName).document(commentId)
                val commentSnapshot = transaction.get(commentRef)
                if (!commentSnapshot.exists()) {
                    throw Exception("Comment not found")
                }
                val comment = commentSnapshot.toObject(CommentReview::class.java)
                    ?: throw Exception("Failed to parse comment")
                if (comment.studentId != uid) {
                    throw Exception("User is not the author of the comment")
                }
                val courseRef = firestore.collection("courses").document(comment.courseId)
                val courseSnapshot = transaction.get(courseRef)
                if (courseSnapshot.exists()) {
                    val ratingToDelete = comment.rating.toDouble()
                    val currentAverage = courseSnapshot.getDouble("averageRating") ?: 0.0
                    val currentTotal = courseSnapshot.getLong("totalReviews") ?: 0
                    val newTotal = if (currentTotal > 0) currentTotal - 1 else 0
                    val newAverage = if (newTotal > 0) {
                        ((currentAverage * currentTotal) - ratingToDelete) / newTotal
                    } else {
                        0.0
                    }
                    transaction.update(courseRef, "averageRating", newAverage)
                    transaction.update(courseRef, "totalReviews", newTotal)
                }
                transaction.delete(commentRef)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting comment", e)
            Result.failure(e)
        }
    }

}