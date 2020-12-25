package github.sachin2dehury.nitrmail.api.databases.parsedmails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import github.sachin2dehury.nitrmail.api.data.parsedmails.ParsedMail
import kotlinx.coroutines.flow.Flow

@Dao
interface ParsedMailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMail(mail: ParsedMail)

    @Query("SELECT * FROM parsed WHERE id= :id")
    fun getMailItem(id: String): Flow<ParsedMail>
}