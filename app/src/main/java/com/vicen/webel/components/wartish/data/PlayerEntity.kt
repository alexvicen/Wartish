package com.vicen.webel.components.wartish.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Vicen",
    val level: Int = 1,
    val experience: Int = 0,
    val helmet: Int = 0,
    val bow: Int = 0,
    val shield: Int = 0,
    val gloves: Int = 0,
    val boots: Int = 0,
    val arrows: Int = 0,
    val rock: Int = 0,
    val wood: Int = 0,
    val iron: Int = 0,
    val gold: Int = 0,
    val gem: Int = 0,
    val stone: Int = 0,
    val boards: Int = 0,
    val ironIngots: Int = 0,
    val goldIngots: Int = 0,
    val cutGems: Int = 0,
    val currentHealth: Int = 70,
) {
    val maxHealth get() = 70 * level + 5 * helmet
    val attack get() = 3 * level + 2 * bow + arrows
    val magic get() = 3 * level + 2 * gloves
    val defense get() = 3 * level + 2 * shield
    val speed get() = 3 * level + 2 * boots + gloves
    val critical get() = minOf(30, 5 + bow + gloves + arrows)

    fun amount(item: Material): Int = when (item) {
        Material.ROCK -> rock
        Material.WOOD -> wood
        Material.IRON -> iron
        Material.GOLD -> gold
        Material.GEM -> gem
        Material.STONE -> stone
        Material.BOARDS -> boards
        Material.IRON_INGOT -> ironIngots
        Material.GOLD_INGOT -> goldIngots
        Material.CUT_GEM -> cutGems
    }

    fun withAmount(item: Material, value: Int): PlayerEntity = when (item) {
        Material.ROCK -> copy(rock = value)
        Material.WOOD -> copy(wood = value)
        Material.IRON -> copy(iron = value)
        Material.GOLD -> copy(gold = value)
        Material.GEM -> copy(gem = value)
        Material.STONE -> copy(stone = value)
        Material.BOARDS -> copy(boards = value)
        Material.IRON_INGOT -> copy(ironIngots = value)
        Material.GOLD_INGOT -> copy(goldIngots = value)
        Material.CUT_GEM -> copy(cutGems = value)
    }
}

enum class Material(val label: String, val symbol: String) {
    ROCK("Roca", "🪨"),
    WOOD("Madera", "🪵"),
    IRON("Hierro", "⚙️"),
    GOLD("Oro", "🟡"),
    GEM("Gema", "💎"),
    STONE("Piedra", "⬛"),
    BOARDS("Tablones", "🪚"),
    IRON_INGOT("Lingote de hierro", "🔩"),
    GOLD_INGOT("Lingote de oro", "🟨"),
    CUT_GEM("Gema tallada", "💠");

    val refined: Material? get() = when (this) {
        ROCK -> STONE
        WOOD -> BOARDS
        IRON -> IRON_INGOT
        GOLD -> GOLD_INGOT
        GEM -> CUT_GEM
        else -> null
    }
}

enum class Equipment(val label: String, val icon: String, val primary: Material, val secondary: List<Material>) {
    HELMET("Casco", "🪖", Material.IRON_INGOT, listOf(Material.STONE)),
    BOW("Arco", "🏹", Material.BOARDS, listOf(Material.IRON_INGOT)),
    SHIELD("Escudo", "🛡️", Material.IRON_INGOT, listOf(Material.STONE)),
    GLOVES("Guantes", "🧤", Material.CUT_GEM, listOf(Material.BOARDS)),
    BOOTS("Botas", "🥾", Material.BOARDS, listOf(Material.STONE)),
    ARROWS("Flechas", "🏹", Material.IRON_INGOT, listOf(Material.GOLD_INGOT)),
}

fun PlayerEntity.equipmentLevel(item: Equipment): Int = when (item) {
    Equipment.HELMET -> helmet
    Equipment.BOW -> bow
    Equipment.SHIELD -> shield
    Equipment.GLOVES -> gloves
    Equipment.BOOTS -> boots
    Equipment.ARROWS -> arrows
}

fun PlayerEntity.withEquipmentLevel(item: Equipment, value: Int): PlayerEntity = when (item) {
    Equipment.HELMET -> copy(helmet = value)
    Equipment.BOW -> copy(bow = value)
    Equipment.SHIELD -> copy(shield = value)
    Equipment.GLOVES -> copy(gloves = value)
    Equipment.BOOTS -> copy(boots = value)
    Equipment.ARROWS -> copy(arrows = value)
}
