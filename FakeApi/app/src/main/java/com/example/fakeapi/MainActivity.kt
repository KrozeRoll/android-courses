package com.example.fakeapi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Parcelize
data class Post(val id : Int, val title: String, val body: String, val userId : Int) : Parcelable

data class PostBody(val title : String, val body : String, val userId : Int)

class PostAdapter(
    private val posts: List<Post>,
    private val onClick: (Post) -> Unit
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        fun bind(post: Post) {
            with(root) {
                titleTextView.text = post.title
                bodyTextView.text = post.body
                deleteButton.setOnClickListener {
                    val currentId = post.id
                    MyApp.instance.apiService.deletePost(currentId).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            Log.d(
                                "APIService/Get",
                                "Finished with ${response.code()}, body: ${response.body()}"
                            )
                            Toast.makeText(
                                context,
                                "Post ${currentId} deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d(
                                "APIService/Delete",
                                "${t} happened"
                            )
                        }

                    })
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val holder = PostViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
        holder.root.setOnClickListener {
            onClick(posts[holder.adapterPosition])
        }
        return holder

    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) = holder.bind(posts[position])

    override fun getItemCount(): Int = posts.size

}

class MainActivity : AppCompatActivity() {
    lateinit var postList : ArrayList<Post>

    private fun makeRecycleView(contactsList : ArrayList<Post>) {
        val viewManager = LinearLayoutManager(this@MainActivity)
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = PostAdapter(contactsList) {
                //TODO(onClick)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            progressBar.visibility = View.VISIBLE
            MyApp.instance.apiService.listPosts().enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    Log.d(
                        "APIService/Get",
                        "Finished with ${response.code()}, body: ${response.body()?.size}"
                    )
                    postList = ArrayList(response.body()!!)
                    makeRecycleView(postList)
                    progressBar.visibility = View.INVISIBLE
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    Log.d(
                        "APIService/Get",
                        "${t} happened"
                    )
                }
            })
        }
        addButton.setOnClickListener {
            val newPost = PostBody("NewPost", "It's a post", 1)
            MyApp.instance.apiService.postPost(newPost).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    Log.d(
                        "APIService/Post",
                        "Finished with ${response.code()}, body: ${response.body()?.id}"
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "Post added: ${response.body()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Log.d(
                        "APIService/Post",
                        "${t} happened"
                    )
                }
            })
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("POST_LIST", postList)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        postList = savedInstanceState.getParcelableArrayList("POST_LIST")!!
        makeRecycleView(postList)
    }


}