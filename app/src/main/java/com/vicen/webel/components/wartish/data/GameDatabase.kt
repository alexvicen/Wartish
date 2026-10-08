package com.vicen.webel.components.wartish.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey val key: String,
    val payload: String,
    val updatedAt: Long = System.currentTimeMillis(),
)

@Dao
interface GameDao {
    @Query("SELECT * FROM player WHERE id = 1") fun observePlayer(): Flow<PlayerEntity?>
    @Query("SELECT * FROM player WHERE id = 1") suspend fun player(): PlayerEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun savePlayer(player: PlayerEntity)
    @Query("SELECT * FROM game_sessions WHERE `key` = :key") suspend fun session(key: String): GameSessionEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveSession(session: GameSessionEntity)
    @Query("DELETE FROM game_sessions WHERE `key` = :key") suspend fun deleteSession(key: String)
}

@Database(entities = [PlayerEntity::class, GameSessionEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile private var instance: GameDatabase? = null
        fun get(context: Context): GameDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, "wartish.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build().also { instance = it }
        }
    }
}
