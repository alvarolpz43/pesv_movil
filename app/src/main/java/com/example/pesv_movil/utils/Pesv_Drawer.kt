package com.example.pesv_movil.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.R
import com.example.pesv_movil.navigationApp.PesvNavigationActions
import com.example.pesv_movil.ui.theme.Pesv_movilTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppModalDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    navigationActions: PesvNavigationActions,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable () -> Unit
){


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                navigateToHome = { navigationActions.navigateToHome() },
                navigateToGaraje = { navigationActions.navigateToGaraje() },
                closeDrawer = {coroutineScope.launch { drawerState.close() } }
            )
        }
    ){
        content()
    }
}

@Composable
private fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToGaraje: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
){
    Surface (color = MaterialTheme.colorScheme.background) {
        Column (modifier = modifier.width(300.dp).fillMaxHeight()) {
            DrawerHeader()
            DrawerButton(
                painter = painterResource(id = R.drawable.ic_home),
                label = stringResource(id = R.string.home_title),
                isSelected = currentRoute == PesvScreens.HOME_SCREEN,
                action = {
                    navigateToHome()
                    closeDrawer()
                }
            )

            DrawerButton(

                painter = painterResource(id = R.drawable.ic_garage),
                label = stringResource(id = R.string.garage_title),
                isSelected = currentRoute == PesvScreens.GARAJE_SCREEN,
                action = {
                    navigateToGaraje()
                    closeDrawer()
                }
            )
        }
    }
}


@Composable
private fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .height(dimensionResource(id = R.dimen.header_height))
            .padding(dimensionResource(id = R.dimen.header_padding))
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_efa),
            contentDescription =
            stringResource(id = R.string.tasks_header_image_content_description),
            modifier = Modifier.width(dimensionResource(id = R.dimen.header_image_width))
        )
        Text(
            text = stringResource(id = R.string.navigation_view_header_title),
            color = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun DrawerButton(
    painter: Painter,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    TextButton(
        onClick = action,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painter,
                contentDescription = null, // decorative
                tint = tintColor
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = tintColor
            )
        }
    }
}

@Preview("Drawer contents")
@Composable
fun PreviewAppDrawer() {
    Pesv_movilTheme {
        Surface {
            AppDrawer(
                currentRoute = PesvScreens.HOME_SCREEN,
                navigateToHome = {},
                navigateToGaraje = {},
                closeDrawer = {}
            )
        }
    }
}



