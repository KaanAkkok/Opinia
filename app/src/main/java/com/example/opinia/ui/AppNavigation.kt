package com.example.opinia.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController


//BU YOLLAR UPDATE EDİLEBİLİR
enum class Destination(val route: String) {
    START("splash"),
    CHOOSE_LOGIN_OR_SIGNUP("choose_login_or_signup"),
    LOGIN("login"),
    FORGOT_PASSWORD("forgot_password"),
    SIGNUP_PERSONAL_INFO("signup_personal_info"),
    CHOOSE_AVATAR("choose_avatar"),
    SIGNUP_COURSE_INFO("signup_course_info"),
    DASHBOARD("dashboard"),
    STUDENT_PROFILE("student_profile"),
    COURSE_CATALOG("course_catalog"),
    COURSE_DETAIL("course_detail"),
    INSTRUCTOR_CATALOG("instructor_catalog"),
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destination.START.route, builder = {

        }
    )
}