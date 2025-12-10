package com.example.opinia.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.opinia.ui.dashboard.DashboardScreen
import com.example.opinia.ui.dashboard.DashboardViewModel
import com.example.opinia.ui.onboarding_authentication.ChooseLoginOrSignupScreen
import com.example.opinia.ui.onboarding_authentication.ForgotPasswordScreen
import com.example.opinia.ui.onboarding_authentication.ForgotPasswordViewModel
import com.example.opinia.ui.onboarding_authentication.LoginScreen
import com.example.opinia.ui.onboarding_authentication.LoginViewModel
import com.example.opinia.ui.onboarding_authentication.RegisterViewModel
import com.example.opinia.ui.onboarding_authentication.SignupStudentAcademicScreen
import com.example.opinia.ui.onboarding_authentication.SignupStudentAvatarScreen
import com.example.opinia.ui.onboarding_authentication.SignupStudentCourseScreen
import com.example.opinia.ui.onboarding_authentication.SignupStudentPersonalScreen
import com.example.opinia.ui.onboarding_authentication.SplashScreen
import com.example.opinia.ui.onboarding_authentication.SplashViewModel
import com.example.opinia.ui.profile.ProfileScreen
import com.example.opinia.ui.profile.ProfileScreenViewModel
import com.example.opinia.utils.sharedViewModel


//BU YOLLAR UPDATE EDİLEBİLİR
enum class Destination(val route: String) {
    START("splash"),
    CHOOSE_LOGIN_OR_SIGNUP("choose_login_or_signup"),
    LOGIN("login"),
    FORGOT_PASSWORD("forgot_password"),
    SIGNUP_PERSONAL_INFO("signup_personal_info"),
    CHOOSE_AVATAR("choose_avatar"),
    SIGNUP_ACADEMIC_INFO("signup_academic_info"),
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
        composable(Destination.START.route) {
            val splashViewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController, splashViewModel)
        }

        composable(Destination.CHOOSE_LOGIN_OR_SIGNUP.route) {
            ChooseLoginOrSignupScreen(navController)
        }

        composable(Destination.LOGIN.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(navController, loginViewModel)
        }

        composable(Destination.FORGOT_PASSWORD.route) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(navController, forgotPasswordViewModel)
        }

        // SIGNUP EKRANLARI İÇİNDE KULLANILACAK SCREENLER
        navigation(startDestination = Destination.SIGNUP_PERSONAL_INFO.route, route = "register_flow") {
            composable(Destination.SIGNUP_PERSONAL_INFO.route) {
                val registerViewModel = it.sharedViewModel<RegisterViewModel>(navController)
                SignupStudentPersonalScreen(navController, registerViewModel)
            }
            composable(Destination.CHOOSE_AVATAR.route) {
                val registerViewModel = it.sharedViewModel<RegisterViewModel>(navController)
                SignupStudentAvatarScreen(navController, registerViewModel)
            }
            composable(Destination.SIGNUP_ACADEMIC_INFO.route) {
                val registerViewModel = it.sharedViewModel<RegisterViewModel>(navController)
                SignupStudentAcademicScreen(navController, registerViewModel)
            }
            composable(Destination.SIGNUP_COURSE_INFO.route) {
                val registerViewModel = it.sharedViewModel<RegisterViewModel>(navController)
                SignupStudentCourseScreen(navController, registerViewModel)
            }
        }
        composable(Destination.DASHBOARD.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(navController, dashboardViewModel)
        }
        composable(Destination.STUDENT_PROFILE.route) {
            val profileScreenViewModel: ProfileScreenViewModel = hiltViewModel()
            ProfileScreen(navController, profileScreenViewModel)
        }

    }
    )
}