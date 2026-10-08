package com.vicen.webel.components.wartish.ui.screens

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import com.vicen.webel.components.wartish.data.Material
import com.vicen.webel.components.wartish.game.FallingEngine
import com.vicen.webel.components.wartish.game.FallingState
import com.vicen.webel.components.wartish.game.GameUiState
import com.vicen.webel.components.wartish.ui.*
import com.vicen.webel.components.wartish.ui.components.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun ProcessingScreen(ui: GameUiState, actions: ProcessingScreenActions, onBack: () -> Unit) {
    val game = ui.falling
    DisposableEffect(Unit) { onDispose { actions.pause() } }
    LaunchedEffect(game?.running) {
        while (game?.running == true) {
            delay(1000)
            actions.tick()
        }
    }
    val pendingClear = game?.pendingClear
    val clearProgress = remember(pendingClear?.id) {
        pendingClear?.cells?.map { Animatable(0f) }.orEmpty()
    }
    LaunchedEffect(pendingClear?.id) {
        val animation = pendingClear ?: return@LaunchedEffect
        coroutineScope {
            clearProgress.forEachIndexed { index, progress ->
                launch {
                    delay(index * 22L)
                    progress.animateTo(1f, tween(720, easing = LinearOutSlowInEasing))
                }
            }
        }
        actions.completeClear(animation.id)
    }
    Box(Modifier.fillMaxSize()) {
        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.fondo_horno_procesamiento),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
        )
        Box(Modifier.fillMaxSize().background(Ink.copy(alpha = .25f)))

        Column(
            Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(Modifier.fillMaxWidth().height(52.dp)) {
                RusticBackButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
                Text(
                    "FÁBRICA",
                    modifier = Modifier.align(Alignment.Center),
                    color = Gold,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    letterSpacing = .8.sp,
                    maxLines = 1,
                )
                GoldButton(
                    label = if (game?.running == true) "PAUSAR" else "JUGAR",
                    modifier = Modifier.align(Alignment.CenterEnd).width(82.dp),
                    onClick = {
                        if (game?.running == true) actions.pause() else actions.startOrResume()
                    },
                    enabled = game != null && game.pendingClear == null,
                )
            }

            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (game == null) {
                    Text("Preparando tablero…", color = TextSoft)
                } else {
                    val activeCells = FallingEngine.activeCellIndices(game)
                    val boardHeight = minOf(maxWidth * 2f, (maxHeight - 18.dp).coerceAtLeast(1.dp))
                    val boardWidth = boardHeight / 2f
                    val clearTail = 18.dp
                    Box(Modifier.width(boardWidth).height(boardHeight + clearTail)) {
                        val boardModifier = Modifier.width(boardWidth).height(boardHeight)
                            .border(3.dp, Gold, RoundedCornerShape(4.dp))
                            .padding(3.dp)
                            .pointerInput(game.running, pendingClear?.id) {
                                if (game.running && pendingClear == null) {
                                    var dragX = 0f
                                    var dragY = 0f
                                    var lastSoftDropAt = 0L
                                    detectDragGestures(
                                        onDragStart = {
                                            dragX = 0f
                                            dragY = 0f
                                            lastSoftDropAt = SystemClock.uptimeMillis() - SOFT_DROP_INTERVAL_MS
                                        },
                                        onDrag = { change, amount ->
                                            dragX += amount.x
                                            dragY += amount.y
                                            val stepX = size.width / FallingEngine.COLUMNS.toFloat()
                                            val stepY = size.height / FallingEngine.ROWS.toFloat()
                                            while (dragX >= stepX * .6f) { actions.move(1); dragX -= stepX }
                                            while (dragX <= -stepX * .6f) { actions.move(-1); dragX += stepX }
                                            val now = SystemClock.uptimeMillis()
                                            if (dragY >= stepY * .75f && now - lastSoftDropAt >= SOFT_DROP_INTERVAL_MS) {
                                                actions.tick()
                                                dragY -= stepY
                                                lastSoftDropAt = now
                                            }
                                            if (dragY <= -stepY * .8f) { actions.rotate(); dragY = 0f }
                                            change.consume()
                                        },
                                    )
                                }
                            }
                            .pointerInput(game.running, pendingClear?.id) {
                                if (game.running && pendingClear == null) detectTapGestures(onTap = { actions.rotate() })
                            }
                        Box(boardModifier) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(FallingEngine.COLUMNS),
                                modifier = Modifier.fillMaxSize(),
                                userScrollEnabled = false,
                                verticalArrangement = Arrangement.spacedBy(0.dp),
                                horizontalArrangement = Arrangement.spacedBy(0.dp),
                            ) {
                                itemsIndexed(game.cells) { index, cell ->
                                    val current = index in activeCells
                                    val row = index / FallingEngine.COLUMNS
                                    Box(
                                        Modifier.aspectRatio(1f)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(if (row >= FallingEngine.ROWS - 4) Ink.copy(alpha = .42f) else Ink.copy(alpha = .57f))
                                            .border(0.5.dp, TextSoft.copy(alpha = .13f)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        when {
                                            current -> PixelMaterial(game.material, Modifier.fillMaxSize().padding(1.dp))
                                            cell >= 0 -> PixelMaterial(cell, Modifier.fillMaxSize().padding(1.dp))
                                        }
                                    }
                                }
                            }
                        }

                        pendingClear?.cells?.forEachIndexed { index, clearedCell ->
                            val progress = clearProgress.getOrNull(index)?.value ?: 0f
                            if (progress < 1f) {
                                val row = clearedCell.index / FallingEngine.COLUMNS
                                val column = clearedCell.index % FallingEngine.COLUMNS
                                val cellWidth = (boardWidth - 6.dp) / FallingEngine.COLUMNS
                                val cellHeight = (boardHeight - 6.dp) / FallingEngine.ROWS
                                val startX = 3.dp + cellWidth * (column + .5f)
                                val startY = 3.dp + cellHeight * (row + .5f)
                                val centerX = boardWidth / 2f
                                val centerY = boardHeight + 9.dp
                                val x = startX + (centerX - startX) * progress
                                val y = startY + (centerY - startY) * progress
                                Box(
                                    Modifier.offset(x = x - 11.dp, y = y - 11.dp).size(22.dp)
                                        .graphicsLayer {
                                            val scale = 1f - progress * .78f
                                            scaleX = scale
                                            scaleY = scale
                                            alpha = 1f - progress
                                            rotationZ = progress * 36f
                                        }
                                        .zIndex(3f),
                                ) {
                                    PixelMaterial(clearedCell.material, Modifier.fillMaxSize())
                                }
                            }
                        }
                    }
                }
            }

            CampBackpack(ui.player, Modifier.fillMaxWidth())
        }

        AnimatedVisibility(
            visible = !ui.notice.isNullOrBlank(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 104.dp).zIndex(10f),
            enter = fadeIn(tween(160)) + slideInVertically(tween(180)) { -it / 2 },
            exit = fadeOut(tween(180)),
        ) {
            Surface(color = PanelRaised, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Gold)) {
                Text(ui.notice.orEmpty(), color = Gold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), textAlign = TextAlign.Center, fontSize = 11.sp)
            }
        }
    }
}

private const val SOFT_DROP_INTERVAL_MS = 120L

private fun processingShapeName(shapeVariant: Int): String = when (shapeVariant) {
    FallingEngine.SHAPE_I -> "I"
    FallingEngine.SHAPE_O -> "O"
    FallingEngine.SHAPE_L -> "L"
    FallingEngine.SHAPE_Z -> "Z"
    FallingEngine.SHAPE_T -> "T"
    FallingEngine.SHAPE_MIRRORED_L -> "L espejo"
    FallingEngine.SHAPE_MIRRORED_Z -> "Z espejo"
    else -> ""
}


internal data class ProcessingScreenActions(
    val pause: () -> Unit = {},
    val tick: () -> Unit = {},
    val completeClear: (Long) -> Unit = {},
    val move: (Int) -> Unit = {},
    val rotate: () -> Unit = {},
    val startOrResume: () -> Unit = {},
)

@Preview(name = "Fábrica", showBackground = true)
@Composable
private fun ProcessingScreenPreview() {
    WartishTheme {
        ProcessingScreen(
            ui = GameUiState(
                falling = FallingState(material = Material.WOOD.ordinal, shapeVariant = FallingEngine.SHAPE_T),
            ),
            actions = ProcessingScreenActions(),
            onBack = {},
        )
    }
}
