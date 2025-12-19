package com.example.opinia.ui.catalog

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.component.BottomNavBar
import com.example.opinia.ui.components.CustomTopAppBar
import com.example.opinia.ui.components.SearchBar
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

// Yorum verisi için basit bir model (Normalde data/model altında olmalı)
data class CommentUiModel(
    val id: String,
    val userName: String,
    val userAvatarRes: Int,
    val date: String,
    val rating: Int,
    val text: String,
    val semester: String,
    val isHighlighted: Boolean = false
)

@Composable
fun CourseDetailContent(
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    navController: NavController,
    courseCode: String,
    courseName: String,
    rating: Double,
    reviewCount: Int,
    comments: List<CommentUiModel>,
    onAddCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OpiniaGreyWhite,
        topBar = {
            CustomTopAppBar(
                avatarResId = avatarResId,
                onAvatarClick = onAvatarClick,
                text = "Course Comments"
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCommentClick,
                containerColor = OpiniaPurple,
                contentColor = black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Add Comment")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(OpiniaGreyWhite)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Arama Çubuğu
            SearchBar(query = "", onQueryChange = {})

            Spacer(modifier = Modifier.height(24.dp))

            // --- HEADER: Ders Adı ve Bookmark ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$courseCode - $courseName",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = black,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color.Blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ISTATISTIK ALANI (Puan ve Grafik) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sol Taraf: Puan
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = rating.toString().replace('.', ','),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = black
                    )
                    Row {
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB74D), // Turuncu yıldız
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "$reviewCount review",
                        style = MaterialTheme.typography.bodySmall,
                        color = gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Sağ Taraf: Bar Grafiği
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .height(120.dp)
                        .padding(horizontal = 18.dp)
                ) {
                    BarItem(height = 20.dp, label = "1")
                    BarItem(height = 30.dp, label = "2")
                    BarItem(height = 40.dp, label = "3")
                }
            } // Istatistik Row'u burada biter

            Spacer(modifier = Modifier.height(32.dp))

            // --- YORUM LİSTESİ ---
            // LazyColumn, ana Column'un içinde olmalı
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ekranda kalan boşluğu doldurur
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
            ) {
                items(comments) { comment ->
                    CommentCard(comment = comment)
                }
            }
        }
    }
}

// --- DÜZELTİLMİŞ YERLEŞİM: FONKSİYONLAR DIŞARI TAŞINDI ---

@Composable
fun BarItem(height: Dp, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom // Çubuğu aşağı yasla
    ) {
        // ÇUBUK (BOX)
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(height)
                .background(
                    color = OpiniaPurple,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        // ETİKET (1, 2, 3)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = black
        )
    }
}

@Composable
fun CommentCard(comment: CommentUiModel) {
    val borderColor = if (comment.isHighlighted) OpinialightBlue else Color.Transparent
    val borderWidth = if (comment.isHighlighted) 2.dp else 0.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        // HATALI SATIR DÜZELTİLDİ: 'contain' yerine 'containerColor' ve bir renk belirtildi.
        colors = CardDefaults.cardColors(containerColor = OpiniaPurple.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Üst Kısım: Avatar, İsim, Tarih, Yıldızlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Image(
                    painter = painterResource(id = comment.userAvatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = black
                    )
                    Text(
                        text = comment.semester,
                        style = MaterialTheme.typography.labelSmall,
                        color = gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = comment.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = gray
                    )
                    Row {
                        repeat(comment.rating) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Yorum Metni
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodySmall,
                color = black.copy(alpha = 0.7f),
                maxLines = 3
            )

            // "More" linki
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = "More",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = black
                )
            }
        }
    }
}
@Composable
fun CourseDetailScreen(
    navController: NavController,
    viewModel: CourseDetailViewModel
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    CourseDetailContent(
        avatarResId = uiState.avatarResId ?: R.drawable.turuncu,
        onAvatarClick = { navController.navigate(Destination.STUDENT_PROFILE.route) },
        navController = navController,
        courseCode = uiState.courseCode,
        courseName = uiState.courseName,
        rating = uiState.rating,
        reviewCount = uiState.reviewCount,
        comments = uiState.comments,
        onAddCommentClick = {
            Toast.makeText(context, "Open Add Comment Dialog", Toast.LENGTH_SHORT).show()
        },
        onBookmarkClick = {
            viewModel.toggleBookmark()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CourseDetailPreview() {
    val mockComments = listOf(
        CommentUiModel("1", "Aylin K***", R.drawable.turuncu, "08/11/23", 3, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh...", "2024 Fall"),
        CommentUiModel("2", "Sıla K***", R.drawable.mor, "08/11/23", 3, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit...", "2024 Fall", isHighlighted = true),

    )

    CourseDetailContent(
        avatarResId = R.drawable.turuncu,
        onAvatarClick = {},
        navController = rememberNavController(),
        courseCode = "VCD 111",
        courseName = "Basic Drawing",
        rating = 2.8,
        reviewCount = 4,
        comments = mockComments,
        onAddCommentClick = {},
        onBookmarkClick = {}
    )
}