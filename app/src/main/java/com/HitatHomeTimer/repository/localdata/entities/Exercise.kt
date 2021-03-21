package com.HitatHomeTimer.repository.localdata.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat


@Parcelize
@Entity(
    tableName = "exercise_table", indices = [Index(value = ["name", "stepOwnerId", "timer", "position", "exerciseId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            // deferred = true,
            entity = Step::class,
            parentColumns = ["stepId"],
            childColumns = ["stepOwnerId"],
            deferred = true,
            onUpdate = CASCADE,
            onDelete = CASCADE
        )]
)
data class Exercise(
    var name: String,
    val stepOwnerId: Long = 0L,
    var timer: Long = 30000L,
    val position: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Long = 0L
) : Parcelable {
    val timerFormatted: String
        get() = SimpleDateFormat("mm:ss").format(timer)
}
