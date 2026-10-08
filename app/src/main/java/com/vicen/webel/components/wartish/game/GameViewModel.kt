package com.vicen.webel.components.wartish.game

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vicen.webel.components.wartish.data.Equipment
import com.vicen.webel.components.wartish.data.EquipmentUpgradeOutcome
import com.vicen.webel.components.wartish.data.GameDatabase
import com.vicen.webel.components.wartish.data.GameRepository
import com.vicen.webel.components.wartish.data.Material
import com.vicen.webel.components.wartish.data.PlayerEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class EnemyState(
    val level: Int,
    val health: Int,
    val maxHealth: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
)

data class BattleAnimationEvent(
    val id: Long,
    val special: Boolean = false,
    val targetIndex: Int = -1,
)

data class BattleDamageEvent(
    val id: Long,
    val damage: Int,
    val critical: Boolean = false,
    val targetIndex: Int = -1,
    val defeated: Boolean = false,
)

data class BattleState(
    val enemies: List<EnemyState>,
    val enemyIndex: Int = 0,
    val playerHealth: Int,
    val specialCharge: Int = 0,
    val selectedEnemy: Int = 0,
    val playerRemainingMs: Long,
    val enemyRemainingMs: Long,
    val running: Boolean = true,
    val paused: Boolean = false,
    val complete: Boolean = false,
    val won: Boolean = false,
    val experienceEarned: Int = 0,
    val playerAnimation: BattleAnimationEvent? = null,
    val enemyAnimation: BattleAnimationEvent? = null,
    val playerDamage: BattleDamageEvent? = null,
    val enemyDamage: BattleDamageEvent? = null,
    val encounterNumber: Int = 1,
    val entrySequence: Long = 0L,
    val entering: Boolean = false,
    val entryRemainingMs: Long = 0L,
    val treatmentUsed: Boolean = false,
)

data class UpgradeFeedbackEvent(
    val id: Long,
    val outcome: EquipmentUpgradeOutcome,
)

data class GameUiState(
    val player: PlayerEntity = PlayerEntity(),
    val collection: CollectionState? = null,
    val collectionAnimation: CollectionTurnAnimation? = null,
    val falling: FallingState? = null,
    val battle: BattleState? = null,
    val equipmentDialog: Equipment? = null,
    val upgradeFeedback: UpgradeFeedbackEvent? = null,
    val notice: String? = null,
)

class GameViewModel internal constructor(
    application: Application,
    private val clock: GameClock,
    private val random: Random = Random.Default,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(application, GameClock { SystemClock.elapsedRealtime() })

    private val repository = GameRepository(GameDatabase.get(application))
    private val gson = Gson()
    private val _state = MutableStateFlow(GameUiState())
    val state = _state.asStateFlow()
    private var battleJob: Job? = null
    private val collectionMutex = Mutex()
    private val processingMutex = Mutex()
    private val battleMutex = Mutex()
    private val upgradeMutex = Mutex()
    private var foreground = true
    private var backgroundPausedBattle = false
    private var collectionAnimationSequence = 0L
    private var upgradeFeedbackSequence = 0L
    private val collectionType = object : TypeToken<CollectionState>() {}.type
    private val fallingType = object : TypeToken<FallingState>() {}.type
    private val battleType = object : TypeToken<BattleState>() {}.type

    init {
        viewModelScope.launch {
            repository.initialize()
            repository.player.collect { player -> if (player != null) _state.update { it.copy(player = player) } }
        }
    }

    fun openCollection() = viewModelScope.launch {
        val saved = repository.session("collection")
        val game = saved?.let { runCatching { gson.fromJson<CollectionState>(it, collectionType) }.getOrNull() }
            ?.copy(movesLeft = Int.MAX_VALUE, complete = false)
            ?: CollectionEngine.newGame()
        _state.update { it.copy(collection = game, collectionAnimation = null, notice = null) }
        repository.saveSession("collection", gson.toJson(game))
    }

    fun chooseCell(index: Int) = viewModelScope.launch {
        collectionMutex.withLock {
            if (_state.value.collectionAnimation != null) return@withLock
            val current = _state.value.collection ?: CollectionEngine.newGame()
            val result = CollectionEngine.select(current, index)
            commitCollectionResult(result)
        }
    }

    fun swapCells(first: Int, second: Int, onResolved: (Boolean) -> Unit = {}) = viewModelScope.launch {
        val accepted = collectionMutex.withLock {
            if (_state.value.collectionAnimation != null) return@withLock false
            val current = _state.value.collection ?: CollectionEngine.newGame()
            val result = CollectionEngine.swap(current, first, second)
            commitCollectionResult(result)
            result.accepted
        }
        onResolved(accepted)
    }

    private suspend fun commitCollectionResult(result: CellSwapResult) {
        if (result.accepted) {
            var next = result.state
            var message: String? = null
            if (!next.complete && !CollectionEngine.hasAvailableMove(next.board)) {
                next = CollectionEngine.reshuffle(next)
                message = "Tablero reorganizado"
            }
            val committedPlayer = repository.commitCollection(result.rewards, gson.toJson(next))
            val animation = result.animation.takeIf { it.isNotEmpty() }?.let {
                CollectionTurnAnimation(++collectionAnimationSequence, it)
            }
            _state.update {
                it.copy(
                    player = committedPlayer,
                    collection = next,
                    collectionAnimation = animation,
                    notice = message,
                )
            }
        } else {
            _state.update { it.copy(collection = result.state) }
            repository.saveSession("collection", gson.toJson(result.state))
        }
    }

    fun clearCollectionAnimation(id: Long) {
        _state.update { current ->
            if (current.collectionAnimation?.id == id) current.copy(collectionAnimation = null) else current
        }
    }

    fun restartCollection() = viewModelScope.launch {
        collectionMutex.withLock {
            val game = CollectionEngine.newGame()
            _state.update { it.copy(collection = game, collectionAnimation = null, notice = null) }
            repository.saveSession("collection", gson.toJson(game))
        }
    }

    fun openProcessing() = viewModelScope.launch {
        val saved = repository.session("processing")
        val restored = saved?.let { runCatching { gson.fromJson<FallingState>(it, fallingType) }.getOrNull() }
        val game = when {
            restored?.pieceVersion == 2 && restored.cells.size == FallingEngine.COLUMNS * FallingEngine.ROWS -> restored
            restored?.pieceVersion == 1 && restored.cells.size == 72 -> {
                val expandedCells = MutableList(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 }
                for (row in 0 until 12) for (column in 0 until 6) {
                    expandedCells[row * FallingEngine.COLUMNS + column + 2] = restored.cells[row * 6 + column]
                }
                restored.copy(
                    cells = expandedCells,
                    column = restored.column + 2,
                    shapeVariant = restored.material.coerceIn(0, FallingEngine.SHAPE_T),
                    pendingClear = restored.pendingClear?.copy(
                        cells = restored.pendingClear.cells.map { cell ->
                            cell.copy(index = (cell.index / 6) * FallingEngine.COLUMNS + cell.index % 6 + 2)
                        },
                    ),
                    pieceVersion = 2,
                )
            }
            else -> FallingState()
        }
        _state.update { it.copy(falling = game.copy(running = false), notice = null) }
        if (restored !== game) repository.saveSession("processing", gson.toJson(game.copy(running = false)))
    }

    fun startOrResumeProcessing() = viewModelScope.launch {
        processingMutex.withLock {
            var current = _state.value.falling ?: FallingState()
            if (current.complete) {
                current = FallingState()
                _state.update { it.copy(falling = current, notice = null) }
                repository.saveSession("processing", gson.toJson(current))
            }
            if (current.pendingClear != null) return@withLock
            if (current.material >= 0 && current.started) {
                val resumed = current.copy(running = true)
                _state.update { it.copy(falling = resumed) }
                repository.saveSession("processing", gson.toJson(resumed))
                return@withLock
            }
            val player = repository.playerOnce()
            val eligible = eligibleProcessingMaterials(Material.entries.take(5).associateWith(player::amount))
            if (eligible.isEmpty()) {
                val ended = current.copy(
                    cells = List(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 },
                    material = -1,
                    running = false,
                    complete = true,
                    started = true,
                )
                _state.update { it.copy(falling = ended, notice = "No tienes 4 unidades de ningún material. El tablero se ha perdido.") }
                repository.saveSession("processing", gson.toJson(ended))
                return@withLock
            }
            val material = chooseProcessingMaterial(eligible)
            val shapeVariant = Random.nextInt(FallingEngine.SHAPE_COUNT)
            val next = current.copy(
                material = material.ordinal,
                shapeVariant = shapeVariant,
                row = 0,
                column = FallingEngine.spawnColumn(shapeVariant),
                rotation = 0,
                running = true,
                started = true,
            )
            if (!FallingEngine.canPlace(next.cells, next.shapeVariant, next.row, next.column, next.rotation)) {
                val ended = next.copy(cells = List(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 }, material = -1, running = false, complete = true)
                _state.update { it.copy(falling = ended, notice = "El tablero llegó arriba. Las piezas que quedaban se han perdido.") }
                repository.saveSession("processing", gson.toJson(ended))
                return@withLock
            }
            val consumed = repository.commitProcessing(emptyMap(), material, gson.toJson(next), consumeCount = 4) ?: return@withLock
            _state.update { it.copy(falling = next, player = consumed, notice = null) }
        }
    }

    fun restartProcessing() = viewModelScope.launch {
        processingMutex.withLock {
            val fresh = FallingState()
            _state.update { it.copy(falling = fresh, notice = null) }
            repository.saveSession("processing", gson.toJson(fresh))
        }
    }

    fun moveFalling(delta: Int) = updateFalling { FallingEngine.move(it, delta) }
    fun rotateFalling() = updateFalling(FallingEngine::rotate)
    fun dropFalling() = resolveFalling { FallingEngine.drop(it) }
    fun tickFalling() = resolveFalling { FallingEngine.tick(it) }

    fun completeProcessingClear(animationId: Long) = viewModelScope.launch {
        processingMutex.withLock {
            val current = _state.value.falling ?: return@withLock
            val animation = current.pendingClear?.takeIf { it.id == animationId } ?: return@withLock
            val processedRewards = animation.cells.mapNotNull { cell ->
                Material.entries.getOrNull(cell.material)?.refined
            }.groupingBy { it }.eachCount()
            finishProcessingTurn(current.copy(pendingClear = null), processedRewards)
        }
    }

    private fun updateFalling(transform: (FallingState) -> FallingState) = viewModelScope.launch {
        processingMutex.withLock {
            val current = _state.value.falling ?: return@withLock
            val next = transform(current)
            _state.update { it.copy(falling = next) }
            repository.saveSession("processing", gson.toJson(next))
        }
    }

    private fun resolveFalling(transform: (FallingState) -> LockResult) = viewModelScope.launch {
        processingMutex.withLock {
            val current = _state.value.falling ?: return@withLock
            if (current.pendingClear != null) return@withLock
            val result = transform(current)
            val next = result.state
            if (next.material >= 0 || next.pendingClear != null) {
                _state.update { it.copy(falling = next) }
                repository.saveSession("processing", gson.toJson(next))
                return@withLock
            }
            finishProcessingTurn(next, emptyMap())
        }
    }

    private suspend fun finishProcessingTurn(state: FallingState, processedRewards: Map<Material, Int>) {
        var next = state
        var consume: Material? = null
        var message = processedRewards.entries.takeIf { it.isNotEmpty() }
            ?.joinToString(" · ") { "+${it.value} ${it.key.label}" }

        if (next.complete) {
            next = next.copy(cells = List(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 }, material = -1, running = false)
            message = listOfNotNull(message, "El tablero llegó arriba; las piezas restantes se han perdido.").joinToString(" ")
        } else if (next.material < 0) {
            val player = repository.playerOnce()
            val updatedInventory = processedRewards.entries.fold(player) { currentPlayer, (material, quantity) ->
                currentPlayer.withAmount(material, currentPlayer.amount(material) + quantity)
            }
            val eligible = eligibleProcessingMaterials(Material.entries.take(5).associateWith(updatedInventory::amount))
            if (eligible.isEmpty()) {
                next = next.copy(
                    cells = List(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 },
                    running = false,
                    complete = true,
                    material = -1,
                )
                message = listOfNotNull(message, "No quedan 4 unidades de ningún material; el tablero se ha perdido.").joinToString(" ")
            } else {
                val chosen = chooseProcessingMaterial(eligible)
                val spawned = next.copy(
                    material = chosen.ordinal,
                    row = 0,
                    shapeVariant = Random.nextInt(FallingEngine.SHAPE_COUNT),
                    column = 0,
                    rotation = 0,
                    running = true,
                    complete = false,
                    started = true,
                )
                val centeredSpawn = spawned.copy(column = FallingEngine.spawnColumn(spawned.shapeVariant))
                if (!FallingEngine.canPlace(centeredSpawn.cells, centeredSpawn.shapeVariant, centeredSpawn.row, centeredSpawn.column, centeredSpawn.rotation)) {
                    next = centeredSpawn.copy(cells = List(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 }, material = -1, running = false, complete = true)
                    message = listOfNotNull(message, "El tablero llegó arriba; las piezas restantes se han perdido.").joinToString(" ")
                } else {
                    consume = chosen
                    next = centeredSpawn
                }
            }
        }

        val committedPlayer = repository.commitProcessing(
            processedRewards,
            consume,
            gson.toJson(next),
            consumeCount = if (consume == null) 0 else 4,
        ) ?: return
        _state.update { it.copy(falling = next, player = committedPlayer, notice = message) }
    }

    fun pauseProcessing() = viewModelScope.launch {
        processingMutex.withLock {
            val game = _state.value.falling ?: return@withLock
            if (game.running) {
                val paused = game.copy(running = false)
                _state.update { it.copy(falling = paused) }
                repository.saveSession("processing", gson.toJson(paused))
            }
        }
    }

    fun abandonProcessing() = viewModelScope.launch {
        processingMutex.withLock {
            val current = _state.value.falling ?: return@withLock
            val end = current.copy(
                cells = List(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 },
                running = false,
                complete = true,
                material = -1,
            )
            _state.update { it.copy(falling = end, notice = "Procesamiento terminado. Las piezas del tablero se han perdido.") }
            repository.saveSession("processing", gson.toJson(end))
        }
    }

    fun showUpgrade(equipment: Equipment) {
        _state.update { it.copy(equipmentDialog = equipment, upgradeFeedback = null, notice = null) }
    }
    fun dismissUpgrade() { _state.update { it.copy(equipmentDialog = null, upgradeFeedback = null) } }
    fun clearNotice() { _state.update { it.copy(notice = null) } }
    fun upgrade(equipment: Equipment) = viewModelScope.launch {
        upgradeMutex.withLock {
            val fails = random.nextFloat() < GameBalance.BLACKSMITH_FAILURE_PROBABILITY
            val attempt = repository.upgrade(equipment, fails)
            upgradeFeedbackSequence += 1
            _state.update {
                it.copy(
                    player = attempt.player,
                    equipmentDialog = equipment,
                    upgradeFeedback = UpgradeFeedbackEvent(upgradeFeedbackSequence, attempt.outcome),
                    notice = null,
                )
            }
        }
    }

    private fun eligibleProcessingMaterials(amounts: Map<Material, Int>): List<Pair<Material, Int>> =
        amounts.mapNotNull { (material, amount) ->
            val completePieces = amount.coerceAtLeast(0) / 4
            if (completePieces > 0) material to completePieces else null
        }

    private fun chooseProcessingMaterial(eligible: List<Pair<Material, Int>>): Material {
        var pick = Random.nextInt(eligible.sumOf { it.second })
        return eligible.first { (_, weight) -> (pick < weight).also { pick -= weight } }.first
    }

    fun openBattle() = viewModelScope.launch {
        val saved = repository.session("combat")
        val existing = saved?.let { serialized ->
            runCatching { gson.fromJson<BattleState>(serialized, battleType) }.getOrNull()
                ?.let { it.copy(encounterNumber = it.encounterNumber.coerceAtLeast(1)) }
        }
        if (existing != null) {
            _state.update { it.copy(battle = existing, notice = null) }
            if (existing.running && !existing.complete && !existing.paused) launchBattleClock()
        } else {
            _state.update { it.copy(battle = null, notice = null) }
        }
    }

    fun startBattle() = viewModelScope.launch {
        battleMutex.withLock {
            battleJob?.cancel()
            battleJob = null
            val player = repository.playerOnce()
            val previous = _state.value.battle
            if (previous?.complete == true && !previous.won && player.currentHealth <= 0) {
                _state.update { it.copy(notice = "Necesitas curarte en la hoguera antes de reintentar.") }
                return@withLock
            }
            val nextSequence = (previous?.entrySequence ?: 0L) + 1L
            val battle = if (previous?.complete == true && !previous.won) {
                val failedIndex = previous.enemyIndex.coerceIn(previous.enemies.indices)
                val restoredEnemies = previous.enemies.mapIndexed { index, enemy ->
                    when {
                        index < failedIndex -> enemy.copy(health = 0)
                        index == failedIndex -> enemy.copy(health = enemy.maxHealth)
                        else -> enemy
                    }
                }
                val failedEnemy = restoredEnemies[failedIndex]
                previous.copy(
                    enemies = restoredEnemies,
                    enemyIndex = failedIndex,
                    playerHealth = player.currentHealth,
                    specialCharge = 0,
                    selectedEnemy = failedIndex,
                    playerRemainingMs = attackInterval(player.speed),
                    enemyRemainingMs = attackInterval(failedEnemy.speed),
                    running = true,
                    paused = false,
                    complete = false,
                    won = false,
                    experienceEarned = 0,
                    playerAnimation = null,
                    enemyAnimation = null,
                    playerDamage = null,
                    enemyDamage = null,
                    entrySequence = nextSequence,
                    entering = true,
                    entryRemainingMs = 1_250L,
                    treatmentUsed = false,
                )
            } else {
                createBattle(
                    player = player,
                    encounterNumber = (previous?.encounterNumber ?: 0).coerceAtLeast(0) + 1,
                    entrySequence = nextSequence,
                )
            }
            _state.update { it.copy(player = player.copy(currentHealth = battle.playerHealth), battle = battle, notice = null) }
            repository.commitBattle(gson.toJson(battle), battle.playerHealth)
        }
        launchBattleClock()
    }

    private fun createBattle(player: PlayerEntity, encounterNumber: Int, entrySequence: Long): BattleState {
        val level = player.level
        val enemies = List(3) {
            val hp = 25 * level
            EnemyState(level, hp, hp, 4 * level, level, 2 * level)
        }
        return BattleState(
            enemies = enemies,
            playerHealth = player.currentHealth,
            playerRemainingMs = attackInterval(player.speed),
            enemyRemainingMs = attackInterval(enemies.first().speed),
            encounterNumber = encounterNumber,
            entrySequence = entrySequence,
            entering = true,
            entryRemainingMs = 1_250L,
        )
    }

    fun completeBattleEntry(sequence: Long) = viewModelScope.launch {
        battleMutex.withLock {
            val current = _state.value.battle ?: return@withLock
            if (!current.entering || current.entrySequence != sequence) return@withLock
            val ready = current.copy(entering = false, entryRemainingMs = 0L)
            _state.update { it.copy(battle = ready) }
            repository.commitBattle(gson.toJson(ready), ready.playerHealth)
        }
    }

    fun selectEnemy(index: Int) {
        val current = _state.value.battle ?: return
        if (index == current.enemyIndex) _state.update { it.copy(battle = current.copy(selectedEnemy = index)) }
    }

    fun useSpecial() = viewModelScope.launch {
        battleMutex.withLock {
            val battle = _state.value.battle ?: return@withLock
            if (!battle.running || battle.paused || battle.entering || battle.specialCharge < 3 || battle.complete) return@withLock
            val enemy = battle.enemies[battle.enemyIndex]
            val damage = CombatEngine.specialAttack(_state.value.player.magic, enemy.defense)
            val next = damageEnemy(
                battle.copy(
                    specialCharge = 0,
                    playerAnimation = BattleAnimationEvent(
                        (battle.playerAnimation?.id ?: 0L) + 1L,
                        special = true,
                        targetIndex = battle.enemyIndex,
                    ),
                    enemyDamage = BattleDamageEvent(
                        (battle.enemyDamage?.id ?: 0L) + 1L,
                        damage,
                        targetIndex = battle.enemyIndex,
                        defeated = damage >= enemy.health,
                    ),
                ),
                damage,
            )
            val earned = next.experienceEarned - battle.experienceEarned
            commitBattle(next, earned)
        }
    }

    fun applySuture(successfulWounds: Int) = viewModelScope.launch {
        battleMutex.withLock {
            val battle = _state.value.battle ?: return@withLock
            if (!battle.complete || battle.treatmentUsed) return@withLock
            val player = repository.playerOnce()
            val healingPerWound = ((player.maxHealth + 9) / 10).coerceAtLeast(1)
            val health = (battle.playerHealth + healingPerWound * successfulWounds.coerceIn(0, 3))
                .coerceAtMost(player.maxHealth)
            val treatedBattle = battle.copy(playerHealth = health, treatmentUsed = true)
            val updatedPlayer = repository.commitSuture(gson.toJson(treatedBattle), health)
            if (updatedPlayer != null) {
                _state.update {
                    it.copy(
                        player = updatedPlayer,
                        battle = treatedBattle,
                        notice = "Sutura terminada: ${health - battle.playerHealth} PV recuperados.",
                    )
                }
            } else {
                _state.update { it.copy(notice = "Esta etapa ya recibió una cura.") }
            }
        }
    }

    fun pauseBattle() = viewModelScope.launch {
        battleMutex.withLock {
            val current = _state.value.battle ?: return@withLock
            if (current.complete || current.paused) return@withLock
            battleJob?.cancel(); battleJob = null
            val paused = current.copy(paused = true)
            _state.update { it.copy(battle = paused) }
            repository.commitBattle(gson.toJson(paused), paused.playerHealth)
        }
    }

    fun resumeBattle() {
        viewModelScope.launch {
            battleMutex.withLock {
                val current = _state.value.battle ?: return@withLock
                if (!current.complete && current.paused) {
                    val resumed = current.copy(paused = false)
                    _state.update { it.copy(battle = resumed) }
                    repository.commitBattle(gson.toJson(resumed), resumed.playerHealth)
                }
            }
            if (foreground) launchBattleClock()
        }
    }

    fun pauseForBackground() {
        foreground = false
        viewModelScope.launch {
            battleMutex.withLock {
                val current = _state.value.battle
                if (current != null && current.running && !current.complete && !current.paused) {
                    backgroundPausedBattle = true
                    battleJob?.cancel(); battleJob = null
                    val paused = current.copy(paused = true)
                    _state.update { it.copy(battle = paused) }
                    repository.commitBattle(gson.toJson(paused), paused.playerHealth)
                }
            }
            pauseProcessing()
        }
    }

    fun resumeFromBackground() {
        foreground = true
        if (backgroundPausedBattle) {
            backgroundPausedBattle = false
            resumeBattle()
        }
    }

    private fun launchBattleClock() {
        if (battleJob?.isActive == true || !foreground) return
        battleJob = viewModelScope.launch {
            var lastTime = clock.nowMillis()
            while (true) {
                delay(100)
                val now = clock.nowMillis()
                val delta = (now - lastTime).coerceIn(0L, 500L)
                lastTime = now
                if (advanceBattle(delta, now)) break
            }
        }
    }

    private suspend fun advanceBattle(delta: Long, now: Long): Boolean = battleMutex.withLock {
        var battle = _state.value.battle ?: return@withLock true
        if (!foreground || battle.complete || battle.paused || !battle.running) return@withLock true
        if (battle.entering) {
            val remaining = (battle.entryRemainingMs - delta).coerceAtLeast(0L)
            val updated = battle.copy(entering = remaining > 0L, entryRemainingMs = remaining)
            _state.update { it.copy(battle = updated) }
            if (remaining == 0L || now % 1000L < 100L) {
                repository.commitBattle(gson.toJson(updated), updated.playerHealth)
            }
            return@withLock false
        }
        val previousExperience = battle.experienceEarned
        battle = battle.copy(
            playerRemainingMs = CombatEngine.remainingAfter(battle.playerRemainingMs, delta),
            enemyRemainingMs = CombatEngine.remainingAfter(battle.enemyRemainingMs, delta),
        )
        var changed = false
        if (battle.playerRemainingMs <= 0 && !battle.complete) {
            val player = _state.value.player
            val enemy = battle.enemies[battle.enemyIndex]
            val attack = CombatEngine.normalAttack(player.attack, enemy.defense, player.critical, Random)
            battle = damageEnemy(
                battle.copy(
                    playerRemainingMs = attackInterval(player.speed),
                    specialCharge = (battle.specialCharge + 1).coerceAtMost(3),
                    playerAnimation = BattleAnimationEvent(
                        (battle.playerAnimation?.id ?: 0L) + 1L,
                        targetIndex = battle.enemyIndex,
                    ),
                    enemyDamage = BattleDamageEvent(
                        (battle.enemyDamage?.id ?: 0L) + 1L,
                        attack.damage,
                        attack.critical,
                        battle.enemyIndex,
                        defeated = attack.damage >= enemy.health,
                    ),
                ),
                attack.damage,
            )
            changed = true
        }
        if (battle.enemyRemainingMs <= 0 && !battle.complete) {
            val enemy = battle.enemies[battle.enemyIndex]
            val damage = CombatEngine.normalAttack(enemy.attack, _state.value.player.defense, 0).damage
            val hp = (battle.playerHealth - damage).coerceAtLeast(0)
            battle = battle.copy(
                playerHealth = hp,
                enemyRemainingMs = attackInterval(enemy.speed),
                enemyAnimation = BattleAnimationEvent(
                    (battle.enemyAnimation?.id ?: 0L) + 1L,
                    targetIndex = battle.enemyIndex,
                ),
                playerDamage = BattleDamageEvent((battle.playerDamage?.id ?: 0L) + 1L, damage),
            )
            if (hp == 0) battle = battle.copy(running = false, complete = true, won = false)
            changed = true
        }
        _state.update { it.copy(battle = battle) }
        if (changed) {
            val earned = battle.experienceEarned - previousExperience
            commitBattle(battle, earned)
            battle.complete
        } else {
            if (now % 1000L < 100L) commitBattle(battle)
            battle.complete
        }
    }

    private fun damageEnemy(battle: BattleState, damage: Int): BattleState {
        val enemies = battle.enemies.toMutableList()
        val enemy = enemies[battle.enemyIndex]
        val remaining = (enemy.health - damage).coerceAtLeast(0)
        enemies[battle.enemyIndex] = enemy.copy(health = remaining)
        if (remaining == 0) {
            val experience = battle.experienceEarned + 50 * enemy.level
            if (battle.enemyIndex == enemies.lastIndex) {
                return battle.copy(enemies = enemies, running = false, complete = true, won = true, experienceEarned = experience)
            }
            val nextIndex = battle.enemyIndex + 1
            return battle.copy(
                enemies = enemies,
                enemyIndex = nextIndex,
                selectedEnemy = nextIndex,
                enemyRemainingMs = attackInterval(enemies[nextIndex].speed),
                experienceEarned = experience,
            )
        }
        return battle.copy(enemies = enemies)
    }

    private suspend fun commitBattle(battle: BattleState, experience: Int = 0) {
        _state.update { it.copy(battle = battle) }
        repository.commitBattle(gson.toJson(battle), battle.playerHealth, experience)
    }

    private fun attackInterval(speed: Int): Long = CombatEngine.attackInterval(speed)

    override fun onCleared() {
        battleJob?.cancel()
        super.onCleared()
    }
}
