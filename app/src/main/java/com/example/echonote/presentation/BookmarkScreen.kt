package com.example.echonote.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.echonote.R
import com.example.echonote.core.navigation.NavConstants
import com.example.echonote.core.ui_componantes.EchoNoteLoading
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.model.Notes
import com.example.echonote.domain.utils.NotesAction
import com.example.echonote.presentation.viewModel.NotesViewModel
import com.example.echonote.ui.theme.DarkBackground
import com.example.echonote.ui.theme.LightBlack
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkNotesScreen(
    notesViewModel: NotesViewModel,
    navController: NavController,
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.no_bookmarks_state))
    val notes by notesViewModel.bookmarkedNotes.collectAsState()
    val snackBarState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        notesViewModel.bookmarkNoteFlow.collectLatest { state ->
            when (state) {
                is UiState.Error -> {
                    isLoading = false
                    snackBarState.showSnackbar(state.message)

                }

                UiState.Idle -> {}
                UiState.Loading -> {
                    isLoading = true
                }

                is UiState.Success -> {
                    isLoading = false
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bookmarks",
                        fontFamily = PoppinsFamily,
                        color = Lighter
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()

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
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (notes.isEmpty()) {
                Spacer(modifier = Modifier.height(100.dp))
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(250.dp),
                    iterations = Int.MAX_VALUE,

                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "No Bookmarks",
                    fontFamily = PoppinsFamily,
                    fontSize = 20.sp,
                    color = Lighter,
                )
            } else {
                if(isLoading){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){

                        EchoNoteLoading()
                    }
                }else{
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notes) { note ->
                            BookMarkNotesCard(
                                note = note,
                                onNoteClick = {
                                    notesViewModel.onNoteSelected(note)
                                    navController.navigate(NavConstants.UPDATE_NOTE_SCREEN.name)
                                },
                                onBookmarkClick = {
                                    notesViewModel.onEvent(NotesAction.BookmarkNote(note.id, false))
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun BookMarkNotesCard(
    modifier: Modifier = Modifier,
    note: Notes,
    onNoteClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
) {
    val richTextSate = rememberRichTextState()
    richTextSate.setHtml(note.text)
    Box(
        modifier = modifier
            .size(width = 300.dp, height = 220.dp)
            .background(
                LightBlack,
                RoundedCornerShape(10.dp)
            )
            .clickable {
                onNoteClick()
            },
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
                    modifier = Modifier.weight(0.7f)
                )
                IconButton(onClick = {
                    onBookmarkClick()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bookmarked),
                        contentDescription = null,
                        tint = RichBlue,
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