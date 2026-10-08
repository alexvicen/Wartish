package com.vicen.webel.components.wartish.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.game.CollectionEngine
import com.vicen.webel.components.wartish.game.CollectionRewardPopup
import com.vicen.webel.components.wartish.game.CollectionState
import com.vicen.webel.components.wartish.game.CollectionTurnAnimation
import com.vicen.webel.components.wartish.game.GameUiState
import com.vicen.webel.components.wartish.ui.*
import com.vicen.webel.components.wartish.ui.components.*
import kotlin.math.abs
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun CollectionScreen(ui: GameUiState, actions: CollectionScreenActions, onBack: () -> Unit) {
    val game = ui.collection
    GamePage(
        "Mina", "Combina materiales para extraer recursos", ui.player, onBack, ui.notice,
        backpackAtStart = true,
        backButtonAboveProfile = true,
        fillRemainingHeight = true,
        centerTitleAtTop = true,
        showProfile = false,
        campBackpack = true,
        screenBackground = R.drawable.fondo_mina,
    ) {
        if (game == null) Text("Preparando tablero…", color = TextSoft)
        else {
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                val boardSize = minOf(maxWidth, maxHeight)
                CollectionBoard(game, ui.collectionAnimation, actions, Modifier.size(boardSize))
            }
            if (game.complete) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("La partida ha terminado.", color = Mint, fontWeight = FontWeight.Bold)
                    GoldButton("NUEVA PARTIDA", onClick = { actions.restart() })
                }
            }
        }
    }
}

@Composable
private fun CollectionBoard(game: CollectionState, turn: CollectionTurnAnimation?, actions: CollectionScreenActions, modifier: Modifier = Modifier) {
    var displayedBoard by remember { mutableStateOf(game.board) }
    var matchedCells by remember { mutableStateOf(emptySet<Int>()) }
    var sourceRows by remember { mutableStateOf(emptyMap<Int, Int>()) }
    var activePopups by remember { mutableStateOf(emptyList<CollectionRewardPopup>()) }
    var animationFrame by remember { mutableIntStateOf(0) }
    var draggedIndex by remember { mutableIntStateOf(-1) }
    var dragTarget by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val matchProgress = remember { Animatable(1f) }
    val fallProgress = remember { Animatable(1f) }
    val dragReturnProgress = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val gapPx = with(androidx.compose.ui.platform.LocalDensity.current) { 2.dp.toPx() }

    fun clearDrag() {
        draggedIndex = -1
        dragTarget = -1
        dragOffset = Offset.Zero
        coroutineScope.launch { dragReturnProgress.snapTo(1f) }
    }

    fun returnDragToCell(index: Int) {
        coroutineScope.launch {
            dragReturnProgress.animateTo(0f, tween(150, easing = FastOutSlowInEasing))
            if (draggedIndex == index) clearDrag()
        }
    }

    LaunchedEffect(game.board, turn?.id) {
        if (turn == null) {
            displayedBoard = game.board
            matchedCells = emptySet()
            sourceRows = emptyMap()
            activePopups = emptyList()
            return@LaunchedEffect
        }

        clearDrag()
        turn.steps.forEach { step ->
            displayedBoard = step.matchedBoard
            sourceRows = emptyMap()
            matchedCells = step.matchedCells
            activePopups = step.popups
            animationFrame++
            matchProgress.snapTo(1f)
            delay(35)
            matchProgress.animateTo(0f, tween(260, easing = LinearOutSlowInEasing))

            matchedCells = emptySet()
            displayedBoard = step.fallenBoard
            sourceRows = step.sourceRows
            fallProgress.snapTo(0f)
            fallProgress.animateTo(1f, tween(430, easing = FastOutSlowInEasing))
            activePopups = emptyList()
            sourceRows = emptyMap()
        }

        displayedBoard = game.board
        actions.clearAnimation(turn.id)
    }

    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val cellHeight = ((maxHeight - 20.dp) / CollectionEngine.SIZE).coerceAtLeast(1.dp)
        val cellWidth = ((maxWidth - 20.dp) / CollectionEngine.SIZE).coerceAtLeast(1.dp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(CollectionEngine.SIZE),
            modifier = Modifier.fillMaxSize().border(3.dp, Gold, RoundedCornerShape(4.dp)).padding(3.dp),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            itemsIndexed(displayedBoard, key = { index, _ -> index }) { index, item ->
                val row = index / CollectionEngine.SIZE
                val column = index % CollectionEngine.SIZE
                val matching = index in matchedCells
                val dragEnabled = !game.complete && turn == null

                Box(
                    Modifier.fillMaxWidth().height(cellHeight)
                        .graphicsLayer {
                            val sourceRow = sourceRows[index] ?: row
                            translationX = 0f
                            translationY = (sourceRow - row) * (size.height + gapPx) * (1f - fallProgress.value)
                            val scale = if (matching) matchProgress.value else 1f
                            val isDragged = draggedIndex == index
                            val isDragTarget = dragTarget == index && draggedIndex >= 0
                            val horizontalDrag = abs(dragOffset.x) >= abs(dragOffset.y)
                            val cellSpan = (if (horizontalDrag) size.width else size.height) + gapPx
                            val threshold = (if (horizontalDrag) size.width else size.height) * .28f
                            val primaryOffset = if (horizontalDrag) dragOffset.x else dragOffset.y
                            val swapProgress = ((abs(primaryOffset) - threshold) / (cellSpan - threshold).coerceAtLeast(1f)).coerceIn(0f, 1f)
                            val sign = if (primaryOffset >= 0f) 1f else -1f
                            if (isDragged) {
                                translationX = dragOffset.x * dragReturnProgress.value
                                translationY = dragOffset.y * dragReturnProgress.value
                            } else if (isDragTarget) {
                                if (horizontalDrag) translationX = -sign * swapProgress * cellSpan * dragReturnProgress.value
                                else translationY = -sign * swapProgress * cellSpan * dragReturnProgress.value
                            }
                            scaleX = if (isDragged) 1.12f else scale
                            scaleY = if (isDragged) 1.12f else scale
                            alpha = if (matching) matchProgress.value else 1f
                        }
                        .zIndex(if (draggedIndex == index) 4f else if (matching) 2f else 0f)
                        .pointerInput(index, dragEnabled) {
                            if (dragEnabled) {
                                detectDragGestures(
                                    onDragStart = {
                                        draggedIndex = index
                                        dragTarget = -1
                                        dragOffset = Offset.Zero
                                        coroutineScope.launch { dragReturnProgress.snapTo(1f) }
                                    },
                                    onDragCancel = { returnDragToCell(index) },
                                    onDragEnd = {
                                        val target = collectionDragTarget(index, dragOffset, size.width, size.height)
                                        if (target != null) {
                                            actions.swapCells(index, target) { accepted ->
                                                if (accepted) clearDrag() else returnDragToCell(index)
                                            }
                                        } else returnDragToCell(index)
                                    },
                                    onDrag = { change, amount ->
                                        dragOffset += amount
                                        dragTarget = collectionDragTarget(index, dragOffset, size.width, size.height) ?: -1
                                        change.consume()
                                    },
                                )
                            }
                        }
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (index == game.selected) Gold.copy(alpha = .35f) else PanelRaised)
                        .then(if (index == game.selected) Modifier.border(2.dp, Gold, RoundedCornerShape(3.dp)) else Modifier)
                        .clickable(enabled = dragEnabled) { actions.chooseCell(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    PixelMaterial(item, Modifier.fillMaxSize().padding(3.dp))
                }
            }
        }

        if (activePopups.isNotEmpty()) {
            Box(Modifier.fillMaxSize().zIndex(10f)) {
                activePopups.forEachIndexed { popupIndex, popup ->
                    val row = popup.anchorIndex / CollectionEngine.SIZE
                    val column = popup.anchorIndex % CollectionEngine.SIZE
                    val centerX = 3.dp + cellWidth / 2 + (cellWidth + 2.dp) * column.toFloat()
                    val centerY = 3.dp + cellHeight / 2 + (cellHeight + 2.dp) * row.toFloat()
                    CollectionRewardFloat(
                        popup = popup,
                        frame = animationFrame,
                        popupIndex = popupIndex,
                        modifier = Modifier.offset(x = centerX - 56.dp, y = centerY - 9.dp),
                    )
                }
            }
        }
    }
}

private fun collectionDragTarget(index: Int, offset: Offset, cellWidth: Int, cellHeight: Int): Int? {
    val row = index / CollectionEngine.SIZE
    val column = index % CollectionEngine.SIZE
    val threshold = minOf(cellWidth, cellHeight) * .28f
    return when {
        abs(offset.x) >= abs(offset.y) && abs(offset.x) >= threshold -> {
            val nextColumn = column + if (offset.x > 0f) 1 else -1
            if (nextColumn in 0 until CollectionEngine.SIZE) row * CollectionEngine.SIZE + nextColumn else null
        }
        abs(offset.y) >= threshold -> {
            val nextRow = row + if (offset.y > 0f) 1 else -1
            if (nextRow in 0 until CollectionEngine.SIZE) nextRow * CollectionEngine.SIZE + column else null
        }
        else -> null
    }
}

@Composable
private fun CollectionRewardFloat(
    popup: CollectionRewardPopup,
    frame: Int,
    popupIndex: Int,
    modifier: Modifier = Modifier,
) {
    val rise = remember(frame, popupIndex) { Animatable(0f) }
    val opacity = remember(frame, popupIndex) { Animatable(1f) }

    LaunchedEffect(frame, popupIndex) {
        delay(40)
        launch { rise.animateTo(48f, tween(650, easing = LinearOutSlowInEasing)) }
        opacity.animateTo(0f, tween(650, easing = LinearOutSlowInEasing))
    }

    Box(modifier.width(112.dp).zIndex(10f), contentAlignment = Alignment.Center) {
        Text(
            text = buildString {
                append("+${popup.amount} ${popup.material.label}")
                if (popup.multiplier > 1) append(" ×${popup.multiplier}")
            },
            color = Gold,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = (-rise.value + popupIndex * 13f).dp)
                .graphicsLayer { alpha = opacity.value }
                .background(Ink.copy(alpha = .88f), RoundedCornerShape(3.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp),
        )
    }
}


internal data class CollectionScreenActions(
    val restart: () -> Unit = {},
    val clearAnimation: (Long) -> Unit = {},
    val swapCells: (Int, Int, (Boolean) -> Unit) -> Unit = { _, _, resolved -> resolved(false) },
    val chooseCell: (Int) -> Unit = {},
)

@Preview(name = "Mina", showBackground = true)
@Composable
private fun CollectionScreenPreview() {
    WartishTheme {
        CollectionScreen(
            ui = GameUiState(collection = CollectionEngine.newGame()),
            actions = CollectionScreenActions(),
            onBack = {},
        )
    }
}
