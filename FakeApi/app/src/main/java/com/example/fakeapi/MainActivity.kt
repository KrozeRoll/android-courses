package com.example.fakeapi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
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
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

@Parcelize
data class Post(val id : Int, val title: String, val body: String, val userId : Int) : Parcelable

data class PostBody(val title : String, val body : String, val userId : Int)

class PostAdapter(
    private val posts: List<Post>
): RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
        var call : Call<ResponseBody>? = null
        fun bind(post: Post) {
            with(root) {
                titleTextView.text = post.title
                bodyTextView.text = post.body
                deleteButton.setOnClickListener {
                    val currentId = post.id
                    call = MyApp.instance.apiService.deletePost(currentId)
                    call?.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            Log.d(
                                "APIService/Get",
                                "Finished with ${response.code()}, body: ${response.body()}"
                            )
                            if (response.isSuccessful) {
                                MainActivity.makeToast("Post $currentId deleted", context)
                            } else {
                                MainActivity.makeProblemMessage(response.code(), context)
                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d(
                                "APIService/Delete",
                                "$t happened"
                            )
                            MainActivity.makeExceptionMessage(t, context)
                        }
                    })
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PostViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.call?.cancel()
        holder.call = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) = holder.bind(posts[position])

    override fun getItemCount(): Int = posts.size

}

class MainActivity : AppCompatActivity() {
    lateinit var postList : ArrayList<Post>
    private var getCall : Call<List<Post>>? = null
    private var addCall : Call<Post>? = null

    private fun makeRecycleView(contactsList : ArrayList<Post>) {
        val viewManager = LinearLayoutManager(this@MainActivity)
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = PostAdapter(contactsList)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            progressBar.visibility = View.VISIBLE
            getCall = MyApp.instance.apiService.listPosts()
            getCall?.enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    Log.d(
                        "APIService/Get",
                        "Finished with ${response.code()}, body: ${response.body()?.size}"
                    )
                    if (response.isSuccessful) {
                        postList = ArrayList(response.body()!!)
                        makeRecycleView(postList)
                        progressBar.visibility = View.INVISIBLE
                        addButton.visibility = View.VISIBLE
                    } else {
                        makeProblemMessage(response.code(), this@MainActivity)
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    Log.d(
                        "APIService/Get",
                        "$t happened"
                    )
                    makeExceptionMessage(t, this@MainActivity)
                    progressBar.visibility = View.INVISIBLE
                }
            })
        }

        addButton.setOnClickListener {
            val newPost = PostBody("NewPost", "It's a post", 1)
            addCall = MyApp.instance.apiService.postPost(newPost)
            addCall?.enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    Log.d(
                        "APIService/Post",
                        "Finished with ${response.code()}, body: ${response.body()?.id}"
                    )
                    if (response.isSuccessful) {
                        makeToast("Post added: ${response.body()}", this@MainActivity)
                    } else {
                        makeProblemMessage(response.code(), this@MainActivity)
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Log.d(
                        "APIService/Post",
                        "$t happened"
                    )
                    makeExceptionMessage(t, this@MainActivity)
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
        addButton.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        addCall?.cancel()
        addCall = null
        getCall?.cancel()
        getCall = null
    }

    companion object {
        fun makeToast(msg : String, context : Context) {
            Toast.makeText(
                context,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }

        fun makeExceptionMessage(t : Throwable, context : Context) {
            try {
                throw t
            } catch (e : SocketTimeoutException) {
                makeToast("Timeout exceeded, try again", context)
            } catch (e : IOException) {
                makeToast("Load canceled", context)
            } catch (e : Exception) {
                makeToast(e.localizedMessage ?: "Unknown error", context)
            }
        }

        fun makeProblemMessage(code : Int, context: Context) {
            when (code) {
                in 300..399 -> {
                    makeToast("Error: Resourse moved", context)
                }
                in 400..499 -> {
                    makeToast("Error: Bad request", context)
                }
                in 500..599 -> {
                    makeToast("Error: Server error", context)
                }
            }
        }
    }

}