package com.taras_bekhta.doittest.views.tasks

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.taras_bekhta.doittest.R
import com.taras_bekhta.doittest.events.TaskClickedEvent
import com.taras_bekhta.doittest.retrofit.Task
import kotlinx.android.synthetic.main.task_item.view.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter(val tasks: ArrayList<Task>, val context: Context): RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yy")

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.task_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currTask = tasks[position]

        holder.taskTitleTextView.text = currTask.title
        val dueDate = Calendar.getInstance()
        dueDate.timeInMillis = currTask.dueBy.toLong() * 1000
        holder.dueDateTextView.text = dateFormat.format(dueDate.time)
        holder.priorityTextView.text = currTask.priority

        when(currTask.priority) {
            context.getString(R.string.high) -> {
//                holder.priorityTextView.compoundDrawableTintList = ColorStateList.valueOf(context.getColor(R.color.red))
                DrawableCompat.setTint(holder.priorityTextView.compoundDrawables[0].mutate(), ContextCompat.getColor(context, R.color.red))
            }
            context.getString(R.string.normal) -> {
//                holder.priorityTextView.compoundDrawableTintList = ColorStateList.valueOf(context.getColor(R.color.orange))
                DrawableCompat.setTint(holder.priorityTextView.compoundDrawables[0].mutate(), ContextCompat.getColor(context, R.color.orange))
            }
            context.getString(R.string.low) -> {
//                holder.priorityTextView.compoundDrawableTintList = ColorStateList.valueOf(context.getColor(R.color.green))
                DrawableCompat.setTint(holder.priorityTextView.compoundDrawables[0].mutate(), ContextCompat.getColor(context, R.color.green))
            }
        }

        holder.itemView.setOnClickListener {
            EventBus.getDefault().post(TaskClickedEvent(currTask.id))
        }
    }

    fun sortTasks(type: SortType, isAscending: Boolean) {
        when(type) {
            SortType.SORT_BY_TITLE -> {
                tasks.sortWith(Comparator { lhs, rhs -> if(isAscending) lhs.title.compareTo(rhs.title) else rhs.title.compareTo(lhs.title) })
            }
            SortType.SORT_BY_DATE -> {
                tasks.sortWith(Comparator { lhs, rhs -> if(isAscending) lhs.dueBy.compareTo(rhs.dueBy) else rhs.dueBy.compareTo(lhs.dueBy) })
            }
            SortType.SORT_BY_PRIORITY -> {
                tasks.sortWith(Comparator { lhs, rhs ->
                    val left = if(isAscending) lhs else rhs
                    val right = if(isAscending) rhs else lhs
                    when(left.priority){
                        context.getString(R.string.high) -> {
                            if(right.priority == context.getString(R.string.high))0
                            else 1
                        }
                        context.getString(R.string.normal) -> {
                            if(right.priority == context.getString(R.string.normal))0
                            else if(right.priority == context.getString(R.string.low)) 1
                            else -1
                        }
                        context.getString(R.string.low) -> {
                            if(right.priority == context.getString(R.string.low))0
                            else -1
                        }
                        else -> 0
                    }
                })
            }
        }

        notifyDataSetChanged()
    }

    enum class SortType {
        SORT_BY_TITLE,
        SORT_BY_DATE,
        SORT_BY_PRIORITY
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitleTextView: TextView = view.taskTitleTextView
        val dueDateTextView: TextView = view.dueDateTextView
        val priorityTextView: TextView = view.priorityTextView
    }
}