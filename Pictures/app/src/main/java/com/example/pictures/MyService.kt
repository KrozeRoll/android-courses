package com.example.pictures

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import android.util.LruCache
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL

class MyService : Service() {
    var cacheSize = 4 * 1024 * 1024
    private var imageCache : LruCache<String?, ByteArray> = LruCache<String?, ByteArray>(cacheSize)
    //private var imageMap = ConcurrentHashMap<String?, ByteArray>()

    fun makeBroadcast(content: Pair<ByteArray, String>?) {
        val image = content?.first
        val url = content?.second

        if (image != null) {
            synchronized (imageCache) {
                imageCache.put(url, image);
            }
            sendBroadcast(Intent().apply {
                action = "RESPONSE"
                addCategory(Intent.CATEGORY_DEFAULT)
                putExtra("image", image)
            })
        }
    }

    class PictureAsyncTask(service: MyService) : AsyncTask<String, Unit, Pair<ByteArray, String>?>() {
        private val serviceRef = WeakReference(service)

        override fun doInBackground(vararg params: String?): Pair<ByteArray, String>? {
            Log.d("ASYNC_TASK", params[0].toString())
            return if (!isCancelled) {
                val input : InputStream = URL(params[0] ?: "").openStream()
                val image = BitmapFactory.decodeStream(input)
                val imageArray = ByteArrayOutputStream().apply {
                    image.compress(Bitmap.CompressFormat.JPEG, 20, this)
                }.toByteArray()
                Pair(imageArray, params[0]!!)
            } else {
                null
            }
        }

        override fun onPostExecute(result: Pair<ByteArray, String>?) {
            Log.d("ASYNC_TASK", "po krasote result")
            serviceRef.get()?.makeBroadcast(result)
        }

        override fun onCancelled() {
            Log.d("ASYNC_TASK", "canceled")
            super.onCancelled()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.extras?.getString("url")
        synchronized (imageCache) {
            if (imageCache.get(url) != null) {
                val image = imageCache.get(url)
                makeBroadcast(Pair(image!!, url!!))
            } else {
                PictureAsyncTask(this).execute(url)
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        synchronized(imageCache) {
            imageCache.evictAll()
        }
        super.onDestroy()
    }
}
