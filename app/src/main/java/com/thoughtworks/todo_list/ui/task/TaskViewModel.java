package com.thoughtworks.todo_list.ui.task;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.Collections;
import java.util.Comparator;
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
                // todo 自定义排序抽到工具类中
                Collections.sort(tasks, new Comparator(){
                    public int compare(Object obj1, Object obj2) {
                        Task task1 = (Task) obj1;
                        Task task2 = (Task) obj2;
                        if(task1.isCompleted() != task2.isCompleted()) {
                            return task1.isCompleted() ? 1 : -1;
                        } else {
                            return task1.getDeadline().compareTo(task2.getDeadline());
                        }
                    }
                });
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
