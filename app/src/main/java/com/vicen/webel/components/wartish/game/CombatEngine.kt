package com.vicen.webel.components.wartish.game

import kotlin.random.Random

fun interface GameClock {
    fun nowMillis(): Long
}

data class AttackResult(val damage: Int, val critical: Boolean)

object CombatEngine {
    fun attackInterval(speed: Int): Long = maxOf(600L, (3000.0 / (1.0 + speed / 10.0)).toLong())

    fun normalAttack(attack: Int, defense: Int, criticalPercent: Int, random: Random = Random.Default): AttackResult {
        val critical = random.nextInt(100) < criticalPercent
        val baseDamage = maxOf(1, attack - defense)
        return AttackResult(if (critical) baseDamage * 2 else baseDamage, critical)
    }

    fun specialAttack(magic: Int, defense: Int): Int = maxOf(1, 2 * magic - defense)

    fun remainingAfter(remainingMillis: Long, elapsedMillis: Long): Long =
        (remainingMillis - elapsedMillis.coerceAtLeast(0)).coerceAtLeast(0)
}
