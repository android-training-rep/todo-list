package com.thoughtworks.todo_list.repository.task;

import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface TaskDataSource {
    Maybe<Task> findById(int id);
    List<Task> findAllTasks();
    Single<Long> save(Task task);
    Completable update(Task task);
    Completable delete(Task task);
}
