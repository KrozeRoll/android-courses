package com.example.fakeapi

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface PostsDao {
    @Insert
    fun insertInDao(post : Post)

    @Delete
    fun deleteFromDao(post : Post)
}