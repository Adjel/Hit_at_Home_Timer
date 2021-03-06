package com.HitatHomeTimer.ui.session

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.HitatHomeTimer.di.SessionApplication
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentWorkoutSessionBinding

private const val TAG = "SessionFragment"

class SessionFragment : Fragment(R.layout.fragment_workout_session),
    SessionListAdapter.OnItemClickListener {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var floatingActionButton: FloatingActionButton
    private val sessionListAdapter = SessionListAdapter(this)
    private val args: SessionFragmentArgs by navArgs()

    private val sessionViewModel: SessionViewModel by viewModels{
            SessionViewModelFactory(
                ((requireActivity().application as SessionApplication).repository),
                this,
                args.toBundle()
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWorkoutSessionBinding.bind(view)


        bottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation_view)

        navController = view.findNavController()

        floatingActionButton =
            requireActivity().findViewById(R.id.floating_action_button_add_session)


        binding.apply {
            recyclerviewSession.apply {
                adapter = sessionListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

//            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//                override fun onMove(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    target: RecyclerView.ViewHolder
//                ): Boolean {
//                    return false
//                }
//
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                    val session = sessionListAdapter.currentList[viewHolder.adapterPosition]
//                    viewModel.onTaskSwiped(session)
//                }
//        }).attachToRecyclerView(recyclerViewTasks)
        }

        sessionViewModel.allSessionWithStepsAndExercises.observe(viewLifecycleOwner) {
            sessionListAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.session_item_menu, menu)
    }


    override fun onItemClick(sessionWithStepsAndExercises: SessionWithStepsAndExercises) {
        val action = SessionFragmentDirections.actionSessionFragmentToPracticeFragment(
            sessionWithStepsAndExercises
        )
        navController.navigate(action)
        sessionViewModel.setBottomNavigationViewsCheckable(bottomNavigationView, 2, true)
    }

    override fun onItemButtonMenuDelete(session: Session) {
        sessionViewModel.onDeleteSessionClicked(session)
    }

    override fun onItemButtonMenuEdit(sessionWithStepsAndExercises: SessionWithStepsAndExercises) {
        val action = SessionFragmentDirections.actionSessionFragmentToCreateSessionFragment(
            sessionWithStepsAndExercises
        )
        navController.navigate(action)
        sessionViewModel.setBottomNavigationViewsCheckable(bottomNavigationView, 1, false)
        sessionViewModel.setFloatingActionButtonColor(floatingActionButton, this.requireView())
    }

    override fun onItemButtonMenuDuplicate(sessionWithStepsAndExercises: SessionWithStepsAndExercises) {
        sessionViewModel.onDuplicateSessionClicked(sessionWithStepsAndExercises)
            val action = SessionFragmentDirections.actionSessionFragmentToCreateSessionFragment(sessionWithStepsAndExercises)
            navController.navigate(action)
    }


}