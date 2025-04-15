package com.example.pesv_movil.desplazamientos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pesv_movil.R
import com.example.pesv_movil.desplazamientos.model.OpcionVehiculo


@Composable
fun GreenLogo(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.green_logo),
        contentDescription = "GreenLogo",
        modifier = modifier
    )
}

@Composable
fun DesplazamientosIcon(modifier: Modifier = Modifier.size(50.dp)) {
    Image(
        painter = painterResource(id = R.drawable.ic_desplazamientos),
        contentDescription = "Ícono de Desplazamientos",
        contentScale = ContentScale.Fit, // Ajusta sin deformar
        modifier = modifier
    )
}


@Composable
fun PreoperacionalIcon(modifier: Modifier = Modifier.size(50.dp)) {
    Image(
        painter = painterResource(id = R.drawable.ic_preoperacional),
        contentDescription = "Ícono de Desplazamientos",
        contentScale = ContentScale.Fit, // Ajusta sin deformar
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectField(
    modifier: Modifier,
    label: String,
    error: String? = null,
    options: List<OpcionVehiculo>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedText = options.find { it.id == selectedOption }?.displayText ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = selectedText,
            onValueChange = {},
            label = { Text(label) },
            isError = error != null, // ✅ activa el estado de error

            supportingText = {
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->


                DropdownMenuItem(
                    text = { Text(option.displayText) },
                    onClick = {
                        onOptionSelected(option.id)
                        expanded = false

                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    error: String? = null,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },

        isError = error != null, // ✅ activa el estado de error

        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },

        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType
        ),
        modifier = modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            containerColor = Color(0xFFF1F1F1),
            focusedLabelColor = Color(0xFF165A31),
            cursorColor = Color(0xFF165A31),
        )
    )
}


