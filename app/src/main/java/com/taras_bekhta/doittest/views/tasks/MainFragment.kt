package com.taras_bekhta.doittest.views.tasks


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import androidx.navigation.fragment.findNavController
import com.taras_bekhta.doittest.R
import com.taras_bekhta.doittest.events.TaskClickedEvent
import com.taras_bekhta.doittest.retrofit.ITaskService
import com.taras_bekhta.doittest.retrofit.RetrofitClient
import com.taras_bekhta.doittest.retrofit.Task
import com.taras_bekhta.doittest.retrofit.TasksResponse
import com.taras_bekhta.doittest.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : BaseFragment() {

    private var tasks = arrayListOf<Task>()
    private var isAscending = true
    private var selectedSortType = TasksAdapter.SortType.SORT_BY_PRIORITY

    override fun getTitle(): String {
        return getString(R.string.tasks)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TasksAdapter(tasks, context!!)
        tasksRecyclerView.adapter = adapter
        tasksRecyclerView.layoutManager = LinearLayoutManager(parentActivity)

        var itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.tasks_divider)!!)
        tasksRecyclerView.addItemDecoration(itemDecorator)

        refreshLayout.isRefreshing = true
        loadTasks()

        addTaskFab.setOnClickListener {
            val dir = MainFragmentDirections.actionMainFragmentToEditTaskFragment(true, -1)
            findNavController().navigate(dir)
        }

        refreshLayout.setOnRefreshListener {
            //AnimationUtil.animateViewChangeScaleFade(tasksRecyclerView, tasksProgressBar)
            loadTasks()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_tasks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_name -> {
                selectedSortType = TasksAdapter.SortType.SORT_BY_TITLE
            }
            R.id.action_sort_due_date -> {
                selectedSortType = TasksAdapter.SortType.SORT_BY_DATE
            }
            R.id.action_sort_priority -> {
                selectedSortType = TasksAdapter.SortType.SORT_BY_PRIORITY
            }
            R.id.action_order_ascending -> {
                isAscending = true
            }
            R.id.action_order_descending -> {
                isAscending = false
            }
            else -> return false
        }
        (tasksRecyclerView.adapter as TasksAdapter).sortTasks(selectedSortType, isAscending)

        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun taskClicked(event: TaskClickedEvent) {
        val dir = MainFragmentDirections.actionMainFragmentToTaskDetailsFragment(event.taskId)
        findNavController().navigate(dir)
    }

    private fun updateTasks(newTasks: List<Task>) {
        if(tasksRecyclerView == null) {
            return
        }
        tasks.clear()
        tasks.addAll(newTasks)

        tasksRecyclerView.adapter!!.notifyDataSetChanged()
        (tasksRecyclerView.adapter as TasksAdapter).sortTasks(selectedSortType, isAscending)
        //AnimationUtil.animateViewChangeScaleFade(tasksProgressBar, tasksRecyclerView, true)
    }

    private fun loadTasks() {
        RetrofitClient(parentActivity.authToken).getRetrofit().create(ITaskService::class.java).getTasks().enqueue(
            object : Callback<TasksResponse> {
                override fun onResponse(call: Call<TasksResponse>, response: Response<TasksResponse>) {
                    if (response.isSuccessful) {
                        updateTasks(response.body()!!.tasks)
                    } else {
                        showErrorSnackBar(getString(R.string.tasks_loading_failed), tasksRecyclerView)
                    }
                    if(refreshLayout != null) {
                        refreshLayout.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<TasksResponse>, t: Throwable) {
                    showErrorSnackBar(getString(R.string.tasks_loading_failed), tasksRecyclerView)
                    if(refreshLayout != null) {
                        refreshLayout.isRefreshing = false
                    }
                }
            }
        )
    }

}
