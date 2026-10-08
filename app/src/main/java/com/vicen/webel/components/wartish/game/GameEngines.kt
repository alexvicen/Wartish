package com.vicen.webel.components.wartish.game

import com.vicen.webel.components.wartish.data.Material
import kotlin.random.Random

data class CellSwapResult(
    val state: CollectionState,
    val rewards: Map<Material, Int> = emptyMap(),
    val accepted: Boolean = false,
    val animation: List<CollectionCascadeStep> = emptyList(),
)

data class CollectionRewardPopup(
    val anchorIndex: Int,
    val material: Material,
    val amount: Int,
    val multiplier: Int = 1,
)

data class CollectionCascadeStep(
    val matchedBoard: List<Int>,
    val matchedCells: Set<Int>,
    val fallenBoard: List<Int>,
    /** Destination cell index to the row the tile falls from. New tiles start above row zero. */
    val sourceRows: Map<Int, Int>,
    val popups: List<CollectionRewardPopup>,
)

data class CollectionTurnAnimation(
    val id: Long,
    val steps: List<CollectionCascadeStep>,
)

data class CollectionState(
    val board: List<Int>,
    val movesLeft: Int = Int.MAX_VALUE,
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
        return swap(state, selected, index, random)
    }

    fun swap(state: CollectionState, first: Int, second: Int, random: Random = Random.Default): CellSwapResult {
        val clearedSelection = state.copy(selected = null)
        if (state.complete || first !in state.board.indices || second !in state.board.indices || !adjacent(first, second)) {
            return CellSwapResult(clearedSelection)
        }
        val swapped = state.board.toMutableList().also {
            val value = it[first]
            it[first] = it[second]
            it[second] = value
        }
        if (matches(swapped).isEmpty()) return CellSwapResult(clearedSelection)

        val rewards = mutableMapOf<Material, Int>()
        val animation = mutableListOf<CollectionCascadeStep>()
        var board: List<Int> = swapped
        var cascadeMultiplier = 1
        do {
            val matched = matches(board)
            val popups = createPopups(board, matched, cascadeMultiplier)
            val cleared = board.toMutableList()
            matched.forEach { cell ->
                cleared[cell] = -1
            }
            popups.forEach { popup ->
                rewards[popup.material] = (rewards[popup.material] ?: 0) + popup.amount
            }
            val gravity = collapse(cleared, random)
            animation += CollectionCascadeStep(
                matchedBoard = board,
                matchedCells = matched,
                fallenBoard = gravity.board,
                sourceRows = gravity.sourceRows,
                popups = popups,
            )
            board = gravity.board
            cascadeMultiplier++
        } while (matches(board).isNotEmpty())
        return CellSwapResult(
            state = CollectionState(board, Int.MAX_VALUE),
            rewards = rewards,
            accepted = true,
            animation = animation,
        )
    }

    fun hasAvailableMove(board: List<Int>): Boolean = hasMove(board)
    fun hasMatches(board: List<Int>): Boolean = matches(board).isNotEmpty()
    fun reshuffle(state: CollectionState, random: Random = Random.Default): CollectionState =
        state.copy(board = newGame(random).board, movesLeft = Int.MAX_VALUE, selected = null, complete = false)

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

    private data class GravityResult(val board: List<Int>, val sourceRows: Map<Int, Int>)

    private fun collapse(board: List<Int>, random: Random): GravityResult {
        val result = board.toMutableList()
        val sourceRows = mutableMapOf<Int, Int>()
        for (column in 0 until SIZE) {
            val existing = (SIZE - 1 downTo 0).mapNotNull { row ->
                result[row * SIZE + column].takeIf { it >= 0 }?.let { row to it }
            }
            val emptyRows = SIZE - existing.size
            for ((offset, source) in existing.withIndex()) {
                val destinationRow = SIZE - 1 - offset
                val destinationIndex = destinationRow * SIZE + column
                result[destinationIndex] = source.second
                sourceRows[destinationIndex] = source.first
            }
            for (row in 0 until emptyRows) {
                val destinationIndex = row * SIZE + column
                result[destinationIndex] = random.nextInt(5)
                sourceRows[destinationIndex] = row - emptyRows
            }
        }
        return GravityResult(result, sourceRows)
    }

    private fun createPopups(
        board: List<Int>,
        matched: Set<Int>,
        cascadeMultiplier: Int,
    ): List<CollectionRewardPopup> {
        val remaining = matched.toMutableSet()
        val popups = mutableListOf<CollectionRewardPopup>()
        while (remaining.isNotEmpty()) {
            val origin = remaining.minOrNull() ?: break
            val materialIndex = board[origin]
            val cells = mutableListOf<Int>()
            val pending = ArrayDeque<Int>()
            remaining.remove(origin)
            pending.addLast(origin)

            while (pending.isNotEmpty()) {
                val cell = pending.removeFirst()
                cells += cell
                val row = cell / SIZE
                val column = cell % SIZE
                val neighbors = buildList {
                    if (row > 0) add(cell - SIZE)
                    if (row < SIZE - 1) add(cell + SIZE)
                    if (column > 0) add(cell - 1)
                    if (column < SIZE - 1) add(cell + 1)
                }
                neighbors.forEach { neighbor ->
                    if (neighbor in remaining && board[neighbor] == materialIndex) {
                        remaining.remove(neighbor)
                        pending.addLast(neighbor)
                    }
                }
            }

            val material = Material.entries.getOrNull(materialIndex) ?: continue
            val multiplier = cascadeMultiplier + (cells.size - 3).coerceAtLeast(0)
            val averageRow = cells.map { it / SIZE }.average().toInt()
            val averageColumn = cells.map { it % SIZE }.average().toInt()
            popups += CollectionRewardPopup(
                anchorIndex = averageRow * SIZE + averageColumn,
                material = material,
                amount = cells.size * multiplier,
                multiplier = multiplier,
            )
        }
        return popups
    }
}

data class FallingState(
    val cells: List<Int> = List(FallingEngine.COLUMNS * FallingEngine.ROWS) { -1 },
    val material: Int = -1,
    val shapeVariant: Int = FallingEngine.SHAPE_I,
    val row: Int = 0,
    val column: Int = 2,
    val rotation: Int = 0,
    val running: Boolean = false,
    val complete: Boolean = false,
    val started: Boolean = false,
    val pieceVersion: Int = 2,
    val clearSequence: Long = 0L,
    val pendingClear: FallingClearAnimation? = null,
)

data class FallingClearCell(val index: Int, val material: Int)

data class FallingClearAnimation(val id: Long, val cells: List<FallingClearCell>)

data class LockResult(val state: FallingState)

object FallingEngine {
    const val COLUMNS = 10
    const val ROWS = 20
    const val SHAPE_COUNT = 7
    const val SHAPE_I = 0
    const val SHAPE_O = 1
    const val SHAPE_L = 2
    const val SHAPE_Z = 3
    const val SHAPE_T = 4
    const val SHAPE_MIRRORED_L = 5
    const val SHAPE_MIRRORED_Z = 6
    private val emptyBoard = List(COLUMNS * ROWS) { -1 }

    // Coordinates are normalized to the top-left of each tetromino.
    private val baseShapes = mapOf(
        SHAPE_I to listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3),
        SHAPE_O to listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1),
        SHAPE_L to listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1),
        SHAPE_Z to listOf(0 to 1, 0 to 2, 1 to 0, 1 to 1),
        SHAPE_T to listOf(0 to 0, 0 to 1, 0 to 2, 1 to 1),
        SHAPE_MIRRORED_L to listOf(0 to 1, 1 to 1, 2 to 0, 2 to 1),
        SHAPE_MIRRORED_Z to listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2),
    )

    fun spawnColumn(shapeVariant: Int): Int =
        ((COLUMNS - (baseShapes[shapeVariant]?.maxOf { it.second + 1 } ?: 1)) / 2).coerceAtLeast(0)

    fun activeCellIndices(state: FallingState): Set<Int> =
        if (state.material in Material.entries.take(5).indices && state.shapeVariant in baseShapes && canPlace(state.cells, state.shapeVariant, state.row, state.column, state.rotation)) {
            shape(state.shapeVariant, state.rotation).mapTo(mutableSetOf()) { (row, column) ->
                (state.row + row) * COLUMNS + state.column + column
            }
        } else emptySet()

    fun move(state: FallingState, delta: Int): FallingState {
        if (!state.running || state.complete || state.shapeVariant !in baseShapes) return state
        val column = state.column + delta
        return if (canPlace(state.cells, state.shapeVariant, state.row, column, state.rotation)) state.copy(column = column) else state
    }

    fun rotate(state: FallingState): FallingState {
        if (!state.running || state.complete || state.shapeVariant !in baseShapes) return state
        val nextRotation = (state.rotation + 1) % 4
        val kicks = listOf(0 to 0, 0 to -1, 0 to 1, 0 to -2, 0 to 2, -1 to 0, -1 to -1, -1 to 1, -2 to 0)
        val placement = kicks.firstOrNull { (rowOffset, columnOffset) ->
            canPlace(
                state.cells,
                state.shapeVariant,
                state.row + rowOffset,
                state.column + columnOffset,
                nextRotation,
            )
        } ?: return state
        return state.copy(row = state.row + placement.first, column = state.column + placement.second, rotation = nextRotation)
    }

    fun tick(state: FallingState): LockResult {
        if (!state.running || state.complete || state.material < 0) return LockResult(state)
        if (canPlace(state.cells, state.shapeVariant, state.row + 1, state.column, state.rotation)) {
            return LockResult(state.copy(row = state.row + 1))
        }
        return lock(state)
    }

    fun drop(state: FallingState): LockResult {
        if (!state.running || state.complete || state.material < 0) return LockResult(state)
        var row = state.row
        while (canPlace(state.cells, state.shapeVariant, row + 1, state.column, state.rotation)) row++
        return lock(state.copy(row = row))
    }

    private fun lock(state: FallingState): LockResult {
        val cells = state.cells.toMutableList()
        val indices = shape(state.shapeVariant, state.rotation).map { (row, column) ->
            (state.row + row) * COLUMNS + state.column + column
        }
        if (indices.any { it !in cells.indices || cells[it] >= 0 }) return LockResult(gameOver(state))
        indices.forEach { cells[it] = state.material }

        val fullRows = (0 until ROWS).filter { row ->
            (0 until COLUMNS).all { column -> cells[row * COLUMNS + column] >= 0 }
        }
        val clearedCells = fullRows.flatMap { row ->
            (0 until COLUMNS).map { column ->
                val index = row * COLUMNS + column
                FallingClearCell(index, cells[index])
            }
        }
        if (fullRows.isNotEmpty()) clearRows(cells, fullRows.toSet())

        val hasReachedTop = (0 until COLUMNS).any { cells[it] >= 0 }
        val nextClearSequence = state.clearSequence + if (clearedCells.isNotEmpty()) 1 else 0
        val pendingClear = clearedCells.takeIf { it.isNotEmpty() }?.let { FallingClearAnimation(nextClearSequence, it) }
        val nextState = state.copy(
            cells = if (hasReachedTop && pendingClear == null) emptyBoard else cells,
            material = -1,
            row = 0,
            column = 2,
            rotation = 0,
            shapeVariant = SHAPE_I,
            running = false,
            complete = hasReachedTop,
            started = true,
            clearSequence = nextClearSequence,
            pendingClear = pendingClear,
        )
        return LockResult(nextState)
    }

    fun canPlace(cells: List<Int>, shapeVariant: Int, row: Int, column: Int, rotation: Int = 0): Boolean {
        if (cells.size != COLUMNS * ROWS || shapeVariant !in baseShapes) return false
        return shape(shapeVariant, rotation).all { (pieceRow, pieceColumn) ->
            val boardRow = row + pieceRow
            val boardColumn = column + pieceColumn
            boardRow in 0 until ROWS && boardColumn in 0 until COLUMNS && cells[boardRow * COLUMNS + boardColumn] < 0
        }
    }

    private fun shape(material: Int, rotation: Int): List<Pair<Int, Int>> {
        var result = baseShapes[material] ?: return emptyList()
        repeat(rotation.mod(4)) {
            result = result.map { (row, column) -> column to -row }
            val minRow = result.minOf { it.first }
            val minColumn = result.minOf { it.second }
            result = result.map { (row, column) -> (row - minRow) to (column - minColumn) }
        }
        return result
    }

    private fun clearRows(cells: MutableList<Int>, clearedRows: Set<Int>) {
        val remainingRows = (0 until ROWS).filterNot { it in clearedRows }
            .map { row -> (0 until COLUMNS).map { column -> cells[row * COLUMNS + column] } }
        val emptyRows = List(clearedRows.size) { List(COLUMNS) { -1 } }
        val packedRows = emptyRows + remainingRows
        packedRows.forEachIndexed { row, values ->
            values.forEachIndexed { column, value -> cells[row * COLUMNS + column] = value }
        }
    }

    private fun gameOver(state: FallingState) = state.copy(
        cells = emptyBoard,
        material = -1,
        row = 0,
        column = 2,
        rotation = 0,
        shapeVariant = SHAPE_I,
        running = false,
        complete = true,
        started = true,
    )
}
