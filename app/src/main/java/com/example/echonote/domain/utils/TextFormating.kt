    package com.example.echonote.domain.utils

    data class TextEditorState(
        val isBold :Boolean =false,
        val isItalic:Boolean=false,
        val isUnderline:Boolean=false,
        val isHighlighterClicked:Boolean=false,
        val isListClicked:Boolean=false,
        val isImageClicked:Boolean=false

    )
    data class ListState(
        val orderedListSelected:Boolean=false,
        val unorderedListSelected:Boolean=false,
    )
    data class ColorSelectionState (
        val yellowSelected:Boolean =false,
        val pinkSelected:Boolean =false,
        val greenSelected:Boolean =false,
        val blueSelected:Boolean =false,
    )
