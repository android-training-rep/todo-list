package com.thoughtworks.todo_list.ui.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.Calendar;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    private TaskRepository taskRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean remind = false;
    private TextView calendarText;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        configCustomActionBar();

        TaskViewModel taskViewModel = obtainViewModel();


        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

             @Override
             public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                 String selectedDay = i + " " + (i1+1) + " " + i2;
                 calendarText.setText(selectedDay);
//                 calendarView.setVisibility(View.GONE);
             }
        });

        FloatingActionButton saveBtn = findViewById(R.id.save_task);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void configCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.task_action_bar);  //绑定自定义的布局：actionbar_layout.xml

            ImageView remindView = actionBar.getCustomView().findViewById(R.id.action_remind);
            calendarText = findViewById(R.id.deadline);

            remindView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remind = !remind;
                    if (remind) {
                        remindView.setImageResource(R.drawable.remind_selected);
                    } else {
                        remindView.setImageResource(R.drawable.remind_unselected);
                    }
                }
            });

            calendarText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "calendar click!");
                    calendarView.setVisibility(View.VISIBLE);
                }
            });
        }else {
            Log.e("actionbar","is null");
        }
    }

    private void saveTask() {
//        final CalendarView calendarView = (CalendarView) findViewById(R.id.deadline);
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
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        Log.d(TAG, "save task successfully" + aLong);
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
}