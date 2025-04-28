package com.example.pesv_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.pesv_movil.login.domain.LoginUseCase
import com.example.pesv_movil.ui.theme.Pesv_movilTheme
import com.example.pesv_movil.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loginUseCase: LoginUseCase
    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isLoading
        }


        viewModel.checkAuthentication { isAuthenticated ->
            val startDestination = if (isAuthenticated) {
                PesvScreens.HOME_SCREEN
            } else {
                PesvScreens.LOGIN_SCREEN
            }

            setContent {
                Pesv_movilTheme {
                    PesvNavGraph(
                        loginUseCase = loginUseCase,
                        tokenManager = tokenManager,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
