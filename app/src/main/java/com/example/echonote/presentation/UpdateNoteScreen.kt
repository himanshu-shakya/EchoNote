package com.example.echonote.presentation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.echonote.core.ui_componantes.EchoNoteButton
import com.example.echonote.core.utils.ButtonState
import com.example.echonote.core.utils.UiState
import com.example.echonote.core.utils.UriPathFinder
import com.example.echonote.domain.model.CreateNote
import com.example.echonote.domain.utils.NotesAction
import com.example.echonote.presentation.viewModel.NotesViewModel
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.DarkGray
import com.example.echonote.ui.theme.LightBlack
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UpdateNoteScreen(notesViewModel: NotesViewModel,navController: NavController) {
    val selectedNote by notesViewModel.selectedNote.collectAsState()
    val snackBarSate = remember { SnackbarHostState() }

    var buttonState by remember { mutableStateOf(ButtonState.IDLE) }

    val noteText by notesViewModel.noteText.collectAsState()

    val noteHeading by notesViewModel.noteHeading.collectAsState()

    val textEditorState by notesViewModel.textFormatting.collectAsState()

    val listState by notesViewModel.listState.collectAsState()

    val colorSelectionState by notesViewModel.colorState.collectAsState()
    val richTextFieldState = rememberRichTextState()
    val context = LocalContext.current
    var currentWordSize by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    var imageUriPath by remember { mutableStateOf("") }
    var isNoteTextChanged by remember { mutableStateOf(false) }
    var isNoteHeadingChanged by remember { mutableStateOf(false) }
    var isImageChanged by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        dateText = notesViewModel.getCurrentDateTimeFormatted()
        selectedNote?.let { note ->
            richTextFieldState.setHtml(note.text)
            notesViewModel.onNoteHeadingText(note.title)
            imageUriPath = note.image
            imageUri= note.image.toUri()
        }
    }

    var isButtonEnabled = isNoteTextChanged || isNoteHeadingChanged || isImageChanged
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

    LaunchedEffect(richTextFieldState.annotatedString) {
        currentWordSize = richTextFieldState.annotatedString.text.length.toString()
        if (richTextFieldState.annotatedString.text.isEmpty()) {
            notesViewModel.resetCreateNoteScreenState(false)
        }
        notesViewModel.onNoteTextChange(richTextFieldState.toHtml())
        isNoteTextChanged =selectedNote?.text !=richTextFieldState.toHtml()

    }
    LaunchedEffect(imageUriPath) {
        if (imageUriPath.isNotEmpty()) {
            isImageChanged = imageUriPath != selectedNote?.image
        }
    }

    LaunchedEffect(true) {
        notesViewModel.updateNoteFlow.collectLatest { state ->
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
                    notesViewModel.resetUpdateNoteState()
                    navController.navigateUp()
                }
            }
        }
    }

     isButtonEnabled = isNoteTextChanged || isNoteHeadingChanged || isImageChanged

    BackHandler(onBack = {
        notesViewModel.resetCreateNoteScreenState(true)
        navController.navigateUp()
    })
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarSate) },
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                },
                navigationIcon = {
                    IconButton(onClick = {
                        notesViewModel.resetCreateNoteScreenState(true)
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
                    val createNote = CreateNote(
                        text = noteText,
                        title = noteHeading,
                        image = imageUri,
                        date = dateText
                    )
             notesViewModel.onEvent(NotesAction.UpdateNote(selectedNote?.id!!,createNote))

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
                    onValueChange ={noteHeading->
                    notesViewModel.onNoteHeadingText(noteHeading)
                        isNoteHeadingChanged  = selectedNote?.title != noteHeading
                    },
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
                        text = selectedNote?.date ?: "",
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