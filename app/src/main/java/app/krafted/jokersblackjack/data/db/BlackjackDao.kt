package app.krafted.jokersblackjack.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlackjackDao {
    @Insert
    suspend fun insertSession(record: SessionRecord)

    @Query("SELECT MAX(score) FROM session_records WHERE difficulty = :difficulty")
    suspend fun getBestScore(difficulty: String): Int?

    @Query("SELECT * FROM session_records WHERE difficulty = :difficulty ORDER BY score DESC")
    fun getAllSessions(difficulty: String): Flow<List<SessionRecord>>

    @Query("SELECT MAX(score) FROM session_records WHERE difficulty = :difficulty")
    fun getBestScoreFlow(difficulty: String): Flow<Int?>
}
