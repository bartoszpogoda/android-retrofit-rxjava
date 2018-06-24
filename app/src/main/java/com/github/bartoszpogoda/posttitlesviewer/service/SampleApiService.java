package com.github.bartoszpogoda.posttitlesviewer.service;

import com.github.bartoszpogoda.posttitlesviewer.entity.Post;
import com.github.bartoszpogoda.posttitlesviewer.entity.User;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SampleApiService {

    SampleApi sampleApi;

    public SampleApiService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        sampleApi = retrofit.create(SampleApi.class);
    }

    public Single<User> getUserByUsername(String username) {

        return proxy(sampleApi.getUser(username)
                .flatMapIterable(x -> x)
                .firstOrError());

    }

    public Observable<Post> getPostsOfUser(User user) {

        if(user == null) {
            return Observable.empty();
        }

        return proxy(sampleApi.getPostsOfUser(user.getId())
                .flatMapIterable(x -> x));
    }


    private <T> Observable<T> proxy(Observable<T> obs) {
        return obs.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T> Single<T> proxy(Single<T> obs) {
        return obs.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
