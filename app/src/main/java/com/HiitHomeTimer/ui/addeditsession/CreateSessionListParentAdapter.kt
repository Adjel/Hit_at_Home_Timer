package com.HiitHomeTimer.ui.addeditsession

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HiitHomeTimer.repository.localdata.relations.StepWithExercises
import com.hiithometimer.R
import com.hiithometimer.databinding.ItemCreationStepBinding

class CreateSessionListParentAdapter(val fragment: CreateSessionFragment, val event: MutableLiveData<CreationListEvent> = MutableLiveData()) :
    ListAdapter<StepWithExercises, CreateSessionListParentAdapter.CreateSessionListParentViewHolder>(
        CreateStepListDiffUtilCallback()
    ) {

    inner class CreateSessionListParentViewHolder(private val binding: ItemCreationStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {

                /**
                 * create a popup menu to delete or duplicate a step
                 */
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
                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.session_item_menu_duplicate ->
                                    event.value =
                                        CreationListEvent.OnDuplicateStepClicked(stepWithExercises)
                                R.id.session_item_menu_delete ->
                                    event.value = CreationListEvent.OnDeleteStepClick(position)
                            }
                            true
                        }
                        popupMenu.show()
                    }
                }

                buttonCreationIncrementSets.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        event.value = CreationListEvent.OnStepTimerChanged(
                            adapterPosition,
                            UpdateTimeNumber.INCREMENT
                        )
                    }
                }

                buttonCreationDecrementSets.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        event.value = CreationListEvent.OnStepTimerChanged(
                            adapterPosition,
                            UpdateTimeNumber.DECREMENT
                        )
                    }
                }

                buttonCreationAddExercise.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        event.value = CreationListEvent.OnNewExerciseClicked(adapterPosition)

                    }
                }


                editTextViewCreationStepTime.doAfterTextChanged { editable ->
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(adapterPosition)

                        // input is send only if value changed and is not empty
                        if (editable != null && editable.toString() != stepWithExercises.step.timesNumber.toString()) {
                            if (editable.isNotEmpty()) {
                                event.value = CreationListEvent.OnStepTimerChanged(adapterPosition, UpdateTimeNumber.EDIT, editable.toString().toInt()
                                )
                            }
                        }
                    }
                }

            }
        }



        fun bind(stepWithExercises: StepWithExercises, listener: CreateSessionListChildAdapter.CustomClickListener) {

            /**
             * create the child adapter
             * pass the parent adapter position in child ListAdapter parameters to pass it into events in child ListAdapter
             */
            val childLayoutManager = LinearLayoutManager(binding.recyclerViewCreationExercise.context)

            /**
             *  pass a parent adapterPosition listener to the childAdapter
             */
            val createSessionListChildAdapter = CreateSessionListChildAdapter(parentPosition = listener)

            createSessionListChildAdapter.submitList(stepWithExercises.exerciseLists.toMutableList())

            binding.apply {

                editTextViewCreationStepTime.setText(stepWithExercises.step.timesNumber.toString())

                recyclerViewCreationExercise.apply {
                    adapter = createSessionListChildAdapter
                    layoutManager = childLayoutManager

                    /**
                    *  observe child ListAdapter events and pass it into viewModel by fragment
                     */
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

        /**
         * pass the adapterPosition to the child adapter by overriding a child parameter object
         */
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



