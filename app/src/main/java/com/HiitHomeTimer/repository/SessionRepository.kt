package com.HiitHomeTimer.repository

import androidx.annotation.WorkerThread
import com.HiitHomeTimer.repository.localdata.SessionDao
import com.HiitHomeTimer.repository.localdata.entities.Session
import com.HiitHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * Declares the DAO as a private property in the constructor. Pass in the DAO
 * instead of the whole database, because you only need access to the DAO
 */
class SessionRepository(private val sessionDao: SessionDao) {

    /**
     *  Room executes all queries on a separate thread.
     *   Observed Flow will notify the observer when the data has changed.
     */
    val allSessionWithStepsAndExercises: Flow<List<SessionWithStepsAndExercises>> =
        sessionDao.getAllSessionWithStepsAndExercises()

    suspend fun lastSessionWithStepsAndExercises(): SessionWithStepsAndExercises? =
        coroutineScope {
            sessionDao.lastSessionWithStepsAndExercises()
        }

    suspend fun upsertSession(sessionWithStepsAndExercises: SessionWithStepsAndExercises) {
        sessionDao.upsertSession(sessionWithStepsAndExercises)
    }

    suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    /**
     *  By default Room runs suspend queries off the main thread, therefore, we don't need to
     *  implement anything else to ensure we're not doing long running database work
     *  off the main thread.
     */

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSessionWithStepsAndExercises(sessionWithStepsExerciseList: SessionWithStepsAndExercises) =
        coroutineScope {
            sessionDao.insertSessionWithStepsAndExercises(sessionWithStepsExerciseList)
        }

}