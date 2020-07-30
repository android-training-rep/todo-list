package com.thoughtworks.todo_list.repository.task;

import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface TaskRepository {
    Maybe<Task> findById(int id);
    Maybe<List<Task>> getTask();
    Completable save(Task task);
    Completable update(Task task);
    Completable delete(Task task);
}
