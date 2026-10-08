package com.vicen.webel.components.wartish

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vicen.webel.components.wartish.data.GameDatabase
import com.vicen.webel.components.wartish.data.GameRepository
import com.vicen.webel.components.wartish.data.Material
import com.vicen.webel.components.wartish.data.PlayerEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameRepositoryTest {
    private lateinit var database: GameDatabase
    private lateinit var repository: GameRepository

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GameRepository(database)
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun processingCannotConsumeMoreRawMaterialsThanTheInventoryContains() = runBlocking {
        database.gameDao().savePlayer(PlayerEntity(rock = 1))

        val first = repository.commitProcessing(emptyMap(), Material.ROCK, "piece-1")
        val second = repository.commitProcessing(emptyMap(), Material.ROCK, "piece-2")

        assertNotNull(first)
        assertNull(second)
        assertEquals(0, database.gameDao().player()?.rock)
        assertEquals("piece-1", repository.session("processing"))
    }

    @Test
    fun collectionRewardAndBoardSnapshotAreCommittedTogether() = runBlocking {
        repository.commitCollection(mapOf(Material.GEM to 5), "next-board")

        assertEquals(5, database.gameDao().player()?.gem)
        assertEquals("next-board", repository.session("collection"))
    }
}
