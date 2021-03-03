package com.HitatHomeTimer.ui.session

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.HitatHomeTimer.repository.localdata.entities.Session
import com.HitatHomeTimer.repository.localdata.relations.SessionWithStepsAndExercises
import com.hitathometimer.R
import com.hitathometimer.databinding.ItemWorkoutSessionBinding

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
//                                    Toast.makeText(imageButtonMenuItemSession.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                                R.id.session_item_menu_copy ->
                                    Toast.makeText(imageButtonMenuItemSession.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
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


        fun bind(session: Session) {
            binding.apply {
                textViewItemSessionName.text = session.name
                textViewItemSessionDuration.text = session.timeFormatted
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(sessionWithStepsAndExercises: SessionWithStepsAndExercises)
        fun onItemButtonMenuDelete(session: Session)
        fun onItemButtonMenuEdit(sessionWithStepsAndExercises: SessionWithStepsAndExercises)
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
        holder.bind(currentItem.session)
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