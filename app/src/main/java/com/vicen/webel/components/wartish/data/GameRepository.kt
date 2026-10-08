package com.vicen.webel.components.wartish.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

enum class EquipmentUpgradeOutcome {
    SUCCESS,
    FAILURE,
    INSUFFICIENT_MATERIALS,
}

data class EquipmentUpgradeAttempt(
    val outcome: EquipmentUpgradeOutcome,
    val player: PlayerEntity,
)

class GameRepository(private val database: GameDatabase) {
    private val dao = database.gameDao()
    val player: Flow<PlayerEntity?> = dao.observePlayer()

    suspend fun initialize() {
        database.withTransaction {
            if (dao.player() == null) dao.savePlayer(PlayerEntity())
        }
    }

    suspend fun playerOnce(): PlayerEntity = dao.player() ?: PlayerEntity().also { dao.savePlayer(it) }

    suspend fun update(transform: (PlayerEntity) -> PlayerEntity): PlayerEntity = database.withTransaction {
        transform(dao.player() ?: PlayerEntity()).also { dao.savePlayer(it) }
    }

    suspend fun collect(rewards: Map<Material, Int>) = update { player ->
        rewards.entries.fold(player) { current, (material, quantity) ->
            current.withAmount(material, current.amount(material) + quantity)
        }
    }

    suspend fun commitCollection(rewards: Map<Material, Int>, state: String): PlayerEntity = database.withTransaction {
        val current = dao.player() ?: PlayerEntity()
        val updated = rewards.entries.fold(current) { player, (material, quantity) ->
            if (quantity > 0) player.withAmount(material, player.amount(material) + quantity) else player
        }
        dao.savePlayer(updated)
        dao.saveSession(GameSessionEntity("collection", state))
        updated
    }

    suspend fun commitProcessing(
        rewards: Map<Material, Int>,
        consume: Material?,
        state: String,
        consumeCount: Int = 1,
    ): PlayerEntity? = database.withTransaction {
        var player = dao.player() ?: PlayerEntity()
        rewards.forEach { (material, quantity) ->
            player = player.withAmount(material, player.amount(material) + quantity)
        }
        if (consume != null) {
            if (consumeCount < 0 || player.amount(consume) < consumeCount) return@withTransaction null
            player = player.withAmount(consume, player.amount(consume) - consumeCount)
        }
        dao.savePlayer(player)
        dao.saveSession(GameSessionEntity("processing", state))
        player
    }

    suspend fun commitBattle(
        state: String,
        health: Int,
        experience: Int = 0,
    ) = database.withTransaction {
        var player = dao.player() ?: PlayerEntity()
        var level = player.level
        var remaining = player.experience + experience
        while (remaining >= 200 * level) {
            remaining -= 200 * level
            level++
        }
        player = player.copy(
            level = level,
            experience = remaining,
            currentHealth = minOf(health, 70 * level + 5 * player.helmet),
        )
        dao.savePlayer(player)
        dao.saveSession(GameSessionEntity("combat", state))
    }

    suspend fun commitSuture(state: String, health: Int): PlayerEntity? = database.withTransaction {
        val session = dao.session("combat") ?: return@withTransaction null
        val treatmentAlreadyUsed = runCatching {
            JSONObject(session.payload).optBoolean("treatmentUsed", false)
        }.getOrDefault(false)
        if (treatmentAlreadyUsed) return@withTransaction null
        val player = dao.player() ?: PlayerEntity()
        val updated = player.copy(currentHealth = health.coerceIn(0, player.maxHealth))
        dao.savePlayer(updated)
        dao.saveSession(GameSessionEntity("combat", state))
        updated
    }

    suspend fun refine(material: Material, quantity: Int) = update { player ->
        val refined = requireNotNull(material.refined)
        player.withAmount(refined, player.amount(refined) + quantity)
    }

    suspend fun upgrade(equipment: Equipment, fails: Boolean): EquipmentUpgradeAttempt = database.withTransaction {
        val player = dao.player() ?: PlayerEntity()
        val nextLevel = player.equipmentLevel(equipment) + 1
        val costs = buildMap {
            put(equipment.primary, 2 * nextLevel)
            equipment.secondary.forEach { put(it, nextLevel) }
        }
        if (costs.any { (material, cost) -> player.amount(material) < cost }) {
            return@withTransaction EquipmentUpgradeAttempt(EquipmentUpgradeOutcome.INSUFFICIENT_MATERIALS, player)
        }
        var updated = player
        costs.forEach { (material, cost) -> updated = updated.withAmount(material, updated.amount(material) - cost) }
        if (!fails) updated = updated.withEquipmentLevel(equipment, nextLevel)
        dao.savePlayer(updated)
        EquipmentUpgradeAttempt(
            outcome = if (fails) EquipmentUpgradeOutcome.FAILURE else EquipmentUpgradeOutcome.SUCCESS,
            player = updated,
        )
    }

    suspend fun awardExperience(experience: Int) = update { player ->
        var level = player.level
        var remaining = player.experience + experience
        while (remaining >= 200 * level) {
            remaining -= 200 * level
            level++
        }
        player.copy(level = level, experience = remaining, currentHealth = minOf(player.maxHealth, player.currentHealth))
    }

    suspend fun session(key: String): String? = dao.session(key)?.payload
    suspend fun saveSession(key: String, payload: String) = dao.saveSession(GameSessionEntity(key, payload))
    suspend fun deleteSession(key: String) = dao.deleteSession(key)
}
