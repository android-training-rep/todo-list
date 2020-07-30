package com.thoughtworks.todo_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.thoughtworks.todo_list.repository.AppDatabase;
import com.thoughtworks.todo_list.repository.task.TaskRepository;
import com.thoughtworks.todo_list.repository.task.TaskRepositoryImpl;
import com.thoughtworks.todo_list.ui.login.UserRepository;
import com.thoughtworks.todo_list.repository.user.UserRepositoryImpl;

public class MainApplication extends Application {
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase db = getDatabase();
        userRepository = new UserRepositoryImpl(db.userDBDataSource());
        taskRepository = new TaskRepositoryImpl(db.taskDataSource());
    }


    public AppDatabase getDatabase() {
        return Room.databaseBuilder(getApplicationContext(), AppDatabase.class, this.getClass().getSimpleName())
//                .addMigrations(migration_2_to_3)
                .fallbackToDestructiveMigration()
                .build();
    }

    public UserRepository userRepository() {
        return userRepository;
    }

    public TaskRepository taskRepository() {
        return taskRepository;
    }

    //数据库升级用的
    static Migration migration_2_to_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("create table task(id INTEGER primary key, title TEXT, content TEXT, deadline TEXT, isRemind INTEGER, isDelete INTEGER)");
        }
    };
}
