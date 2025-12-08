package com.example.opinia.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.opinia.ui.Destination
// Tema dosyasındaki rengi import ediyoruz
import com.example.opinia.ui.theme.OpiniaDeepBlue

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        BottomNavItem("Courses", Destination.COURSE_CATALOG.route, Icons.Outlined.MenuBook),
        BottomNavItem("Dashboard", Destination.DASHBOARD.route, Icons.Outlined.Home),
        BottomNavItem("Instructors", Destination.INSTRUCTOR_CATALOG.route, Icons.Outlined.School)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        // Hardcoded hex yerine senin tema rengini kullandık
        containerColor = OpiniaDeepBlue,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        modifier = Modifier.size(48.dp)
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    // Color(OpiniaDeepBlue) YANLIŞ -> OpiniaDeepBlue DOĞRU
                    selectedIconColor = OpiniaDeepBlue,
                    indicatorColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    val navController = rememberNavController()
    BottomNavBar(navController = navController)
}



