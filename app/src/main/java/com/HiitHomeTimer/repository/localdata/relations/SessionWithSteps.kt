package com.HiitHomeTimer.repository.localdata.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.HiitHomeTimer.repository.localdata.entities.Session
import com.HiitHomeTimer.repository.localdata.entities.Step
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionWithSteps(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionOwnerId"
    )
    var stepsList:
    List<Step>
) : Parcelable
