package com.HiitHomeTimer.di

import android.app.Application
import com.HiitHomeTimer.repository.SessionRepository
import com.HiitHomeTimer.repository.localdata.SessionDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class HiitHomeTimerApplication: Application() {

    /**
     *  No need to cancel this scope as it'll be torn down with the process
     */
    private val applicationScope = CoroutineScope(SupervisorJob())

    /**
     * Using by lazy so the database and the repository are only created when they're needed
       rather than when the application starts
     */
    private val database by lazy { SessionDatabase.getInstance(this, applicationScope) }
    val repository by lazy { SessionRepository(database.sessionDao()) }
}