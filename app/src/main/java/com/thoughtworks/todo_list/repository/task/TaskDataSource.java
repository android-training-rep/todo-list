package com.thoughtworks.todo_list.repository.task;

import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface TaskDataSource {
    Maybe<Task> findById(int id);
    Maybe<List<Task>> findAllTasks();
    Single<Long> save(Task task);
    Maybe<Integer> update(Task task);
    Maybe<Integer> delete(Task task);
}
