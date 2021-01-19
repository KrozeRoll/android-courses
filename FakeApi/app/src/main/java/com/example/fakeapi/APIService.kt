package com.example.fakeapi

import okhttp3.ResponseBody
import retrofit2.Call;
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

public interface APIService {
    @GET("posts")
    fun listPosts() : Call<List<Post>>

    @DELETE("posts/{postId}")
    fun deletePost(@Path("postId") postId : Int) : Call<ResponseBody>

    @POST("posts")
    fun postPost(@Body postBody : PostBody) : Call<Post>

    companion object Factory {
        val BASE_URL = "https://jsonplaceholder.typicode.com/"
        fun create(): APIService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(APIService::class.java);
        }
    }
}