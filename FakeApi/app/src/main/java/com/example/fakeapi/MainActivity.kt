package com.example.fakeapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

@Parcelize
@Entity
data class Post(val id : Int, val title: String, val body: String, val userId : Int,
                @PrimaryKey(autoGenerate = true) val hshId : Int = 0)
    : Parcelable

data class PostBody(val title : String, val body : String, val userId : Int)

class PostAdapter(
    private val posts: MutableList<Post>,
    private val onClick : (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        var call : Call<ResponseBody>? = null
        fun bind(post: Post) {
            with(root) {
                titleTextView.text = post.title
                bodyTextView.text = post.body
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PostViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.call?.cancel()
        holder.call = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val holder = PostViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
        holder.root.deleteButton.setOnClickListener {
            onClick(posts[holder.adapterPosition])
            posts.removeAt(holder.adapterPosition)
            notifyDataSetChanged()
        }
        return holder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) = holder.bind(posts[position])

    override fun getItemCount(): Int = posts.size

    fun addPost(post : Post) {
        posts.add(post)
        notifyDataSetChanged()
    }
}

class MainActivity : AppCompatActivity() {
    lateinit var postList : MutableList<Post>
    private var broadcastReceiver : BroadcastReceiver? = null
    var postAdapter : PostAdapter? = null
    var currNumber : Int = 0

    private fun makeRecycleView(contactsList : MutableList<Post>) {
        val viewManager = LinearLayoutManager(this@MainActivity)
        postAdapter = PostAdapter(contactsList) {
            deletePost(it)
        }
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = postAdapter
        }
        addButton.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val operation = intent?.getStringExtra("operation")
                if (operation == "post") {
                    val resultCode = intent.getIntExtra("resultCode", 0)
                    val post = intent.getParcelableExtra<Post>("post")
                    if (resultCode != 0) {
                        makeToast("Server connection problem", this@MainActivity)
                    } else {
                        makeToast("Posted $post", this@MainActivity)
                    }
                    if (post != null) {
                        postAdapter?.addPost(post)
                    }
                } else if (operation == "delete") {
                    val message = intent.getStringExtra("message")
                    makeToast(message.toString(), this@MainActivity)
                } else if (operation == "upload") {
                    val executed = intent.getBooleanExtra("has_list", false)
                    if (!executed) {
                        makeToast("Server connection problem", this@MainActivity)
                        progressBar.visibility = View.INVISIBLE
                    } else {
                        makeToast("List uploaded", this@MainActivity)
                        postList = intent.getParcelableArrayListExtra("list")!!
                        makeRecycleView(postList)
                    }
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("response").apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        })

        if (savedInstanceState == null) {
            uploadList()
            progressBar.visibility = View.VISIBLE
        } else {
            postList = savedInstanceState.getParcelableArrayList("postList")!!
            makeRecycleView(postList)
        }

        addButton.setOnClickListener {
            val newPost = PostBody("NewPost#${currNumber++}", "It's a post", 1)
            postPost(newPost)
        }

        refreshButton.setOnClickListener {
            uploadList()
        }
    }

    private fun uploadList() {
        val serviceIntent = Intent(this@MainActivity, MyService::class.java)
        serviceIntent.putExtra("operation", "upload")
        startService(serviceIntent)
    }

    private fun deletePost(post : Post) {
        val serviceIntent = Intent(this@MainActivity, MyService::class.java)
        with (serviceIntent) {
            putExtra("operation", "delete")
            putExtra("post", post)
        }
        startService(serviceIntent)
    }

    private fun postPost(post : PostBody) {
        val serviceIntent = Intent(this@MainActivity, MyService::class.java)
        with (serviceIntent) {
            putExtra("operation", "post")
            putExtra("title", post.title)
            putExtra("body", post.body)
        }
        startService(serviceIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("postList", postList as ArrayList<out Parcelable>)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        postList = savedInstanceState.getParcelableArrayList("postList")!!
    }


    companion object {
        fun makeToast(msg : String, context : Context) {
            Toast.makeText(
                context,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

}