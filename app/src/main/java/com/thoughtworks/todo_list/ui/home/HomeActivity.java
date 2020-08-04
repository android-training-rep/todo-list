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
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getName();
    private TaskRepository taskRepository;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView dayView, monthView, countTaskView;
    private HomeAdapter myAdapter;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiey_home);

//        configCustomActionBar();

        homeViewModel = obtainViewModel();

        initRecyclerView(homeViewModel);

        Observer<List<Task>> observer = tasks -> {
            myAdapter.setTasks(tasks);
            countTaskView.setText("Total:" + tasks.size());
        };
        homeViewModel.getTasks().observe(this, observer);

        homeViewModel.getToDetail().observe(this, task -> {
            this.openTaskActivityWithExtra(task);
        });
        homeViewModel.getUpdateTask().observe(this, task -> {
            Log.d(TAG, "after:"+task.isCompleted());

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

        dayView = findViewById(R.id.day);
        monthView = findViewById(R.id.month);
        dayView.setText(toCurrentDayAndWeek());
        monthView.setText(toCurrentMonth());
        countTaskView = findViewById(R.id.count_task);


    }

    private String toCurrentDayAndWeek() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE,  dd", Locale.ENGLISH);
        return dateFormat.format(new Date()).concat("th");
    }

    private String toCurrentMonth() {
        return String.format(Locale.US, "%tB", new Date());
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_exit) {
//            // todo 处理退出逻辑
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}