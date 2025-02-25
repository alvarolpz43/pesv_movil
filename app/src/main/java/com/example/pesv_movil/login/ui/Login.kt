package com.example.pesv_movil.login.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.R

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavHostController) {
    val isLoading by loginViewModel.isLoading.observeAsState(initial = false)
    val loginSuccess by loginViewModel.loginSuccess.observeAsState(initial = false)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loginViewModel.resetLoginState()
    }

    LaunchedEffect(loginSuccess) {
        loginSuccess?.let { success ->
            if (success) {
                navController.navigate(PesvScreens.HOME_SCREEN) {
                    popUpTo(PesvScreens.LOGIN_SCREEN) { inclusive = true }
                }
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            Body(loginViewModel)
        }
    }
}

@Composable
fun Body(loginViewModel: LoginViewModel) {
    val user by loginViewModel.email.observeAsState("")
    val password by loginViewModel.password.observeAsState("")
    val isLoginEnable by loginViewModel.isLoginEnable.observeAsState(false)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageLogo(modifier = Modifier.size(200.dp))
        UserNameField(user) { loginViewModel.onLoginChanged(user = it, password = password) }
        Spacer(modifier = Modifier.size(10.dp))
        PasswordField(password) { loginViewModel.onLoginChanged(user = user, password = it) }
        Spacer(modifier = Modifier.size(16.dp))
        LoginButton(onClick = { loginViewModel.onLoginSelected() }, loginEnable = isLoginEnable)
    }
}

@Composable
fun LoginButton(
    onClick: () -> Unit,
    loginEnable: Boolean
) {
    Button(
        onClick = onClick,
        enabled = loginEnable,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (loginEnable) Color.Blue else Color.Red,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFCFD8DC),
            disabledContentColor = Color(0xFF90A4AE)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNameField(user: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = user,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Usuario", color = Color.Black) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(password: String, onValueChange: (String) -> Unit) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Contraseña", color = Color.Black) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            val icon = if (passwordVisibility) {
                Icons.Filled.VisibilityOff
            } else {
                Icons.Filled.Visibility
            }
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = icon, contentDescription = "Mostrar contraseña")
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray
        )
    )
}

@Composable
fun ImageLogo(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_efa),
        contentDescription = "logo",
        modifier = modifier
    )
}
