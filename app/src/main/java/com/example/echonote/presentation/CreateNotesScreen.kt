package com.example.echonote.presentation

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.echonote.R
import com.example.echonote.core.ui_componantes.EchoNoteButton
import com.example.echonote.core.ui_componantes.EchoNoteColorButton
import com.example.echonote.core.ui_componantes.EchoNoteIconButton
import com.example.echonote.core.ui_componantes.bounceClick
import com.example.echonote.core.utils.ButtonState
import com.example.echonote.core.utils.UiState
import com.example.echonote.core.utils.UriPathFinder
import com.example.echonote.domain.model.CreateNote
import com.example.echonote.domain.utils.ColorSelectionState
import com.example.echonote.domain.utils.ListState
import com.example.echonote.domain.utils.NotesAction
import com.example.echonote.domain.utils.TextEditorState
import com.example.echonote.presentation.viewModel.NotesViewModel
import com.example.echonote.ui.theme.Blue
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.DarkGray
import com.example.echonote.ui.theme.Green
import com.example.echonote.ui.theme.LightBlack
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.Pink
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import com.example.echonote.ui.theme.Yellow
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun CreateNotesScreen(
    navController: NavController,
    notesViewModel: NotesViewModel,
) {
    val snackBarSate = remember { SnackbarHostState() }
    var buttonState by remember {
        mutableStateOf(ButtonState.IDLE)
    }
    val noteText by notesViewModel.noteText.collectAsState()
    val noteHeading by notesViewModel.noteHeading.collectAsState()
    val textEditorState by notesViewModel.textFormatting.collectAsState()
    val listState by notesViewModel.listState.collectAsState()
    val colorSelectionState by notesViewModel.colorState.collectAsState()
    val richTextFieldState = rememberRichTextState()
    val context = LocalContext.current
    var currentWordSize by remember { mutableStateOf("") }
    val isButtonEnabled =
        richTextFieldState.annotatedString.text.isNotEmpty() && noteHeading.isNotEmpty()
    var dateText by remember { mutableStateOf("") }
    var isGranted by remember { mutableStateOf(false) }
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isGranted = it
        })
    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    var imageUriPath by remember {
        mutableStateOf("")
    }

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

                        snackBarSate.showSnackbar("Not Selected Any Image Yet")
                    }
                }
            })
    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isGranted = askPermission(context)
        } else {
            launcher.launch(permission)
        }
    }
    LaunchedEffect(richTextFieldState.annotatedString) {
        currentWordSize = richTextFieldState.annotatedString.text.length.toString()
        if (richTextFieldState.annotatedString.text.isEmpty()) {
            notesViewModel.resetCreateNoteScreenState(false)
        }
        notesViewModel.onNoteTextChange(richTextFieldState.toHtml())
    }
    LaunchedEffect(true) {
        dateText = notesViewModel.getCurrentDateTimeFormatted()
        notesViewModel.createNoteFlow.collectLatest { state ->
            when (state) {
                is UiState.Error -> {
                    buttonState = ButtonState.ERROR
                    snackBarSate.showSnackbar(state.message)
                    delay(500)
                    buttonState = ButtonState.IDLE
                }

                UiState.Idle -> {
                    buttonState = ButtonState.IDLE
                }

                UiState.Loading -> {
                    buttonState = ButtonState.LOADING
                }

                is UiState.Success -> {
                    buttonState = ButtonState.SUCCESS
                    delay(300)
                    notesViewModel.resetCreateNoteScreenState(true)
                    navController.navigateUp()
                }
            }
        }
    }
    BackHandler(onBack = {
        notesViewModel.resetCreateNoteScreenState(true)
        navController.navigateUp()
    })
    if (!isGranted) {
        PermissionDeniedScreen(requestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                isGranted = askPermission(context)
            } else {
                launcher.launch(permission)
            }
        })

    }else{
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackBarSate) },
            containerColor = DarkBackground,
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                navController.navigateUp()
                            }
                            notesViewModel.resetCreateNoteScreenState(true)
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
                    finishedText = "Created",
                    defaultText = "Create",
                    errorText = "Error",
                    containerColor = RichBlue,
                    disabledColor = RichBlue,
                    errorColor = Color.Red,
                    finishedColor = RichBlue,
                    state = buttonState,
                    onClick = {
                        val createNote = CreateNote(
                            text = noteText,
                            title = noteHeading,
                            image = imageUri,
                            date = dateText
                        )
                        notesViewModel.onEvent(NotesAction.CreateNote(createNote))
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                stickyHeader {
                    AnimatedVisibility(
                        visible = textEditorState.isListClicked,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 15.dp)
                    ) {
                        ListSelectionRow(notesViewModel, listState, richTextFieldState)

                    }
                    AnimatedVisibility(
                        visible = textEditorState.isHighlighterClicked,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 15.dp)
                    ) {
                        ColorSelectionRow(
                            notesViewModel = notesViewModel,
                            richTextFieldState = richTextFieldState,
                            colorSelectionState = colorSelectionState
                        )
                    }

                    TextFormattingRow(
                        viewModel = notesViewModel,
                        textEditorState = textEditorState,
                        richTextFieldState = richTextFieldState,

                        ) {
                        if (imageUriPath.isEmpty()) {
                            singleGalleryLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )

                            notesViewModel.onTextFormattingChange(
                                textEditorState.copy(isImageClicked = true)
                            )
                        } else {
                            scope.launch {
                                snackBarSate.showSnackbar("Image Already Selected")
                            }
                        }
                    }
                }
                item {
                    TextField(
                        value = noteHeading,
                        onValueChange = notesViewModel::onNoteHeadingText,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            cursorColor = RichBlue
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 25.sp,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                        ),
                        placeholder = {
                            Text(
                                text = "Title",
                                color = DarkGray,
                                fontSize = 25.sp,
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateText,
                            fontFamily = PoppinsFamily,
                            fontSize = 11.sp,
                            color = DarkGray
                        )
                        VerticalDivider(
                            modifier = Modifier
                                .height(10.dp),
                            thickness = 1.dp,
                            color = DarkGray
                        )
                        Text(
                            text = "$currentWordSize words",
                            fontFamily = PoppinsFamily,
                            fontSize = 11.sp,
                            color = DarkGray
                        )

                    }
                    AnimatedVisibility(visible = imageUriPath.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 5.dp, horizontal = 15.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        ) {
                            AsyncImage(
                                model = imageUri,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = "",
                                contentScale = ContentScale.FillBounds
                            )
                            IconButton(
                                onClick = {
                                    imageUriPath = ""
                                    notesViewModel.onTextFormattingChange(
                                        textEditorState.copy(isImageClicked = false)
                                    )
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = LightBlack.copy(
                                        alpha = 0.5f
                                    )
                                )

                            ) {

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Lighter
                                )
                            }
                        }
                    }
                    RichTextEditor(
                        state = richTextFieldState,
                        colors = RichTextEditorDefaults.richTextEditorColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = RichBlue,
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Your Text",
                                color = DarkGray,
                                fontSize = 15.sp,
                                fontFamily = PoppinsFamily,
                            )
                        },
                        textStyle = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontSize = 15.sp,
                            color = Lighter,
                        ),
                    )
                }


            }
        }
    }
}

@Composable
fun ListSelectionRow(
    notesViewModel: NotesViewModel,
    listState: ListState,
    richTextFieldState: RichTextState,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                LightBlack,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 5.dp, horizontal = 15.dp)
    ) {
        EchoNoteIconButton(
            icon = R.drawable.ic_ordered_list,
            onClick = {
                notesViewModel.onListStateChange(ListState(orderedListSelected = !listState.orderedListSelected))
                richTextFieldState.toggleOrderedList()
            },
            isClicked = listState.orderedListSelected
        )
        EchoNoteIconButton(
            icon = R.drawable.ic_list,
            onClick = {
                notesViewModel.onListStateChange(ListState(unorderedListSelected = !listState.unorderedListSelected))
                richTextFieldState.toggleUnorderedList()
            },
            isClicked = listState.unorderedListSelected
        )
    }
}

@Composable
fun ColorSelectionRow(
    notesViewModel: NotesViewModel,
    richTextFieldState: RichTextState,
    colorSelectionState: ColorSelectionState,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                LightBlack,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 5.dp, horizontal = 15.dp)
    ) {
        EchoNoteColorButton(
            color = Yellow, onClick = {
                if (colorSelectionState.yellowSelected) {
                    notesViewModel.onColorStateChange(ColorSelectionState(yellowSelected = false))
                } else {
                    notesViewModel.onColorStateChange(ColorSelectionState(yellowSelected = true))
                }
                richTextFieldState.toggleSpanStyle(
                    SpanStyle(
                        background = Yellow.copy(
                            0.8f
                        )
                    )
                )
            },
            clicked = colorSelectionState.yellowSelected
        )
        EchoNoteColorButton(
            color = Blue, onClick = {
                if (colorSelectionState.blueSelected) {
                    notesViewModel.onColorStateChange(ColorSelectionState(blueSelected = false))
                } else {
                    notesViewModel.onColorStateChange(ColorSelectionState(blueSelected = true))
                }
                richTextFieldState.toggleSpanStyle(
                    SpanStyle(
                        background = Blue.copy(
                            0.8f
                        )
                    )
                )
            },
            clicked = colorSelectionState.blueSelected
        )
        EchoNoteColorButton(
            color = Pink, onClick = {
                if (colorSelectionState.pinkSelected) {
                    notesViewModel.onColorStateChange(ColorSelectionState(pinkSelected = false))
                } else {
                    notesViewModel.onColorStateChange(ColorSelectionState(pinkSelected = true))
                }
                richTextFieldState.toggleSpanStyle(
                    SpanStyle(
                        background = Pink.copy(
                            0.8f
                        )
                    )
                )
            },
            clicked = colorSelectionState.pinkSelected
        )
        EchoNoteColorButton(
            color = Green, onClick = {
                if (colorSelectionState.greenSelected) {
                    notesViewModel.onColorStateChange(ColorSelectionState(greenSelected = false))
                } else {
                    notesViewModel.onColorStateChange(ColorSelectionState(greenSelected = true))
                }
                richTextFieldState.toggleSpanStyle(
                    SpanStyle(
                        background = Green.copy(
                            0.8f
                        )
                    )
                )
            },
            clicked = colorSelectionState.greenSelected
        )
    }
}

@Composable
fun TextFormattingRow(
    viewModel: NotesViewModel,
    textEditorState: TextEditorState,
    richTextFieldState: RichTextState,
    onImageClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 15.dp)
            .background(LightBlack, RoundedCornerShape(5.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EchoNoteIconButton(
            icon = R.drawable.ic_bold,
            onClick = {
                viewModel.onTextFormattingChange(
                    textEditorState.copy(isBold = !textEditorState.isBold)
                )
                richTextFieldState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
            },
            isClicked = textEditorState.isBold
        )
        EchoNoteIconButton(
            icon = R.drawable.ic_italic,
            onClick = {
                viewModel.onTextFormattingChange(
                    textEditorState.copy(isItalic = !textEditorState.isItalic)
                )
                richTextFieldState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
            },
            isClicked = textEditorState.isItalic
        )
        EchoNoteIconButton(
            icon = R.drawable.ic_underline,
            onClick = {
                viewModel.onTextFormattingChange(
                    textEditorState.copy(isUnderline = !textEditorState.isUnderline)
                )
                richTextFieldState.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            },
            isClicked = textEditorState.isUnderline
        )
        EchoNoteIconButton(
            icon = Icons.Default.Create,
            onClick = {
                viewModel.onTextFormattingChange(
                    textEditorState.copy(
                        isHighlighterClicked = !textEditorState.isHighlighterClicked,
                        isListClicked = false
                    )
                )
            },
            isClicked = textEditorState.isHighlighterClicked
        )
        EchoNoteIconButton(
            icon = R.drawable.ic_list,
            onClick = {
                viewModel.onTextFormattingChange(
                    textEditorState.copy(
                        isListClicked = !textEditorState.isListClicked,
                        isHighlighterClicked = false
                    )
                )
            },
            isClicked = textEditorState.isListClicked
        )
        EchoNoteIconButton(
            icon = R.drawable.ic_image,
            onClick = {
                onImageClick()
            },
            isClicked = textEditorState.isImageClicked
        )
    }
}


@RequiresApi(Build.VERSION_CODES.R)
private fun askPermission(context: Context): Boolean {
    var granted = false
    if (!Environment.isExternalStorageManager()) {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            )

            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            context.startActivity(intent)
        }
    } else {
        granted = true
    }
    return granted
}

@Composable
fun PermissionDeniedScreen(
    requestPermission: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Permission Denied",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Please grant permission to access files",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    requestPermission()
                },
                colors = ButtonDefaults.buttonColors(containerColor = RichBlue),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.bounceClick()
            ) {
                Text(
                    text = "Grant Permission",
                    fontFamily = PoppinsFamily,
                    fontSize = 15.sp    ,
                    color =Lighter
                )
            }
        }
    }
}


