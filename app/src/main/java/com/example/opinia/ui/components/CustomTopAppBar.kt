package com.example.opinia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.R
import com.example.opinia.ui.theme.NunitoFontFamily
import com.example.opinia.ui.theme.OpiniaGreyWhite
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    avatarResId: Int,
    onAvatarClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                modifier = modifier
                    .background(OpiniaGreyWhite)
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.lacivert_amblem),
                        contentDescription = "Logo",
                        modifier = modifier.size(50.dp)
                    )
                    Spacer(modifier = modifier.width(6.dp))
                    Text(
                        text = text,
                        color = black,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                // SAĞ: Avatar
                Image(
                    painter = painterResource(id = avatarResId),
                    contentDescription = "Profile Avatar",
                    modifier = modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(OpinialightBlue)
                        .clickable { onAvatarClick() }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = OpiniaGreyWhite,
            scrolledContainerColor = OpiniaGreyWhite
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 36.dp)
    )
}