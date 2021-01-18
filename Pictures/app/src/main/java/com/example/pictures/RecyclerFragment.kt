package com.example.pictures

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pictures.extension.navigate
import com.google.gson.Gson
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.recycler_fragment.*
import java.net.URL

class RecyclerFragment : Fragment() {
    data class PictureInfo(
        val id : Int,
        val author : String,
        val wight : Int,
        val height : Int, val url : String,
        val download_url : String)

    var pictures : List<PictureInfo> = listOf()
    class UserAdapter(
        private val pictures : List<PictureInfo>,
        private val onClick: (PictureInfo) -> Unit
    ): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
        class UserViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
            fun bind(pictureInfo : PictureInfo) {
                with(root) {
                    description.text = pictureInfo.author
                }
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val holder = UserViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            )
            holder.root.setOnClickListener {
                onClick(pictures[holder.adapterPosition])
            }
            return holder
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) = holder.bind(pictures[position])

        override fun getItemCount(): Int = pictures.size
    }

    private fun showPicture(download_url: String) {
        navigate(RecyclerFragmentDirections.actionRecyclerFragmentToImageFragment(download_url))
    }

    private fun makeRecycleView(pictureList : List<PictureInfo>) {
        val viewManager = LinearLayoutManager(context)
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = UserAdapter(pictureList) {
                showPicture(it.download_url);
            }
        }
    }

    inner class MyDownloadAsyncTask : AsyncTask<String, Unit, List<PictureInfo>>() {
        override fun doInBackground(vararg params: String?): List<PictureInfo> {
            val result = URL(params[0]).openConnection()
                .getInputStream()
                .bufferedReader().readLine()
            val pictures = Gson().fromJson(result, Array<PictureInfo>::class.java)

            Log.d("ASYNC_TASK", pictures.size.toString());
            return pictures.asList();
        }

        override fun onPostExecute(result: List<PictureInfo>) {
            Log.d("ASYNC_TASK", "po krasote result")
            pictures = result
            makeRecycleView(result)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("BEDA", "onCreateView")
        Log.d("BEDA", "picture = " + pictures.toString())

        val view = inflater.inflate(R.layout.recycler_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeRecycleView(pictures)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("BEDA", "onCreate")
        super.onCreate(savedInstanceState)
        retainInstance = true
        MyDownloadAsyncTask().execute("https://picsum.photos/v2/list?limit=50")
    }
}