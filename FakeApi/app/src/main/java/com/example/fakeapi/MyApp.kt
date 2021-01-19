package com.example.fakeapi

import android.app.Application
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

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