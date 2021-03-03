package com.HitatHomeTimer.repository.localdata.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SessionWithStepsAndExercises(
    @Embedded val session: Session,
    @Relation(
        entity = Step::class,
        parentColumn = "sessionId",
        entityColumn = "sessionOwnerId"
    )
    val stepList:
    @RawValue
    List<StepWithExercises>
) : Parcelable
