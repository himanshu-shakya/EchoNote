package com.example.echonote.presentation

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.echonote.core.navigation.NavConstants
import com.example.echonote.ui.theme.ArchitectsFamily
import com.example.echonote.ui.theme.Lighter
import com.example.echonote.ui.theme.RichBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController,firebaseAuth:FirebaseAuth) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val color  = remember{
        Animatable(Lighter)
    }

    LaunchedEffect(key1 = Unit ){
        delay(50)
        scale.animateTo(
            targetValue = 1.8f,
            animationSpec =tween(300, easing = EaseInBounce  ),

        )
        scale.animateTo(
            targetValue =1f,
            animationSpec = tween(300,easing = EaseOutBounce)
        )
        color.animateTo(
            targetValue = RichBlue,
            animationSpec = tween(
                durationMillis = 500,
                easing = EaseInSine
            )
        )
        delay(100)
            if(firebaseAuth.currentUser!=null ){
                navController.popBackStack()
                navController.navigate(NavConstants.MAIN_SCREEN.name){
                    launchSingleTop =true
                }
            }else{
                navController.popBackStack()
                navController.navigate(NavConstants.LOGIN_SCREEN.name){
                    launchSingleTop = true
                }
            }


    }
    LaunchedEffect(true) {
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale.value),    
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Echo",
                fontFamily = ArchitectsFamily,
                fontSize = 50.sp,
                color = Lighter,
            )
            Text(
                text = "Note",
                fontFamily = ArchitectsFamily,
                fontSize = 50.sp,
                color = color.value,

            )
        }
    }
}


