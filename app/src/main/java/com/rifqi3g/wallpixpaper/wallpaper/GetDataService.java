package com.rifqi3g.wallpixpaper.wallpaper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("api")
    Call<ResponseBody>getData(
            @Query("key") String key,
            @Query("q") String q,
            @Query("image_type") String image_type,
            @Query("page") Integer page
    );

}
