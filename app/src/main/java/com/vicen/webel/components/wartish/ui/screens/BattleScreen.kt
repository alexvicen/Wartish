package com.vicen.webel.components.wartish.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vicen.webel.components.wartish.R
import com.vicen.webel.components.wartish.data.PlayerEntity
import com.vicen.webel.components.wartish.game.BattleAnimationEvent
import com.vicen.webel.components.wartish.game.BattleDamageEvent
import com.vicen.webel.components.wartish.game.BattleState
import com.vicen.webel.components.wartish.game.CombatEngine
import com.vicen.webel.components.wartish.game.EnemyState
import com.vicen.webel.components.wartish.game.GameUiState
import com.vicen.webel.components.wartish.ui.*
import com.vicen.webel.components.wartish.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun BattleScreen(ui: GameUiState, actions: BattleScreenActions, onBack: () -> Unit) {
    val battle = ui.battle
    var showResult by remember { mutableStateOf(battle?.complete == true) }
    LaunchedEffect(battle?.complete) {
        if (battle?.complete == true) {
            if (!showResult) delay(1600L)
            showResult = true
        } else {
            showResult = false
        }
    }
    GamePage(
        title = "Combate",
        subtitle = "",
        player = ui.player,
        onBack = onBack,
        notice = ui.notice,
        backButtonAboveProfile = true,
        fillRemainingHeight = true,
        centerTitleAtTop = true,
        showBackpack = false,
        screenBackground = R.drawable.fondo_combate_bosque,
        centerHeaderStatus = battle?.let {
            if (it.entering) "RONDA ${it.encounterNumber} · ENTRADA"
            else "RONDA ${it.encounterNumber} · ENEMIGO ${it.enemyIndex + 1}/${it.enemies.size}"
        } ?: "PREPARADO",
    ) {
        when {
            battle == null -> {
                PixelPanel {
                    Text("Tu equipo: ataque ${ui.player.attack} · magia ${ui.player.magic} · defensa ${ui.player.defense} · velocidad ${ui.player.speed}", color = TextSoft)
                    Text("Enfrenta oleadas de tres enemigos. Los golpes y sus respuestas ocurren automáticamente.", color = TextMain)
                    GoldButton("INICIAR ENCUENTRO", onClick = { actions.start() })
                }
            }
            battle.complete && showResult -> BattleResult(
                battle = battle,
                currentHealth = ui.player.currentHealth,
                maxHealth = ui.player.maxHealth,
                onAgain = { actions.start() },
                onBack = onBack,
            )
            else -> ActiveBattle(ui.player, battle, actions, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ActiveBattle(player: PlayerEntity, battle: BattleState, actions: BattleScreenActions, modifier: Modifier = Modifier) {
    var playerFrame by remember(battle.entrySequence) {
        mutableIntStateOf(if (battle.entering) R.drawable.chicoentra1 else R.drawable.chicoentra9)
    }
    var enemyFrames by remember(battle.entrySequence) {
        mutableStateOf(List(battle.enemies.size) {
            if (battle.entering) R.drawable.esqueletoentrada1 else R.drawable.esqueletoentrada9
        })
    }
    var lastPlayerAnimation by remember(battle.entrySequence) { mutableLongStateOf(battle.playerAnimation?.id ?: 0L) }
    var lastEnemyAnimation by remember(battle.entrySequence) { mutableLongStateOf(battle.enemyAnimation?.id ?: 0L) }
    var lastEnemyDamage by remember(battle.entrySequence) { mutableLongStateOf(battle.enemyDamage?.id ?: 0L) }
    var lastPlayerProjectile by remember(battle.entrySequence) { mutableLongStateOf(battle.playerAnimation?.id ?: 0L) }
    var lastEnemyProjectile by remember(battle.entrySequence) { mutableLongStateOf(battle.enemyAnimation?.id ?: 0L) }
    var playerProjectile by remember(battle.entrySequence) { mutableStateOf<BattleAnimationEvent?>(null) }
    var enemyProjectile by remember(battle.entrySequence) { mutableStateOf<BattleAnimationEvent?>(null) }
    var specialImpactTarget by remember(battle.entrySequence) { mutableIntStateOf(-1) }
    val playerProjectileProgress = remember(battle.entrySequence) { Animatable(0f) }
    val enemyProjectileProgress = remember(battle.entrySequence) { Animatable(0f) }
    val entryProgress = remember(battle.entrySequence) { Animatable(if (battle.entering) 0f else 1f) }

    LaunchedEffect(battle.entrySequence, battle.entering) {
        if (!battle.entering) {
            entryProgress.snapTo(1f)
            return@LaunchedEffect
        }
        val playerEntry = listOf(
            R.drawable.chicoentra1, R.drawable.chicoentra2, R.drawable.chicoentra3,
            R.drawable.chicoentra4, R.drawable.chicoentra5, R.drawable.chicoentra6,
            R.drawable.chicoentra7, R.drawable.chicoentra8, R.drawable.chicoentra9,
        )
        val enemyEntry = listOf(
            R.drawable.esqueletoentrada1, R.drawable.esqueletoentrada2, R.drawable.esqueletoentrada3,
            R.drawable.esqueletoentrada4, R.drawable.esqueletoentrada5, R.drawable.esqueletoentrada6,
            R.drawable.esqueletoentrada7, R.drawable.esqueletoentrada8, R.drawable.esqueletoentrada9,
        )
        entryProgress.snapTo(0f)
        coroutineScope {
            playerEntry.forEachIndexed { index, frame ->
                playerFrame = frame
                enemyFrames = List(battle.enemies.size) { enemyEntry[index] }
                entryProgress.animateTo((index + 1) / playerEntry.size.toFloat(), tween(130, easing = LinearEasing))
            }
        }
        actions.completeEntry(battle.entrySequence)
    }

    LaunchedEffect(battle.playerAnimation?.id) {
        val event = battle.playerAnimation ?: return@LaunchedEffect
        if (event.id <= lastPlayerAnimation) return@LaunchedEffect
        lastPlayerAnimation = event.id
        val frames = if (event.special) {
            listOf(
                R.drawable.chicoataqueespecial1, R.drawable.chicoataqueespecial2,
                R.drawable.chicoataqueespecial3, R.drawable.chicoataqueespecial4,
                R.drawable.chicoataqueespecial5, R.drawable.chicoataqueespecial6,
                R.drawable.chicoataqueespecial7, R.drawable.chicoataqueespecial1,
            )
        } else {
            listOf(
                R.drawable.chicoataque1, R.drawable.chicoataque2, R.drawable.chicoataque3,
                R.drawable.chicoataque4, R.drawable.chicoataque5, R.drawable.chicoataque6,
                R.drawable.chicoataque7, R.drawable.chicoataque8, R.drawable.chicoataque9,
                R.drawable.chicoataque10, R.drawable.chicoataque11, R.drawable.chicoataque12,
                R.drawable.chicoataque4, R.drawable.chicoataque3, R.drawable.chicoataque2,
                R.drawable.chicoataque1,
            )
        }
        frames.forEachIndexed { index, frame ->
            playerFrame = frame
            delay(if (event.special && index == 5) 350L else if (event.special) 150L else 100L)
        }
        playerFrame = R.drawable.chicoentra9
    }

    LaunchedEffect(battle.playerAnimation?.id) {
        val event = battle.playerAnimation ?: return@LaunchedEffect
        if (event.id <= lastPlayerProjectile) return@LaunchedEffect
        lastPlayerProjectile = event.id
        playerProjectile = event
        playerProjectileProgress.snapTo(0f)
        playerProjectileProgress.animateTo(1f, tween(520, easing = LinearEasing))
        if (event.special) {
            specialImpactTarget = event.targetIndex
            delay(220L)
            specialImpactTarget = -1
        }
        playerProjectile = null
    }

    LaunchedEffect(battle.enemyAnimation?.id, battle.enemyDamage?.id) {
        val enemyAttack = battle.enemyAnimation
        val hit = battle.enemyDamage
        val didAttack = enemyAttack != null && enemyAttack.id > lastEnemyAnimation
        val wasHit = hit != null && hit.id > lastEnemyDamage
        if (!didAttack && !wasHit) return@LaunchedEffect
        if (didAttack) lastEnemyAnimation = enemyAttack.id
        if (wasHit) lastEnemyDamage = hit.id
        if (!didAttack && hit?.defeated != true) return@LaunchedEffect
        val targetIndex = when {
            didAttack -> enemyAttack.targetIndex
            wasHit -> hit.targetIndex
            else -> -1
        }.takeIf { it in enemyFrames.indices } ?: battle.enemyIndex
        val frames = if (didAttack) {
            listOf(
                R.drawable.esqueletoataque1, R.drawable.esqueletoataque2, R.drawable.esqueletoataque3,
                R.drawable.esqueletoataque4, R.drawable.esqueletoataque5, R.drawable.esqueletoataque6,
                R.drawable.esqueletoataque7, R.drawable.esqueletoataque8, R.drawable.esqueletoataque9,
                R.drawable.esqueletoataque10, R.drawable.esqueletoataque4, R.drawable.esqueletoataque3,
                R.drawable.esqueletoataque2, R.drawable.esqueletoataque1,
            )
        } else {
            listOf(
                R.drawable.esqueletogolpe1, R.drawable.esqueletogolpe2, R.drawable.esqueletogolpe3,
                R.drawable.esqueletogolpe4, R.drawable.esqueletogolpe5, R.drawable.esqueletogolpe6,
            )
        }
        frames.forEach { frame ->
            enemyFrames = enemyFrames.toMutableList().also { it[targetIndex] = frame }
            delay(100L)
        }
        enemyFrames = enemyFrames.toMutableList().also { it[targetIndex] = R.drawable.esqueletoentrada9 }
    }

    LaunchedEffect(battle.enemyAnimation?.id) {
        val event = battle.enemyAnimation ?: return@LaunchedEffect
        if (event.id <= lastEnemyProjectile) return@LaunchedEffect
        lastEnemyProjectile = event.id
        enemyProjectile = event
        enemyProjectileProgress.snapTo(0f)
        enemyProjectileProgress.animateTo(1f, tween(520, easing = LinearEasing))
        enemyProjectile = null
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BattleField(
            player = player,
            battle = battle,
            playerFrame = playerFrame,
            enemyFrames = enemyFrames,
            playerProjectile = playerProjectile,
            playerProjectileProgress = playerProjectileProgress.value,
            enemyProjectile = enemyProjectile,
            enemyProjectileProgress = enemyProjectileProgress.value,
            specialImpactTarget = specialImpactTarget,
            entryProgress = entryProgress.value,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
        PixelPanel {
            if (battle.paused) Text("COMBATE EN PAUSA", color = Gold, fontWeight = FontWeight.Black)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (battle.paused) GoldButton("CONTINUAR", Modifier.weight(1f), actions.resume)
                else OutlinedButton(onClick = { actions.pause() }, modifier = Modifier.weight(1f)) { Text("PAUSAR") }
            }
            BattleSpecialButton(
                charge = battle.specialCharge,
                enabled = battle.specialCharge >= 3 && !battle.paused && !battle.entering && !battle.complete,
                onClick = actions.useSpecial,
            )
        }
    }
}

@Composable
private fun BattleField(
    player: PlayerEntity,
    battle: BattleState,
    playerFrame: Int,
    enemyFrames: List<Int>,
    playerProjectile: BattleAnimationEvent?,
    playerProjectileProgress: Float,
    enemyProjectile: BattleAnimationEvent?,
    enemyProjectileProgress: Float,
    specialImpactTarget: Int,
    entryProgress: Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.clipToBounds()) {
        val heroX = maxWidth * .14f
        val heroSize = 72.dp
        val heroWidth = 82.dp
        val heroBlockHeight = 128.dp
        val heroTopY = ((maxHeight - heroBlockHeight) / 2f).coerceAtLeast(8.dp)
        val enemySize = 58.dp
        val enemyWidth = 76.dp
        val enemyBlockHeight = 98.dp
        val enemySpacing = 8.dp
        val enemyColumnHeight = enemyBlockHeight * battle.enemies.size + enemySpacing * (battle.enemies.size - 1)
        val enemyTopY = ((maxHeight - enemyColumnHeight) / 2f).coerceAtLeast(4.dp)
        val enemyFinalX = maxWidth * .66f
        val enemyX: (Int) -> androidx.compose.ui.unit.Dp = { index ->
            enemyFinalX + (maxWidth - enemyFinalX + enemyWidth) * (1f - entryProgress)
        }
        val movingHeroX = heroX - (heroX + heroWidth) * (1f - entryProgress)
        val heroSpriteCenterY = heroTopY + 55.dp
        val playerInterval = CombatEngine.attackInterval(player.speed)
        val playerAttackProgress = 1f - battle.playerRemainingMs.toFloat() / playerInterval
        val enemyCenterY: (Int) -> androidx.compose.ui.unit.Dp = { index ->
            enemyTopY + (enemyBlockHeight + enemySpacing) * index + 54.dp
        }

        Column(
            Modifier.offset(x = movingHeroX, y = heroTopY).width(heroWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text("${battle.playerHealth}/${player.maxHealth} PV", color = TextMain, fontSize = 7.sp, maxLines = 1)
            LinearProgressIndicator(
                progress = { (battle.playerHealth.toFloat() / player.maxHealth.coerceAtLeast(1)).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(5.dp),
                color = Mint,
                trackColor = PanelRaised,
            )
            LinearProgressIndicator(
                progress = { playerAttackProgress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = Gold,
                trackColor = PanelRaised,
            )
            Box(Modifier.size(heroSize)) {
                Image(
                    bitmap = ImageBitmap.imageResource(playerFrame),
                    contentDescription = player.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
                )
                FloatingBattleDamage(battle.playerDamage, Coral, Modifier.align(Alignment.TopCenter))
            }
            Text(player.name, color = Gold, fontWeight = FontWeight.Black, fontSize = 8.sp, maxLines = 1)
        }

        battle.enemies.forEachIndexed { index, enemy ->
            val current = index == battle.enemyIndex
            val attackInterval = CombatEngine.attackInterval(enemy.speed)
            val attackProgress = if (current) 1f - battle.enemyRemainingMs.toFloat() / attackInterval else 0f
            Column(
                Modifier.offset(x = enemyX(index), y = enemyTopY + (enemyBlockHeight + enemySpacing) * index).width(enemyWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text("${enemy.health}/${enemy.maxHealth} PV", color = TextMain, fontSize = 7.sp, maxLines = 1)
                LinearProgressIndicator(
                    progress = { (enemy.health.toFloat() / enemy.maxHealth.coerceAtLeast(1)).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(5.dp),
                    color = Mint,
                    trackColor = PanelRaised,
                )
                LinearProgressIndicator(
                    progress = { attackProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = Gold,
                    trackColor = PanelRaised,
                )
                Box(Modifier.size(enemySize)) {
                    Image(
                        bitmap = ImageBitmap.imageResource(enemyFrames.getOrElse(index) { R.drawable.esqueletoentrada9 }),
                        contentDescription = "Esqueleto ${index + 1}",
                        modifier = Modifier.fillMaxSize().graphicsLayer { alpha = if (enemy.health == 0) .42f else 1f },
                        contentScale = ContentScale.Fit,
                        filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
                    )
                    val damageEvent = battle.enemyDamage?.takeIf { it.targetIndex == index }
                    FloatingBattleDamage(damageEvent, Gold, Modifier.align(Alignment.TopCenter))
                }
                Text("Esqueleto ${index + 1}", color = if (current) Gold else TextMain, fontWeight = FontWeight.Bold, fontSize = 8.sp, maxLines = 1)
            }
        }

        playerProjectile?.let { projectile ->
            val target = projectile.targetIndex.coerceIn(battle.enemies.indices)
            val progress = playerProjectileProgress.coerceIn(0f, 1f)
            if (progress < 1f) {
                val startX = movingHeroX + heroSize - 3.dp
                val startY = heroSpriteCenterY - 12.dp
                val endX = enemyX(target) + enemyWidth / 2
                val endY = enemyCenterY(target) - 12.dp
                Image(
                    bitmap = ImageBitmap.imageResource(if (projectile.special) R.drawable.personajeflechaespecial else R.drawable.personajeflecha),
                    contentDescription = if (projectile.special) "Flecha especial" else "Flecha",
                    modifier = Modifier.offset(
                        x = startX + (endX - startX) * progress,
                        y = startY + (endY - startY) * progress,
                    ).size(38.dp),
                    contentScale = ContentScale.Fit,
                    filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
                )
            }
        }

        enemyProjectile?.let { projectile ->
            val source = projectile.targetIndex.coerceIn(battle.enemies.indices)
            val progress = enemyProjectileProgress.coerceIn(0f, 1f)
            if (progress < 1f) {
                val startX = enemyX(source) + 2.dp
                val startY = enemyCenterY(source) - 12.dp
                val endX = movingHeroX + heroSize - 8.dp
                val endY = heroSpriteCenterY - 12.dp
                Image(
                    bitmap = ImageBitmap.imageResource(if (projectile.special) R.drawable.esqueletoflechaespecial else R.drawable.esqueletoflecha),
                    contentDescription = "Flecha enemiga",
                    modifier = Modifier.offset(
                        x = startX + (endX - startX) * progress,
                        y = startY + (endY - startY) * progress,
                    ).size(38.dp),
                    contentScale = ContentScale.Fit,
                    filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
                )
            }
        }

        if (specialImpactTarget in battle.enemies.indices) {
            Image(
                bitmap = ImageBitmap.imageResource(R.drawable.rayo),
                contentDescription = "Impacto especial",
                modifier = Modifier.offset(
                    x = enemyX(specialImpactTarget) + enemyWidth / 2 - 9.dp,
                    y = enemyTopY + (enemyBlockHeight + enemySpacing) * specialImpactTarget - 12.dp,
                )
                    .size(width = 18.dp, height = 82.dp)
                    .graphicsLayer { alpha = .88f },
                contentScale = ContentScale.FillBounds,
                filterQuality = androidx.compose.ui.graphics.FilterQuality.None,
            )
        }
    }
}

@Composable
private fun BattleSpecialButton(charge: Int, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    val progress = (charge / 3f).coerceIn(0f, 1f)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(50.dp).border(2.dp, if (enabled) Gold else PanelRaised, shape),
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PanelRaised,
            contentColor = Ink,
            disabledContainerColor = PanelRaised,
            disabledContentColor = TextMain,
        ),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                Modifier.align(Alignment.CenterStart).fillMaxHeight().fillMaxWidth(progress)
                    .background(if (enabled) Gold else Gold.copy(alpha = .48f)),
            )
            Text(
                if (enabled) "ATAQUE ESPECIAL · LISTO"
                else "ATAQUE ESPECIAL · $charge/3",
                color = if (progress >= .66f) Ink else TextMain,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun FloatingBattleDamage(event: BattleDamageEvent?, color: Color, modifier: Modifier = Modifier) {
    val rise = remember { Animatable(0f) }
    val opacity = remember { Animatable(0f) }
    var visible by remember { mutableStateOf(false) }
    var lastEventId by remember { mutableLongStateOf(event?.id ?: 0L) }

    LaunchedEffect(event?.id) {
        val current = event ?: return@LaunchedEffect
        if (current.id <= lastEventId) return@LaunchedEffect
        lastEventId = current.id
        rise.snapTo(0f)
        opacity.snapTo(1f)
        visible = true
        launch { rise.animateTo(-42f, tween(850, easing = LinearOutSlowInEasing)) }
        opacity.animateTo(0f, tween(850, easing = LinearOutSlowInEasing))
        visible = false
    }

    if (visible && event != null) {
        Column(
            modifier = modifier.offset(y = rise.value.dp).graphicsLayer { alpha = opacity.value }
                .background(Ink.copy(alpha = .92f), RoundedCornerShape(3.dp)).padding(horizontal = 5.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("-${event.damage}", color = color, fontWeight = FontWeight.Black, fontSize = 15.sp, maxLines = 1)
        }
    }
}

@Composable
private fun BattleResult(
    battle: BattleState,
    currentHealth: Int,
    maxHealth: Int,
    onAgain: () -> Unit,
    onBack: () -> Unit,
) {
    PixelPanel {
        Text(if (battle.won) "¡VICTORIA!" else "DERROTA", color = if (battle.won) Mint else Coral, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Text("Experiencia obtenida: ${battle.experienceEarned}", color = Gold, fontWeight = FontWeight.Bold)
        Text("Vida: $currentHealth/$maxHealth PV", color = TextMain, fontWeight = FontWeight.Bold)
        Text(
            if (battle.won) "Los tres esqueletos han caído. La vida se conserva: puedes curarte en la hoguera antes de la siguiente ronda."
            else "Derrota en la ronda ${battle.encounterNumber}. Se conservan la experiencia y los enemigos derrotados; al reintentar, volverás a luchar desde este enemigo.",
            color = TextSoft,
        )
        if (battle.won || currentHealth > 0) {
            GoldButton(if (battle.won) "SIGUIENTE RONDA" else "REINTENTAR ETAPA", onClick = onAgain)
        } else {
            Text("No te quedan fuerzas. Vuelve a la hoguera y cose una herida para poder reintentar.", color = Coral, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("VOLVER AL CAMPAMENTO") }
    }
}


internal data class BattleScreenActions(
    val start: () -> Unit = {},
    val completeEntry: (Long) -> Unit = {},
    val useSpecial: () -> Unit = {},
    val pause: () -> Unit = {},
    val resume: () -> Unit = {},
)

@Preview(name = "Combate", showBackground = true)
@Composable
private fun BattleScreenPreview() {
    val enemy = EnemyState(level = 1, health = 25, maxHealth = 25, attack = 4, defense = 1, speed = 2)
    WartishTheme {
        BattleScreen(
            ui = GameUiState(
                battle = BattleState(
                    enemies = listOf(enemy, enemy, enemy),
                    playerHealth = 70,
                    playerRemainingMs = 2400L,
                    enemyRemainingMs = 3000L,
                    running = false,
                    paused = true,
                ),
            ),
            actions = BattleScreenActions(),
            onBack = {},
        )
    }
}
