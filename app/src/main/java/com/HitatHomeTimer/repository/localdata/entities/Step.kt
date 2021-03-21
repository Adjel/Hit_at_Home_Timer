package com.HitatHomeTimer.repository.localdata.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "step_table", indices = [Index(value = ["sessionOwnerId","timesNumber","stepId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            // deferred = true,
            entity = Session::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionOwnerId"],
            deferred = true,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Step(
    val sessionOwnerId: Long = 0L,
    var timesNumber: Long = 1L,
    @PrimaryKey(autoGenerate = true)
    val stepId: Long = 0L
) : Parcelable