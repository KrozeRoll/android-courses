package com.example.fakeapi

import android.app.Application
import android.util.Log

class MyApp : Application() {
    lateinit var apiService : APIService

    override fun onCreate() {
        Log.d("MyApp", "onCreate")
        super.onCreate()
        instance = this
        apiService = APIService.create()
    }

    companion object {
        lateinit var instance : MyApp
            private set
    }
}