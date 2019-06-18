package com.rifqit.animeList2.Database;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCilentInstance {

    private static Retrofit retrofit;
    private static final  String BASE_URL = "https://api.jikan.moe/v3/";

    public static Retrofit getRetrofitInstance(){
        if (retrofit == null){
            retrofit  = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
