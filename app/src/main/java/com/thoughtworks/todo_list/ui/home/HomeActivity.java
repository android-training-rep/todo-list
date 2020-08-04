package com.thoughtworks.todo_list.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;
import com.thoughtworks.todo_list.repository.utils.RecyclerViewDivider;
import com.thoughtworks.todo_list.ui.task.TaskActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getName();
    private TaskRepository taskRepository;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView todayView, countTaskView;
    private HomeAdapter myAdapter;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiey_home);

        configCustomActionBar();

        homeViewModel = obtainViewModel();

        initRecyclerView(homeViewModel);

        Observer<List<Task>> observer = tasks -> {
            myAdapter.setTasks(tasks);
            countTaskView.setText("Total: " + tasks.size());
        };
        homeViewModel.getTasks().observe(this, observer);

        homeViewModel.getToDetail().observe(this, task -> {
            this.openTaskActivityWithExtra(task);
        });
        homeViewModel.getUpdateTask().observe(this, task -> {
            this.updateTask(task);
        });

        homeViewModel.loadTasks();

        FloatingActionButton addTaskBtn = findViewById(R.id.add_task);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTaskActivityWithExtra(null);
            }
        });
    }

    private void updateTask(Task task) {
        homeViewModel.updateTask(task);
    }

    private void initRecyclerView(HomeViewModel homeViewModel) {
        recyclerView = findViewById(R.id.task_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new HomeAdapter(homeViewModel);
        recyclerView.setAdapter(myAdapter);
        recyclerView.addItemDecoration(new RecyclerViewDivider(HomeActivity.this, LinearLayoutManager.HORIZONTAL));
    }

    private void openTaskActivityWithExtra(Task task) {
        Intent intent = new Intent(this, TaskActivity.class);
        if (Objects.nonNull(task)) {
            String taskJson = new Gson().toJson(task);
            intent.putExtra("exist", taskJson);
        }
        startActivity(intent);
    }

    private HomeViewModel obtainViewModel() {
        taskRepository = (((MainApplication) getApplicationContext())).taskRepository();
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setTaskRepository(taskRepository);
        return homeViewModel;
    }

    private void configCustomActionBar() {
        // todo 修改 actionbar 和 menu 样式
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.home_action_bar);
            actionBar.setDisplayHomeAsUpEnabled(true);

            todayView = actionBar.getCustomView().findViewById(R.id.today);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            todayView.setText(simpleDateFormat.format(new Date()));
            countTaskView = actionBar.getCustomView().findViewById(R.id.count_task);
        }else {
            Log.d(TAG,"action bar is null");
        }
    }

    /* 数据库不可用时可先使用mock task */
    private List<Task> loadMockTasks() {
        List<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 12; i++) {
            Task task = new Task();
            task.setTitle("todo title"+i);
            task.setContent("todo content"+i);
            task.setDeadline("2020-07-31");
            task.setRemind(true);
            task.setCompleted(i%2 == 0 ? false : true);
            tasks.add(task);
        }
        return tasks;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            // todo 处理退出逻辑
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}