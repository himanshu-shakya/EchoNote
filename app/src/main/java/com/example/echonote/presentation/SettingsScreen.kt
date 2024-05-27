package com.example.echonote.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.example.echonote.R
import com.example.echonote.core.navigation.NavConstants
import com.example.echonote.core.ui_componantes.EchoNoteLoading
import com.example.echonote.core.ui_componantes.UserCard
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.model.User
import com.example.echonote.domain.utils.AuthAction
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.DarkGray
import com.example.echonote.ui.theme.LightBlack
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    var user by remember { mutableStateOf(User()) }
    var isLoading by remember { mutableStateOf(false) }
    val snackBarState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var userLoading by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(true) {
        authViewModel.logout.collectLatest { state ->
            when (state) {
                is UiState.Error -> {
                    isLoading = false
                    snackBarState.showSnackbar(state.message)
                }

                UiState.Loading -> {
                    isLoading = true
                }

                is UiState.Success -> {
                    isLoading = false
                    authViewModel.resetLogoutState()
                    navController.navigate(NavConstants.LOGIN_SCREEN.name) {
                        launchSingleTop = true
                        popUpTo(NavConstants.MAIN_ROUTE.name) {
                            inclusive = true
                        }
                    }
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        authViewModel.userFlow.collectLatest {
            when (it) {
                is UiState.Error -> {
                    userLoading = false
                    snackBarState.showSnackbar(it.message)
                }

                UiState.Idle -> {

                }

                UiState.Loading -> {
                    userLoading = true
                }

                is UiState.Success -> {
                    user = it.data
                    userLoading = false
                }

            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        authViewModel.onEvent(AuthAction.GetUser)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Lighter,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                            navController.navigateUp()
                        }

                    }) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EchoNoteLoading()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(13.dp)
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = CenterHorizontally
                ) {
                    if (userLoading) {
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EchoNoteLoading()
                        }
                    } else {
                        UserCard(user = user)
                    }
                    HorizontalDivider(
                        thickness = Dp.Hairline,
                        color = DarkGray
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    SettingOptions(
                        icon = R.drawable.ic_edit,
                        text = "Edit Profile",
                        onClick = {
                            navController.navigate(NavConstants.EDIT_PROFILE_SCREEN.name)
                        }
                    )
                    SettingOptions(
                        icon = R.drawable.ic_bookmarked,
                        text = "Bookmarks",
                        onClick = {
                            navController.navigate(NavConstants.BOOKMARKS_SCREEN.name)
                        }
                    )
                    SettingOptions(
                        icon = R.drawable.ic_logout,
                        text = "Logout",
                        onClick = {
                            authViewModel.onEvent(AuthAction.Logout)
                        },
                        showTrailingIcon = false
                    )
                    SettingOptions(
                        icon = R.drawable.ic_delete,
                        text = "Delete account",
                        onClick = {
                            showDialog = true
                        },
                        showTrailingIcon = false,
                        color = Color.Red
                    )
                }
            }
            if (showDialog) {
                AlertDialog(
                    containerColor = LightBlack,
                    text = {
                        Text(
                            text = "Are you sure you want to delete your account?",
                            fontSize = 16.sp,
                            color = Lighter,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                navController.navigate(NavConstants.DELETE_ACCOUNT_SCREEN.name) {
                                    launchSingleTop = true
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text(
                                text = "Delete",
                                color = Lighter
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text(
                                text = "Cancel",
                                color = Lighter
                            )
                        }
                    }


                )
            }
        }


    }

}


@Composable
fun SettingOptions(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    showTrailingIcon: Boolean = true,
    color: Color = Lighter,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            tint = color,
        )

        Text(
            text = text,
            fontSize = 16.sp,
            color = color,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )
        if (showTrailingIcon) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Arrow",
                tint = Lighter,
            )
        }
    }
}