package com.example.pictures

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.image_fragment.*


class ImageFragment : Fragment() {
    private var url : String = ""
    private var broadcastReceiver : BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.image_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener {
            Log.d("ImageFragment", "onDestroyWithButton")
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ImageFragment", "onDestroy")
        activity?.unregisterReceiver(broadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url = ImageFragmentArgs.fromBundle(requireArguments()).url

        val serviceIntent = Intent(context, MyService::class.java)
            .putExtra("url", url)
        activity?.startService(serviceIntent)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                val byteArray = intent?.getByteArrayExtra("image")
                if (byteArray != null) {
                    val byteImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    image.setImageBitmap(byteImage)
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }

        activity?.registerReceiver(broadcastReceiver, IntentFilter("RESPONSE").apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        })
    }
}