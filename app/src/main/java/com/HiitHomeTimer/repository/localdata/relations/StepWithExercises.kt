package com.HiitHomeTimer.repository.localdata.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.HiitHomeTimer.repository.localdata.entities.Exercise
import com.HiitHomeTimer.repository.localdata.entities.Step
import kotlinx.parcelize.Parcelize

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
