package github.sachin2dehury.owlmail.api.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import github.sachin2dehury.owlmail.api.data.ParsedMail
import kotlinx.coroutines.flow.Flow

@Dao
interface ParsedMailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMail(parsedMail: ParsedMail)

    @Query("SELECT * FROM parsed_mails WHERE conversationId = :conversationId ORDER BY time DESC")
    fun getConversationMails(conversationId: Int): Flow<List<ParsedMail>>

    @Query("DELETE FROM parsed_mails")
    suspend fun deleteAllMails()
}