package com.byted.camp.todolist.db.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@Database(entities = {TodoEntity.class}, version = 1, exportSchema = false)
@TypeConverters(value = {Converters.class}) // add converters
public abstract class TodoRoomDatabase extends RoomDatabase {

    public abstract TodoDao todoDao();

    private static TodoRoomDatabase INSTANCE;

    public static TodoRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TodoRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TodoRoomDatabase.class, "todo_list_database")
                            .fallbackToDestructiveMigrationOnDowngrade() // we just care about upgrade yet
//                            .fallbackToDestructiveMigration() // destructive upgrade
                            .addMigrations(MIGRATION_1_2)
                            .addCallback(populateCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback populateCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // populate only when the database is created
            Disposable d = Single.just(INSTANCE).subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<TodoRoomDatabase>() {
                        @Override
                        public void accept(TodoRoomDatabase todoRoomDatabase) {
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 1", Priority.from(2))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 2", Priority.from(2))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 3", Priority.from(1))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 4", Priority.from(0))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 5", Priority.from(2))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 6", Priority.from(0))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 7", Priority.from(0))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 8", Priority.from(1))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "Some content 9", Priority.from(0))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "More content 1", Priority.from(0))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "More content 2", Priority.from(0))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "More content 3", Priority.from(0))).subscribe();
                            todoRoomDatabase.todoDao().insertTodo(new TodoEntity(Calendar.getInstance().getTime(), State.from(0), "More content 4", Priority.from(1))).subscribe();
                        }
                    });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
            // do the migration job here
        }
    };
}
