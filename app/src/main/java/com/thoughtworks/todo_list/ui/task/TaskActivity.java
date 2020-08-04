package com.thoughtworks.todo_list.ui.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    private TaskRepository taskRepository;
    private Task existTask = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FloatingActionButton saveBtn;
    private TextView calendarSelected;
    private CalendarView calendarView;
    private EditText titleEditText;
    private boolean isRemind = false;
    private boolean isCompleted = false;
    private String deadline = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initTask();

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
                 updateSaveButtonState();
             }
        });

        titleEditText = findViewById(R.id.title);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSaveButtonState();
            }
        });


        saveBtn = findViewById(R.id.save_task);
        saveBtn.setEnabled(false);
        updateSaveButtonState();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void initTask() {
        Intent intent = getIntent();
        String existTaskJson = intent.getStringExtra("exist");
        existTask = new Gson().fromJson(existTaskJson, Task.class);

        if (Objects.nonNull(existTask)) {
            Log.d(TAG, "EXIST HAVE TASK");
            // todo 视图初始化
        }
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

    private ColorStateList getColorStateListTest(int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int color = ContextCompat.getColor(getApplicationContext(), colorRes);
        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }

    private void updateSaveButtonState(){
        if(titleEditText.getText().toString() != "" && deadline != "") {
            saveBtn.setEnabled(true);
            ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.colorBlue);
            saveBtn.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
            saveBtn.setBackgroundTintList(colorStateList);
        } else {
            saveBtn.setEnabled(false);
            ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.colorPrimary);
            saveBtn.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
            saveBtn.setBackgroundTintList(colorStateList);
        }
    }

    private void saveTask() {
        EditText contentView = findViewById(R.id.content);

        Task task = new Task();
        task.setTitle(titleEditText.getText().toString());
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