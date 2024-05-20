package com.example.echonote.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.echonote.R
import com.example.echonote.core.ui_componantes.EchoNoteButton
import com.example.echonote.core.ui_componantes.EchoNoteTextField
import com.example.echonote.core.utils.ButtonState
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.utils.AuthAction
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.LightGray
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(authViewModel: AuthViewModel, navController: NavController) {
    val email by authViewModel.fgPassEmail.collectAsState()
    val emailError by authViewModel.fgPassEmailError.collectAsState()
    val snakbarState = remember { SnackbarHostState() }
    var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
    val isButtonEnabled = email.isNotEmpty() && emailError.isEmpty()
    var isTextFieldEnabled by remember{ mutableStateOf(false )}
    LaunchedEffect(true) {
        authViewModel.forgotPasswordFlow.collectLatest { state ->
            when (state) {
                is UiState.Error -> {
                    buttonState = ButtonState.ERROR
                    snakbarState.showSnackbar(state.message)
                    isTextFieldEnabled = true
                    delay(500)
                    buttonState =ButtonState.IDLE
                }
                UiState.Loading -> {
                    buttonState = ButtonState.LOADING
                    isTextFieldEnabled = false
                }

                is UiState.Success -> {
                    if(state.data){
                        buttonState = ButtonState.SUCCESS
                        delay(300)
                        isTextFieldEnabled = true
                        authViewModel.resetForgotPasswordState()
                        navController.navigateUp()
                    }
                }
                is UiState.Idle->{

                }
            }

        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snakbarState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Forgot Password",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Lighter,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "An email will be sent to your registered email address with instructions on how to reset your password.",
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = LightGray
            )
            EchoNoteTextField(
                labelText = "Email",
                placeHolder = "Enter your email",
                text = email,
                onTextChange = authViewModel::fgPassEmailTextChange,
                leadingIcon = R.drawable.ic_email,
                keyboardType = KeyboardType.Email,
                errorText = emailError,
                isEnabled = isTextFieldEnabled
            )
            EchoNoteButton(
                finishedText = "Email Sent",
                defaultText = "Send Email",
                errorText = "Error",
                containerColor = RichBlue,
                disabledColor = RichBlue,
                errorColor = Color.Red,
                finishedColor = RichBlue,
                state = buttonState,
                onClick = {
                    authViewModel.onEvent(AuthAction.ForgotPassword(email))
                },
                isEnabled =isButtonEnabled,
                shape = RoundedCornerShape(5.dp),
                textSize = 14.sp,
                modifier = Modifier.height(45.dp)
            )
        }
    }
}