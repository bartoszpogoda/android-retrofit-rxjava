package com.github.bartoszpogoda.posttitlesviewer.service;

import com.github.bartoszpogoda.posttitlesviewer.entity.Post;
import com.github.bartoszpogoda.posttitlesviewer.entity.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SampleApi {

    @GET("posts")
    Observable<List<Post>> getPostsOfUser(@Query("userId") long userId);

    @GET("users")
    Observable<List<User>> getUser(@Query("username") String username);
}