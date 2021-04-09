package com.HiitHomeTimer.ui.session

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HiitHomeTimer.repository.localdata.entities.Session
import com.HiitHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.hiithometimer.R
import com.hiithometimer.databinding.ItemWorkoutSessionBinding

class SessionListAdapter(private val listener: OnItemClickListener) :
    ListAdapter<SessionWithStepsAndExercises, SessionListAdapter.SessionViewHolder>(
        SessionListItemDiffCallBack()
    ) {

    inner class SessionViewHolder(private val binding: ItemWorkoutSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                cardviewItemSession.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val sessionWithStepsAndExercises = getItem(position)
                        listener.onItemClick(sessionWithStepsAndExercises)
                    }
                }
                imageButtonMenuItemSession.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val session = getItem(position).session
                        val sessionWithStepsAndExercises = getItem(position)


                        val popupMenu = PopupMenu(
                            imageButtonMenuItemSession.context,
                            imageButtonMenuItemSession
                        )
                        popupMenu.menuInflater.inflate(R.menu.session_item_menu, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.session_item_menu_edit ->
                                    listener.onItemButtonMenuEdit(sessionWithStepsAndExercises)
                                R.id.session_item_menu_duplicate ->
                                   listener.onItemButtonMenuDuplicate(sessionWithStepsAndExercises)
                                R.id.session_item_menu_delete ->
                                        listener.onItemButtonMenuDelete(session)
                            }
                            true
                        })
                        popupMenu.show()
                    }
                }
            }
        }


        fun bind(sessionWithStepsAndExercises: SessionWithStepsAndExercises) {
            binding.apply {
                textViewItemSessionName.text = sessionWithStepsAndExercises.session.name
                textViewItemSessionDuration.text = sessionWithStepsAndExercises.timeFormatted
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(sessionWithStepsAndExercises: SessionWithStepsAndExercises)
        fun onItemButtonMenuDelete(session: Session)
        fun onItemButtonMenuEdit(sessionWithStepsAndExercises: SessionWithStepsAndExercises)
        fun onItemButtonMenuDuplicate(sessionWithStepsAndExercises: SessionWithStepsAndExercises)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemWorkoutSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val currentItem = getItem(holder.adapterPosition)
        holder.bind(currentItem)
    }


    class SessionListItemDiffCallBack : DiffUtil.ItemCallback<SessionWithStepsAndExercises>() {

        override fun areItemsTheSame(
            oldItem: SessionWithStepsAndExercises,
            newItem: SessionWithStepsAndExercises
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: SessionWithStepsAndExercises,
            newItem: SessionWithStepsAndExercises
        ): Boolean {
            return oldItem == newItem
        }
    }

}