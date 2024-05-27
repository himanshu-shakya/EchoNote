package com.example.echonote.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.echonote.R
import com.example.echonote.core.ui_componantes.EchoNoteButton
import com.example.echonote.core.ui_componantes.EchoNoteLoading
import com.example.echonote.core.ui_componantes.EchoNoteTextField
import com.example.echonote.core.ui_componantes.bounceClick
import com.example.echonote.core.utils.ButtonState
import com.example.echonote.core.utils.UiState
import com.example.echonote.core.utils.UriPathFinder
import com.example.echonote.domain.model.User
import com.example.echonote.domain.utils.AuthAction
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
) {
    var user by remember {
        mutableStateOf(User())
    }
    var emailText by remember {
        mutableStateOf("")
    }
    val userNameText by authViewModel.createAccountName.collectAsState()
    var isLoading by remember {
        mutableStateOf(false)
    }
    var buttonState by remember {
        mutableStateOf(ButtonState.IDLE)
    }
    var isButtonEnabled by remember {
        mutableStateOf(false)
    }
    val snackBarState = remember { SnackbarHostState() }
    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    var imageUriPath by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val singleGalleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { image ->
                if (image != Uri.EMPTY && image != null) {
                    val temp = UriPathFinder().getPath(context, image)

                    imageUriPath = temp.toString()

                    imageUri = image

                } else {
                    scope.launch {
                        snackBarState.showSnackbar("Not Selected Any Image Yet")
                    }
                }
            })
    LaunchedEffect(imageUri) {
        isButtonEnabled = imageUri != Uri.parse(user.avatar)
    }
    LaunchedEffect(userNameText) {
        isButtonEnabled = userNameText != user.name
    }
    LaunchedEffect(true) {
        authViewModel.onEvent(AuthAction.GetUser)
        authViewModel.userFlow.collectLatest { state ->
            when (state) {
                is UiState.Error -> {
                    snackBarState.showSnackbar(state.message)
                    isLoading = false
                }

                UiState.Idle -> {
                    isLoading = false
                }

                UiState.Loading -> {
                    isLoading = true
                }

                is UiState.Success -> {
                    user = state.data
                    emailText = user.email
                    authViewModel.createAccountNameTextChange(user.name)
                    imageUri = Uri.parse(user.avatar)
                    isLoading = false
                }
            }


        }
    }
    BackHandler {
        navController.navigateUp()
        authViewModel.resetUpdateUserState()
    }
    LaunchedEffect(true) {
        authViewModel.onEvent(AuthAction.GetUser)
        authViewModel.updateUser.collectLatest { state ->
            when (state) {
                is UiState.Error -> {
                    snackBarState.showSnackbar(state.message)
                    buttonState = ButtonState.ERROR
                }

                UiState.Idle -> {
                    isLoading = false
                }

                UiState.Loading -> {
                    buttonState = ButtonState.LOADING
                }

                is UiState.Success -> {
                    buttonState = ButtonState.SUCCESS
                    delay(300)
                    navController.navigateUp()
                    authViewModel.resetUpdateUserState()
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
                        text = "Edit Profile",
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
                        authViewModel.resetUpdateUserState()

                    }) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            EchoNoteButton(
                finishedText = "Updated",
                defaultText = "Update",
                errorText = "Error",
                containerColor = RichBlue,
                disabledColor = RichBlue,
                errorColor = Color.Red,
                finishedColor = RichBlue,
                state = buttonState,
                onClick = {
                    val updatedUser = User(
                        name = userNameText,
                        avatar = if (imageUri != Uri.parse(user.avatar)) imageUri.toString() else ""
                    )
                    authViewModel.onEvent(AuthAction.UpdateUser(updatedUser))
                },
                isEnabled = isButtonEnabled,
                shape = RoundedCornerShape(5.dp),
                textSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp)

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
                    .padding(it)
                    .padding(13.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    val image = imageUriPath.ifEmpty { user.avatar }
                    AsyncImage(
                        model = image,
                        contentDescription = "avtar",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.FillBounds
                    )

                    IconButton(onClick = {
                        singleGalleryLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )

                    })
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit_image),
                            contentDescription = "add",
                            tint = Lighter,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Column(

                ) {

                    EchoNoteTextField(
                        labelText = "Name",
                        placeHolder = "Enter Your name",
                        text = userNameText,
                        onTextChange = authViewModel::createAccountNameTextChange,
                        leadingIcon = R.drawable.ic_user,
                        keyboardType = KeyboardType.Text,
                        errorText = "",
                        isEnabled = true,
                        modifier = Modifier.bounceClick()
                    )
                    EchoNoteTextField(
                        labelText = "Email",
                        placeHolder = "Enter Your name",
                        text = emailText,
                        onTextChange = {},
                        leadingIcon = R.drawable.ic_email,
                        keyboardType = KeyboardType.Email,
                        errorText = "",
                        isEnabled = false,
                        modifier = Modifier
                            .bounceClick()
                    )

                }
            }
        }
    }
}

