package com.HiitHomeTimer.repository.localdata.entities

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.DateFormat

@Parcelize
//, indices = [Index(value = ["sessionId"], unique = true)]
@Entity(tableName = "session_table", indices = [Index(value = ["name", "dateCreated","sessionId"], unique = true)])
data class Session(
    var name: String,
    val dateCreated: Long? = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0L
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(dateCreated)
}
