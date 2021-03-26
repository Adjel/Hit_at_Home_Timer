package com.HitatHomeTimer.repository.localdata

import android.util.Log
import androidx.room.*
import androidx.room.Dao
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.entities.Step
import com.HitatHomeTimer.repository.localdata.relations.SessionWithSteps
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface SessionDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionWithStepsAndExercises(sessionWithStepsExercise: SessionWithStepsAndExercises) {

        val sessionId = insertSession(sessionWithStepsExercise.session.copy(sessionId = 0))
        sessionWithStepsExercise.stepList.forEach { stepList ->
            val stepId = insertStep(stepList.step.copy(sessionOwnerId = sessionId, stepId = 0))
            stepList.exerciseLists.forEach { exercise ->
                insertExercise(exercise.copy(stepOwnerId = stepId, exerciseId = 0))
            }
        }
    }

    @Transaction
    @Query("SELECT * FROM session_table")
    fun getAllSessionWithStepsAndExercises(): Flow<List<SessionWithStepsAndExercises>>

    @Transaction
    @Query("SELECT * FROM session_table WHERE sessionId = :sessionId")
    fun getSessionWithStepsAndExercisesById(sessionId: Long): Flow<SessionWithStepsAndExercises>

    @Transaction
    suspend fun upsertSession(sessionWithStepsExercise: SessionWithStepsAndExercises) {
        if (isSessionExist(sessionWithStepsExercise.session.sessionId)) {
            // TODO create an upsert function
            deleteSession(sessionWithStepsExercise.session)
            insertSessionWithStepsAndExercises(sessionWithStepsExercise)
        }
        else {
            insertSessionWithStepsAndExercises(sessionWithStepsExercise)
        }
    }

    @Update
    suspend fun updateSession(session: Session)

    @Update
    suspend fun updateStep(step: Step)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteSession(session: Session)

    @Delete
    suspend fun deleteStep(step: Step)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("DELETE FROM session_table")
    suspend fun deleteAllSessionWithStepsAndExercises()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: Step): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    @Transaction
    @Query("SELECT * FROM exercise_table")
    fun getAllExercise(): Flow<List<Exercise>>

    @Transaction
    @Query("SELECT * FROM step_table")
    fun getStepsWithExercises(): Flow<List<StepWithExercises>>


    @Transaction
    @Query("SELECT * FROM session_table ORDER BY dateCreated DESC LIMIT 1")
    suspend fun lastSessionWithStepsAndExercises(): SessionWithStepsAndExercises


    @Transaction
    @Query("SELECT * FROM session_table WHERE sessionId = :sessionOwnerId")
    fun getStepAndSessionsWithSessionOwnerId(sessionOwnerId: Long): List<SessionWithSteps>

    @Transaction
    @Query("SELECT * FROM session_table WHERE sessionId = :sessionOwnerId")
    fun isSessionExist(sessionOwnerId: Long): Boolean {
        return sessionOwnerId > -1
    }

}