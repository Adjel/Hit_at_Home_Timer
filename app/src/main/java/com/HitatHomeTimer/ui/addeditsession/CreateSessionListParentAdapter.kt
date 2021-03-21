package com.HitatHomeTimer.ui.addeditsession


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.hitathometimer.R
import com.hitathometimer.databinding.ItemCreationExerciseBinding
import com.hitathometimer.databinding.ItemCreationStepBinding

class CreateSessionListParentAdapter(val fragment: CreateSessionFragment, val event: MutableLiveData<CreationListEvent> = MutableLiveData()) :
    ListAdapter<StepWithExercises, CreateSessionListParentAdapter.CreateSessionListParentViewHolder>(
        CreateStepListDiffUtilCallback()
    ) {

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class CreateSessionListParentViewHolder(private val binding: ItemCreationStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var createSessionListChildAdapter: CreateSessionListChildAdapter


        init {

            binding.apply {

                imageButtonCreationSetsMenu.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(position)

                        val popupMenu = PopupMenu(
                            imageButtonCreationSetsMenu.context,
                            imageButtonCreationSetsMenu
                        )
                        popupMenu.menuInflater.inflate(
                            R.menu.edit_step_and_exercise_item_menu,
                            popupMenu.menu
                        )
                        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.session_item_menu_duplicate ->
                                    event.value =
                                        CreationListEvent.OnDuplicateStepClicked(stepWithExercises)
                                R.id.session_item_menu_delete ->
                                    event.value = CreationListEvent.OnDeleteStepClick(position)
                            }
                            true
                        })
//                        this@CreateSessionListParentAdapter.notifyDataSetChanged()
                        popupMenu.show()
                    }
                }

                buttonCreationIncrementSets.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(position)
                        Log.d("ViewModel", "position in adapter = ${position}")
                        Log.d("ViewModel", "stepWithExercises in adapter = ${stepWithExercises}")
                        event.value = CreationListEvent.OnStepTimerChanged(
                            position,
                            UpdateTimeNumber.INCREMENT
                        )
//                        this@CreateSessionListParentAdapter.notifyDataSetChanged()
                    }
                }

                buttonCreationDecrementSets.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(position)
                        event.value = CreationListEvent.OnStepTimerChanged(
                            position,
                            UpdateTimeNumber.DECREMENT
                        )
//                        this@CreateSessionListParentAdapter.notifyDataSetChanged()
                    }
                }

                buttonCreationAddExercise.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(position)
                        event.value = CreationListEvent.OnNewExerciseClicked(adapterPosition, stepWithExercises)

                    }
                }


                editTextViewCreationStepTime.doAfterTextChanged { editable ->
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(position)

                        if (editable != null && editable.toString() != stepWithExercises.step.timesNumber.toString()) {
                            if (editable.isNotEmpty()) {
                                Log.d(
                                    "ViewModel",
                                    "onCreateViewHolder: adapter POSITION in ONcreate = $adapterPosition"
                                )
                                event.value = CreationListEvent.OnStepTimerChanged(adapterPosition, UpdateTimeNumber.EDIT, editable.toString().toLong()
                                )
                            }
                        }
                    }
                }

            }
        }



        fun bind(stepWithExercises: StepWithExercises, listener: CreateSessionListChildAdapter.CustomClickListener) {

            // create the child adapter
            // pass the parent adapter position in child ListAdapter parameters to pass it into events in child ListAdapter
            val childLayoutManager = LinearLayoutManager(binding.recyclerViewCreationExercise.context)
            createSessionListChildAdapter = CreateSessionListChildAdapter(parentPosition = listener)
            Log.d("CreateSessionChild", "bind: CHILD SUBMITLIST CHANGED")
            createSessionListChildAdapter.submitList(stepWithExercises.exerciseLists.toMutableList())

            binding.apply {
                editTextViewCreationStepTime.setText(stepWithExercises.step.timesNumber.toString())
                Log.d("ViewModel", "bind: editTextViewCreationStepTime")
                recyclerViewCreationExercise.apply {
                    adapter = createSessionListChildAdapter
                    layoutManager = childLayoutManager


                    // observe child ListAdapter events and pass it into viewModel by fragment

                    createSessionListChildAdapter.event.observe(
                        fragment.viewLifecycleOwner) {
                        fragment.createSessionViewModel.handleEvent(it)
                    }
                }
            }
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ):
            CreateSessionListParentViewHolder {
        val binding =
            ItemCreationStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CreateSessionListParentViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CreateSessionListParentViewHolder, position: Int) {
        val current = getItem(holder.adapterPosition)
        Log.d("CreateSessionChild", "onBindViewHolder: ${holder.adapterPosition}")

       val listener = object : CreateSessionListChildAdapter.CustomClickListener {
           override fun onItemClick(): Int {
               return holder.adapterPosition
           }
       }
        holder.bind(current, listener)
    }


    class CreateStepListDiffUtilCallback : DiffUtil.ItemCallback<StepWithExercises>() {
        override fun areItemsTheSame(
            oldItem: StepWithExercises,
            newItem: StepWithExercises
        ) : Boolean {
            return oldItem === newItem
        }


        override fun areContentsTheSame(
            oldItem: StepWithExercises,
            newItem: StepWithExercises

        ) = oldItem == newItem
    }

}



