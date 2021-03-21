package com.HitatHomeTimer.repository.localdata.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Step
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class StepWithExercises(
    @Embedded var step: Step,
    @Relation(
        parentColumn = "stepId",
        entityColumn = "stepOwnerId"
    )
    var exerciseLists:
    List<Exercise>
) : Parcelable
