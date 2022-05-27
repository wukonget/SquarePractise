package com.wk.squarepratice.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val LightColorPalette = WeColors(
    squareItemBg = black1,
    textOnItem = black1,
    background = white2,
    onBackground = grey3,
    actionColor = BLUEMk,
    onActionColor = white,
    actionSecondColor = white,
    onActionSecondColor = BLUEMk,
    success = green3,
    error = red1,
    normal = Purple200,
)

private val DarkColorPalette = WeColors(
    squareItemBg = grey2,
    textOnItem = grey2,
    background = black2,
    onBackground = grey1,
    actionColor = grey5,
    onActionColor = grey4,
    actionSecondColor = grey4,
    onActionSecondColor = grey5,
    success = green3,
    error = red7,
    normal = grey1,
)

private val NewYearColorPalette = WeColors(
    squareItemBg = yellow1,
    textOnItem = yellow1,
    background = red5,
    onBackground = grey2,
    actionColor = red7,
    onActionColor = white,
    actionSecondColor = white,
    onActionSecondColor = red7,
    success = green1,
    error = red1,
    normal = yellow1,
)

private val LocalWeColors = compositionLocalOf {
    LightColorPalette
}

@Stable
object SquarePraticeTheme {
    val colors: WeColors
        @Composable
        get() = LocalWeColors.current

    enum class Theme {
        Light, Dark, NewYear
    }
}

@Stable
class WeColors(
    squareItemBg: Color,
    textOnItem: Color,
    background: Color,
    onBackground: Color,
    actionColor: Color,
    onActionColor: Color,
    actionSecondColor: Color,
    onActionSecondColor: Color,
    success: Color,
    error: Color,
    normal: Color,
) {
    var squareItemBg: Color by mutableStateOf(squareItemBg)
        private set
    var textOnItem: Color by mutableStateOf(textOnItem)
        private set
    var background: Color by mutableStateOf(background)
        private set
    var onBackground: Color by mutableStateOf(onBackground)
        private set
    var actionColor: Color by mutableStateOf(actionColor)
        private set
    var onActionColor: Color by mutableStateOf(onActionColor)
        private set
    var actionSecondColor: Color by mutableStateOf(actionSecondColor)
        private set
    var onActionSecondColor: Color by mutableStateOf(onActionSecondColor)
        private set
    var success: Color by mutableStateOf(success)
        private set
    var error: Color by mutableStateOf(error)
        private set
    var normal: Color by mutableStateOf(normal)
        private set
}

@Composable
fun SquarePraticeTheme(
    theme: SquarePraticeTheme.Theme = SquarePraticeTheme.Theme.Light,
    content: @Composable () -> Unit
) {
    val targetColors = when (theme) {
        SquarePraticeTheme.Theme.NewYear -> NewYearColorPalette
        SquarePraticeTheme.Theme.Dark -> DarkColorPalette
        SquarePraticeTheme.Theme.Light -> if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette
    }
    val squareItemBg = animateColor(targetColors.squareItemBg)
    val textOnItem = animateColor(targetColors.textOnItem)
    val background = animateColor(targetColors.background)
    val onBackground = animateColor(targetColors.onBackground)
    val actionColor = animateColor(targetColors.actionColor)
    val onActionColor = animateColor(targetColors.onActionColor)
    val actionSecondColor = animateColor(targetColors.actionSecondColor)
    val onActionSecondColor = animateColor(targetColors.onActionSecondColor)
    val success = animateColor(targetColors.success)
    val error = animateColor(targetColors.error)
    val normal = animateColor(targetColors.normal)

    val colors = WeColors(
        squareItemBg = squareItemBg.value,
        textOnItem = textOnItem.value,
        background = background.value,
        onBackground = onBackground.value,
        actionColor = actionColor.value,
        onActionColor = onActionColor.value,
        actionSecondColor = actionSecondColor.value,
        onActionSecondColor = onActionSecondColor.value,
        success = success.value,
        error = error.value,
        normal = normal.value
    )

    CompositionLocalProvider(LocalWeColors provides colors) {
        MaterialTheme(
            shapes = shapes,
            content = content
        )
    }
}

@Composable
private fun animateColor(targetColor: Color) =
    animateColorAsState(targetColor, TweenSpec(600))