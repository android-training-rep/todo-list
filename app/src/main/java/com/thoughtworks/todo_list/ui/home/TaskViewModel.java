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
    private MutableLiveData<Task> toDetail = new MutableLiveData<Task>();
    private MutableLiveData<Task> updateTask = new MutableLiveData<Task>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TaskRepository taskRepository;

    void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public LiveData<List<Task>> getTaskList() {
        if (Objects.isNull(taskList)) {
            taskList = new MutableLiveData<>();
        }
        return taskList;
    }

    public LiveData<Task> getToDetail() {
        if (Objects.isNull(toDetail)) {
            toDetail = new MutableLiveData<>();
        }
        return toDetail;
    }

    public LiveData<Task> getUpdateTask() {
        if (Objects.isNull(updateTask)) {
            updateTask = new MutableLiveData<>();
        }
        return updateTask;
    }

    public void setToDetail(Task task) {
        toDetail.postValue(task);
    }

    public void setUpdateTask(Task task) {
        updateTask.postValue(task);
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

    // TODO ADD UPDATE FUNCTION

    void observeTaskList(LifecycleOwner lifecycleOwner, Observer<List<Task>> observer) {
        taskList.observe(lifecycleOwner, observer);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
