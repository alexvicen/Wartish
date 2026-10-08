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
    fun collectionRewardsEachMatchedCellOnceAndHasUnlimitedMoves() {
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
        assertEquals(Int.MAX_VALUE, result.state.movesLeft)
        assertFalse(result.state.complete)
        assertTrue(result.rewards.values.sum() >= 5)
    }

    @Test
    fun tetrominoClearsFullRowAndRewardsEveryBlock() {
        val cells = MutableList(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 }
        for (column in 4 until FallingEngine.COLUMNS) cells[19 * FallingEngine.COLUMNS + column] = Material.ROCK.ordinal
        val falling = FallingState(
            cells = cells,
            material = Material.WOOD.ordinal,
            shapeVariant = FallingEngine.SHAPE_I,
            row = 19,
            column = 0,
            running = true,
            started = true,
        )
        val result = FallingEngine.tick(falling)
        assertEquals(10, result.state.pendingClear?.cells?.size)
        assertEquals(4, result.state.pendingClear?.cells?.count { it.material == Material.WOOD.ordinal })
        assertEquals(6, result.state.pendingClear?.cells?.count { it.material == Material.ROCK.ordinal })
        assertTrue(result.state.cells.all { it == -1 })
        assertFalse(result.state.complete)
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
