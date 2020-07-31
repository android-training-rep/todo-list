package com.thoughtworks.todo_list.ui.task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.Calendar;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    private TaskRepository taskRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean remind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        Toolbar taskToolbar = findViewById(R.id.task_toolbar);
        setSupportActionBar(taskToolbar);



        FloatingActionButton saveBtn = findViewById(R.id.save_task);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        final CalendarView calendarView = (CalendarView) findViewById(R.id.deadline);
        final Calendar c = Calendar.getInstance();
        String deadline = String.valueOf(c.get(Calendar.YEAR)) + " "
                + String.valueOf(c.get(Calendar.MONTH)) + " "
                + String.valueOf(c.get(Calendar.DATE));

        EditText titleView = findViewById(R.id.title);
        EditText contentView = findViewById(R.id.content);

        Task task = new Task();
        task.setTitle(titleView.getText().toString());
        task.setContent(contentView.getText().toString());
        task.setDeadline(deadline);
        task.setRemind(remind);
        task.setDeleted(false);

        taskRepository.save(task).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "save task successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "save task failure");
                    }
                });


    }

    private TaskViewModel obtainViewModel() {
        taskRepository = (((MainApplication) getApplicationContext())).taskRepository();
        TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.setTaskRepository(taskRepository);
        return taskViewModel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_remind) {
            remind = !remind;
            if (remind) {
                item.setIcon(R.drawable.remind_selected);
            } else {
                item.setIcon(R.drawable.remind_unselected);
            }
            Log.d(TAG, ""+remind);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}