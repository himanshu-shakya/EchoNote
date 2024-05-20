package com.example.echonote.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.echonote.R
import com.example.echonote.core.navigation.NavConstants
import com.example.echonote.core.ui_componantes.EchoNoteButton
import com.example.echonote.core.ui_componantes.EchoNoteTextField
import com.example.echonote.core.ui_componantes.bounceClick
import com.example.echonote.core.utils.ButtonState
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.utils.AuthAction
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.DarkGray
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavHostController) {
    val email by authViewModel.loginEmailText.collectAsState()
    val password by authViewModel.loginPasswordText.collectAsState()
    var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
    val emailError by authViewModel.loginEmailError.collectAsState()
    val passwordError by authViewModel.loginPasswordError.collectAsState()
    val buttonEnabled = emailError.isEmpty() && passwordError.isEmpty() && password.isNotEmpty() && email.isNotEmpty()
    var textFieldEnabled by remember{mutableStateOf(true)}
    val snackBarState = remember{SnackbarHostState()}
    LaunchedEffect(true){
        authViewModel.loginFlow.collectLatest {state->
            when(state){
                is UiState.Error -> {
                    buttonState = ButtonState.ERROR
                    delay(200)
                    textFieldEnabled= true
                    snackBarState.showSnackbar(state.message)
                    buttonState = ButtonState.IDLE
                }
                UiState.Idle -> {
                    buttonState = ButtonState.IDLE
                }
                UiState.Loading -> {
                    buttonState = ButtonState.LOADING
                    textFieldEnabled =false
                }
                is UiState.Success -> {
                    buttonState = ButtonState.SUCCESS
                    delay(200)
                    textFieldEnabled= true
                    navController.navigate(NavConstants.MAIN_SCREEN.name)
                }
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
                        text = "Login",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Lighter,
                    )
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
            contentAlignment = Alignment.Center

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column {
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
                    Text(
                        text = "Forgot Password?",
                        fontFamily = PoppinsFamily,
                        fontSize = 13.sp,
                        textDecoration = TextDecoration.Underline,
                        color = RichBlue,
                        modifier = Modifier
                            .align(Alignment.End)
                            .bounceClick()
                            .clickable {
                                navController.navigate(NavConstants.FORGOT_PASSWORD_SCREEN.name) {
                                    launchSingleTop = true
                                }
                            }
                    )
                }
                EchoNoteButton(
                    finishedText = "Login Successful",
                    defaultText = "Login ",
                    errorText = "Error",
                    containerColor = RichBlue,
                    disabledColor = RichBlue,
                    errorColor = Color.Red,
                    finishedColor = RichBlue,
                    state = buttonState,
                    onClick = {
                        authViewModel.onEvent(AuthAction.Login(email, password))
                    },
                    isEnabled = buttonEnabled,
                    shape = RoundedCornerShape(5.dp),
                    textSize = 14.sp,
                    modifier = Modifier.height(45.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Divider(
                        thickness = Dp.Hairline,
                        color = DarkGray
                    )
                    val text = buildAnnotatedString {
                        append("No account yet?")
                        withStyle(
                            style = SpanStyle(
                                color = RichBlue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Create an Account ")
                        }
                    }
                    Text(
                        text = text,
                        fontFamily = PoppinsFamily,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(NavConstants.CREATE_ACCOUNT_SCREEN.name)
                        },
                        color = Lighter
                    )
                }


            }

        }
    }
}