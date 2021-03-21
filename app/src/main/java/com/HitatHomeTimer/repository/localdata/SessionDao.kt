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

//    @Transaction
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertStepWithExercises(step: Step, exerciseList: List<Exercise>) {
//        val stepId = insertStep(step)
//        if (stepId == -1L) {
//            // TODO : Throw a message to user because saved failed
//            Log.d("Dao", "insertStepWithExercises: Long = -1")
//        } else {
//            exerciseList.forEach { exercise ->
//                insertExercise(exercise.copy(stepOwnerId = stepId))
//            }
//        }
//    }

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
    @Query("SELECT * FROM step_table WHERE stepId = :stepId")
    fun getFirstStepWithExercises(stepId: Long): Flow<List<StepWithExercises>>


    @Transaction
    @Query("SELECT * FROM session_table WHERE sessionId = :sessionOwnerId")
    fun getStepAndSessionsWithSessionOwnerId(sessionOwnerId: Long): List<SessionWithSteps>

//    @Update
//    suspend fun updateSession(session: Session)
//
//    @Update
//    suspend fun updateStep(step: Step)
//
//    @Update
//    suspend fun updateExercise(exercise: Exercise)
//
//    @Update
//    suspend fun updateAllSession(sessionWithStepsAndExercises: SessionWithStepsAndExercises)
//
//    @Delete
//    suspend fun deleteSession(session: Session)
//
//    @Delete
//    suspend fun deleteStep(step: Step)
//
//    @Delete
//    fun deleteExercise(exercise: Exercise)
}