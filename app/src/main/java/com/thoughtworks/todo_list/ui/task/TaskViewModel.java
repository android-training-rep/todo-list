package com.thoughtworks.todo_list.ui.task;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;
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
        try {
            taskRepository.findAllTasks().subscribe(new FlowableSubscriber<List<Task>>() {
                @Override
                public void onSubscribe(Subscription s) {
                    Log.d(TAG, "--------------------onSubscribe");

                }

                @Override
                public void onNext(List<Task> tasks) {
                    Log.d(TAG, "--------------------onNext");
                }

                @Override
                public void onError(Throwable t) {
                    Log.d(TAG, "--------------------onError");

                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "--------------------onComplete");

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "--------------------Exception"+ e);

        }
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
