package com.example.echonote.core.ui_componantes


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.echonote.R
import com.example.echonote.core.utils.ButtonState
import com.example.echonote.ui.theme.DarkGray
import com.example.echonote.ui.theme.LightBlack
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.PoppinsFamily
import com.example.echonote.ui.theme.RichBlue


@Composable
fun EchoNoteTextField(
    labelText: String,
    placeHolder: String,
    text: String,
    onTextChange: (text: String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Int,
    isPassword: Boolean = false,
    shape: RoundedCornerShape = RoundedCornerShape(5.dp),
    keyboardType: KeyboardType,
    errorText: String,
    isEnabled: Boolean
    ) {
    var visible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = labelText,
            color = Lighter,
            fontFamily = PoppinsFamily,
            fontSize = 14.sp
        )
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            singleLine =true,
            readOnly = !isEnabled,
            supportingText = {
                Text(
                    text = errorText,
                    color = Color.Red,
                    fontFamily = PoppinsFamily,

                    )
            },
            value = text,
            onValueChange = onTextChange,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = RichBlue,
                focusedContainerColor = LightBlack,
                unfocusedContainerColor = LightBlack,
                unfocusedIndicatorColor = DarkGray
            ),
            textStyle = TextStyle(
                color = Lighter,
                fontFamily = PoppinsFamily,
            ),
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = null,
                        tint = DarkGray
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(DarkGray)
                    )

                }
            },
            trailingIcon = {
                if (isPassword) {
                    Icon(
                        painter = painterResource(id = if (visible) R.drawable.ic_eye else R.drawable.ic_close_eye),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            visible = !visible
                        },
                        tint = DarkGray
                    )
                }
            },
            shape = shape,
            placeholder = {
                Text(
                    text = placeHolder,
                    fontFamily = PoppinsFamily,
                    color = DarkGray,
                    fontSize = 13.sp

                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (!visible && isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Composable
fun EchoNoteButton(
    textColor: Color = Lighter,
    finishedText: String,
    defaultText: String,
    errorText: String,
    containerColor: Color,
    disabledColor: Color,
    errorColor: Color,
    finishedColor: Color,
    state: ButtonState,
    onClick: () -> Unit,
    isEnabled: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    textSize: TextUnit,
) {
    val currentColor = when (state) {
        ButtonState.IDLE -> {
            containerColor
        }

        ButtonState.DISABLED -> {
            disabledColor
        }

        ButtonState.LOADING -> {
            containerColor
        }

        ButtonState.ERROR -> {
            errorColor
        }

        ButtonState.SUCCESS -> {
            finishedColor
        }
    }
    val currentText = when (state) {
        ButtonState.SUCCESS -> {
            finishedText
        }

        ButtonState.LOADING -> {
            ""
        }

        ButtonState.DISABLED -> {
            defaultText
        }

        ButtonState.IDLE -> {
            defaultText
        }

        ButtonState.ERROR -> {
            errorText
        }
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = currentColor),
        enabled = isEnabled,
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .height(50.dp)
            .bounceClick()
    ) {
        when (state) {
            ButtonState.IDLE -> {
                Text(
                    text = currentText,
                    fontFamily = PoppinsFamily,
                    fontSize = textSize,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }

            ButtonState.DISABLED -> {
                Text(
                    text = currentText,
                    fontFamily = PoppinsFamily,
                    fontSize = textSize,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }

            ButtonState.ERROR -> {
                Text(
                    text = currentText,
                    fontFamily = PoppinsFamily,
                    fontSize = textSize,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }

            ButtonState.SUCCESS -> {
                Text(
                    text = currentText,
                    fontFamily = PoppinsFamily,
                    fontSize = textSize,
                    color = textColor,
                    fontWeight = FontWeight.Bold

                )
            }

            ButtonState.LOADING -> {
                val isPlaying by remember {
                    mutableStateOf(true)
                }
                // for speed
                val speed by remember {
                    mutableStateOf(1f)
                }

                // remember lottie composition ,which
                // accepts the lottie composition result
                val composition by rememberLottieComposition(
                    LottieCompositionSpec
                        .RawRes(R.raw.loading)
                )

                val progress by animateLottieCompositionAsState(
                    // pass the composition created above
                    composition,

                    // Iterates Forever
                    iterations = LottieConstants.IterateForever,

                    // pass isPlaying we created above,
                    // changing isPlaying will recompose
                    // Lottie and pause/play
                    isPlaying = isPlaying,

                    // pass speed we created above,
                    // changing speed will increase Lottie
                    speed = speed,

                    // this makes animation to restart when paused and play
                    // pass false to continue the animation at which it was paused
                    restartOnPlay = false

                )

                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(400.dp)
                )

            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoNoteIconButton(
    modifier: Modifier = Modifier,
    defaultColor: Color = LightBlack,
    icon: Int,
    onClick: () -> Unit,
    isClicked: Boolean,
) {
    val color by animateColorAsState(
        if (isClicked) RichBlue else defaultColor,
        label = ""
    )
    val rotationAngle by animateFloatAsState(
        targetValue = if (isClicked) 360f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ), label = ""
    )

    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(containerColor = color)
    ) {

        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Lighter,
            modifier = modifier
                .graphicsLayer(
                    rotationZ = rotationAngle
                )
                .size(24.dp)

        )
    }
}

@Composable
fun EchoNoteIconButton(
    modifier: Modifier = Modifier,
    defaultColor: Color = LightBlack,
    icon: ImageVector,
    onClick: () -> Unit,
    isClicked: Boolean,
) {
    val color by animateColorAsState(
        if (isClicked) RichBlue else defaultColor,
        label = ""
    )
    val rotationAngle by animateFloatAsState(
        targetValue = if (isClicked) 360f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ), label = ""
    )

    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(containerColor = color)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Lighter,
            modifier = modifier
                .graphicsLayer(
                    rotationZ = rotationAngle
                )

        )
    }
}

@Composable
fun EchoNoteColorButton(
    modifier: Modifier = Modifier,
    color: Color,
    onClick: () -> Unit,
    clicked:Boolean
) {
    IconButton(
        modifier = Modifier.border(
            width = 1.dp,
            shape = CircleShape,
            color = if (clicked) RichBlue else Color.White
        ),
        onClick = {
            onClick()
        },
        colors = IconButtonDefaults.iconButtonColors(containerColor = color),
    ) {

    }
}

@Composable
fun HighlightedTextField() {
    var text by remember { mutableStateOf(TextFieldValue(AnnotatedString(""))) }

    TextField(
        value = text,
        onValueChange = {
            val newText = buildAnnotatedString {
                withStyle(style = SpanStyle(background = Color.Red)) {
                    append(it.text)
                }
            }
            text = TextFieldValue(newText, it.selection)

        },

        placeholder = {
            Text(
                text = "Notes",
                color = Lighter,
                fontSize = 15.sp
            )
        }
    )


}
@Composable
fun EchoNoteLoading(modifier: Modifier = Modifier) {
    val isPlaying by remember {
        mutableStateOf(true)
    }
    // for speed
    val speed by remember {
        mutableStateOf(1f)
    }

    // remember lottie composition ,which
    // accepts the lottie composition result
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.loading)
    )

    val progress by animateLottieCompositionAsState(
        // pass the composition created above
        composition,

        // Iterates Forever
        iterations = LottieConstants.IterateForever,

        // pass isPlaying we created above,
        // changing isPlaying will recompose
        // Lottie and pause/play
        isPlaying = isPlaying,

        // pass speed we created above,
        // changing speed will increase Lottie
        speed = speed,

        // this makes animation to restart when paused and play
        // pass false to continue the animation at which it was paused
        restartOnPlay = false

    )

    LottieAnimation(
        composition,
        progress,
        modifier = modifier.size(45.dp)
    )
}



