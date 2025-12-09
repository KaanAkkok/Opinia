package com.example.opinia.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    // Bu entry'nin bağlı olduğu parent graph'ı (yani "register_flow") bulur
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()

    // Parent entry'yi hatırlar
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    // ViewModel'i o parent entry üzerinden üretir/getirir
    return hiltViewModel(parentEntry)
}