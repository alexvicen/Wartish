package com.vicen.webel.components.wartish.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.data.Material
import com.vicen.webel.components.wartish.data.PlayerEntity
import com.vicen.webel.components.wartish.ui.Gold
import com.vicen.webel.components.wartish.ui.Ink
import com.vicen.webel.components.wartish.ui.Panel
import com.vicen.webel.components.wartish.ui.PanelRaised
import com.vicen.webel.components.wartish.ui.TextMain
import com.vicen.webel.components.wartish.ui.TextSoft
import com.vicen.webel.components.wartish.ui.WartishTheme
import kotlin.math.roundToInt

@Composable
internal fun GamePage(
    title: String,
    subtitle: String,
    player: PlayerEntity,
    onBack: (() -> Unit)?,
    notice: String?,
    backpackAtStart: Boolean = false,
    backButtonAboveProfile: Boolean = false,
    fillRemainingHeight: Boolean = false,
    centerTitleAtTop: Boolean = false,
    showProfile: Boolean = true,
    campBackpack: Boolean = false,
    showBackpack: Boolean = true,
    screenBackground: Int? = null,
    screenBackgroundFilterQuality: FilterQuality = FilterQuality.None,
    fullScreenOverlay: (@Composable BoxScope.() -> Unit)? = null,
    centerHeaderStatus: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    var showStats by remember { mutableStateOf(false) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val landscape = maxWidth >= maxHeight
        val profileWidth =
            if (landscape) minOf(220.dp, maxWidth * .31f) else minOf(270.dp, maxWidth - 24.dp)
        val backpackWidth = when {
            backpackAtStart && landscape -> minOf(208.dp, maxWidth * .30f)
            landscape -> minOf(260.dp, maxWidth * .36f)
            else -> minOf(320.dp, maxWidth - 24.dp)
        }
        val contentStart = if (landscape) profileWidth + 16.dp else 12.dp
        val contentEnd = when {
            !showBackpack -> 12.dp
            backpackAtStart -> 12.dp
            landscape -> backpackWidth + 16.dp
            else -> 12.dp
        }
        val contentTop = when {
            fillRemainingHeight && backButtonAboveProfile && showProfile && !landscape -> 140.dp
            fillRemainingHeight && centerTitleAtTop -> 58.dp
            fillRemainingHeight && backButtonAboveProfile && !landscape -> 140.dp
            fillRemainingHeight -> 8.dp
            landscape -> 8.dp
            else -> 102.dp
        }
        val contentBottom = when {
            fillRemainingHeight && (backpackAtStart || campBackpack) && !landscape -> 162.dp
            fillRemainingHeight -> 8.dp
            landscape -> 8.dp
            else -> 154.dp
        }

        if (screenBackground == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Ink)
            )
        } else {
            Image(
                bitmap = ImageBitmap.imageResource(screenBackground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                filterQuality = screenBackgroundFilterQuality,
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Ink.copy(alpha = .30f))
            )
        }

        val pageModifier = Modifier
            .fillMaxSize()
            .padding(
                start = contentStart,
                end = contentEnd,
                top = contentTop,
                bottom = contentBottom
            )
            .then(if (fillRemainingHeight) Modifier else Modifier.verticalScroll(rememberScrollState()))
            .padding(horizontal = 8.dp, vertical = 6.dp)
        Column(
            pageModifier,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (!centerTitleAtTop) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onBack != null && !backButtonAboveProfile) OutlinedButton(
                        onClick = onBack,
                        contentPadding = ButtonDefaults.ContentPadding
                    ) { Text("‹", fontSize = 24.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(
                            title.uppercase(),
                            color = Gold,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 1.2.sp
                        )
                        Text(subtitle, color = TextSoft, fontSize = 12.sp, maxLines = 2)
                    }
                }
            }
            content()
        }

        fullScreenOverlay?.invoke(this)

        if (centerTitleAtTop) {
            Text(
                title.uppercase(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 14.dp)
                    .zIndex(9f),
                color = Gold,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                letterSpacing = .5.sp,
                maxLines = 1,
            )
            if (!centerHeaderStatus.isNullOrBlank()) {
                Text(
                    centerHeaderStatus,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 35.dp)
                        .zIndex(9f),
                    color = TextSoft,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }
        }
        if (showProfile) {
            PlayerProfileWidget(
                player = player,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = if (backButtonAboveProfile) 58.dp else 8.dp)
                    .width(profileWidth)
                    .zIndex(8f),
                onClick = { showStats = true },
            )
        }
        if (backButtonAboveProfile && onBack != null) {
            RusticBackButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 6.dp)
                    .zIndex(9f),
            )
        }
        if (showBackpack) {
            if (campBackpack) {
                CampBackpack(
                    player,
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .zIndex(8f),
                )
            } else {
                BackpackCorner(
                    player,
                    Modifier
                        .align(if (backpackAtStart) Alignment.BottomStart else Alignment.BottomEnd)
                        .padding(8.dp)
                        .width(backpackWidth)
                        .zIndex(8f),
                )
            }
        }
        AnimatedVisibility(
            visible = !notice.isNullOrBlank(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = if (centerTitleAtTop) 56.dp else 8.dp)
                .zIndex(10f),
            enter = fadeIn(tween(160)) + slideInVertically(tween(180)) { -it / 2 },
            exit = fadeOut(tween(180)),
        ) {
            Surface(
                color = PanelRaised,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Gold)
            ) {
                Text(
                    notice.orEmpty(),
                    color = Gold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        if (showStats) ProfileStatisticsDialog(player) { showStats = false }
    }
}

@Composable
internal fun PlayerProfileWidget(
    player: PlayerEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = RusticWoodDark.copy(alpha = .96f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, RusticBrass),
    ) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .size(width = 52.dp, height = 62.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(RusticLeather)
                    .border(2.dp, RusticBrass, RoundedCornerShape(4.dp))
                    .padding(3.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(R.drawable.chicoentra1),
                    contentDescription = "Retrato de ${player.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
                )
            }
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        player.name.uppercase(),
                        modifier = Modifier.weight(1f),
                        color = RusticParchment,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                    Surface(color = RusticBrass, shape = RoundedCornerShape(3.dp)) {
                        Text(
                            "NV ${player.level}",
                            color = RusticWoodDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 5.dp),
                            maxLines = 1,
                        )
                    }
                }
                ProfileProgressRow(
                    label = "EXP",
                    value = player.experience,
                    maximum = 200 * player.level,
                    color = RusticBrass,
                    valueText = "${player.experience}/${200 * player.level}",
                )
                ProfileProgressRow(
                    label = "VIDA",
                    value = player.currentHealth.coerceAtMost(player.maxHealth),
                    maximum = player.maxHealth,
                    color = Color(0xFF83B66B),
                    valueText = "${player.currentHealth.coerceAtMost(player.maxHealth)}/${player.maxHealth}",
                )
            }
        }
    }
}

@Composable
private fun ProfileProgressRow(
    label: String,
    value: Int,
    maximum: Int,
    color: Color,
    valueText: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            modifier = Modifier.width(25.dp),
            color = RusticParchment,
            fontWeight = FontWeight.Black,
            fontSize = 8.sp
        )
        LinearProgressIndicator(
            progress = { (value.toFloat() / maximum.coerceAtLeast(1)).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = color,
            trackColor = RusticWood,
        )
        Text(valueText, color = RusticParchment, fontSize = 8.sp, maxLines = 1)
    }
}

@Composable
private fun BackpackCorner(player: PlayerEntity, modifier: Modifier = Modifier) {
    val raw = Material.entries.take(5)
    val processed = raw.mapNotNull { it.refined }.distinct()
    Surface(
        modifier = modifier,
        color = Ink.copy(alpha = .94f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(2.dp, PanelRaised),
    ) {
        Row(
            Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            BackpackColumn("SIN PROCESAR", raw, player, Modifier.weight(1f))
            Box(
                Modifier
                    .width(1.dp)
                    .height(112.dp)
                    .background(PanelRaised)
            )
            BackpackColumn("PROCESADOS", processed, player, Modifier.weight(1f))
        }
    }
}

@Composable
internal fun CampBackpack(player: PlayerEntity, modifier: Modifier = Modifier) {
    val raw = Material.entries.take(5)
    val processed = raw.mapNotNull { it.refined }
    Box(
        modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .aspectRatio(2.55f)
    ) {
        Image(
            bitmap = ImageBitmap.imageResource(R.drawable.mochila_campamento),
            contentDescription = "Mochila de cuero con correas, bolsillos y hebillas de latón",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(.55f)
                .padding(top = 32.dp),
        ) {
            CampBackpackRow(upperText = true, raw, player)
            Spacer(modifier = Modifier.height(8.dp))

            CampBackpackRow(upperText = false, processed, player)
        }
    }
}

@Composable
private fun CampBackpackRow(upperText: Boolean, materials: List<Material>, player: PlayerEntity) {
    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
            materials.forEach { material ->
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (upperText) {
                        CampBackpackRowText(player.amount(material).toString())
                    }

                    if (upperText) {
                        PixelMaterial(material.ordinal, Modifier.size(32.dp))
                    } else {
                        CampProcessedMaterialIcon(material, Modifier.size(32.dp))
                    }
                    if (!upperText) {
                        CampBackpackRowText(player.amount(material).toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun CampProcessedMaterialIcon(material: Material, modifier: Modifier = Modifier) {
    val iconIndex = when (material) {
        Material.STONE -> 0
        Material.BOARDS -> 1
        Material.IRON_INGOT -> 2
        Material.GOLD_INGOT -> 3
        Material.CUT_GEM -> 4
        else -> return PixelMaterial(material.ordinal, modifier)
    }
    val spriteSheet = ImageBitmap.imageResource(R.drawable.materiales_procesados_mochila)
    Canvas(modifier.semantics { contentDescription = material.label }) {
        val cellWidth = (spriteSheet.width * .203f).roundToInt()
        val centerX = (spriteSheet.width * (.1f + iconIndex * .2f)).roundToInt()
        val sourceLeft = (centerX - cellWidth / 2).coerceIn(0, spriteSheet.width - cellWidth)
        val sourceTop = (spriteSheet.height * .215f).roundToInt()
        val sourceHeight = (spriteSheet.height * .57f).roundToInt()
        drawImage(
            image = spriteSheet,
            srcOffset = IntOffset(sourceLeft, sourceTop),
            srcSize = IntSize(cellWidth, sourceHeight),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
            filterQuality = FilterQuality.None,
        )
    }
}

@Composable
private fun CampBackpackRowText(materialCount: String) {
    Text(
        text = materialCount,
        color = RusticParchment,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        maxLines = 1
    )
}

@Composable
private fun BackpackColumn(
    title: String,
    materials: List<Material>,
    player: PlayerEntity,
    modifier: Modifier = Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = Gold, fontWeight = FontWeight.Black, fontSize = 8.sp, maxLines = 1)
        materials.forEach { material ->
            val shortName = when (material) {
                Material.IRON_INGOT -> "Ling. hierro"
                Material.GOLD_INGOT -> "Ling. oro"
                Material.CUT_GEM -> "Gema tallada"
                else -> material.label
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                PixelMaterial(material.ordinal, Modifier.size(17.dp))
                Text(
                    shortName,
                    modifier = Modifier.weight(1f),
                    color = TextMain,
                    fontSize = 8.sp,
                    maxLines = 1
                )
                Text(
                    "×${player.amount(material)}",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
internal fun ProfileStatisticsDialog(player: PlayerEntity, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 420.dp)
                    .heightIn(max = (maxHeight - 32.dp).coerceAtLeast(1.dp)),
                color = RusticWoodDark,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(3.dp, RusticBrass),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            Modifier
                                .size(width = 58.dp, height = 70.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(RusticLeather)
                                .border(2.dp, RusticBrass, RoundedCornerShape(5.dp))
                                .padding(4.dp),
                        ) {
                            Image(
                                bitmap = ImageBitmap.imageResource(R.drawable.chicoentra1),
                                contentDescription = "Retrato de ${player.name}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                                filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
                            )
                        }
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                "HOJA DEL VIAJERO",
                                color = RusticBrass,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                player.name.uppercase(),
                                color = RusticParchment,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                            Text(
                                "NIVEL ${player.level}",
                                color = RusticParchment,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(RusticBrass.copy(alpha = .65f))
                    )
                    ProfileMeter(
                        "VIDA",
                        player.currentHealth.coerceAtMost(player.maxHealth),
                        player.maxHealth,
                        Color(0xFF83B66B),
                    )
                    ProfileMeter("EXPERIENCIA", player.experience, 200 * player.level, RusticBrass)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileStatTile("ATAQUE", player.attack.toString(), Modifier.weight(1f))
                        ProfileStatTile("MAGIA", player.magic.toString(), Modifier.weight(1f))
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileStatTile("DEFENSA", player.defense.toString(), Modifier.weight(1f))
                        ProfileStatTile("VELOCIDAD", player.speed.toString(), Modifier.weight(1f))
                    }
                    ProfileStatTile("GOLPE CRÍTICO", "${player.critical}%", Modifier.fillMaxWidth())
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 46.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RusticBrass,
                            contentColor = RusticWoodDark
                        ),
                    ) { Text("CERRAR", fontWeight = FontWeight.Black, letterSpacing = .7.sp) }
                }
            }
        }
    }
}

@Composable
private fun ProfileMeter(label: String, value: Int, maximum: Int, color: Color) {
    Surface(
        color = RusticWood,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, RusticBrass.copy(alpha = .7f)),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(9.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, color = RusticParchment, fontWeight = FontWeight.Black, fontSize = 9.sp)
                Text(
                    "$value / $maximum",
                    color = RusticParchment,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
            LinearProgressIndicator(
                progress = { (value.toFloat() / maximum.coerceAtLeast(1)).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = RusticWoodDark,
            )
        }
    }
}

@Composable
private fun ProfileStatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = RusticWood,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, RusticBrass.copy(alpha = .7f)),
    ) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(label, color = RusticParchment, fontWeight = FontWeight.Black, fontSize = 8.sp)
            Text(value, color = RusticBrass, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}

@Composable
internal fun PixelPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Panel,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(2.dp, PanelRaised),
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Preview(name = "Marco de pantalla", showBackground = true)
@Composable
private fun GamePagePreview() {
    WartishTheme {
        GamePage(
            title = "Aventura",
            subtitle = "Explora el reino",
            player = PlayerEntity(rock = 4, wood = 8, boards = 2),
            onBack = {},
            notice = null,
        ) {
            PixelPanel {
                Text("Contenido de ejemplo", color = TextMain)
            }
        }
    }
}

@Preview(name = "Perfil del personaje", showBackground = true)
@Composable
private fun PlayerProfileWidgetPreview() {
    WartishTheme {
        PlayerProfileWidget(PlayerEntity(), onClick = {})
    }
}

@Preview(name = "Barra de progreso del perfil", showBackground = true)
@Composable
private fun ProfileProgressRowPreview() {
    WartishTheme {
        ProfileProgressRow("EXP", 125, 200, RusticBrass, "125/200")
    }
}

@Preview(name = "Mochila", showBackground = true)
@Composable
private fun BackpackCornerPreview() {
    WartishTheme {
        BackpackCorner(PlayerEntity(rock = 12, wood = 8, boards = 3, ironIngots = 2))
    }
}

@Preview(name = "Mochila de campamento", showBackground = true)
@Composable
private fun CampBackpackPreview() {
    WartishTheme {
        CampBackpack(PlayerEntity(rock = 12, wood = 8, boards = 3, ironIngots = 2))
    }
}

@Preview(name = "Fila de mochila", showBackground = true)
@Composable
private fun CampBackpackRowPreview() {
    WartishTheme {
        CampBackpackRow(
            upperText = true,
            Material.entries.take(5),
            PlayerEntity(rock = 12, wood = 8, iron = 4)
        )
    }
}

@Preview(name = "Fila de mochila procesada", showBackground = true)
@Composable
private fun CampBackpackProcessedRowPreview() {
    WartishTheme {
        CampBackpackRow(
            upperText = false,
            Material.entries.mapNotNull { it.refined },
            PlayerEntity(stone = 7, boards = 5, ironIngots = 3, goldIngots = 2, cutGems = 1),
        )
    }
}

@Preview(name = "Columna de mochila", showBackground = true)
@Composable
private fun BackpackColumnPreview() {
    WartishTheme {
        BackpackColumn(
            "PROCESADOS",
            Material.entries.mapNotNull { it.refined }.distinct(),
            PlayerEntity(boards = 3, stone = 4, ironIngots = 2),
        )
    }
}

@Preview(name = "Hoja de estadísticas", showBackground = true)
@Composable
private fun ProfileStatisticsDialogPreview() {
    WartishTheme {
        ProfileStatisticsDialog(PlayerEntity(), onDismiss = {})
    }
}

@Preview(name = "Medidor de estadísticas", showBackground = true)
@Composable
private fun ProfileMeterPreview() {
    WartishTheme {
        ProfileMeter("VIDA", 54, 70, Color(0xFF83B66B))
    }
}

@Preview(name = "Valor de estadística", showBackground = true)
@Composable
private fun ProfileStatTilePreview() {
    WartishTheme {
        ProfileStatTile("ATAQUE", "12")
    }
}

@Preview(name = "Panel pixel art", showBackground = true)
@Composable
private fun PixelPanelPreview() {
    WartishTheme {
        PixelPanel {
            Text("Panel de ejemplo", color = TextMain)
            Text("Texto secundario", color = TextSoft)
        }
    }
}
