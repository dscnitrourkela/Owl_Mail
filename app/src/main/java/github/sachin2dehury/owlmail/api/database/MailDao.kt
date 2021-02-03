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

    @Query("SELECT * FROM mails WHERE conversationId = :conversationId ORDER BY id DESC")
    fun getConversationMails(conversationId: String): Flow<List<Mail>>

    @Query("SELECT * FROM mails WHERE id = :id")
    fun getMailItem(id: String): Flow<Mail>

    @Query("SELECT id FROM mails WHERE conversationId = :conversationId ORDER BY id DESC")
    fun getMailsId(conversationId: String): Flow<List<String>>

    @Query("DELETE FROM mails")
    suspend fun deleteAllMails()

    @Query("UPDATE mails SET parsedBody = :parsedBody WHERE id = :id")
    suspend fun updateMail(parsedBody: String, id: String)

    @Query("UPDATE mails SET flag = TRIM('u',flag) WHERE id = :id")
    suspend fun markAsRead(id: String)
}