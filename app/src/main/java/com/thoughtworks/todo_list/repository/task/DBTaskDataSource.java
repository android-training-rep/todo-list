package com.thoughtworks.todo_list.repository.task;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thoughtworks.todo_list.repository.task.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface DBTaskDataSource extends TaskDataSource {
    @Query("SELECT * FROM task WHERE id = :id")
    Maybe<Task> findById(int id);

    @Query("SELECT * FROM task")
    Flowable<List<Task>> findAllTasks();

    @Insert
    Single<Long> save(Task task);

    @Update
    Completable update(Task task);

    @Delete
    Completable delete(Task task);
}
