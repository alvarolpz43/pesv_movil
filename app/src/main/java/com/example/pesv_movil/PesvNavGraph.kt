package com.example.pesv_movil


import BuscarUbicacionScreen
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pesv_movil.Garaje.FormVehicleScreen
import com.example.pesv_movil.Garaje.GarajeScreen
import com.example.pesv_movil.Notificaciones.NotificacionesScreen

import com.example.pesv_movil.desplazamientos.MapaScreen
import com.example.pesv_movil.desplazamientos.DesplazamientosViewModel
import com.example.pesv_movil.home.HomeScreen
import com.example.pesv_movil.login.domain.LoginUseCase
import com.example.pesv_movil.login.ui.LoginScreen
import com.example.pesv_movil.login.ui.LoginViewModel
import com.example.pesv_movil.navigationApp.PesvNavigationActions
import com.example.pesv_movil.preoperacional.FormPreoperacionalScreen
import com.example.pesv_movil.preoperacional.PreoperacionalScreen
import com.example.pesv_movil.utils.AppModalDrawer
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PesvNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    loginUseCase: LoginUseCase,
    tokenManager: TokenManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = PesvScreens.HOME_SCREEN,
    navActions: PesvNavigationActions = remember(navController) {
        PesvNavigationActions(navController)
    }

) {

    val loginViewModel = LoginViewModel(loginUseCase, tokenManager)
    val context = LocalContext.current
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(PesvScreens.LOGIN_SCREEN) {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }

        composable(PesvScreens.DESPLAZAMIENTOS_SCREEN) {
            val desplazamientosViewModel: DesplazamientosViewModel = hiltViewModel()

            val origen by desplazamientosViewModel.origenSeleccionado.collectAsState()
            val destino by desplazamientosViewModel.destinoSeleccionado.collectAsState()

            MapaScreen(
                navController = navController,
                desplazamientosViewModel = desplazamientosViewModel,
                origenSeleccionado = origen,
                destinoSeleccionado = destino
            )
        }

        composable(PesvScreens.BUSCAR_UBICACION_SCREEN) {
            val desplazamientosViewModel: DesplazamientosViewModel =
                hiltViewModel() // ✅ Obtén ViewModel correctamente
            BuscarUbicacionScreen(
                navController = navController,
                desplazamientosViewModel = desplazamientosViewModel
            )
        }

        composable(PesvScreens.PREOPE_SCREEN) {
            PreoperacionalScreen(
                navController = navController,
                onClose = { navController.popBackStack() },
                tokenManager = tokenManager
            )
        }

        composable(
            route = PesvScreens.FORM_PREOPE_SCREEN,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            FormPreoperacionalScreen(
                navController = navController,
                onClose = { navController.popBackStack() },
                tokenManager = tokenManager,
                vehicleId = vehicleId
            )
        }





        composable(PesvScreens.HOME_SCREEN) {
            AppModalDrawer(
                drawerState, currentRoute, navActions
            ) {
                HomeScreen(
                    navController = navController,
                    tokenManager = tokenManager,
                    openDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }

        composable(PesvScreens.GARAJE_SCREEN) {
            AppModalDrawer(
                drawerState, currentRoute, navActions
            ) {
                GarajeScreen(
                    navController = navController,
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    tokenManager = tokenManager
                )
            }
        }
        composable(PesvScreens.NOTIFICACIONES_SCREEN) {
            AppModalDrawer(
                drawerState, currentRoute, navActions
            ) {
                NotificacionesScreen(
                    navController = navController,
                    tokenManager = tokenManager,
                    openDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }

        composable(PesvScreens.FORM_VEHICLE_SCREEN) {
            FormVehicleScreen(
                navController = navController,
                onClose = { navController.popBackStack() })
        }
    }
}





