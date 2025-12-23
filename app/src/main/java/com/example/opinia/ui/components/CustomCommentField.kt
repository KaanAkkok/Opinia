package com.example.opinia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaPurple
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily
import com.example.opinia.ui.theme.black
import com.example.opinia.ui.theme.gray

@Composable
fun CustomCommentField(
    comment: String,
    onCommentChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    isLoading: Boolean = false,
    isInputValid: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(OpinialightBlue)
            .padding(8.dp)
    ) {
        TextField(
            value = comment,
            onValueChange = { newText: String ->
                if (newText.length <= 500) {
                    onCommentChange(newText)
                }
            },
            placeholder = {
                Text(
                    text = "Add comment...",
                    color = gray,
                    fontFamily = WorkSansFontFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .wrapContentHeight(),
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = black,
                unfocusedTextColor = black,
                cursorColor = OpiniaDeepBlue,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(color = black, fontSize = 16.sp)
        )

        // Karakter Sayacı
        Text(
            text = "${comment.length}/500",
            color = gray,
            fontFamily = WorkSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 12.dp)
        )

        // Gönder Butonu
        IconButton(
            onClick = onSubmitComment,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .width(64.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(OpiniaPurple),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = OpinialightBlue,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Image(
                    painter = painterResource(
                        if (isInputValid)
                            com.example.opinia.R.drawable.send_filled
                        else
                            com.example.opinia.R.drawable.send
                    ),
                    contentDescription = "Send Comment",
                    modifier = Modifier
                        .size(36.dp)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CustomCommentfieldPreview() {
    CustomCommentField(
        comment = "",
        onCommentChange = {},
        onSubmitComment = {},
        modifier = Modifier
            .fillMaxWidth()
    )
}