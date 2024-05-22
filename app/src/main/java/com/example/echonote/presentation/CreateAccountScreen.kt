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
fun CreateAccountScreen(authViewModel: AuthViewModel, navController: NavHostController) {7
    val email by authViewModel.createAccountEmailText.collectAsState()
    val password by authViewModel.createAccountPasswordText.collectAsState()
    val name by authViewModel.createAccountName.collectAsState()
    var buttonSate by remember { mutableStateOf(ButtonState.IDLE) }
    val emailError by authViewModel.createAccountEmailError.collectAsState()
    val passwordError by authViewModel.createAccountPasswordError.collectAsState()
    val nameError by authViewModel.createAccountNameError.collectAsState()
    var isTextFieldEnabled by remember{mutableStateOf(true)}

    val snackBarState by remember { mutableStateOf(SnackbarHostState())}
    val buttonEnabled = emailError.isEmpty() && passwordError.isEmpty() && nameError.isEmpty() && email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()
    LaunchedEffect(true){
        authViewModel.createAccount.collectLatest {state->
            when(state){
                is UiState.Error -> {
                    buttonSate = ButtonState.ERROR
                    isTextFieldEnabled =true
                    delay(500)
                    snackBarState.showSnackbar(state.message)
                    buttonSate= ButtonState.IDLE
                }
                UiState.Idle -> {

                }
                UiState.Loading -> {
                    buttonSate = ButtonState.LOADING
                    isTextFieldEnabled =false
                }
                is UiState.Success -> {
                    buttonSate = ButtonState.SUCCESS
                    delay(300)
                    isTextFieldEnabled =true
                    navController.navigate(NavConstants.MAIN_SCREEN.name)

                }
            }
        }
    }
    Scaffold(
        snackbarHost = {
                       SnackbarHost(snackBarState)
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create An Account",
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
                        labelText = "Username",
                        placeHolder = "Enter your name",
                        text = name,
                        onTextChange = authViewModel::createAccountNameTextChange,
                        leadingIcon = R.drawable.ic_user,
                        keyboardType = KeyboardType.Text,
                        errorText = nameError,
                        isEnabled = isTextFieldEnabled
                    )
                    EchoNoteTextField(
                        labelText = "Email",
                        placeHolder = "Enter your email",
                        text = email,
                        onTextChange = authViewModel::createAccountEmailTextChange,
                        leadingIcon = R.drawable.ic_email,
                        keyboardType = KeyboardType.Email,
                        errorText = emailError,
                        isEnabled = isTextFieldEnabled
                    )
                    EchoNoteTextField(
                        labelText = "Password",
                        placeHolder = "Enter your password",
                        text = password,
                        onTextChange = authViewModel::createAccountPasswordTextChange,
                        leadingIcon = R.drawable.ic_lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        errorText = passwordError,
                        isEnabled = isTextFieldEnabled
                    )
                }
                EchoNoteButton(
                    finishedText = "Account Created",
                    defaultText = "Create Account",
                    errorText = "Error",
                    containerColor = RichBlue,
                    disabledColor = RichBlue,
                    errorColor = Color.Red,
                    finishedColor = RichBlue,
                    state = buttonSate,
                    onClick = {
                        authViewModel.onEvent(
                            AuthAction.CreateAccount(
                                name,
                                email,
                                password
                            )
                        )
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
                        append("Have an account already? ")
                        withStyle(
                            style = SpanStyle(
                                color = RichBlue,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Login")
                        }
                    }
                    Text(
                        text = text,
                        fontFamily = PoppinsFamily,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(NavConstants.LOGIN_SCREEN.name)
                        },
                        color = Lighter

                    )
                }
            }

        }
    }
}