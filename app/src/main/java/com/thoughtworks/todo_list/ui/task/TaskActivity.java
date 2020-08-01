package com.thoughtworks.todo_list.ui.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    private TaskRepository taskRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextView calendarSelected;
    private CalendarView calendarView;
    private boolean isRemind = false;
    private boolean isCompleted = false;
    private String deadline = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        configCustomActionBar();

        TaskViewModel taskViewModel = obtainViewModel();


        // todo 使calendar逻辑生效
        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setVisibility(View.GONE);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

             @Override
             public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                 deadline = i + "-" + (i1+1) + "-" + i2;
                 calendarSelected.setText(deadline);
                 calendarView.setVisibility(View.GONE);
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
        // todo 修改actionbar样式
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.task_action_bar);  //绑定自定义的布局：actionbar_layout.xml
            actionBar.setDisplayHomeAsUpEnabled(true);

            CheckBox isCompletedView = actionBar.getCustomView().findViewById(R.id.complete);

            ImageView remindView = actionBar.getCustomView().findViewById(R.id.action_remind);
            calendarSelected = findViewById(R.id.deadline);

            remindView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRemind = !isRemind;
                    if (isRemind) {
                        remindView.setImageResource(R.drawable.remind_selected);
                    } else {
                        remindView.setImageResource(R.drawable.remind_unselected);
                    }
                }
            });

            calendarSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "calendar click!");
                    calendarView.setVisibility(View.VISIBLE);
                }
            });

            isCompletedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    isCompleted = b;
                }
            });
        }else {
            Log.e("actionbar","is null");
        }
    }

    private void saveTask() {
        EditText titleView = findViewById(R.id.title);
        EditText contentView = findViewById(R.id.content);

        Task task = new Task();
        task.setTitle(titleView.getText().toString());
        task.setContent(contentView.getText().toString());
        task.setDeadline(deadline);
        task.setRemind(isRemind);
        task.setCompleted(isCompleted);

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