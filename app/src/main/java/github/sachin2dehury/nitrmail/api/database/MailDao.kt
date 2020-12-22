package github.sachin2dehury.nitrmail.api.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import github.sachin2dehury.nitrmail.api.data.mail.Mail
import kotlinx.coroutines.flow.Flow

@Dao
interface MailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMail(mail: Mail)

    @Query("SELECT * FROM mails WHERE box= :box ORDER BY time DESC")
    fun getMails(box: String): Flow<List<Mail>>

    @Query("DELETE FROM mails WHERE id = :mailId")
    suspend fun deleteMailById(mailId: String)
}