package com.HitatHomeTimer.repository.localdata.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step

data class SessionWithSteps(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionOwnerId"
    )
    val stepsList: List<Step>
)
