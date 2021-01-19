package com.example.fakeapi

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyService : Service() {
    private var getCall : Call<List<Post>>? = null
    private var addCall : Call<Post>? = null
    private var delCall : Call<ResponseBody>? = null
    private var lastId : Int = 101

    private fun savePostAnswer(post : Post?, resultCode : Int) {
        val postDao = MyApp.instance.dataBase.postDao()
        if (post != null) {
            GlobalScope.launch(Dispatchers.IO) {
                postDao.insertInDao(post)
            }
        }

        sendBroadcast(Intent().apply {
            action = "response"
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra("operation", "post")
            putExtra("post", post)
            putExtra("resultCode", resultCode)
        })
    }

    private fun post(title : String, body : String) {
        var curPost: Post?
        addCall = MyApp.instance.apiService.postPost(PostBody(title, body, 1))
        addCall?.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    curPost = response.body()
                    savePostAnswer(curPost, 0)
                } else {
                    savePostAnswer(Post(lastId++, title, body, 1), 1)
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d(
                    "APIService/Post",
                    "$t happened"
                )
                savePostAnswer(Post(lastId++, title, body, 1), 1)
            }
        })
    }

    private fun saveDeleteAnswer(message : String, post : Post?) {
        val postDao = MyApp.instance.dataBase.postDao()
        if (post != null) {
            GlobalScope.launch(Dispatchers.IO) {
                postDao.deleteFromDao(post)
            }
        }
        sendBroadcast(Intent().apply {
            action = "response"
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra("operation", "delete")
            putExtra("message", message)
            putExtra("post", post)
        })
    }

    private fun delete(post : Post) {
        val currentId = post.id
        delCall = MyApp.instance.apiService.deletePost(currentId)
        delCall?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    saveDeleteAnswer("Deleted $response", post)
                } else {
                    saveDeleteAnswer("Connection problem, try again", post)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                saveDeleteAnswer("Connection problem, try again", post)
            }
        })
    }

    private fun saveUploadAnswer(resList: List<Post>?) {
        val list = resList as MutableList<Post>?
        if (list == null) {
            sendBroadcast(Intent().apply {
                action = "response"
                addCategory(Intent.CATEGORY_DEFAULT)
                putExtra("operation", "upload")
                putExtra("has_list", false)
            })
        } else {
            sendBroadcast(Intent().apply {
                action = "response"
                addCategory(Intent.CATEGORY_DEFAULT)
                putExtra("has_list", true)
                putExtra("operation", "upload")
                putExtra("list", list as ArrayList<Post>)
            })
        }
    }

    private fun upload() {
        getCall = MyApp.instance.apiService.listPosts()
        var list : List<Post>? = null
        getCall?.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    list = response.body()
                }
                saveUploadAnswer(list)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                saveUploadAnswer(list)
            }
        })
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.extras?.getString("operation")) {
            "post" -> {
                val title = intent.extras?.getString("title")
                val body = intent.extras?.getString("body")
                if (title != null && body != null) {
                    post(title, body)
                }
            }
            "delete" -> {
                val curPost = intent.extras?.getParcelable<Post>("post")
                if (curPost != null) {
                    delete(curPost)
                }
            }
            "upload" -> {
                upload()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        addCall?.cancel()
        addCall = null
        getCall?.cancel()
        getCall = null
        delCall?.cancel()
        delCall = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
