package com.example.opinia.ui.search

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.gray
import com.example.opinia.data.model.Instructor

@Composable
fun GeneralSearchBarContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToInstructor: (Instructor) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.searchQuery) {
        if (uiState.searchQuery.isNotEmpty()) {
            isExpanded = true
        }
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!isExpanded) isExpanded = true
                }
        ) {
            TextField(
                value = uiState.searchQuery,
                onValueChange = { onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .focusRequester(focusRequester)
                    .border(
                        width = 2.dp,
                        color = OpinialightBlue,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                shape = MaterialTheme.shapes.extraLarge,

                placeholder = {
                    Text(
                        text = "search",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = Color(0xFF1E2223)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = OpiniaDeepBlue,
                    )
                },
                trailingIcon = {
                    if (isExpanded && uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            onQueryChange("")
                            isExpanded = false
                        }
                    ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close icon",
                                tint = OpiniaDeepBlue
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = OpiniaDeepBlue,
                    unfocusedTextColor = OpiniaDeepBlue,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = OpiniaDeepBlue,
                    focusedIndicatorColor = OpiniaDeepBlue,
                    unfocusedIndicatorColor = OpinialightBlue,
                    disabledIndicatorColor = Color.LightGray
                ),
                singleLine = true,
                enabled = true,
                readOnly = !isExpanded
            )
            if (!isExpanded) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { isExpanded = true }
                )
            }
        }

        if (isExpanded && uiState.searchQuery.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = OpinialightBlue)
            ) {

                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = gray,
                        trackColor = Color.Transparent
                    )
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    if(uiState.searchResultsCourses.isNotEmpty()) {
                        item {
                            Text(
                                text = "Courses",
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = OpiniaDeepBlue,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        items(uiState.searchResultsCourses) { course ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToCourse(course.courseId) }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = course.courseCode,
                                    fontFamily = WorkSansFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = black
                                )
                                Text(
                                    text = course.courseName,
                                    fontFamily = WorkSansFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = black
                                )
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color(0XFFF5F5F5),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }

                    if(uiState.searchResultsInstructors.isNotEmpty()) {
                        item {
                            Text(
                                text = "Instructors",
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = OpiniaDeepBlue,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        items(uiState.searchResultsInstructors) { instructor ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToInstructor(instructor) }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = instructor.instructorTitle + " " + instructor.instructorName,
                                    fontFamily = WorkSansFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = black
                                )
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color(0XFFF5F5F5),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }

                    if (!uiState.isLoading &&
                        uiState.searchResultsCourses.isEmpty() &&
                        uiState.searchResultsInstructors.isEmpty()
                    ) {
                        item {
                            Text(
                                text = "No results found for \"${uiState.searchQuery}\"",
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                color = gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralSearchBar(
    searchViewModel: SearchViewModel,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToInstructor: (Instructor) -> Unit
) {
    val uiState by searchViewModel.uiState.collectAsState()
    GeneralSearchBarContent(
        uiState = uiState,
        onQueryChange = searchViewModel::onQueryChange,
        onNavigateToCourse = onNavigateToCourse,
        onNavigateToInstructor = onNavigateToInstructor
    )
}

@Preview(showBackground = true)
@Composable
fun GeneralSearchBarPreview() {
    GeneralSearchBarContent(
        uiState = SearchUiState(),
        onQueryChange = {},
        onNavigateToCourse = {},
        onNavigateToInstructor = {}
    )
}
