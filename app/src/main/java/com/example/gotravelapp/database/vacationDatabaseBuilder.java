package com.example.gotravelapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gotravelapp.dao.ExcursionDAO;
import com.example.gotravelapp.dao.UserDAO;
import com.example.gotravelapp.dao.VacationDAO;
import com.example.gotravelapp.entities.Excursion;
import com.example.gotravelapp.entities.User;
import com.example.gotravelapp.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class, User.class}, version = 16, exportSchema = false)

public abstract class vacationDatabaseBuilder extends RoomDatabase {
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    public abstract UserDAO userDao();
    private static volatile vacationDatabaseBuilder INSTANCE;

    static vacationDatabaseBuilder getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (vacationDatabaseBuilder.class){
                if (INSTANCE == null) {
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(), vacationDatabaseBuilder.class, "MyVacationDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
