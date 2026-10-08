package com.vicen.webel.components.wartish.game

import com.vicen.webel.components.wartish.data.Material
import kotlin.random.Random

data class CellSwapResult(
    val state: CollectionState,
    val rewards: Map<Material, Int> = emptyMap(),
    val accepted: Boolean = false,
)

data class CollectionState(
    val board: List<Int>,
    val movesLeft: Int = 20,
    val selected: Int? = null,
    val complete: Boolean = false,
)

object CollectionEngine {
    const val SIZE = 8

    fun newGame(random: Random = Random.Default): CollectionState {
        repeat(500) {
            val board = List(SIZE * SIZE) { random.nextInt(5) }
            if (matches(board).isEmpty() && hasMove(board)) return CollectionState(board)
        }
        val safeBoard = MutableList(SIZE * SIZE) { index -> (index / SIZE + index % SIZE) % 5 }.also { board ->
            // A deterministic, match-free layout with a known valid swap.
            for (row in 0 until SIZE) {
                for (column in 0 until SIZE) board[row * SIZE + column] = (row + column * 2) % 5
            }
            board[0] = 0; board[1] = 1; board[2] = 0
            board[8] = 2; board[9] = 0; board[10] = 0
        }
        return CollectionState(safeBoard)
    }

    fun select(state: CollectionState, index: Int, random: Random = Random.Default): CellSwapResult {
        if (state.complete || index !in 0 until SIZE * SIZE) return CellSwapResult(state)
        val selected = state.selected
        if (selected == null) return CellSwapResult(state.copy(selected = index))
        if (selected == index) return CellSwapResult(state.copy(selected = null))
        if (!adjacent(selected, index)) return CellSwapResult(state.copy(selected = index))
        val swapped = state.board.toMutableList().also {
            val first = it[selected]
            it[selected] = it[index]
            it[index] = first
        }
        if (matches(swapped).isEmpty()) return CellSwapResult(state.copy(selected = null))
        val rewards = mutableMapOf<Material, Int>()
        var board: List<Int> = swapped
        do {
            val matched = matches(board)
            val cleared = board.toMutableList()
            matched.forEach { cell ->
                rewards[Material.entries[board[cell]]] = (rewards[Material.entries[board[cell]]] ?: 0) + 1
                cleared[cell] = -1
            }
            board = collapse(cleared, random)
        } while (matches(board).isNotEmpty())
        val moves = state.movesLeft - 1
        return CellSwapResult(
            state = CollectionState(board, moves, complete = moves <= 0),
            rewards = rewards,
            accepted = true,
        )
    }

    fun hasAvailableMove(board: List<Int>): Boolean = hasMove(board)
    fun hasMatches(board: List<Int>): Boolean = matches(board).isNotEmpty()
    fun reshuffle(state: CollectionState, random: Random = Random.Default): CollectionState =
        newGame(random).copy(movesLeft = state.movesLeft)

    private fun adjacent(a: Int, b: Int): Boolean {
        val ar = a / SIZE; val ac = a % SIZE
        val br = b / SIZE; val bc = b % SIZE
        return kotlin.math.abs(ar - br) + kotlin.math.abs(ac - bc) == 1
    }

    private fun hasMove(board: List<Int>): Boolean {
        for (cell in board.indices) for (neighbor in listOf(cell + 1, cell + SIZE)) {
            if (neighbor !in board.indices || (neighbor == cell + 1 && cell % SIZE == SIZE - 1)) continue
            val copy = board.toMutableList()
            val value = copy[cell]; copy[cell] = copy[neighbor]; copy[neighbor] = value
            if (matches(copy).isNotEmpty()) return true
        }
        return false
    }

    private fun matches(board: List<Int>): Set<Int> {
        val result = mutableSetOf<Int>()
        for (row in 0 until SIZE) {
            var start = 0
            while (start < SIZE) {
                val value = board[row * SIZE + start]
                var end = start + 1
                while (value >= 0 && end < SIZE && board[row * SIZE + end] == value) end++
                if (value >= 0 && end - start >= 3) (start until end).forEach { result += row * SIZE + it }
                start = end
            }
        }
        for (column in 0 until SIZE) {
            var start = 0
            while (start < SIZE) {
                val value = board[start * SIZE + column]
                var end = start + 1
                while (value >= 0 && end < SIZE && board[end * SIZE + column] == value) end++
                if (value >= 0 && end - start >= 3) (start until end).forEach { result += it * SIZE + column }
                start = end
            }
        }
        return result
    }

    private fun collapse(board: List<Int>, random: Random): List<Int> {
        val result = board.toMutableList()
        for (column in 0 until SIZE) {
            val values = (SIZE - 1 downTo 0).mapNotNull { row -> result[row * SIZE + column].takeIf { it >= 0 } }
            for (row in SIZE - 1 downTo 0) {
                val offset = SIZE - 1 - row
                result[row * SIZE + column] = values.getOrNull(offset) ?: random.nextInt(5)
            }
        }
        return result
    }
}

data class FallingState(
    val cells: List<Int> = List(72) { -1 },
    val material: Int = -1,
    val row: Int = 0,
    val column: Int = 2,
    val running: Boolean = false,
    val complete: Boolean = false,
    val started: Boolean = false,
)

data class LockResult(val state: FallingState, val refined: Map<Material, Int> = emptyMap())

object FallingEngine {
    const val COLUMNS = 6
    const val ROWS = 12

    fun move(state: FallingState, delta: Int): FallingState {
        val column = (state.column + delta).coerceIn(0, COLUMNS - 1)
        return if (state.material >= 0 && canPlace(state.cells, state.row, column)) state.copy(column = column) else state
    }

    fun tick(state: FallingState): LockResult {
        if (!state.running || state.complete || state.material < 0) return LockResult(state)
        if (canPlace(state.cells, state.row + 1, state.column)) return LockResult(state.copy(row = state.row + 1))
        return lock(state)
    }

    fun drop(state: FallingState): LockResult {
        if (!state.running || state.complete || state.material < 0) return LockResult(state)
        var row = state.row
        while (canPlace(state.cells, row + 1, state.column)) row++
        return lock(state.copy(row = row))
    }

    private fun lock(state: FallingState): LockResult {
        val cells = state.cells.toMutableList()
        val index = state.row * COLUMNS + state.column
        if (index !in cells.indices || cells[index] >= 0) return LockResult(state.copy(running = false, complete = true))
        cells[index] = state.material
        val rewards = mutableMapOf<Material, Int>()
        var changed: Boolean
        do {
            changed = false
            val groups = groups(cells)
            groups.filter { it.size >= 3 }.forEach { group ->
                val type = Material.entries[cells[group.first()]]
                rewards[type.refined!!] = (rewards[type.refined] ?: 0) + group.size / 3
                group.forEach { cells[it] = -1 }
                changed = true
            }
            if (changed) fall(cells)
        } while (changed)
        return LockResult(FallingState(cells, running = true, started = true), rewards)
    }

    private fun canPlace(cells: List<Int>, row: Int, column: Int): Boolean {
        if (row !in 0 until ROWS || column !in 0 until COLUMNS) return false
        return cells[row * COLUMNS + column] < 0
    }

    private fun groups(cells: List<Int>): List<Set<Int>> {
        val visited = mutableSetOf<Int>()
        val result = mutableListOf<Set<Int>>()
        for (origin in cells.indices) {
            if (cells[origin] < 0 || origin in visited) continue
            val group = mutableSetOf<Int>()
            val queue = ArrayDeque<Int>().also { it.add(origin) }
            visited += origin
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                group += current
                val row = current / COLUMNS; val col = current % COLUMNS
                val neighbors = listOf(
                    if (row > 0) current - COLUMNS else -1,
                    if (row < ROWS - 1) current + COLUMNS else -1,
                    if (col > 0) current - 1 else -1,
                    if (col < COLUMNS - 1) current + 1 else -1,
                )
                neighbors.filter { it >= 0 && it !in visited && cells[it] == cells[current] }.forEach {
                    visited += it; queue.add(it)
                }
            }
            result += group
        }
        return result
    }

    private fun fall(cells: MutableList<Int>) {
        for (column in 0 until COLUMNS) {
            val values = (ROWS - 1 downTo 0).mapNotNull { row -> cells[row * COLUMNS + column].takeIf { it >= 0 } }
            for (row in ROWS - 1 downTo 0) {
                cells[row * COLUMNS + column] = values.getOrNull(ROWS - 1 - row) ?: -1
            }
        }
    }
}
