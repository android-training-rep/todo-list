package com.thoughtworks.todo_list.ui.task;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class TaskViewModel extends ViewModel {
    public static final String TAG = "TaskViewModel";
    private MutableLiveData<List<Task>> taskList = new MutableLiveData<List<Task>>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TaskRepository taskRepository;

    void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    void observeTaskList(LifecycleOwner lifecycleOwner, Observer<List<Task>> observer) {
        taskList.observe(lifecycleOwner, observer);
    }

    public void loadTasks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = taskRepository.findAllTasks();
                // todo task排序
                taskList.postValue(tasks);
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
