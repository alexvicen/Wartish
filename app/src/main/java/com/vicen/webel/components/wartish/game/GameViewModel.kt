package com.vicen.webel.components.wartish.game

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vicen.webel.components.wartish.data.Equipment
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
)

data class GameUiState(
    val player: PlayerEntity = PlayerEntity(),
    val collection: CollectionState? = null,
    val falling: FallingState? = null,
    val battle: BattleState? = null,
    val equipmentDialog: Equipment? = null,
    val notice: String? = null,
)

class GameViewModel internal constructor(
    application: Application,
    private val clock: GameClock,
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
    private var foreground = true
    private var backgroundPausedBattle = false
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
            ?: CollectionEngine.newGame()
        _state.update { it.copy(collection = game, notice = null) }
        repository.saveSession("collection", gson.toJson(game))
    }

    fun chooseCell(index: Int) = viewModelScope.launch {
        collectionMutex.withLock {
            val current = _state.value.collection ?: CollectionEngine.newGame()
            val result = CollectionEngine.select(current, index)
            if (result.accepted) {
                var next = result.state
                val extras = result.rewards.entries.joinToString(" · ") { "${it.value} ${it.key.label.lowercase()}" }
                var message = if (extras.isBlank()) null else "+$extras"
                if (!next.complete && !CollectionEngine.hasAvailableMove(next.board)) {
                    next = CollectionEngine.reshuffle(next)
                    message = "Tablero reorganizado"
                }
                repository.commitCollection(result.rewards, gson.toJson(next))
                _state.update { it.copy(collection = next, notice = message) }
            } else {
                _state.update { it.copy(collection = result.state) }
                repository.saveSession("collection", gson.toJson(result.state))
            }
        }
    }

    fun restartCollection() = viewModelScope.launch {
        collectionMutex.withLock {
            val game = CollectionEngine.newGame()
            _state.update { it.copy(collection = game, notice = null) }
            repository.saveSession("collection", gson.toJson(game))
        }
    }

    fun openProcessing() = viewModelScope.launch {
        val saved = repository.session("processing")
        val game = saved?.let { runCatching { gson.fromJson<FallingState>(it, fallingType) }.getOrNull() }
            ?: FallingState()
        _state.update { it.copy(falling = game.copy(running = false), notice = null) }
    }

    fun startOrResumeProcessing() = viewModelScope.launch {
        processingMutex.withLock {
            val current = _state.value.falling ?: FallingState()
            if (current.complete) {
                _state.update { it.copy(notice = "La partida terminó. Vuelve al menú para conservar los refinados.") }
                return@withLock
            }
            if (current.material >= 0 && current.started) {
                val resumed = current.copy(running = true)
                _state.update { it.copy(falling = resumed) }
                repository.saveSession("processing", gson.toJson(resumed))
                return@withLock
            }
            val player = repository.playerOnce()
            val raw = Material.entries.take(5).map { it to player.amount(it) }.filter { it.second > 0 }
            if (raw.isEmpty()) {
                _state.update { it.copy(notice = "No tienes materias primas. Consigue algunas en Recolección.") }
                return@withLock
            }
            if (current.started && current.cells[2] >= 0) {
                val ended = current.copy(running = false, complete = true, material = -1)
                _state.update { it.copy(falling = ended, notice = "El tablero se ha llenado. Las piezas sin transformar se han perdido.") }
                repository.saveSession("processing", gson.toJson(ended))
                return@withLock
            }
            val total = raw.sumOf { it.second }
            var pick = Random.nextInt(total)
            val material = raw.first { (_, quantity) -> (pick < quantity).also { pick -= quantity } }.first
            val next = current.copy(material = material.ordinal, row = 0, column = 2, running = true, started = true)
            val consumed = repository.commitProcessing(emptyMap(), material, gson.toJson(next)) ?: return@withLock
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
    fun dropFalling() = resolveFalling { FallingEngine.drop(it) }
    fun tickFalling() = resolveFalling { FallingEngine.tick(it) }

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
            val result = transform(current)
            var next = result.state
            var consume: Material? = null
            var message: String? = result.refined.entries.takeIf { it.isNotEmpty() }
                ?.joinToString(" · ") { "${it.value} ${it.key.label}" }
            if (next.material < 0 && !next.complete) {
                val player = repository.playerOnce()
                if (Material.entries.take(5).none { player.amount(it) > 0 }) {
                    next = next.copy(running = false, complete = true)
                } else if (next.cells[2] >= 0) {
                    next = next.copy(running = false, complete = true)
                    message = "El tablero se ha llenado. Las piezas sin transformar se han perdido."
                } else {
                    val raw = Material.entries.take(5).map { it to player.amount(it) }.filter { it.second > 0 }
                    val total = raw.sumOf { it.second }
                    var pick = Random.nextInt(total)
                    val chosen = raw.first { (_, quantity) -> (pick < quantity).also { pick -= quantity } }.first
                    consume = chosen
                    next = next.copy(material = chosen.ordinal, row = 0, column = 2, running = true, started = true)
                }
            }
            val committedPlayer = repository.commitProcessing(result.refined, consume, gson.toJson(next)) ?: return@withLock
            _state.update { it.copy(falling = next, player = committedPlayer, notice = message) }
        }
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
            val end = current.copy(running = false, complete = true, material = -1)
            _state.update { it.copy(falling = end, notice = "Procesamiento terminado. Las piezas del tablero se han perdido.") }
            repository.saveSession("processing", gson.toJson(end))
        }
    }

    fun showUpgrade(equipment: Equipment) { _state.update { it.copy(equipmentDialog = equipment) } }
    fun dismissUpgrade() { _state.update { it.copy(equipmentDialog = null) } }
    fun clearNotice() { _state.update { it.copy(notice = null) } }
    fun upgrade(equipment: Equipment) = viewModelScope.launch {
        val success = repository.upgrade(equipment)
        _state.update { it.copy(equipmentDialog = null, notice = if (success) "${equipment.label} mejorado" else "No tienes materiales suficientes") }
    }

    fun openBattle() = viewModelScope.launch {
        val saved = repository.session("combat")
        val existing = saved?.let { runCatching { gson.fromJson<BattleState>(it, battleType) }.getOrNull() }
        if (existing != null) {
            _state.update { it.copy(battle = existing.copy(paused = true), notice = null) }
        } else {
            _state.update { it.copy(battle = null, notice = null) }
        }
    }

    fun startBattle() = viewModelScope.launch {
        battleMutex.withLock {
            val player = repository.playerOnce()
            val enemyLevel = player.level
            val enemies = List(3) {
                val hp = 25 * enemyLevel
                EnemyState(enemyLevel, hp, hp, 4 * enemyLevel, enemyLevel, 2 * enemyLevel)
            }
            val playerInterval = attackInterval(player.speed)
            val enemyInterval = attackInterval(enemies.first().speed)
            val battle = BattleState(enemies, playerHealth = player.maxHealth, playerRemainingMs = playerInterval, enemyRemainingMs = enemyInterval)
            _state.update { it.copy(player = player, battle = battle, notice = null) }
            repository.commitBattle(gson.toJson(battle), battle.playerHealth)
            _state.update { it.copy(player = player.copy(currentHealth = player.maxHealth)) }
        }
        launchBattleClock()
    }

    fun selectEnemy(index: Int) {
        val current = _state.value.battle ?: return
        if (index == current.enemyIndex) _state.update { it.copy(battle = current.copy(selectedEnemy = index)) }
    }

    fun useSpecial() = viewModelScope.launch {
        battleMutex.withLock {
            val battle = _state.value.battle ?: return@withLock
            if (!battle.running || battle.paused || battle.specialCharge < 3 || battle.complete) return@withLock
            val enemy = battle.enemies[battle.enemyIndex]
            val damage = CombatEngine.specialAttack(_state.value.player.magic, enemy.defense)
            val next = damageEnemy(battle.copy(specialCharge = 0), damage)
            commitBattle(next, next.experienceEarned - battle.experienceEarned)
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
                ),
                attack.damage,
            )
            changed = true
        }
        if (battle.enemyRemainingMs <= 0 && !battle.complete) {
            val enemy = battle.enemies[battle.enemyIndex]
            val damage = CombatEngine.normalAttack(enemy.attack, _state.value.player.defense, 0).damage
            val hp = (battle.playerHealth - damage).coerceAtLeast(0)
            battle = battle.copy(playerHealth = hp, enemyRemainingMs = attackInterval(enemy.speed))
            if (hp == 0) battle = battle.copy(running = false, complete = true, won = false)
            changed = true
        }
        _state.update { it.copy(battle = battle) }
        if (changed) commitBattle(battle, battle.experienceEarned - previousExperience)
        else if (now % 1000L < 100L) commitBattle(battle)
        battle.complete
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
