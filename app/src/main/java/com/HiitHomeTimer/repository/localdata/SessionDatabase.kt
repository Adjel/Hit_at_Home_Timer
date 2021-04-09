package com.HiitHomeTimer.repository.localdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.HiitHomeTimer.repository.localdata.entities.Exercise
import com.HiitHomeTimer.repository.localdata.entities.Session
import com.HiitHomeTimer.repository.localdata.entities.Step
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Session::class,
        Step::class,
        Exercise::class],
    version = 1
)
abstract class SessionDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: SessionDatabase? = null

        fun getInstance(
            context: Context,
            // update to delete/create data at launch [
            scope: CoroutineScope
            // ]
        ): SessionDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SessionDatabase::class.java,
                    "session_database"
                )
                    //  the     .addCallback(SessionDatabaseCallBack(scope)     is an update to add a callback which delete/create data at launch [
                    .addCallback(SessionDatabaseCallBack(scope))
                    // ]
                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }

    // This code will be use full to populate database at app launch and test it and UI  [

    private class SessionDatabaseCallBack(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.sessionDao())
                }
            }
        }

        suspend fun populateDatabase(sessionDao: SessionDao) {
            // Delete all content here.
//            sessionDao.deleteAllSessionWithStepsAndExercises()



            // Add sample data.

//            var exerciseList = listOf<Exercise>(Exercise("Abdos"), Exercise("Repos", timer = 15000), Exercise("Squats"), Exercise("Repos", timer = 15000), Exercise("Pompes"))
//            var step = Step()
//            var exerciseListTwo = listOf<Exercise>(Exercise("Climbers"), Exercise("Repos", timer = 15000), Exercise("Jumpin' Jacks"), Exercise("Repos", timer = 15000), Exercise("Burpees"))
//            var stepTwo = Step()
//            var stepWithExercises = listOf<StepWithExercises>(StepWithExercises(step, exerciseList), StepWithExercises(stepTwo, exerciseListTwo))
//            var session = Session("Hit")
//            var sessionWithStepsAndExercises = SessionWithStepsAndExercises(session, stepWithExercises)
//            sessionDao.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
//
//            exerciseList = listOf<Exercise>(Exercise("Prepare", timer = 1200000))
//            step = Step()
//            exerciseListTwo = listOf<Exercise>(Exercise("Run"), Exercise("Repos"), Exercise("Run"), Exercise("Repos",), Exercise("Run"), Exercise("Repos",), Exercise("Run"),
//                Exercise("Run"), Exercise("Repos"), Exercise("Run"), Exercise("Repos",), Exercise("Run"), Exercise("Repos",), Exercise("Run"))
//            stepTwo = Step(timesNumber = 5)
//            var exerciseListThree = listOf<Exercise>(Exercise("PompesPompesPompes"), Exercise("Repos", timer = 10000))
//            var stepThree = Step(timesNumber = 2)
//            stepWithExercises = listOf<StepWithExercises>(StepWithExercises(step, exerciseList), StepWithExercises(stepTwo, exerciseListTwo), StepWithExercises(stepThree, exerciseListThree))
//            session = Session("Fractionné")
//            sessionWithStepsAndExercises = SessionWithStepsAndExercises(session, stepWithExercises)
//            sessionDao.insertSessionWithStepsAndExercises(sessionWithStepsAndExercises)
        }
    }
}