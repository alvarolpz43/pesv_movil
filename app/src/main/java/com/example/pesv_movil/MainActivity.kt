package com.example.pesv_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val startDestination = PesvScreens.SPLASH_SCREEN

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


