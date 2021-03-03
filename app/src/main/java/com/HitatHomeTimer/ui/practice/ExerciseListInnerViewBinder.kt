package com.HitatHomeTimer.ui.practice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Exercise
import com.HitatHomeTimer.repository.localdata.relations.StepWithExercises
import com.HitatHomeTimer.util.nestedlistadapterfactory.FeedItemViewBinder
import com.hitathometimer.R

class ExerciseListInnerViewBinder(val block: (data: Exercise) -> Unit) :
    FeedItemViewBinder<Exercise, ExerciseListViewHolder>(
        Exercise::class.java
    ) {
    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ExerciseListViewHolder(
            LayoutInflater.from(parent.context).inflate(getFeedItemType(), parent, false), block)
    }

    override fun bindViewHolder(model: Exercise, viewHolder: ExerciseListViewHolder) {
        viewHolder.bind(model)
    }

    override fun getFeedItemType(): Int = R.layout.item_practice_exercise

    override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise)
    = oldItem == newItem

    override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise)
    = oldItem == newItem
}

class ExerciseListViewHolder(val view : View, val block : (data: Exercise) -> Unit)
    : RecyclerView.ViewHolder(view) {

    fun bind(data: Exercise) {



        itemView.setOnClickListener {
            // TODO
        }

        itemView.apply {

        }
    }
}