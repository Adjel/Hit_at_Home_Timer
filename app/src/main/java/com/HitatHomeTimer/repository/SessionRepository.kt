package com.HitatHomeTimer.repository

import androidx.annotation.WorkerThread
import com.HitatHomeTimer.repository.localdata.SessionDao
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class SessionRepository(private val sessionDao: SessionDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSessionWithStepsAndExercises: Flow<List<SessionWithStepsAndExercises>> = sessionDao.getAllSessionWithStepsAndExercises()

    val firstStepWithExercise: Flow<List<StepWithExercises>> = sessionDao.getFirstStepWithExercises(1)

    suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSessionWithStepsAndExercises(sessionWithStepsExerciseList: SessionWithStepsAndExercises) {
        sessionDao.insertSessionWithStepsAndExercises(sessionWithStepsExerciseList)
    }

}