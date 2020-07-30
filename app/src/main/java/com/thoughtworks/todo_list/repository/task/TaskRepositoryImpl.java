package com.thoughtworks.todo_list.repository.task;

import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class TaskRepositoryImpl implements TaskRepository {
    private TaskDataSource dataSource;

    public TaskRepositoryImpl(TaskDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Maybe<Task> findById(int id) {
        return dataSource.findById(id);
    }

    public Maybe<List<Task>> getTask() {
        return dataSource.getTask();
    }

    public Completable save(Task task) {
        return dataSource.save(task);
    }

    public Completable update(Task task) {
        return dataSource.update(task);
    }

    public Completable delete(Task task) {
        return dataSource.delete(task);
    }
}
