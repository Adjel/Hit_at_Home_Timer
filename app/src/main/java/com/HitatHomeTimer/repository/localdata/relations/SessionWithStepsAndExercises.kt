package com.HitatHomeTimer.repository.localdata.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.text.SimpleDateFormat

@Parcelize
data class SessionWithStepsAndExercises(
    @Embedded val session: Session,
    @Relation(
        entity = Step::class,
        parentColumn = "sessionId",
        entityColumn = "sessionOwnerId"
    )
    var stepList:
    List<StepWithExercises>
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    private var sessionTime = 0L


    @IgnoredOnParcel
    @Ignore
    private var getTime = stepList.forEach { step ->
        step.exerciseLists.forEach { exercise ->
            sessionTime += step.step.timesNumber * exercise.timer
        }
    }

    val timeFormatted: String
        get() = SimpleDateFormat("mm:ss").format(sessionTime)

}
