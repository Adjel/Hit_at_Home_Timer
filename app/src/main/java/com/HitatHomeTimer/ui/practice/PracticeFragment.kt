package com.HitatHomeTimer.ui.practice

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.di.SessionApplication
import com.HitatHomeTimer.repository.localdata.SessionDatabase
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.HitatHomeTimer.ui.session.SessionFragment
import com.HitatHomeTimer.ui.session.SessionViewModel
import com.HitatHomeTimer.ui.session.SessionViewModelFactory
import com.HitatHomeTimer.util.StepListViewBinder
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedAdapter
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedItemBinder
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedItemClass
import com.hitathometimer.R
import com.hitathometimer.databinding.FragmentPracticeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PracticeFragment : Fragment(R.layout.fragment_practice) {

    private val practiceParentListAdapter = PracticeParentListAdapter()

    private val practiceViewModel: PracticeViewModel by viewModels(){
        PracticeViewModelFactory(
            ((requireActivity().application as SessionApplication).repository),
            args.sessionWith
        )
    }

    private val args: PracticeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPracticeBinding.bind(view)


        binding.apply {
            recyclerViewPracticeSession.apply {
                adapter = practiceParentListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            practiceParentListAdapter.submitList(practiceViewModel.arguments?.stepList)
            binding.textViewPracticeSessionName.text = practiceViewModel.arguments?.session?.name
        }

    }
}
