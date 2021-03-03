package com.HitatHomeTimer.ui.addeditsession

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.hitathometimer.databinding.ItemCreationStepBinding

class CreateSessionListParentAdapter(private val listener: OnItemClickListener) :
    ListAdapter<StepWithExercises, CreateSessionListParentAdapter.CreateSessionListParentViewHolder>(
        CreateStepListDiffCallback()
    ) {

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class CreateSessionListParentViewHolder(private val binding: ItemCreationStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                imageButtonCreationSetsMenu.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val stepWithExercises = getItem(position)
                        listener.newStepWithExercise()
                    }
                }
            }
        }

        fun bind(stepWithExercises: StepWithExercises) {

            val createSessionListChildAdapter = CreateSessionListChildAdapter()
            createSessionListChildAdapter.submitList(stepWithExercises.exerciseLists)

            binding.apply {
                textViewCreationStepTime.text = stepWithExercises.step.timesNumber.toString()

                recyclerViewCreationExercise.apply {
                    adapter = createSessionListChildAdapter
                    layoutManager = LinearLayoutManager(binding.recyclerViewCreationExercise.context)
                    setHasFixedSize(true)
                    setRecycledViewPool(viewPool)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun newStepWithExercise()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int):
            CreateSessionListParentViewHolder {
        val binding =
            ItemCreationStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreateSessionListParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateSessionListParentViewHolder, position: Int) {
        val current = getItem(holder.adapterPosition)
        holder.bind(current)
    }
}

class CreateStepListDiffCallback: DiffUtil.ItemCallback<StepWithExercises>() {
    override fun areItemsTheSame(
        oldItem: StepWithExercises,
        newItem: StepWithExercises
    ) =
        oldItem === newItem

    override fun areContentsTheSame(
        oldItem: StepWithExercises,
        newItem: StepWithExercises
    ) =
        oldItem == newItem

}


