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

    @Query("SELECT * FROM mails WHERE box = :box ORDER BY time DESC")
    fun getMails(box: Int): Flow<List<Mail>>

    @Query("SELECT * FROM mails WHERE conversationId = :conversationId ORDER BY id DESC")
    fun getConversationMails(conversationId: Int): Flow<List<Mail>>

    @Query("SELECT id FROM mails WHERE conversationId = :conversationId ORDER BY id DESC")
    fun getMailsId(conversationId: Int): Flow<List<Int>>

    @Query("DELETE FROM mails")
    suspend fun deleteAllMails()

    @Query("UPDATE mails SET parsedBody = :parsedBody WHERE id = :id")
    suspend fun updateMail(parsedBody: String, id: Int)

    @Query("UPDATE mails SET flag = :flag WHERE id = :id")
    suspend fun markAsRead(flag: String, id: Int)

    @Query("SELECT * FROM mails WHERE body LIKE '%' || :query || '%' OR subject LIKE '%' || :query || '%' ORDER BY time DESC")
    fun searchMails(query: String): Flow<List<Mail>>
}