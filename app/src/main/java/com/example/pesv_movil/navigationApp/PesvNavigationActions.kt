package com.example.pesv_movil.navigationApp

import androidx.navigation.NavHostController
import com.example.pesv_movil.PesvScreens

class PesvNavigationActions (private val navController: NavHostController) {




    fun navigateToHome() {
        navController.navigate(PesvScreens.HOME_SCREEN) {
            popUpTo(PesvScreens.LOGIN_SCREEN) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun navigateToGaraje() {
        navController.navigate(PesvScreens.GARAJE_SCREEN) {
            popUpTo(PesvScreens.HOME_SCREEN) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun navigateToLogin() {
        navController.navigate(PesvScreens.LOGIN_SCREEN) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun navigateToFormVehicle() {
        navController.navigate(PesvScreens.FORM_VEHICLE_SCREEN) {
            popUpTo(PesvScreens.GARAJE_SCREEN) { inclusive = false }
            launchSingleTop = true
        }

    }

    fun navigateToDesplazamientos() {
        navController.navigate(PesvScreens.FORM_VEHICLE_SCREEN) {
            popUpTo(PesvScreens.DESPLAZAMIENTOS_SCREEN) { inclusive = false }
            launchSingleTop = true
        }

    }
}