package com.example.fakeapi

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostsDao {
    @Query("SELECT * FROM Post")
    fun getFromDao() : List <Post>

    @Insert
    fun insertInDao(post : Post)

    @Delete
    fun deleteFromDao(post : Post)
}