package com.HitatHomeTimer.ui.addeditsession

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.hitathometimer.databinding.ItemCreationExerciseBinding

class CreateSessionListChildAdapter():
    ListAdapter<Exercise, CreateSessionListChildAdapter.CreateSessionChildViewHolder>(CreateExerciseListDiffCallback()) {

    inner class CreateSessionChildViewHolder(private val binding: ItemCreationExerciseBinding) :
    RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

            }
        }

        fun bind(exercise: Exercise) {
            binding.apply {
                editTextCreationExerciseName.setText(exercise.name)
                editTextTimerItemExerciseCreation.setText(exercise.timerFormatted)
            }
        }
    }

//    interface OnItemClickListener {
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSessionChildViewHolder {
        val binding = ItemCreationExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreateSessionChildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateSessionChildViewHolder, position: Int) {
        val current = getItem(holder.adapterPosition)
        holder.bind(current)
    }

}

class CreateExerciseListDiffCallback: DiffUtil.ItemCallback<Exercise>() {
    override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise)=
        oldItem == newItem
}
