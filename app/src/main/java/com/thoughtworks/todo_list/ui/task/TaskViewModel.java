package com.thoughtworks.todo_list.ui.task;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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
        taskRepository.findAllTasks()
                .subscribe(new MaybeObserver<List<Task>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "-------------------onSubscribe-------------------");
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Task> tasks) {
                        Log.d(TAG, "-------------------onSuccess-------------------");
                        // todo 排序，vm变化
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "-------------------onError-------------------");

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "-------------------onComplete-------------------");

                    }
                });
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
