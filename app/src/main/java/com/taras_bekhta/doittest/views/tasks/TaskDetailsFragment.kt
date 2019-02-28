package com.taras_bekhta.doittest.views.tasks


import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.taras_bekhta.doittest.R
import com.taras_bekhta.doittest.retrofit.AddTaskResponse
import com.taras_bekhta.doittest.retrofit.ITaskService
import com.taras_bekhta.doittest.retrofit.RetrofitClient
import com.taras_bekhta.doittest.retrofit.Task
import com.taras_bekhta.doittest.util.AnimationUtil
import com.taras_bekhta.doittest.views.BaseFragment
import com.taras_bekhta.doittest.views.datetime.DateTimePickerFragment
import kotlinx.android.synthetic.main.fragment_task_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class TaskDetailsFragment : BaseFragment(), DateTimePickerFragment.DateTimeSelectedListener {
    private var task: Task? = null
    private var dateFormat = SimpleDateFormat("EEEE dd MMM, yyyy")
    private var dateTimeFormat = SimpleDateFormat("dd/MM/yy HH:mm")

    private var selectedDateTime: Date? = null

    private var isProcessing = false

    override fun getTitle(): String {
        return "Task Details"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTask(TaskDetailsFragmentArgs.fromBundle(arguments).taskId)

        deleteTaskButton.setOnClickListener {
            if(isProcessing) {
                return@setOnClickListener
            }
            deleteTaskButton.isEnabled = false
            isProcessing = true
            RetrofitClient(parentActivity.authToken).getRetrofit().create(ITaskService::class.java).deleteTask(task!!.id).enqueue(
                object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        deleteTaskButton.isEnabled = true
                        isProcessing = false
                        if (response.isSuccessful) {
                            Toast.makeText(context!!, "Task was successfully deleted", Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        } else {
                            showErrorSnackBar(getString(R.string.task_remove_failed), titleTextView)
                            //AnimationUtil.animateViewHide(detailsProgressBar)
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        //AnimationUtil.animateViewHide(detailsProgressBar)
                        deleteTaskButton.isEnabled = true
                        isProcessing = false
                        showErrorSnackBar(getString(R.string.task_remove_failure), titleTextView)
                    }
                }
            )
        }

        notificationLayout.setOnClickListener {
            val dialog = DateTimePickerFragment()
            if(selectedDateTime != null) {
                dialog.setDateTime(selectedDateTime!!)
            }
            dialog.dateTimeSelectedListener = this
            dialog.show(fragmentManager, "DateTimeRangePicker")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(task == null) {
            return true
        }
        return when (item.itemId) {
            R.id.action_edit -> {
                val dir = TaskDetailsFragmentDirections.actionTaskDetailsFragmentToEditTaskFragment(false, task!!.id)
                findNavController().navigate(dir)
                true
            }
            else -> false
        }
    }

    override fun onDateTimeSelected(date: Date) {
        selectedDateTime = date
        val calendar = Calendar.getInstance()
        calendar.time = selectedDateTime

        notificationTimeTextView.text = dateTimeFormat.format(selectedDateTime)
    }

    private fun getTask(id: Int) {
        if(id < 0) {
            showErrorSnackBar(getString(R.string.incorrect_id), titleTextView)
            return
        }
        RetrofitClient(parentActivity.authToken).getRetrofit().create(ITaskService::class.java)
            .getTask(id).enqueue(
                object : Callback<AddTaskResponse> {
                    override fun onResponse(call: Call<AddTaskResponse>, response: Response<AddTaskResponse>) {
                        if (response.isSuccessful) {
                            task = response.body()!!.task
                            updateScreen()
                            if(detailsProgressBar != null) {
                                AnimationUtil.animateViewChangeScaleFade(detailsProgressBar, detailsLayout)
                            }
                        } else {
                            if(detailsProgressBar != null) {
                                showErrorSnackBar(getString(R.string.task_loading_failed), titleTextView)
                                AnimationUtil.animateViewHide(detailsProgressBar)
                            }
                        }
                    }

                    override fun onFailure(call: Call<AddTaskResponse>, t: Throwable) {
                        if(detailsProgressBar != null) {
                            AnimationUtil.animateViewHide(detailsProgressBar)
                            showErrorSnackBar(getString(R.string.task_loading_failure), titleTextView)
                        }
                    }
                }
            )
    }

    fun updateScreen() {
        if(titleTextView == null || dueDateTextView == null || priorityTextView == null) {
            return
        }
        titleTextView.text = task!!.title
        val dueDate = Calendar.getInstance()
        dueDate.timeInMillis = task!!.dueBy.toLong() * 1000
        dueDateTextView.text = dateFormat.format(dueDate.time)
        priorityTextView.text = task!!.priority
//        when(task!!.priority) {
//            getString(R.string.high) -> {
//                priorityTextView.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.red))
//            }
//            getString(R.string.normal) -> {
//                priorityTextView.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.orange))
//            }
//            getString(R.string.low) -> {
//                priorityTextView.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.green))
//            }
//        }
        when(task!!.priority) {
            getString(R.string.high) -> {
                priorityTextView.compoundDrawables[0].mutate().setColorFilter(ContextCompat.getColor(context!!, R.color.red), PorterDuff.Mode.SRC_ATOP)
            }
            getString(R.string.normal) -> {
                priorityTextView.compoundDrawables[0].mutate().setColorFilter(ContextCompat.getColor(context!!, R.color.orange), PorterDuff.Mode.SRC_ATOP)
            }
            getString(R.string.low) -> {
                priorityTextView.compoundDrawables[0].mutate().setColorFilter(ContextCompat.getColor(context!!, R.color.green), PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

}
