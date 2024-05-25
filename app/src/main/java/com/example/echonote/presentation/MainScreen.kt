package com.example.echonote.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.echonote.R
import com.example.echonote.core.navigation.NavConstants
import com.example.echonote.core.ui_componantes.EchoNoteLoading
import com.example.echonote.core.ui_componantes.bounceClick
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.model.Notes
import com.example.echonote.domain.model.User
import com.example.echonote.domain.utils.AuthAction
import com.example.echonote.domain.utils.NotesAction
import com.example.echonote.presentation.viewModel.AuthViewModel
import com.example.echonote.presentation.viewModel.NotesViewModel
import com.example.echonote.ui.theme.ArchitectsFamily
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.DarkGray
import com.example.echonote.ui.theme.LightBlack
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    notesViewModel: NotesViewModel,
    authViewModel: AuthViewModel,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    var user by remember { mutableStateOf(User()) }
    val searchText by notesViewModel.searchQuery.collectAsState()
    var notes by remember { mutableStateOf(emptyList<Notes>()) }
    var isLoading by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val selectedNote by notesViewModel.selectedNote.collectAsState()
    var isBookmarking by remember {
        mutableStateOf(false)
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_state))
    var isDeleting by remember {
        mutableStateOf(false)
    }
    val isSearching by notesViewModel.isSearching.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        notesViewModel.onEvent(NotesAction.FetchNotes)
        authViewModel.onEvent(AuthAction.GetUser)
    }
    LaunchedEffect(true) {
        notesViewModel.fetchNotesFlow.collect { state ->
            when (state) {
                is UiState.Error -> {
                    snackBarHostState.showSnackbar(message = state.message)
                    isLoading = false
                }

                UiState.Idle -> {}
                UiState.Loading -> {
                    isLoading = true
                }

                is UiState.Success -> {
                    notes = state.data
                    isLoading = false
                }
            }

        }
    }
    LaunchedEffect(true) {
        notesViewModel.deleteNoteFlow.collect { state ->
            when (state) {
                is UiState.Error -> {
                    isDeleting = false
                    snackBarHostState.showSnackbar(message = state.message)

                }

                UiState.Idle -> {

                }

                UiState.Loading -> {
                    isDeleting = true
                }

                is UiState.Success -> {
                    isDeleting = false
                    snackBarHostState.showSnackbar(message = "Note Deleted")
                    notesViewModel.resetDeleteNoteState()
                }
            }

        }
    }
    LaunchedEffect(true) {
        notesViewModel.bookmarkNoteFlow.collect { state ->
            when (state) {
                is UiState.Error -> {
                    snackBarHostState.showSnackbar(message = state.message)
                    isBookmarking = false
                }

                UiState.Idle -> {}
                UiState.Loading -> {
                    isBookmarking = true
                }

                is UiState.Success -> {
                    isBookmarking = false

                }
            }
        }
    }
    LaunchedEffect(true) {
        authViewModel.userFlow.collect { state ->
            when (state) {
                is UiState.Success -> {
                    user = state.data
                }

                is UiState.Error -> {
                    snackBarHostState.showSnackbar(message = state.message)
                }

                else -> {}
            }
        }

    }

    Scaffold(
        topBar = {
            val appName = buildAnnotatedString {
                append("Echo")
                withStyle(
                    style = SpanStyle(
                        color = RichBlue
                    )
                ) {
                    append("Note")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp,horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = appName,
                    fontFamily = ArchitectsFamily,
                    fontSize = 28.sp,
                    color = Lighter.copy(alpha = 0.7f)
                )
                if (user.avatar.isEmpty()) {
                    IconButton(
                        onClick = {
                                  navController.navigate(NavConstants.SETTINGS_SCREEN.name)
                        },
                        colors = IconButtonDefaults.iconButtonColors(LightBlack ),
                        modifier =Modifier
                            .size(40.dp)
//                            .border(BorderStroke(0.5.dp,RichBlue),shape =CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = RichBlue,
                        )
                    }
                } else {
                    AsyncImage(
                        model = user.avatar,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }

            }

        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavConstants.CREATE_NOTE_SCREEN.name)

                },
                containerColor = RichBlue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Lighter
                )
            }
        }
    ) {
        if (isLoading || isBookmarking || isDeleting) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EchoNoteLoading()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 10.dp)
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = searchText,
                    onValueChange = notesViewModel::onSearchTextChange,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                        cursorColor = RichBlue,
                        focusedContainerColor = LightBlack,
                        unfocusedContainerColor = LightBlack,
                        focusedLeadingIconColor = RichBlue,
                        unfocusedLeadingIconColor = Lighter,
                    ),
                    placeholder = {
                        Text(
                            text = "Search",
                            fontFamily = PoppinsFamily,
                            fontSize = 16.sp,
                            color = DarkGray
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Lighter,
                        fontFamily = PoppinsFamily,
                        fontSize = 16.sp,
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                        )
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()

                )
                if (isSearching) {
                    EchoNoteLoading()

                } else {
                    if (notes.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = composition,
                                iterations = 1,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp),
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notes) { note ->

                            NotesCard(
                                note = note,
                                onMoreClick = {
                                    notesViewModel.onNoteSelected(note)
                                    showBottomSheet = true
                                },
                                onNoteClick = {
                                    notesViewModel.onNoteSelected(note)
                                    navController.navigate(NavConstants.UPDATE_NOTE_SCREEN.name)
                                },
                            )
                        }
                    }
                }
                if (showBottomSheet) {
                    val isBookmarked = selectedNote?.isBookmarked ?: false
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        containerColor = LightBlack,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp, 5.dp)
                                    .bounceClick(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = {
                                        showBottomSheet = false
                                        selectedNote?.let { note ->
                                            notesViewModel.onEvent(NotesAction.DeleteNote(noteId = note.id))
                                        }
                                    },
                                    modifier = Modifier
                                        .background(
                                            DarkBackground,
                                            RoundedCornerShape(50)
                                        )

                                ) {

                                    Icon(
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = "delete",
                                        tint = Lighter

                                    )
                                }
                                Text(
                                    text = "Delete",
                                    fontSize = 10.sp,
                                    fontFamily = PoppinsFamily,
                                    color = Lighter
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .padding(10.dp, 5.dp)
                                    .bounceClick(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(
                                    onClick = {
                                        showBottomSheet = false
                                        notesViewModel.onEvent(
                                            NotesAction.BookmarkNote(
                                                selectedNote?.id!!,
                                                !isBookmarked
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .background(
                                            if (isBookmarked) RichBlue else DarkBackground,
                                            RoundedCornerShape(50)
                                        )
                                        .bounceClick()
                                ) {

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_bookmark),
                                        contentDescription = "bookmark",
                                        tint = Lighter
                                    )
                                }

                                Text(
                                    text = if (isBookmarked) "Bookmarked" else "Bookmark",
                                    fontSize = 10.sp,
                                    fontFamily = PoppinsFamily,
                                    color = Lighter
                                )


                            }
                        }

                    }
                }


            }
        }

    }


}

@Composable
fun NotesCard(
    modifier: Modifier = Modifier,
    note: Notes,
    onNoteClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    val richTextSate = rememberRichTextState()
    richTextSate.setHtml(note.text)
    Box(
        modifier = modifier
            .size(width = 300.dp, height = 220.dp)
            .background(
                if (note.isBookmarked) RichBlue.copy(alpha = 0.6f) else LightBlack,
                RoundedCornerShape(10.dp)
            )
            .clickable {
                onNoteClick()
            }.bounceClick(),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Lighter,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(if (note.isBookmarked) 1f else 0.7f)
                )
                IconButton(onClick = { onMoreClick() }) {

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = Lighter,
                    )
                }

            }


            RichText(
                state = richTextSate,
                color = Lighter,
                fontFamily = PoppinsFamily,
                fontSize = 12.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = note.date,
            fontFamily = PoppinsFamily,
            fontSize = 10.sp,
            color = Lighter.copy(alpha = 0.6f),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.BottomEnd)
        )
    }
}



