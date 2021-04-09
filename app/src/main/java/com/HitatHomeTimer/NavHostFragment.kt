package com.HitatHomeTimer

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.HitatHomeTimer.di.SessionApplication
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hitathometimer.R

class NavHostFragment : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var addSessionFloatingActionButton: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView

    enum class ActiveFragment {
        SESSION, CREATE, PRACTICE
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_host_fragment)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        addSessionFloatingActionButton =
            findViewById(R.id.floating_action_button_add_session)

        val actionBar = supportActionBar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()


        /**
         * set clickable to false for BottomNavigationView invisible button
         * which is created to center other buttons
         *  1 is the index of button (first is 0)
         */
        bottomNavigationView.menu.getItem(1).isEnabled = false

        /**
         *   NAVIGATION TRIGGERS
         **/
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_button_session_fragment -> {
                    navController.navigate(R.id.sessionFragment)
                    setBottomNavigationViewsCheckable(bottomNavigationView, true)
                    setFloatingActionButtonColor(addSessionFloatingActionButton, R.color.blue_yonder)
                    true
                }
                R.id.navigation_button_practice_fragment -> {
                    val action =
                    navController.navigate(R.id.practiceFragment)
                    setBottomNavigationViewsCheckable(bottomNavigationView, true)
                    setFloatingActionButtonColor(addSessionFloatingActionButton, R.color.blue_yonder)
                    true
                }
                else -> false
            }
        }

        addSessionFloatingActionButton.setOnClickListener {
            navController.navigate(R.id.createSessionFragment)
            setFloatingActionButtonColor(addSessionFloatingActionButton, R.color.ivory)
            setBottomNavigationViewsCheckable(bottomNavigationView, false)
        }

    }

    /**
     * FUNCTIONS TO SET NAVIGATION ITEMS CHECKABLE AND COLORS
     */

    private fun setBottomNavigationViewsCheckable(
        bottomNavigationView: BottomNavigationView,
        isCheckable: Boolean
    ) {
        bottomNavigationView.menu.setGroupCheckable(0, isCheckable, isCheckable)
    }

    private fun setFloatingActionButtonColor(
        floatingActionButton: FloatingActionButton,
        fabColor: Int,
    ) {
        floatingActionButton.drawable.setTint(resources.getColor(fabColor))
    }
}