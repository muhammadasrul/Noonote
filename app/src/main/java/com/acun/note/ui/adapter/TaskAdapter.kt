package com.acun.note.ui.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.acun.note.R
import com.acun.note.databinding.TaskItemBinding
import com.acun.note.model.TaskModel

class TaskAdapter(
    private val taskList: List<TaskModel>,
    private val onItemClicked: (task: TaskModel) -> Unit,
    private val onDotsClicked: (task: TaskModel) -> Unit,
    private val onExpandClicked: (pos: Int, view: View) -> Unit
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position], holder.itemView.context, position)
    }

    override fun getItemCount(): Int = taskList.size

    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TaskModel, context: Context, position: Int) {
            with(binding) {
                taskTextView.text = task.title
                taskDescTextView.text = task.description
                if (task.isCompleted) {
                    checkboxImageView.setImageResource(R.drawable.ic_tick_square_enabled)
                    taskTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    checkboxImageView.setImageResource(R.drawable.ic_tick_square_disabled)
                }

                when (task.importance.plus(task.urgency).toInt()) {
                    in 0..2 -> taskLevelIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_app))
                    in 3..5 -> taskLevelIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue_app))
                    in 6..8 -> taskLevelIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellow_app))
                    in 9..10 -> taskLevelIcon.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_app))
                }

                dotsImageView.setOnClickListener {
                    onDotsClicked(task)
                }
                checkboxImageView.setOnClickListener {
                    onItemClicked(task)
                }

                taskLayout.setOnClickListener {
                    onExpandClicked(position, taskDescTextView)
                }
            }
        }
    }
}