package com.vicen.webel.components.wartish.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.game.GameUiState
import com.vicen.webel.components.wartish.ui.*
import com.vicen.webel.components.wartish.ui.components.*

@Composable
internal fun HomeScreen(
    ui: GameUiState,
    onCollect: () -> Unit,
    onProcess: () -> Unit,
    onBattle: () -> Unit,
    onSmith: () -> Unit,
    onSuture: () -> Unit,
) {
    var showStats by remember { mutableStateOf(false) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.campamento_circular),
            contentDescription = "Campamento con mina, fábrica, herrería y un bosque sombrío alrededor de una plaza circular",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
        )
        CampBuildingHotspot("MINA", .025f, .165f, .45f, .18f, .35f, onCollect)
        CampBuildingHotspot("FÁBRICA", .53f, .165f, .445f, .18f, .35f, onProcess)
        CampBuildingHotspot("HERRERÍA", .02f, .535f, .465f, .235f, .775f, onSmith)
        CampBuildingHotspot("BOSQUE SOMBRÍO", .525f, .535f, .45f, .245f, .775f, onBattle)
        CampBuildingHotspot("HOGUERA", .265f, .39f, .47f, .14f, .50f, onSuture)

        PlayerProfileWidget(
            ui.player,
            Modifier.align(Alignment.TopStart).padding(8.dp).width(minOf(270.dp, maxWidth - 24.dp)).zIndex(8f),
            onClick = { showStats = true },
        )
        AnimatedVisibility(
            visible = !ui.notice.isNullOrBlank(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 86.dp).zIndex(10f),
            enter = fadeIn(tween(160)) + slideInVertically(tween(180)) { -it / 2 },
            exit = fadeOut(tween(180)),
        ) {
            Surface(color = PanelRaised, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Gold)) {
                Text(ui.notice.orEmpty(), color = Gold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), textAlign = TextAlign.Center)
            }
        }
        if (showStats) ProfileStatisticsDialog(ui.player) { showStats = false }
    }
}

@Composable
private fun CampBuildingHotspot(
    label: String,
    xFraction: Float,
    yFraction: Float,
    widthFraction: Float,
    heightFraction: Float,
    labelYFraction: Float,
    onClick: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val left = maxWidth * xFraction
        val width = maxWidth * widthFraction
        Box(
            Modifier.offset(x = left, y = maxHeight * yFraction)
                .size(width = width, height = maxHeight * heightFraction)
                .clickable(onClick = onClick),
        )
        Text(
            label,
            modifier = Modifier.offset(x = left, y = maxHeight * labelYFraction)
                .width(width)
                .background(Ink.copy(alpha = .50f), RoundedCornerShape(3.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            color = Gold,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}


@Preview(name = "Campamento", showBackground = true)
@Composable
private fun HomeScreenPreview() {
    WartishTheme {
        HomeScreen(GameUiState(), onCollect = {}, onProcess = {}, onBattle = {}, onSmith = {}, onSuture = {})
    }
}
