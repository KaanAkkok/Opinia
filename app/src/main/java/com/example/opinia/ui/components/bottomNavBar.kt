package com.example.opinia.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.opinia.R
import com.example.opinia.ui.Destination
import com.example.opinia.ui.theme.OpiniaDeepBlue
import com.example.opinia.ui.theme.OpiniaPurple

// Veri Sınıfı
data class BottomNavItem(
    val name: String,
    val route: String,
    @DrawableRes val picture: Int,
)

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        BottomNavItem("Courses", Destination.COURSE_CATALOG.route, R.drawable.book_ribbon),
        BottomNavItem("Dashboard", Destination.DASHBOARD.route, R.drawable.home),
        BottomNavItem("Instructors", Destination.INSTRUCTOR_CATALOG.route, R.drawable.school)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val darkerIndicatorColor = lerp(OpiniaDeepBlue, OpiniaPurple, 0.1f)

    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .height(100.dp),
        containerColor = OpiniaDeepBlue,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.5f else 1.0f,
                animationSpec = tween(durationMillis = 300),
                label = "iconScale"
            )

            NavigationBarItem(
                alwaysShowLabel = false,
                icon = {
                    Icon(
                        painter = painterResource(item.picture),
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(48.dp)
                            .scale(scale)
                            .padding(5.dp)
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = darkerIndicatorColor,
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.DASHBOARD.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.COURSE_CATALOG.route) {}
            composable(Destination.DASHBOARD.route) {}
            composable(Destination.INSTRUCTOR_CATALOG.route) {}
        }
    }
}