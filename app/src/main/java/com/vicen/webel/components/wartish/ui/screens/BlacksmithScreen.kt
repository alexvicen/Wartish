package com.vicen.webel.components.wartish.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.data.Equipment
import com.vicen.webel.components.wartish.data.EquipmentUpgradeOutcome
import com.vicen.webel.components.wartish.data.PlayerEntity
import com.vicen.webel.components.wartish.data.equipmentLevel
import com.vicen.webel.components.wartish.data.withEquipmentLevel
import com.vicen.webel.components.wartish.game.GameBalance
import com.vicen.webel.components.wartish.game.GameUiState
import com.vicen.webel.components.wartish.ui.*
import com.vicen.webel.components.wartish.ui.components.*

@Composable
internal fun BlacksmithScreen(ui: GameUiState, actions: BlacksmithScreenActions, onBack: () -> Unit) {
    GamePage(
        title = "Herrería",
        subtitle = "Selecciona una pieza del maniquí para forjarla",
        player = ui.player,
        onBack = onBack,
        notice = ui.notice,
        backButtonAboveProfile = true,
        fillRemainingHeight = true,
        centerTitleAtTop = true,
        campBackpack = true,
        screenBackground = R.drawable.blacksmith_shop_fullscreen,
        screenBackgroundFilterQuality = androidx.compose.ui.graphics.FilterQuality.Medium,
        fullScreenOverlay = {
            BlacksmithMannequin(
                ui.player,
                onEquipmentClick = actions.selectEquipment,
                modifier = Modifier.fillMaxSize(),
            )
        },
    ) {}
    UpgradeDialog(ui, { actions.upgrade(it) }, actions.dismissUpgrade)
}

@Composable
private fun BlacksmithMannequin(
    player: PlayerEntity,
    onEquipmentClick: (Equipment) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier.fillMaxSize(),
    ) {
        MannequinEquipmentHotspot(
            Equipment.HELMET, player, onEquipmentClick,
            cropAlignedHitbox(maxWidth, maxHeight, centerX = .49f, centerY = .24f, width = .18f, height = .08f),
        )
        MannequinEquipmentHotspot(
            Equipment.ARROWS, player, onEquipmentClick,
            cropAlignedHitbox(maxWidth, maxHeight, centerX = .34f, centerY = .30f, width = .15f, height = .12f),
        )
        MannequinEquipmentHotspot(
            Equipment.BOW, player, onEquipmentClick,
            cropAlignedHitbox(maxWidth, maxHeight, centerX = .86f, centerY = .56f, width = .12f, height = .45f),
        )
        MannequinEquipmentHotspot(
            Equipment.SHIELD, player, onEquipmentClick,
            cropAlignedHitbox(maxWidth, maxHeight, centerX = .17f, centerY = .60f, width = .18f, height = .25f),
        )
        MannequinEquipmentHotspot(
            Equipment.GLOVES, player, onEquipmentClick,
            cropAlignedHitbox(maxWidth, maxHeight, centerX = .32f, centerY = .52f, width = .10f, height = .15f),
        )
        MannequinEquipmentHotspot(
            Equipment.GLOVES, player, onEquipmentClick,
            cropAlignedHitbox(maxWidth, maxHeight, centerX = .69f, centerY = .53f, width = .10f, height = .13f),
            showLevel = false,
        )
        MannequinEquipmentHotspot(
            Equipment.BOOTS, player, onEquipmentClick,
            cropAlignedHitbox(maxWidth, maxHeight, centerX = .50f, centerY = .75f, width = .38f, height = .18f),
        )
    }
}

private fun cropAlignedHitbox(
    maxWidth: Dp,
    maxHeight: Dp,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
): Modifier {
    val imageAspectRatio = 941f / 1672f
    val viewportAspectRatio = maxWidth.value / maxHeight.value
    val visibleImageWidth = if (viewportAspectRatio < imageAspectRatio) {
        viewportAspectRatio / imageAspectRatio
    } else {
        1f
    }
    val visibleImageHeight = if (viewportAspectRatio > imageAspectRatio) {
        imageAspectRatio / viewportAspectRatio
    } else {
        1f
    }
    val cropStartX = (1f - visibleImageWidth) / 2f
    val cropStartY = (1f - visibleImageHeight) / 2f
    val hitboxWidth = width / visibleImageWidth
    val hitboxHeight = height / visibleImageHeight
    val left = (centerX - width / 2f - cropStartX) / visibleImageWidth
    val top = (centerY - height / 2f - cropStartY) / visibleImageHeight

    return Modifier.offset(x = maxWidth * left, y = maxHeight * top)
        .size(width = maxWidth * hitboxWidth, height = maxHeight * hitboxHeight)
}

@Composable
private fun MannequinEquipmentHotspot(
    equipment: Equipment,
    player: PlayerEntity,
    onClick: (Equipment) -> Unit,
    modifier: Modifier = Modifier,
    showLevel: Boolean = true,
) {
    Box(
        modifier.semantics {
            contentDescription = "${equipment.label}, nivel ${player.equipmentLevel(equipment)}. Toca para mejorar."
        }.clickable { onClick(equipment) },
    ) {
        if (showLevel) {
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp),
                color = RusticWoodDark.copy(alpha = .92f),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(1.dp, RusticBrass),
            ) {
                Text(
                    "N${player.equipmentLevel(equipment)}",
                    color = RusticParchment,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                )
            }
        }
    }
}

private fun equipmentUpgradeEffects(player: PlayerEntity, equipment: Equipment): List<String> {
    val upgraded = player.withEquipmentLevel(equipment, player.equipmentLevel(equipment) + 1)
    return buildList {
        fun addIncrease(label: String, current: Int, next: Int, suffix: String = "") {
            if (next > current) add("$label +${next - current}$suffix")
        }

        addIncrease("Vida máx.", player.maxHealth, upgraded.maxHealth, " PV")
        addIncrease("Ataque", player.attack, upgraded.attack)
        addIncrease("Magia", player.magic, upgraded.magic)
        addIncrease("Defensa", player.defense, upgraded.defense)
        addIncrease("Velocidad", player.speed, upgraded.speed)
        addIncrease("Crítico", player.critical, upgraded.critical, "%")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpgradeDialog(ui: GameUiState, onUpgrade: (Equipment) -> Unit, onDismiss: () -> Unit) {
    val equipment = ui.equipmentDialog ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val level = ui.player.equipmentLevel(equipment)
    val next = level + 1
    val costs = buildList {
        add(equipment.primary to 2 * next)
        equipment.secondary.forEach { add(it to next) }
    }
    val effects = equipmentUpgradeEffects(ui.player, equipment)
    val canAfford = costs.all { (material, count) -> ui.player.amount(material) >= count }
    val failurePercent = (GameBalance.BLACKSMITH_FAILURE_PROBABILITY * 100).toInt()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current.density
    val hammerRotation = remember { Animatable(-34f) }
    val hammerTravel = remember { Animatable(0f) }
    val feedbackAlpha = remember { Animatable(0f) }
    val feedbackRise = remember { Animatable(0f) }
    var hammerFrame by remember { mutableStateOf(0) }
    var isForging by remember { mutableStateOf(false) }
    var lastFeedbackId by remember { mutableStateOf(0L) }

    LaunchedEffect(ui.upgradeFeedback?.id) {
        val feedback = ui.upgradeFeedback ?: return@LaunchedEffect
        if (feedback.id <= lastFeedbackId) return@LaunchedEffect
        lastFeedbackId = feedback.id
        isForging = false
        feedbackAlpha.snapTo(1f)
        feedbackRise.snapTo(0f)
        launch { feedbackAlpha.animateTo(0f, tween(950)) }
        launch { feedbackRise.animateTo(-70f * density, tween(950)) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentColor = RusticParchment,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Ink.copy(alpha = .72f),
        dragHandle = {
            Box(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier.width(48.dp).height(5.dp).clip(CircleShape).background(RusticBrass),
                )
            }
        },
    ) {
        Box(
            Modifier.fillMaxWidth().heightIn(max = 640.dp).clip(
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            ),
        ) {
            Image(
                bitmap = rememberImageBitmap(R.drawable.blacksmith_shop_fullscreen),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.Medium,
                modifier = Modifier.matchParentSize(),
            )
            Box(Modifier.matchParentSize().background(Ink.copy(alpha = .63f)))

            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("FORJA", color = RusticBrass, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                        Text(equipment.label.uppercase(), color = RusticParchment, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    }
                    Surface(
                        modifier = Modifier.size(44.dp)
                            .semantics { contentDescription = "Cerrar forja" }
                            .clickable(onClick = onDismiss),
                        color = RusticWoodDark.copy(alpha = .9f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, RusticBrass),
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("×", color = RusticParchment, fontSize = 30.sp, lineHeight = 30.sp)
                        }
                    }
                }

                Box(
                    Modifier.fillMaxWidth().height(218.dp).clip(RoundedCornerShape(8.dp)),
                ) {
                    Image(
                        bitmap = rememberImageBitmap(R.drawable.forja1),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.Medium,
                        modifier = Modifier.matchParentSize(),
                    )
                    Box(Modifier.matchParentSize().background(Ink.copy(alpha = .25f)))

                    Image(
                        bitmap = rememberImageBitmap(R.drawable.yunque),
                        contentDescription = "Yunque de la herrería",
                        contentScale = ContentScale.FillBounds,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                            .fillMaxWidth(.76f).aspectRatio(3.17f),
                    )

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-62).dp)
                            .semantics {
                                contentDescription = "${equipment.label}, nivel $level. Toca para intentar forjar."
                            }
                            .clickable(enabled = canAfford && !isForging) {
                                scope.launch {
                                    isForging = true
                                    feedbackAlpha.snapTo(0f)
                                    hammerRotation.snapTo(-34f)
                                    hammerTravel.snapTo(-8f * density)
                                    repeat(2) { strike ->
                                        hammerFrame = if (strike == 0) 1 else 2
                                        hammerRotation.animateTo(26f, tween(135))
                                        hammerTravel.animateTo(15f * density, tween(135))
                                        delay(65)
                                        hammerFrame = if (strike == 0) 2 else 1
                                        hammerRotation.animateTo(-34f, tween(150))
                                        hammerTravel.animateTo(-8f * density, tween(150))
                                    }
                                    hammerFrame = 0
                                    hammerRotation.animateTo(-22f, tween(100))
                                    hammerTravel.snapTo(0f)
                                    onUpgrade(equipment)
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Surface(
                            color = RusticWoodDark.copy(alpha = .96f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, RusticBrass),
                        ) {
                            Text(
                                "NIVEL $level",
                                color = RusticParchment,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            )
                        }
                        Image(
                            bitmap = rememberImageBitmap(equipmentSprite(equipment)),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            filterQuality = FilterQuality.None,
                            modifier = Modifier.padding(top = 4.dp).equipmentSpriteSize(equipment),
                        )
                    }

                    Image(
                        bitmap = rememberImageBitmap(
                            when (hammerFrame) {
                                1 -> R.drawable.martillo_pequeno1
                                2 -> R.drawable.martillo_pequeno2
                                else -> R.drawable.martillo_pequeno
                            },
                        ),
                        contentDescription = if (isForging) "Martillo golpeando la pieza" else null,
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 54.dp).offset(y = (-42).dp)
                            .size(width = 48.dp, height = 62.dp)
                            .graphicsLayer {
                                rotationZ = hammerRotation.value
                                translationY = hammerTravel.value
                                transformOrigin = TransformOrigin(.18f, .82f)
                            },
                    )

                    ui.upgradeFeedback?.let { feedback ->
                        Canvas(
                            Modifier.matchParentSize().graphicsLayer {
                                alpha = feedbackAlpha.value
                                translationY = feedbackRise.value
                            },
                        ) {
                            drawForgeFeedback(feedback.outcome, feedbackAlpha.value)
                        }
                    }
                }

                val feedbackText = when (ui.upgradeFeedback?.outcome) {
                    EquipmentUpgradeOutcome.SUCCESS -> "¡Mejora forjada!"
                    EquipmentUpgradeOutcome.FAILURE -> "La forja falló; los materiales se consumieron."
                    EquipmentUpgradeOutcome.INSUFFICIENT_MATERIALS -> "No tienes materiales suficientes."
                    null -> null
                }
                Text(
                    text = when {
                        isForging -> "El martillo golpea la pieza…"
                        feedbackText != null -> feedbackText
                        canAfford -> "Toca la pieza sobre el yunque para forjarla."
                        else -> "Te faltan materiales para esta mejora."
                    },
                    color = when (ui.upgradeFeedback?.outcome) {
                        EquipmentUpgradeOutcome.SUCCESS -> Mint
                        EquipmentUpgradeOutcome.FAILURE,
                        EquipmentUpgradeOutcome.INSUFFICIENT_MATERIALS -> Coral
                        null -> RusticParchment
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("MATERIALES NECESARIOS", color = RusticBrass, fontWeight = FontWeight.Black, fontSize = 11.sp)
                costs.forEach { (material, count) ->
                    val current = ui.player.amount(material)
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PixelMaterial(material.ordinal, Modifier.size(25.dp))
                        Text(material.label, modifier = Modifier.weight(1f), color = RusticParchment)
                        Text(
                            "$current / $count",
                            color = if (current >= count) Mint else Coral,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Text(
                    "Los materiales se consumen al golpear, aunque la forja falle ($failurePercent % de probabilidad).",
                    color = TextSoft,
                    fontSize = 11.sp,
                )
                Surface(
                    color = RusticWoodDark.copy(alpha = .86f),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(1.dp, RusticBrass.copy(alpha = .75f)),
                ) {
                    Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("EFECTO SI LA PIEZA SE FORJA", color = RusticParchment, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        Text(effects.joinToString(" · "), color = Mint, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun equipmentSprite(equipment: Equipment): Int = when (equipment) {
    Equipment.HELMET -> R.drawable.casco_pequeno
    Equipment.BOW -> R.drawable.arco_pequeno
    Equipment.SHIELD -> R.drawable.escudo_pequeno
    Equipment.GLOVES -> R.drawable.guantes_pequenos
    Equipment.BOOTS -> R.drawable.botas_pequenas
    Equipment.ARROWS -> R.drawable.flecha_pequena
}

@Composable
private fun rememberImageBitmap(resourceId: Int): ImageBitmap =
    ImageBitmap.imageResource(resourceId)

private fun Modifier.equipmentSpriteSize(equipment: Equipment): Modifier = when (equipment) {
    Equipment.HELMET -> size(width = 66.dp, height = 50.dp)
    Equipment.BOW -> size(width = 58.dp, height = 58.dp)
    Equipment.SHIELD -> size(width = 54.dp, height = 60.dp)
    Equipment.GLOVES -> size(width = 64.dp, height = 54.dp)
    Equipment.BOOTS -> size(width = 64.dp, height = 54.dp)
    Equipment.ARROWS -> size(width = 68.dp, height = 48.dp)
}

private fun DrawScope.drawForgeFeedback(outcome: EquipmentUpgradeOutcome, alpha: Float) {
    if (alpha <= 0f) return
    val center = Offset(size.width * .50f, size.height * .47f)
    if (outcome == EquipmentUpgradeOutcome.SUCCESS) {
        val gold = Gold.copy(alpha = alpha)
        val light = RusticParchment.copy(alpha = alpha)
        listOf(
            Triple(Offset(-52f, -12f), 13f, gold),
            Triple(Offset(-24f, -42f), 9f, light),
            Triple(Offset(20f, -34f), 14f, gold),
            Triple(Offset(50f, 2f), 9f, light),
            Triple(Offset(4f, 27f), 8f, gold),
        ).forEach { (offset, radius, color) -> drawStar(center + offset, radius, color) }
    } else {
        val spark = Coral.copy(alpha = alpha)
        val ember = RusticBrass.copy(alpha = alpha)
        val rays = listOf(
            Triple(Offset(-28f, -7f), Offset(-50f, -31f), spark),
            Triple(Offset(-13f, -21f), Offset(-18f, -48f), ember),
            Triple(Offset(10f, -16f), Offset(25f, -45f), spark),
            Triple(Offset(24f, 3f), Offset(50f, -10f), ember),
            Triple(Offset(4f, 18f), Offset(18f, 46f), spark),
            Triple(Offset(-20f, 16f), Offset(-42f, 37f), ember),
        )
        rays.forEach { (start, end, color) ->
            val midpoint = Offset((start.x + end.x) * .5f + 5f, (start.y + end.y) * .5f - 4f)
            drawLine(color, center + start, center + midpoint, strokeWidth = 3f, cap = StrokeCap.Square)
            drawLine(color, center + midpoint, center + end, strokeWidth = 2f, cap = StrokeCap.Square)
        }
    }
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    repeat(10) { index ->
        val angle = -PI / 2 + index * PI / 5
        val distance = if (index % 2 == 0) radius else radius * .43f
        val point = Offset(
            x = center.x + cos(angle).toFloat() * distance,
            y = center.y + sin(angle).toFloat() * distance,
        )
        if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
    path.close()
    drawPath(path, color)
}


internal data class BlacksmithScreenActions(
    val selectEquipment: (Equipment) -> Unit = {},
    val upgrade: (Equipment) -> Unit = {},
    val dismissUpgrade: () -> Unit = {},
)

@Preview(name = "Herrería", showBackground = true)
@Composable
private fun BlacksmithScreenPreview() {
    WartishTheme {
        BlacksmithScreen(ui = GameUiState(), actions = BlacksmithScreenActions(), onBack = {})
    }
}
