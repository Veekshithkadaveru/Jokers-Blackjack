package app.krafted.jokersblackjack.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_records")
data class SessionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val difficulty: String,
    val score: Int,
    val maxScore: Int = 15,
    val savedAt: Long = System.currentTimeMillis()
)
