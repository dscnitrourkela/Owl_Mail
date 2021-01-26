package github.sachin2dehury.owlmail.api.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import github.sachin2dehury.owlmail.api.data.Mail
import kotlinx.coroutines.flow.Flow

@Dao
interface MailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMail(mail: Mail)

    @Query("SELECT * FROM mails WHERE box = :box ORDER BY time DESC")//GROUP BY conversationId
    fun getMails(box: String): Flow<List<Mail>>

    @Query("SELECT * FROM mails WHERE conversationId = :conversationId ORDER BY id ASC")
    fun getConversationMails(conversationId: String): Flow<List<Mail>>

    @Query("DELETE FROM mails")
    suspend fun deleteAllMails()

    @Query("SELECT * FROM mails WHERE id = :id")
    fun getMailItem(id: String): Flow<Mail>

    @Query("UPDATE mails SET parsedBody = :parsedBody WHERE id = :id")
    suspend fun updateMail(parsedBody: String, id: String)
}