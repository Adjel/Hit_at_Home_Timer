package com.HitatHomeTimer.repository.localdata.entities

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.HitatHomeTimer.repository.localdata.SessionDao
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat

@Parcelize
@Entity(tableName = "session_table")
data class Session(
    val name: String,
    val dateCreated: Long? = System.currentTimeMillis(),
    val time: Long = 0,
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(dateCreated)
    val timeFormatted: String
    get() = SimpleDateFormat("mm:ss").format(time)
}
