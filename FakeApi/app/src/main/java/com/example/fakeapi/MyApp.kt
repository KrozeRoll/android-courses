package com.example.fakeapi

import android.app.Application
import android.util.Log
import androidx.room.Room

class MyApp : Application() {
    lateinit var apiService : APIService
    lateinit var dataBase : AppDatabase

    override fun onCreate() {
        Log.d("MyApp", "onCreate")
        super.onCreate()
        instance = this
        apiService = APIService.create()
        dataBase = Room.databaseBuilder(this, AppDatabase::class.java, "dataBase")
            .build()
    }

    companion object {
        lateinit var instance : MyApp
            private set
    }
}