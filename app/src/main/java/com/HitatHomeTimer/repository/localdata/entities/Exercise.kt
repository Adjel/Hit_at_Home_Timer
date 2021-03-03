package com.HitatHomeTimer.repository.localdata.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat


@Parcelize
@Entity(
    tableName = "exercise_table",
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
    val name: String,
    val stepOwnerId: Long = 0,
    val timer: Long = 30000,
    val position: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Long = 0
) : Parcelable {
    val timerFormatted: String
        get() = SimpleDateFormat("mm:ss").format(timer)
}
