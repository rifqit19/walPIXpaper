package com.rifqit.animeList2.Database;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("season/{year}/{season}")
    Call<ResponseBody> getSeason(
            @Path("year") String year,
            @Path("season") String season
    );

    @GET("top/{type}/{page}/{subtype}")
    Call<ResponseBody> getTop(
            @Path("type") String type,
            @Path("page") Integer page,
            @Path("subtype") String subtype

    );
    @GET("search/{type}")
    Call<ResponseBody>getSearch(
            @Path("type") String type,
            @Query("q") String q,
            @Query("page") Integer page
    );
    @GET("schedule/{day}")
    Call<ResponseBody>getSchedule(
            @Path("day") String day
    );
    @GET("anime/{malId}/recommendations")
    Call<ResponseBody>getRecomendation(
            @Path("malId") Integer malId
    );

    @GET("manga/{malId}/recommendations")
    Call<ResponseBody>getRecomendationManga(
            @Path("malId") Integer malId
    );

    @GET("anime/{malId}")
    Call<ResponseBody>getDetailRecomendationAnime(
            @Path("malId") Integer malId
    );
    @GET("manga/{malId}")
    Call<ResponseBody>getDetailRecomendationManga(
            @Path("malId") Integer malId
    );

}
