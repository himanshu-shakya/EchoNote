package com.example.echonote.core.ui_componantes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

enum class ButtonAnimationState { Pressed, Idle }

fun Modifier.bounceClick() = composed {
    var buttonAnimationState by remember { mutableStateOf(ButtonAnimationState.Idle) }
    val scale by animateFloatAsState(
        if (buttonAnimationState == ButtonAnimationState.Pressed) 0.98f else 1f,
        label = ""
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }

        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonAnimationState) {
            awaitPointerEventScope {
                buttonAnimationState = if (buttonAnimationState == ButtonAnimationState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonAnimationState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonAnimationState.Pressed
                }
            }
        }
}


