package com.thoughtworks.todo_list.ui.task;

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

import io.reactivex.disposables.CompositeDisposable;

public class TaskViewModel extends ViewModel {
    public static final String TAG = "TaskViewModel";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<Boolean> saveResult;
    private MutableLiveData<Boolean> updateResult;
    private MutableLiveData<Boolean> deleteResult;

    private TaskRepository taskRepository;

    public LiveData<Boolean> getSaveResult() {
        if (Objects.isNull(saveResult)) {
            saveResult = new MutableLiveData<>();
        }
        return saveResult;
    }

    public LiveData<Boolean> getUpdateResult() {
        if (Objects.isNull(updateResult)) {
            updateResult = new MutableLiveData<>();
        }
        return updateResult;
    }

    public LiveData<Boolean> getDeleteResult() {
        if (Objects.isNull(deleteResult)) {
            deleteResult = new MutableLiveData<>();
        }
        return deleteResult;
    }

    void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public void save(Task task) {

    }

    public void update(Task task) {

    }

    public void delete(Task task) {

    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
