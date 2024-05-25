package com.example.echonote.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.example.echonote.R
import com.example.echonote.core.navigation.NavConstants
import com.example.echonote.core.ui_componantes.EchoNoteButton
import com.example.echonote.core.ui_componantes.EchoNoteTextField
import com.example.echonote.core.utils.ButtonState
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.utils.AuthAction
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(authViewModel: AuthViewModel, navController: NavController) {
    val snackBarState = remember{ SnackbarHostState() }
    val email by authViewModel.loginEmailText.collectAsState()
    val password by authViewModel.loginPasswordText.collectAsState()
    var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
    val emailError by authViewModel.loginEmailError.collectAsState()
    val passwordError by authViewModel.loginPasswordError.collectAsState()
    val buttonEnabled = emailError.isEmpty() && passwordError.isEmpty() && password.isNotEmpty() && email.isNotEmpty()
    var textFieldEnabled by remember{ mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        authViewModel.deleteAccount.collectLatest { state ->
            when (state) {
                is UiState.Error -> {
                    textFieldEnabled = true
                    buttonState = ButtonState.ERROR
                    snackBarState.showSnackbar(state.message)
                    buttonState= ButtonState.IDLE
                }

                UiState.Loading -> {
                    buttonState = ButtonState.LOADING
                    textFieldEnabled = false
                }

                is UiState.Success -> {
                    buttonState = ButtonState.SUCCESS
                    textFieldEnabled = true
                    authViewModel.resetDeleteAccountState()
                    navController.navigate(NavConstants.LOGIN_SCREEN.name){
                        launchSingleTop = true
                        popUpTo(NavConstants.DELETE_ACCOUNT_SCREEN.name){
                            inclusive = true
                        }
                    }
                    buttonState= ButtonState.IDLE
                }

                else -> {}
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Delete Account",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Lighter,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if(navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp), verticalArrangement = Arrangement.spacedBy(30.dp),

            ) {
                Column{
                    Text(
                        text = "Enter your credentials to confirm and permanently delete your account.",
                        fontFamily = PoppinsFamily,
                        fontSize = 16.sp,
                        color = Lighter.copy(alpha = 0.7f),
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    EchoNoteTextField(
                        labelText = "Email",
                        placeHolder = "Enter your email",
                        text = email,
                        onTextChange = authViewModel::loginEmailTextChange,
                        leadingIcon = R.drawable.ic_email,
                        keyboardType = KeyboardType.Email,
                        errorText = emailError,
                        isEnabled = textFieldEnabled
                    )
                    EchoNoteTextField(
                        labelText = "Password",
                        placeHolder = "Enter your password",
                        text = password,
                        onTextChange = authViewModel::loginPasswordTextChange,
                        leadingIcon = R.drawable.ic_lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        errorText = "",
                        isEnabled = textFieldEnabled
                    )
                }
                EchoNoteButton(
                    finishedText = "Account Deleted",
                    defaultText = "Delete Account",
                    errorText = "Error",
                    containerColor = RichBlue,
                    disabledColor = RichBlue,
                    errorColor = Color.Red,
                    finishedColor = RichBlue,
                    state = buttonState,
                    onClick = {
                        authViewModel.onEvent(AuthAction.DeleteAccount(email, password))
                    },
                    isEnabled = buttonEnabled,
                    shape = RoundedCornerShape(5.dp),
                    textSize = 14.sp,
                    modifier = Modifier.height(45.dp)
                )



            }

        }
    }
}