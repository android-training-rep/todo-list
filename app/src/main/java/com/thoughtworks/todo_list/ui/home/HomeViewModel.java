package com.thoughtworks.todo_list.ui.home;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class HomeViewModel extends ViewModel {
    public static final String TAG = "TaskViewModel";
    private MutableLiveData<List<Task>> tasks = new MutableLiveData<List<Task>>();
    private MutableLiveData<Task> toDetail = new MutableLiveData<Task>();
    private MutableLiveData<Task> updateTask = new MutableLiveData<Task>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TaskRepository taskRepository;

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public LiveData<List<Task>> getTasks() {
        if (Objects.isNull(tasks)) {
            tasks = new MutableLiveData<>();
        }
        return tasks;
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
                HomeViewModel.this.tasks.postValue(tasks);
            }
        }).start();
    }

    public void observeTasks(LifecycleOwner lifecycleOwner, Observer<List<Task>> observer) {
        tasks.observe(lifecycleOwner, observer);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }

    public void updateTask(Task task) {
        this.taskRepository.update(task).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "update successfully");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "update failure");
            }
        });
    }
}
