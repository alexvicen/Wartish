package com.vicen.webel.components.wartish.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.data.Equipment
import com.vicen.webel.components.wartish.data.Material
import com.vicen.webel.components.wartish.data.PlayerEntity
import com.vicen.webel.components.wartish.data.equipmentLevel
import com.vicen.webel.components.wartish.game.BattleState
import com.vicen.webel.components.wartish.game.CollectionState
import com.vicen.webel.components.wartish.game.FallingState
import com.vicen.webel.components.wartish.game.GameUiState
import com.vicen.webel.components.wartish.game.GameViewModel
import kotlinx.coroutines.delay

private object Route {
    const val HOME = "home"
    const val COLLECT = "collect"
    const val PROCESS = "process"
    const val BATTLE = "battle"
    const val SMITH = "smith"
}

@Composable
fun WartishApp(viewModel: GameViewModel) {
    val nav = rememberNavController()
    val ui by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(ui.notice) {
        if (ui.notice != null) {
            delay(3000)
            viewModel.clearNotice()
        }
    }
    WartishTheme {
        Scaffold(containerColor = Ink) { insets ->
            NavHost(navController = nav, startDestination = Route.HOME, modifier = Modifier.padding(insets)) {
                composable(Route.HOME) {
                    HomeScreen(
                        ui,
                        onCollect = { viewModel.openCollection(); nav.navigate(Route.COLLECT) },
                        onProcess = { viewModel.openProcessing(); nav.navigate(Route.PROCESS) },
                        onBattle = { viewModel.openBattle(); nav.navigate(Route.BATTLE) },
                        onSmith = { nav.navigate(Route.SMITH) },
                    )
                }
                composable(Route.COLLECT) {
                    CollectionScreen(ui, viewModel, onBack = { nav.popBackStack() })
                }
                composable(Route.PROCESS) {
                    ProcessingScreen(ui, viewModel, onBack = { nav.popBackStack() })
                }
                composable(Route.BATTLE) {
                    BattleScreen(ui, viewModel, onBack = { nav.popBackStack() })
                }
                composable(Route.SMITH) {
                    BlacksmithScreen(ui, viewModel, onBack = { nav.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    ui: GameUiState,
    onCollect: () -> Unit,
    onProcess: () -> Unit,
    onBattle: () -> Unit,
    onSmith: () -> Unit,
) {
    GamePage("WARTISH", "Crónicas del campamento", ui.player, null, ui.notice) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Image(
                bitmap = ImageBitmap.imageResource(R.drawable.chicoentra1),
                contentDescription = "Héroe",
                contentScale = ContentScale.Fit,
                filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
                modifier = Modifier.size(96.dp),
            )
            Column(Modifier.weight(1f)) {
                Text("${ui.player.name} · Nivel ${ui.player.level}", color = Gold, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Explora, reúne recursos y mejora tu equipo.", color = TextSoft)
                Text("❤️ ${ui.player.currentHealth.coerceAtMost(ui.player.maxHealth)} / ${ui.player.maxHealth}   ⚔ ${ui.player.attack}   🛡 ${ui.player.defense}", color = TextMain)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("EL CAMPAMENTO", color = Gold, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        HomeAction("💎", "Recolección", "Combina materiales · 20 movimientos", onCollect)
        HomeAction("🧱", "Procesamiento", "Junta grupos de tres o más", onProcess)
        HomeAction("⚔️", "Combate", "Tres enemigos · ataques automáticos", onBattle)
        HomeAction("⚒️", "Herrería", "Invierte refinados para mejorar equipo", onSmith)
    }
}

@Composable
private fun HomeAction(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(2.dp, PanelRaised),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(icon, fontSize = 30.sp)
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = TextMain, fontSize = 18.sp)
                Text(subtitle, color = TextSoft, fontSize = 13.sp)
            }
            Text("›", color = Gold, fontSize = 28.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun GamePage(
    title: String,
    subtitle: String,
    player: PlayerEntity,
    onBack: (() -> Unit)?,
    notice: String?,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier.fillMaxSize().background(Ink).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (onBack != null) OutlinedButton(onClick = onBack, contentPadding = ButtonDefaults.ContentPadding) { Text("‹", fontSize = 24.sp) }
            Column(Modifier.weight(1f)) {
                Text(title.uppercase(), color = Gold, fontWeight = FontWeight.Black, fontSize = 22.sp, letterSpacing = 1.5.sp)
                Text(subtitle, color = TextSoft, fontSize = 13.sp)
            }
            Text("NV ${player.level}", color = Gold, fontWeight = FontWeight.Bold)
        }
        PlayerPanel(player)
        if (!notice.isNullOrBlank()) {
            Surface(color = PanelRaised, shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Gold)) {
                Text(notice, color = Gold, modifier = Modifier.fillMaxWidth().padding(10.dp), textAlign = TextAlign.Center)
            }
        }
        content()
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PlayerPanel(player: PlayerEntity) {
    PixelPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${player.name}  ·  Nivel ${player.level}", color = TextMain, fontWeight = FontWeight.Bold)
                Text("EXP ${player.experience} / ${200 * player.level}", color = TextSoft, fontSize = 12.sp)
            }
            Text("❤️ ${player.currentHealth.coerceAtMost(player.maxHealth)}/${player.maxHealth}", color = Coral, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { (player.experience.toFloat() / (200 * player.level)).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = Gold,
            trackColor = PanelRaised,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            items(Material.entries.size) { index ->
                val material = Material.entries[index]
                InventoryChip(material, player.amount(material))
            }
        }
    }
}

@Composable
private fun InventoryChip(material: Material, amount: Int) {
    Surface(color = PanelRaised, shape = RoundedCornerShape(3.dp)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(material.symbol, fontSize = 13.sp)
            Text("$amount", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextMain)
        }
    }
}

@Composable
private fun PixelPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Panel,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(2.dp, PanelRaised),
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun CollectionScreen(ui: GameUiState, vm: GameViewModel, onBack: () -> Unit) {
    val game = ui.collection
    GamePage("Recolección", "Intercambia fichas vecinas y forma líneas de tres", ui.player, onBack, ui.notice) {
        if (game == null) Text("Preparando tablero…", color = TextSoft)
        else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("MOVIMIENTOS", color = TextSoft, fontWeight = FontWeight.Bold)
                Text("${game.movesLeft} / 20", color = Gold, fontSize = 21.sp, fontWeight = FontWeight.Black)
            }
            BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val boardWidth = minOf(maxWidth, 420.dp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier.width(boardWidth).aspectRatio(1f).border(3.dp, Gold, RoundedCornerShape(4.dp)).padding(3.dp),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    itemsIndexed(game.board) { index, item ->
                        val selected = index == game.selected
                        Box(
                            Modifier.aspectRatio(1f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (selected) Gold.copy(alpha = .35f) else PanelRaised)
                                .then(if (selected) Modifier.border(2.dp, Gold, RoundedCornerShape(3.dp)) else Modifier)
                                .clickable(enabled = !game.complete) { vm.chooseCell(index) },
                            contentAlignment = Alignment.Center,
                        ) {
                            PixelMaterial(item, Modifier.fillMaxSize().padding(3.dp))
                        }
                    }
                }
            }
            PixelPanel {
                Text("Combina horizontal o verticalmente. Los intercambios sin combinación no gastan turno.", color = TextSoft)
                if (game.complete) {
                    Text("¡Turnos completados! Los materiales ya están en tu inventario.", color = Mint, fontWeight = FontWeight.Bold)
                    GoldButton("JUGAR OTRA PARTIDA", onClick = { vm.restartCollection() })
                }
            }
        }
    }
}

@Composable
private fun ProcessingScreen(ui: GameUiState, vm: GameViewModel, onBack: () -> Unit) {
    val game = ui.falling
    var confirmAbandon by remember { mutableStateOf(false) }
    DisposableEffect(Unit) { onDispose { vm.pauseProcessing() } }
    LaunchedEffect(game?.running) {
        while (game?.running == true) {
            delay(1000)
            vm.tickFalling()
        }
    }
    GamePage("Procesamiento", "Alinea grupos conectados para refinar", ui.player, onBack, ui.notice) {
        if (game == null) Text("Preparando tablero…", color = TextSoft)
        else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TABLERO 6 × 12", color = TextSoft, fontWeight = FontWeight.Bold)
                if (game.material >= 0) Text("PIEZA ${Material.entries[game.material].symbol}", color = Gold, fontWeight = FontWeight.Bold)
            }
            BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val boardWidth = minOf(maxWidth, 246.dp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.width(boardWidth).aspectRatio(.5f).border(3.dp, Gold, RoundedCornerShape(4.dp)).padding(3.dp),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    itemsIndexed(game.cells) { index, cell ->
                        val row = index / 6
                        val column = index % 6
                        val current = row == game.row && column == game.column && game.material >= 0
                        Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(2.dp)).background(PanelRaised), contentAlignment = Alignment.Center) {
                            when {
                                current -> PixelMaterial(game.material, Modifier.fillMaxSize().padding(2.dp))
                                cell >= 0 -> PixelMaterial(cell, Modifier.fillMaxSize().padding(2.dp))
                            }
                        }
                    }
                }
            }
            PixelPanel {
                when {
                    game.complete -> {
                        Text("PROCESAMIENTO FINALIZADO", color = Gold, fontWeight = FontWeight.Black)
                        Text("Los refinados se guardaron. Las piezas que quedaron en el tablero se perdieron.", color = TextSoft)
                        GoldButton("OTRA PARTIDA", onClick = { vm.restartProcessing() })
                    }
                    !game.started -> {
                        Text("Cada pieza consume una materia prima. Junta 3 o más piezas conectadas del mismo tipo.", color = TextSoft)
                        GoldButton("INICIAR", onClick = { vm.startOrResumeProcessing() })
                    }
                    !game.running -> {
                        Text("Partida pausada. Las piezas sin transformar siguen en juego.", color = TextSoft)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GoldButton("CONTINUAR", Modifier.weight(1f), { vm.startOrResumeProcessing() })
                            OutlinedButton(onClick = { confirmAbandon = true }, modifier = Modifier.weight(1f)) { Text("ABANDONAR") }
                        }
                    }
                    else -> {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ControlButton("◀", Modifier.weight(1f)) { vm.moveFalling(-1) }
                            ControlButton("▼", Modifier.weight(1f)) { vm.tickFalling() }
                            ControlButton("▶", Modifier.weight(1f)) { vm.moveFalling(1) }
                            GoldButton("CAER", Modifier.weight(1f), { vm.dropFalling() })
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { vm.pauseProcessing() }, modifier = Modifier.weight(1f)) { Text("PAUSAR") }
                            OutlinedButton(onClick = { confirmAbandon = true }, modifier = Modifier.weight(1f)) { Text("ABANDONAR") }
                        }
                    }
                }
            }
        }
    }
    if (confirmAbandon) {
        AlertDialog(
            onDismissRequest = { confirmAbandon = false },
            title = { Text("¿Terminar la partida?") },
            text = { Text("Conservarás los refinados. Las piezas que siguen en el tablero se perderán.") },
            confirmButton = { TextButton(onClick = { confirmAbandon = false; vm.abandonProcessing() }) { Text("TERMINAR", color = Coral) } },
            dismissButton = { TextButton(onClick = { confirmAbandon = false }) { Text("SEGUIR JUGANDO") } },
            containerColor = Panel,
        )
    }
}

@Composable
private fun BattleScreen(ui: GameUiState, vm: GameViewModel, onBack: () -> Unit) {
    val battle = ui.battle
    DisposableEffect(Unit) { onDispose { vm.pauseBattle() } }
    GamePage("Combate", "El ritmo de ataque depende de la velocidad", ui.player, onBack, ui.notice) {
        when {
            battle == null -> {
                PixelPanel {
                    Text("Tu equipo: ataque ${ui.player.attack} · magia ${ui.player.magic} · defensa ${ui.player.defense} · velocidad ${ui.player.speed}", color = TextSoft)
                    Text("Enfrenta tres enemigos. Los golpes y sus respuestas ocurren automáticamente.", color = TextMain)
                    GoldButton("INICIAR ENCUENTRO", onClick = { vm.startBattle() })
                }
            }
            battle.complete -> BattleResult(battle, onAgain = { vm.startBattle() }, onBack = onBack)
            else -> ActiveBattle(ui.player, battle, vm)
        }
    }
}

@Composable
private fun ActiveBattle(player: PlayerEntity, battle: BattleState, vm: GameViewModel) {
    val enemy = battle.enemies[battle.enemyIndex]
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(bitmap = ImageBitmap.imageResource(R.drawable.chicoataque1), contentDescription = "Héroe", modifier = Modifier.size(96.dp), contentScale = ContentScale.Fit, filterQuality = androidx.compose.ui.graphics.FilterQuality.None)
            Text(player.name, color = Gold, fontWeight = FontWeight.Bold)
            HealthBar("${battle.playerHealth} / ${player.maxHealth}", battle.playerHealth, player.maxHealth, Mint)
        }
        Text("VS", color = Gold, fontWeight = FontWeight.Black, fontSize = 22.sp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(bitmap = ImageBitmap.imageResource(R.drawable.esqueletoentrada1), contentDescription = "Esqueleto", modifier = Modifier.size(96.dp), contentScale = ContentScale.Fit, filterQuality = androidx.compose.ui.graphics.FilterQuality.None)
            Text("Esqueleto ${battle.enemyIndex + 1}/3 · Nv ${enemy.level}", color = Coral, fontWeight = FontWeight.Bold)
            HealthBar("${enemy.health} / ${enemy.maxHealth}", enemy.health, enemy.maxHealth, Coral)
        }
    }
    PixelPanel {
        Text(if (battle.paused) "COMBATE EN PAUSA" else "¡ENCUENTRO EN CURSO!", color = Gold, fontWeight = FontWeight.Black)
        Text("Los ataques se repiten según velocidad. Tu crítico es ${player.critical}%.", color = TextSoft)
        val playerProgress = 1f - battle.playerRemainingMs.toFloat() / maxOf(1L, (3000.0 / (1.0 + player.speed / 10.0)).toLong())
        LinearProgressIndicator(progress = { playerProgress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(7.dp), color = Mint, trackColor = PanelRaised)
        Text("Próximo golpe · ${battle.playerRemainingMs.coerceAtLeast(0) / 1000.0}s", color = TextSoft, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Especial", color = TextMain)
            repeat(3) { index ->
                Box(Modifier.size(23.dp).background(if (index < battle.specialCharge) Gold else PanelRaised, RoundedCornerShape(3.dp)))
            }
            Spacer(Modifier.weight(1f))
            GoldButton("USAR ✦", enabled = battle.specialCharge >= 3 && !battle.paused, onClick = { vm.useSpecial() })
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (battle.paused) GoldButton("CONTINUAR", Modifier.weight(1f), vm::resumeBattle)
            else OutlinedButton(onClick = { vm.pauseBattle() }, modifier = Modifier.weight(1f)) { Text("PAUSAR") }
        }
    }
    Text("Objetivo actual", color = Gold, fontWeight = FontWeight.Bold)
    Card(
        onClick = { vm.selectEnemy(battle.enemyIndex) },
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(2.dp, Gold),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("☠ Esqueleto ${battle.enemyIndex + 1}", color = Coral, fontWeight = FontWeight.Bold)
            Text("SIGUIENTE ENTRARÁ AL DERROTARLO", color = TextSoft, fontSize = 11.sp)
        }
    }
}

@Composable
private fun HealthBar(label: String, value: Int, max: Int, color: Color) {
    Column(Modifier.width(138.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextMain, fontSize = 12.sp)
        LinearProgressIndicator(progress = { (value.toFloat() / max.coerceAtLeast(1)).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp), color = color, trackColor = PanelRaised)
    }
}

@Composable
private fun BattleResult(battle: BattleState, onAgain: () -> Unit, onBack: () -> Unit) {
    PixelPanel {
        Text(if (battle.won) "¡VICTORIA!" else "DERROTA", color = if (battle.won) Mint else Coral, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Text("Experiencia obtenida: ${battle.experienceEarned}", color = Gold, fontWeight = FontWeight.Bold)
        Text(if (battle.won) "Los tres esqueletos han caído." else "La experiencia de los enemigos derrotados ya se guardó.", color = TextSoft)
        GoldButton(if (battle.won) "NUEVO ENCUENTRO" else "VOLVER A INTENTAR", onClick = onAgain)
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("VOLVER AL CAMPAMENTO") }
    }
}

@Composable
private fun BlacksmithScreen(ui: GameUiState, vm: GameViewModel, onBack: () -> Unit) {
    GamePage("Herrería", "El próximo nivel cuesta más materiales", ui.player, onBack, ui.notice) {
        PixelPanel {
            Text("INVENTARIO REFINADO", color = Gold, fontWeight = FontWeight.Black)
            Material.entries.filter { it.refined == null }.forEach { InventoryLine(it, ui.player.amount(it)) }
        }
        Text("EQUIPO", color = Gold, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Equipment.entries.forEach { item ->
            val level = ui.player.equipmentLevel(item)
            val next = level + 1
            val costs = buildList {
                add(item.primary to 2 * next)
                item.secondary.forEach { add(it to next) }
            }
            Card(
                onClick = { vm.showUpgrade(item) },
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = BorderStroke(2.dp, PanelRaised),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(item.icon, fontSize = 30.sp)
                    Column(Modifier.weight(1f)) {
                        Text("${item.label} · Nivel $level", fontWeight = FontWeight.Bold, color = TextMain)
                        Text("Siguiente: ${costs.joinToString(" · ") { "${it.second} ${it.first.label.lowercase()}" }}", color = TextSoft, fontSize = 12.sp)
                    }
                    Text("MEJORAR", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
    UpgradeDialog(ui, { vm.upgrade(it) }, vm::dismissUpgrade)
}

@Composable
private fun InventoryLine(material: Material, amount: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("${material.symbol}  ${material.label}", color = TextMain)
        Text(amount.toString(), color = Gold, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UpgradeDialog(ui: GameUiState, onUpgrade: (Equipment) -> Unit, onDismiss: () -> Unit) {
    val equipment = ui.equipmentDialog ?: return
    val level = ui.player.equipmentLevel(equipment)
    val next = level + 1
    val costs = buildList {
        add(equipment.primary to 2 * next)
        equipment.secondary.forEach { add(it to next) }
    }
    val canAfford = costs.all { (material, count) -> ui.player.amount(material) >= count }
    Dialog(onDismissRequest = onDismiss) {
        PixelPanel {
            Text("${equipment.icon}  MEJORAR ${equipment.label.uppercase()}", color = Gold, fontWeight = FontWeight.Black, fontSize = 20.sp)
            Text("Nivel $level → $next", color = TextMain, fontWeight = FontWeight.Bold)
            Text("Necesitas", color = TextSoft)
            costs.forEach { (material, count) ->
                val current = ui.player.amount(material)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${material.symbol} ${material.label}", color = if (current >= count) TextMain else Coral)
                    Text("$current / $count", color = if (current >= count) Mint else Coral, fontWeight = FontWeight.Bold)
                }
            }
            GoldButton("MEJORAR", onClick = { onUpgrade(equipment) }, enabled = canAfford)
            OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("CANCELAR") }
        }
    }
}

@Composable
private fun PixelMaterial(type: Int, modifier: Modifier = Modifier) {
    val drawable = when (type) {
        0 -> R.drawable.roca
        1 -> R.drawable.troncos
        2 -> R.drawable.hierro
        3 -> R.drawable.pepita
        4 -> R.drawable.gema_bruto
        5 -> R.drawable.piedra
        6 -> R.drawable.tablas_madera
        7 -> R.drawable.lingote_hierro
        8 -> R.drawable.lingote_oro
        else -> R.drawable.gema
    }
    Image(
        bitmap = ImageBitmap.imageResource(drawable),
        contentDescription = Material.entries.getOrNull(type)?.label,
        contentScale = ContentScale.FillBounds,
        filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
        modifier = modifier,
    )
}

@Composable
private fun GoldButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 46.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink, disabledContainerColor = PanelRaised, disabledContentColor = TextSoft),
    ) { Text(label, fontWeight = FontWeight.Black, letterSpacing = .6.sp, textAlign = TextAlign.Center) }
}

@Composable
private fun ControlButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier.heightIn(min = 46.dp), shape = RoundedCornerShape(4.dp)) {
        Text(label, color = Gold, fontWeight = FontWeight.Black, fontSize = 20.sp)
    }
}
