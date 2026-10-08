package com.vicen.webel.components.wartish

import com.vicen.webel.components.wartish.data.Material
import com.vicen.webel.components.wartish.game.CollectionEngine
import com.vicen.webel.components.wartish.game.CollectionState
import com.vicen.webel.components.wartish.game.CombatEngine
import com.vicen.webel.components.wartish.game.FallingEngine
import com.vicen.webel.components.wartish.game.FallingState
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEnginesTest {
    @Test
    fun newCollectionBoardHasNoAutomaticMatchAndHasAPlayableMove() {
        val game = CollectionEngine.newGame(Random(8))
        assertEquals(64, game.board.size)
        assertFalse(CollectionEngine.hasMatches(game.board))
        assertTrue(CollectionEngine.hasAvailableMove(game.board))
    }

    @Test
    fun collectionRewardsEachMatchedCellOnceAndSpendsOneMove() {
        val board = MutableList(64) { index -> (index / 8 + index % 8 * 2) % 5 }
        board[8] = 1
        board[9] = 0
        board[1] = 1
        board[17] = 1
        board[10] = 1
        board[11] = 1
        val state = CollectionState(board)
        val result = CollectionEngine.select(CollectionEngine.select(state, 8).state, 9, Random(5))
        assertTrue(result.accepted)
        assertEquals(19, result.state.movesLeft)
        assertTrue(result.rewards.values.sum() >= 5)
    }

    @Test
    fun fallingFourConnectedPiecesRefineOnceAndAreConsumed() {
        val cells = MutableList(72) { -1 }
        cells[11 * 6] = 0
        cells[11 * 6 + 1] = 0
        cells[11 * 6 + 3] = 0
        val falling = FallingState(cells = cells, material = 0, row = 10, column = 2, running = true, started = true)
        val atBottom = FallingEngine.tick(falling).state
        val result = FallingEngine.tick(atBottom)
        assertEquals(1, result.refined[Material.STONE] ?: 0)
        assertTrue(result.state.cells.all { it == -1 })
    }

    @Test
    fun combatUsesSpeedForIntervalsAndCapsFastAttacks() {
        assertEquals(3000L, CombatEngine.attackInterval(0))
        assertEquals(1500L, CombatEngine.attackInterval(10))
        assertEquals(600L, CombatEngine.attackInterval(1000))
    }

    @Test
    fun combatDamageAndTimerAreDeterministicWithInjectedRandomAndElapsedTime() {
        val regular = CombatEngine.normalAttack(10, 4, 0, Random(1))
        assertEquals(6, regular.damage)
        assertFalse(regular.critical)
        val critical = CombatEngine.normalAttack(10, 4, 100, Random(1))
        assertEquals(12, critical.damage)
        assertTrue(critical.critical)
        assertEquals(5, CombatEngine.specialAttack(3, 1))
        assertEquals(0L, CombatEngine.remainingAfter(75, 100))
    }
}
