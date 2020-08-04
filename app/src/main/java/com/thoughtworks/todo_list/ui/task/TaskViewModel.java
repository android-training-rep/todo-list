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

import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
                        saveResult.postValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "save task failure");
                        saveResult.postValue(false);
                    }
                });
    }

    public void update(Task task) {
        taskRepository.update(task).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d(TAG, "update task successfully" + integer);
                        updateResult.postValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "update task failure");
                        updateResult.postValue(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "update task complete");
                    }
                });
    }

    public void delete(Task task) {

    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
