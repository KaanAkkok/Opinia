package com.example.opinia.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue

//UNUSED FOR NOW
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .width(270.dp)
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
            Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = OpiniaDeepBlue)
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
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    SearchBar(query = "", onQueryChange = {})
}