import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pesv_movil.desplazamientos.DesplazamientosViewModel

@Composable
fun BuscarUbicacionScreen(navController: NavHostController, desplazamientosViewModel: DesplazamientosViewModel) {

    var query by remember { mutableStateOf("") }
    val predictions by desplazamientosViewModel.autocompletePredictions.collectAsState()
    val isLoading by desplazamientosViewModel.autocompleteLoading.collectAsState()
    val errorMessage by desplazamientosViewModel.autocompleteError.collectAsState()
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Buscar Ubicación",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp)) // Espacio para alinear el texto al centro
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(16.dp).padding(top = 50.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it

                    desplazamientosViewModel.obtenerPredicciones(it)
                },
                label = { Text("Buscar ubicación") },
                modifier = Modifier.fillMaxWidth()
            )

            if (isLoading) {
                CircularProgressIndicator()
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            predictions.forEach { prediction ->
                Log.d("Prediccion", "Este es la prediccion $prediction")
                Text(
                    text = prediction.getFullText(null).toString(),
                    modifier = Modifier.clickable {
                        // Manejar la selección aquí
                    }
                )
            }
        }
    }
}