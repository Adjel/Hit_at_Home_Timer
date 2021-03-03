package com.HitatHomeTimer.repository.localdata.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Step

data class StepWithExercises(
    @Embedded val step: Step,
    @Relation(
        parentColumn = "stepId",
        entityColumn = "stepOwnerId"
    )
    val exerciseLists: List<Exercise>
)
