package com.example.pictures

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.image_fragment.*
import java.io.InputStream
import java.net.URL


class ImageFragment : Fragment() {
    var url : String = ""
    var imageBitmap : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.image_fragment, container, false)
    }

    class PictureAsyncTask : AsyncTask<String, Unit, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            Log.d("ASYNC_TASK", params[0].toString())
            if (!isCancelled()) {
                val input: InputStream = URL(params[0] ?: "").openStream()
                val image = BitmapFactory.decodeStream(input)
                return image
            } else {
                return null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            Log.d("ASYNC_TASK", "po krasote result")
            imageBitmap = result
            image?.setImageBitmap(result)
            progressBar?.visibility = ProgressBar.INVISIBLE
        }

        override fun onCancelled() {
            Log.d("ASYNC_TASK", "canceled")
            super.onCancelled()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        url = ImageFragmentArgs.fromBundle(requireArguments()).url

        val myTask = PictureAsyncTask()
        if (savedInstanceState == null) {
            myTask.execute(url)
        } else {
            image.setImageBitmap(imageBitmap)
        }

        backButton.setOnClickListener {
            findNavController().navigateUp()
            myTask.cancel(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}