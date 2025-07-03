package com.example.gotravelapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.gotravelapp.entities.User;

import java.util.List;
@Dao
public interface UserDAO {

    @Insert
    void insert(User user);

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM user_table")
    List<User> getAllUsers();
}
