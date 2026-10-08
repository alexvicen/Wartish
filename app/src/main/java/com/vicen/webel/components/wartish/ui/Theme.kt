package com.vicen.webel.components.wartish.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF111521)
val Panel = Color(0xFF20283A)
val PanelRaised = Color(0xFF2C354A)
val Gold = Color(0xFFFFC857)
val Mint = Color(0xFF71D7A5)
val Coral = Color(0xFFFF7B72)
val TextMain = Color(0xFFF3F1E8)
val TextSoft = Color(0xFFAFB9CC)

@Composable
fun WartishTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Gold,
            onPrimary = Ink,
            secondary = Mint,
            background = Ink,
            surface = Panel,
            onBackground = TextMain,
            onSurface = TextMain,
            error = Coral,
        ),
        content = content,
    )
}
