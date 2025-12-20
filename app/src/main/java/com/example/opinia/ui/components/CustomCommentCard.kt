package com.example.opinia.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.R
import com.example.opinia.data.model.CommentReview
import com.example.opinia.ui.course.CommentAndStudent
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black

@Composable
fun CustomCommentCard(
    item: CommentAndStudent,
    modifier: Modifier = Modifier,
    isMyReview: Boolean = false,
    onDeleteClick: () -> Unit = {},
    containerColor: Color = OpinialightBlue
) {
    var isExpanded by remember { mutableStateOf(false) }
    val maxChars = 100
    val commentText = item.comment.comment
    val shouldShowMore = commentText.length > maxChars
    val formattedDate = item.comment.formattedDate

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = item.studentAvatarResId ?: R.drawable.ic_launcher_background),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(OpiniaGreyWhite)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${item.studentYear}",
                    fontFamily = WorkSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 8.sp,
                    color = black,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = formattedDate,
                            fontFamily = WorkSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 8.sp,
                            color = Color.DarkGray
                        )
                        if (isMyReview) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if(onDeleteClick != null) Icons.Outlined.Delete else Icons.Filled.Delete,
                                    contentDescription = "Delete Review",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(end = 50.dp)) {
                        Text(
                            text = "${item.studentName} ${item.studentSurname.trim().firstOrNull()?.toString() + "***"}",
                            fontFamily = WorkSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        RatingBar(rating = item.comment.rating, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val textToShow = if (isExpanded || !shouldShowMore) {
                    commentText
                } else {
                    commentText.take(maxChars) + "..."
                }

                Text(
                    text = textToShow,
                    fontFamily = WorkSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Color(0xFF333333),
                    lineHeight = 18.sp
                )

                if (shouldShowMore) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (isExpanded) "Show Less" else "More",
                            color = OpiniaDeepBlue,
                            fontFamily = WorkSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.CenterEnd) // Sağ alta yasla
                                .clickable { isExpanded = !isExpanded }
                                .padding(top = 4.dp, start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomCommentCardPreview() {
    CustomCommentCard(
        item = CommentAndStudent(
            comment = CommentReview(
                studentId = "123",
                courseId = "456",
                rating = 3,
                comment = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."
            ),
            studentName = "Aylin",
            studentSurname = "Keremaaşlı",
            studentYear = "8th Semester",
            studentAvatarResId = R.drawable.turuncu
        ),
        isMyReview = true,
        onDeleteClick = {}
    )
}