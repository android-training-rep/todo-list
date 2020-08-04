package com.thoughtworks.todo_list.repository.task;

import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class TaskRepositoryImpl implements TaskRepository {
    private TaskDataSource dataSource;

    public TaskRepositoryImpl(TaskDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Maybe<Task> findById(int id) {
        return dataSource.findById(id);
    }

    public Maybe<List<Task>> findAllTasks() {
        return dataSource.findAllTasks();
    }

    public Single<Long> save(Task task) {
        return dataSource.save(task);
    }

    public Maybe<Integer> update(Task task) {
        return dataSource.update(task);
    }

    public Maybe<Integer> delete(Task task) {
        return dataSource.delete(task);
    }
}
