package com.example.opinia.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpinialightBlue
import com.example.opinia.ui.theme.WorkSansFontFamily

@Composable
fun <T> DropdownWithAnimation(
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Arrow Rotation"
    )

    val labelText = if (selectedItem != null) itemLabel(selectedItem) else ""

    val hasValidText = !expanded && labelText.isNotBlank()

    Column(
        modifier = Modifier
            .width(270.dp)
            .animateContentSize()
            .background(OpinialightBlue, MaterialTheme.shapes.extraLarge)
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (hasValidText) {
            Text(
                text = labelText,
                color = OpiniaDeepBlue,
                fontFamily = WorkSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
        else if (expanded) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEach { item ->
                    val isSelected = item == selectedItem

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemSelected(item)
                                expanded = false
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = null,
                            tint = OpiniaDeepBlue,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = itemLabel(item),
                            color = OpiniaDeepBlue,
                            fontFamily = WorkSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }

        Icon(
            imageVector = Icons.Default.ExpandCircleDown,
            contentDescription = "Dropdown Icon",
            tint = OpiniaDeepBlue,
            modifier = Modifier.rotate(rotationState)
        )
    }
}