package com.thoughtworks.todo_list.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getName();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiey_home);

        recyclerView = findViewById(R.id.task_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // todo get task list
        List<Task> tasks = loadTasks();
        myAdapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(myAdapter);


        FloatingActionButton fab = findViewById(R.id.add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,TaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<Task>();

        for (int i = 0; i < 6; i++) {
            Task task = new Task();
            task.setTitle("todo title"+i);
            task.setContent("todo content"+i);
            task.setDeadline("2020-07-31");
            task.setRemind(true);
            task.setDeleted(i%2 == 0 ? false : true);
            tasks.add(task);
        }
        Log.d(TAG, "-------------------Task size:-------------------" + tasks.size());
        return tasks;
    }
}