package com.example.echonote.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echonote.core.utils.Result
import com.example.echonote.core.utils.UiState
import com.example.echonote.domain.model.CreateNote
import com.example.echonote.domain.model.Notes
import com.example.echonote.domain.repository.NotesRepository
import com.example.echonote.domain.utils.ColorSelectionState
import com.example.echonote.domain.utils.ListState
import com.example.echonote.domain.utils.NotesAction
import com.example.echonote.domain.utils.TextEditorState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _bookmarkNoteFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val bookmarkNoteFlow = _bookmarkNoteFlow.asStateFlow()
    private val _selectedNote = MutableStateFlow<Notes?>(null)
    val selectedNote = _selectedNote.asStateFlow()
    private val _createNoteFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val createNoteFlow = _createNoteFlow.asStateFlow()
    private val _fetchNotesFlow = MutableStateFlow<UiState<List<Notes>>>(UiState.Idle)
    private val _deleteNoteFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val deleteNoteFlow = _deleteNoteFlow.asStateFlow()
    private val _updateNoteFlow = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val updateNoteFlow = _updateNoteFlow.asStateFlow()
    val isSearching = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _textEditorState = MutableStateFlow(
        TextEditorState(
            isBold = false,
            isItalic = false,
            isUnderline = false,
            isHighlighterClicked = false
        )
    )
    val textFormatting = _textEditorState.asStateFlow()
    private val _listState = MutableStateFlow(
        ListState(
            unorderedListSelected = false,
            orderedListSelected = false
        )
    )
    val listState = _listState.asStateFlow()
    private val _colorState = MutableStateFlow(
        ColorSelectionState(
            yellowSelected = false,
            pinkSelected = false,
            greenSelected = false,
            blueSelected = false
        )
    )
    val colorState = _colorState.asStateFlow()
    private val _noteText = MutableStateFlow("")
    val noteText = _noteText.asStateFlow()
    private val _noteHeading = MutableStateFlow("")
    val noteHeading = _noteHeading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val fetchNotesFlow = _searchQuery
        .debounce(1000L)
        .onEach {
            isSearching.value = it.isNotBlank()
        }
        .flatMapLatest { query ->
            _fetchNotesFlow.map { notesState ->
                if (query.isBlank()) {
                    notesState
                } else {
                    val filteredNotes = when (notesState) {
                        is UiState.Success -> {
                            delay(1000)
                            notesState.data.filter { note ->
                                note.title.contains(query, ignoreCase = true) ||
                                        note.text.contains(query, ignoreCase = true)
                            }
                        }

                        else -> emptyList()
                    }
                    UiState.Success(filteredNotes)
                }
            }
        }.onEach {
            isSearching.value = false
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UiState.Loading
        )
    private val _bookmarkedNotes = MutableStateFlow<List<Notes>>(emptyList())
    val bookmarkedNotes = _bookmarkedNotes.asStateFlow()


    fun onEvent(notesAction: NotesAction) {
        when (notesAction) {
            is NotesAction.CreateNote -> {
                createNote(notesAction.createNote)
            }

            is NotesAction.FetchNotes -> {
                fetchNotes()
            }

            is NotesAction.DeleteNote -> {
                deleteNote(notesAction.noteId)
            }

            is NotesAction.UpdateNote -> {
                updateNote(notesAction.noteId, notesAction.note)
            }

            is NotesAction.BookmarkNote -> {
                bookmarkNote(notesAction.noteId, notesAction.bookmark)
            }
        }
    }
    private fun bookmarkNote(noteId: String, bookmark: Boolean) {
        _bookmarkNoteFlow.update { UiState.Loading }
        viewModelScope.launch {
            notesRepository.bookmarkNote(noteId, bookmark).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _bookmarkNoteFlow.update { UiState.Error(result.error) }
                    }

                    is Result.Success -> {
                        _bookmarkNoteFlow.update { UiState.Success(result.data) }
                        fetchNotes()
                    }
                }
            }
        }
    }

    private fun updateNote(noteId: String, note: CreateNote) {
        viewModelScope.launch {
            _updateNoteFlow.update { UiState.Loading }
            notesRepository.updateNote(noteId, note).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _updateNoteFlow.update { UiState.Error(result.error) }
                    }

                    is Result.Success -> {
                        _updateNoteFlow.update { UiState.Success(result.data) }
                    }
                }

            }
        }
    }

    private fun deleteNote(noteId: String) {
        _deleteNoteFlow.update { UiState.Loading }
        viewModelScope.launch {
            delay(500)
            notesRepository.deleteNote(noteId).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _deleteNoteFlow.update { UiState.Error(result.error) }
                    }

                    is Result.Success -> {
                        _deleteNoteFlow.update { UiState.Success(result.data) }
                        fetchNotes()
                    }
                }
            }
        }
    }

    private fun fetchNotes() {
        _fetchNotesFlow.update { UiState.Loading }
        viewModelScope.launch {
            notesRepository.fetchNotes().collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _fetchNotesFlow.update { UiState.Error(result.error) }
                    }

                    is Result.Success -> {
                        val sortedNotes = result.data.sortedByDescending { it.isBookmarked }
                        _fetchNotesFlow.update { UiState.Success(sortedNotes) }

                        val bookmarkedNotes = sortedNotes.filter { it.isBookmarked }
                        _bookmarkedNotes.update { bookmarkedNotes }
                    }
                }
            }
        }
    }

    private fun createNote(createNote: CreateNote) {
        viewModelScope.launch {
            _createNoteFlow.update { UiState.Loading }
            notesRepository.createNote(createNote).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _createNoteFlow.update { UiState.Error(result.error) }
                    }

                    is Result.Success -> {
                        _createNoteFlow.update { UiState.Success(result.data) }
                    }
                }
            }
        }
    }

    fun onNoteTextChange(newText: String) {
        _noteText.update { newText }
    }

    fun onNoteHeadingText(newText: String) {
        _noteHeading.update { newText }
    }

    fun onTextFormattingChange(newTextEditorState: TextEditorState) {
        _textEditorState.update { newTextEditorState }
    }

    fun onListStateChange(newListState: ListState) {
        _listState.update { newListState }
    }

    fun onColorStateChange(newColorState: ColorSelectionState) {
        _colorState.update { newColorState }
    }

    fun onSearchTextChange(newText: String) {
        _searchQuery.update { newText }
    }

    fun onNoteSelected(notes: Notes) {
        _selectedNote.update { notes }

    }


    fun resetCreateNoteScreenState(resetAll: Boolean) {
        if (resetAll) {
            _noteText.value = ""
            _noteHeading.value = ""
            _createNoteFlow.update { UiState.Idle }
        }
        _textEditorState.value = TextEditorState(
            isBold = false,
            isItalic = false,
            isUnderline = false,
            isHighlighterClicked = false,
            isListClicked = false,
            isImageClicked = false
        )

        _listState.value = ListState(
            orderedListSelected = false,
            unorderedListSelected = false
        )
    }


    fun getCurrentDateTimeFormatted(): String {
        val currentDate = Date()
        val formatter = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return formatter.format(currentDate)
    }

    fun resetUpdateNoteState() {
        _updateNoteFlow.update { UiState.Idle }
        _noteText.value = ""
        _noteHeading.value = ""
    }


}