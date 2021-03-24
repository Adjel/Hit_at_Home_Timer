package com.HitatHomeTimer.repository

import androidx.annotation.WorkerThread
import com.HitatHomeTimer.repository.localdata.SessionDao
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class SessionRepository(private val sessionDao: SessionDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSessionWithStepsAndExercises: Flow<List<SessionWithStepsAndExercises>> =
        sessionDao.getAllSessionWithStepsAndExercises()

    val firstStepWithExercise: Flow<List<StepWithExercises>> =
        sessionDao.getFirstStepWithExercises(1)

    fun getSessionWithStepsAndExercisesById(sessionOwnerId: Long): Flow<SessionWithStepsAndExercises> =
        sessionDao.getSessionWithStepsAndExercisesById(sessionOwnerId)

    suspend fun upsertSession(sessionWithStepsAndExercises: SessionWithStepsAndExercises) {
        sessionDao.upsertSession(sessionWithStepsAndExercises)
    }

    suspend fun updateStep(step: Step) {
        sessionDao.updateStep(step)
    }

    suspend fun updateExercise(exercise: Exercise) {
        sessionDao.updateExercise(exercise)
    }

    suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    suspend fun deleteStep(step: Step) {
        sessionDao.deleteStep(step)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        sessionDao.deleteExercise(exercise)
    }


    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSessionWithStepsAndExercises(sessionWithStepsExerciseList: SessionWithStepsAndExercises) =
        coroutineScope {
            sessionDao.insertSessionWithStepsAndExercises(sessionWithStepsExerciseList)
        }

}