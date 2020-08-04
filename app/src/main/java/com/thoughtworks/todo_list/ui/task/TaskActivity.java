package com.thoughtworks.todo_list.ui.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.thoughtworks.todo_list.MainApplication;
import com.thoughtworks.todo_list.R;
import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;
import com.thoughtworks.todo_list.ui.home.HomeActivity;
import java.util.Objects;
import io.reactivex.disposables.CompositeDisposable;

public class TaskActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    private TaskViewModel taskViewModel;
    private Task existTask = null;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FloatingActionButton saveBtn;
    private CalendarView calendarView;

    private TextView calendarSelected;
    private EditText contentView;
    private EditText titleEditText;
    private CheckBox isCompletedView;
    private ImageView remindView;

    private boolean isRemind = false;
    private boolean isCompleted = false;
    private String deadline = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


//        configCustomActionBar();

        taskViewModel = obtainViewModel();

        Observer<Boolean> saveObserver = aBoolean -> {
            Toast.makeText(getApplicationContext(), aBoolean ? "新增成功" : "新增失败", Toast.LENGTH_SHORT)
                    .show();
            openHomeActivity();
        };
        taskViewModel.getSaveResult().observe(this, saveObserver);

        Observer<Boolean> deleteObserver = aBoolean -> {
            Toast.makeText(getApplicationContext(), aBoolean ? "删除成功" : "删除失败", Toast.LENGTH_SHORT)
                    .show();
            openHomeActivity();
        };
        taskViewModel.getDeleteResult().observe(this, deleteObserver);

        Observer<Boolean> updateObserver = aBoolean -> {
            Toast.makeText(getApplicationContext(), aBoolean ? "修改成功" : "修改失败", Toast.LENGTH_SHORT)
                    .show();
            openHomeActivity();
        };
        taskViewModel.getUpdateResult().observe(this, updateObserver);


        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setVisibility(View.GONE);

        isCompletedView = findViewById(R.id.complete);
        remindView = findViewById(R.id.action_remind);
        calendarSelected = findViewById(R.id.deadline);
        titleEditText = findViewById(R.id.title);
        contentView = findViewById(R.id.content);


        saveBtn = findViewById(R.id.save_task);
        saveBtn.setEnabled(false);
        updateSaveButtonState();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });

        addListener();
        initTask();
    }

    private void addListener() {
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

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){

            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                deadline = i + "-" + (i1+1) + "-" + i2;
                calendarSelected.setText(deadline);
                calendarView.setVisibility(View.GONE);
                updateSaveButtonState();
            }
        });

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
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void initTask() {
        Intent intent = getIntent();
        String existTaskJson = intent.getStringExtra("exist");
        existTask = new Gson().fromJson(existTaskJson, Task.class);

        if (Objects.nonNull(existTask)) {
            Log.d(TAG, "EXIST HAVE TASK");
            // todo 视图初始化
            titleEditText.setText(existTask.getTitle());

        }
    }

    private void saveTask() {


        Task task = new Task();
        task.setTitle(titleEditText.getText().toString());
        task.setContent(contentView.getText().toString());
        task.setDeadline(deadline);
        task.setRemind(isRemind);
        task.setCompleted(isCompleted);

        taskViewModel.save(task);
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

    private TaskViewModel obtainViewModel() {
        TaskRepository taskRepository = (((MainApplication) getApplicationContext())).taskRepository();
        TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.setTaskRepository(taskRepository);
        return taskViewModel;
    }
}