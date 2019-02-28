package com.taras_bekhta.doittest.views.tasks


import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.content.res.AppCompatResources
import android.view.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.taras_bekhta.doittest.R
import com.taras_bekhta.doittest.retrofit.AddTaskResponse
import com.taras_bekhta.doittest.retrofit.ITaskService
import com.taras_bekhta.doittest.retrofit.RetrofitClient
import com.taras_bekhta.doittest.retrofit.Task
import com.taras_bekhta.doittest.views.BaseFragment
import com.taras_bekhta.doittest.views.datetime.DateTimePickerFragment
import kotlinx.android.synthetic.main.fragment_edit_task.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class EditTaskFragment : BaseFragment(), DateTimePickerFragment.DateTimeSelectedListener {

    private var title = ""
    private var task: Task? = null
    private var createTask = false
    private var selectedPriority = ""

    private var dateTimeFormat = SimpleDateFormat("dd/MM/yy HH:mm")
    private var selectedDueDate: Date? = null
    private var selectedNotificationDate: Date? = null

    private var selectingDueDate = true

    private var isProcessing = false

    override fun getTitle(): String {
        return title
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createTask = EditTaskFragmentArgs.fromBundle(arguments).newTask
        if (createTask) {
            title = getString(R.string.create_task)
            task = Task()
            val calendar = Calendar.getInstance()
            task!!.dueBy = (calendar.timeInMillis / 1000).toInt()
            selectedDueDate = calendar.time
            dueDateTextView.text = dateTimeFormat.format(selectedDueDate)
        } else {
            title = getString(R.string.edit_task)
            getTask(EditTaskFragmentArgs.fromBundle(arguments).taskId)
        }

        setListeners()
    }

    private fun setListeners() {
        val drawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_check)
        val size = resources.getDimensionPixelSize(R.dimen.defaultMargin)
        drawable!!.setBounds(0, 0, size, size)

        highPriorityButton.setOnClickListener {
            deselectAllButtons()
            highPriorityButton.setCompoundDrawables(drawable, null, null, null)
            DrawableCompat.setTint(highPriorityButton.compoundDrawables[0].mutate(), ContextCompat.getColor(context!!, R.color.red))
            //highPriorityButton.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.red))
            selectedPriority = getString(R.string.high)
        }
        mediumPriorityButton.setOnClickListener {
            deselectAllButtons()
            mediumPriorityButton.setCompoundDrawables(drawable, null, null, null)
            DrawableCompat.setTint(mediumPriorityButton.compoundDrawables[0].mutate(), ContextCompat.getColor(context!!, R.color.orange))
            //mediumPriorityButton.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.orange))
            selectedPriority = getString(R.string.normal)
        }
        lowPriorityButton.setOnClickListener {
            deselectAllButtons()
            lowPriorityButton.setCompoundDrawables(drawable, null, null, null)
            DrawableCompat.setTint(lowPriorityButton.compoundDrawables[0].mutate(), ContextCompat.getColor(context!!, R.color.green))
            //lowPriorityButton.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.green))
            selectedPriority = getString(R.string.low)
        }

        if (createTask) {
            deleteTaskButton.visibility = View.GONE
        } else {
            deleteTaskButton.visibility = View.VISIBLE
        }

        deleteTaskButton.setOnClickListener {
            if(isProcessing) {
                return@setOnClickListener
            }
            deleteTaskButton.isEnabled = false
            isProcessing = true
            RetrofitClient(parentActivity.authToken).getRetrofit().create(ITaskService::class.java)
                .deleteTask(task!!.id).enqueue(
                    object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            isProcessing = false
                            deleteTaskButton.isEnabled = true
                            if (response.isSuccessful) {
                                Toast.makeText(context!!, "Task was successfully deleted", Toast.LENGTH_LONG).show()
                                findNavController().popBackStack(R.id.mainFragment, false)
                            } else {
                                showErrorSnackBar(getString(R.string.task_remove_failed), titleLabel)
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            isProcessing = false
                            deleteTaskButton.isEnabled = true
                            showErrorSnackBar(getString(R.string.task_remove_failure), titleLabel)
                        }
                    }
                )
        }

        dueDateTextView.setOnClickListener {
            selectingDueDate = true
            if(task != null) {
                val dialog = DateTimePickerFragment()
                if(selectedDueDate != null) {
                    dialog.setDateTime(selectedDueDate!!)
                } else {
                    dialog.setDateTime(Calendar.getInstance().time)
                }
                dialog.dateTimeSelectedListener = this
                dialog.show(fragmentManager, "DateTimeRangePicker")
            }
        }

        notificationLayout.setOnClickListener {
            selectingDueDate = false
            val dialog = DateTimePickerFragment()
            if(selectedNotificationDate != null) {
                dialog.setDateTime(selectedNotificationDate!!)
            }
            dialog.dateTimeSelectedListener = this
            dialog.show(fragmentManager, "DateTimeRangePicker")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                hideKeyboard()
                if(isProcessing) {
                    return true
                }
                if (verifyFields()) {
                    if(createTask) {
                        addTask()
                    } else {
                        updateTask()
                    }
                }
                true
            }
            else -> false
        }
    }

    override fun onDateTimeSelected(date: Date) {
        if(selectingDueDate) {
            selectedDueDate = date
            val calendar = Calendar.getInstance()
            calendar.time = selectedDueDate

            dueDateTextView.text = dateTimeFormat.format(selectedDueDate)
        } else {
            selectedNotificationDate = date
            val calendar = Calendar.getInstance()
            calendar.time = selectedNotificationDate

            notificationTimeTextView.text = dateTimeFormat.format(selectedNotificationDate)
        }
    }

    private fun verifyFields(): Boolean {
        task!!.title = titleEditText.text.toString()
        task!!.priority = selectedPriority
        task!!.dueBy = (selectedDueDate!!.time / 1000).toInt()

        if (task!!.title.isEmpty()) {
            showErrorSnackBar(getString(R.string.empty_title), titleEditText)
            return false
        }
        if (task!!.priority.isEmpty()) {
            showErrorSnackBar(getString(R.string.empty_priority), titleEditText)
            return false
        }

        return true
    }

    private fun getTask(id: Int) {
        if (id < 0) {
            showErrorSnackBar(getString(R.string.incorrect_id), titleLabel)
            return
        }
        RetrofitClient(parentActivity.authToken).getRetrofit().create(ITaskService::class.java)
            .getTask(id).enqueue(
                object : Callback<AddTaskResponse> {
                    override fun onResponse(call: Call<AddTaskResponse>, response: Response<AddTaskResponse>) {
                        if (response.isSuccessful) {
                            task = response.body()!!.task
                            updateScreen()
                        } else {
                            showErrorSnackBar(getString(R.string.task_loading_failed), titleLabel)
                        }
                    }

                    override fun onFailure(call: Call<AddTaskResponse>, t: Throwable) {
                        showErrorSnackBar(getString(R.string.task_loading_failure), titleLabel)
                    }
                }
            )
    }

    private fun addTask() {
        if(isProcessing) {
            return
        }
        isProcessing = true
        RetrofitClient(parentActivity.authToken).getRetrofit().create(ITaskService::class.java)
            .addTask(task!!.title, task!!.dueBy, task!!.priority).enqueue(
                object : Callback<AddTaskResponse> {
                    override fun onResponse(call: Call<AddTaskResponse>, response: Response<AddTaskResponse>) {
                        isProcessing = false
                        if (response.isSuccessful) {
                            Toast.makeText(context!!, getString(R.string.task_created), Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        } else {
                            showErrorSnackBar(getString(R.string.task_create_failed), titleEditText)
                        }
                    }

                    override fun onFailure(call: Call<AddTaskResponse>, t: Throwable) {
                        isProcessing = false
                        showErrorSnackBar(getString(R.string.task_create_failure), titleEditText)
                    }
                }
            )
    }

    private fun updateTask() {
        if(isProcessing) {
            return
        }
        isProcessing = true
        RetrofitClient(parentActivity.authToken).getRetrofit().create(ITaskService::class.java)
            .updateTask(task!!.id, task!!.title, task!!.dueBy, task!!.priority)
            .enqueue(
                object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        isProcessing = false
                        if (response.isSuccessful) {
                            Toast.makeText(context!!, getString(R.string.task_updated), Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        } else {
                            showErrorSnackBar(getString(R.string.task_update_failed), titleEditText)
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        isProcessing = false
                        showErrorSnackBar(getString(R.string.task_update_failure), titleEditText)
                    }
                }
            )
    }

    private fun deselectAllButtons() {
        highPriorityButton.setCompoundDrawables(null, null, null, null)
        mediumPriorityButton.setCompoundDrawables(null, null, null, null)
        lowPriorityButton.setCompoundDrawables(null, null, null, null)
    }

    private fun updateScreen() {
        titleEditText.setText(task!!.title)
        selectedPriority = task!!.priority

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = task!!.dueBy.toLong() * 1000
        selectedDueDate = calendar.time
        dueDateTextView.text = dateTimeFormat.format(selectedDueDate)

        deselectAllButtons()

        val drawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_check)
        val size = resources.getDimensionPixelSize(R.dimen.defaultMargin)
        drawable!!.setBounds(0, 0, size, size)

//        when (task!!.priority) {
//            context!!.getString(R.string.high) -> {
//                highPriorityButton.setCompoundDrawables(drawable, null, null, null)
//                highPriorityButton.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.red))
//            }
//            context!!.getString(R.string.normal) -> {
//                mediumPriorityButton.setCompoundDrawables(drawable, null, null, null)
//                mediumPriorityButton.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.orange))
//            }
//            context!!.getString(R.string.low) -> {
//                lowPriorityButton.setCompoundDrawables(drawable, null, null, null)
//                lowPriorityButton.compoundDrawableTintList = ColorStateList.valueOf(context!!.getColor(R.color.green))
//            }
//        }

        when (task!!.priority) {
            getString(R.string.high) -> {
                highPriorityButton.performClick()
            }
            getString(R.string.normal) -> {
                mediumPriorityButton.performClick()
            }
            getString(R.string.low) -> {
                lowPriorityButton.performClick()
            }
        }
    }
}
