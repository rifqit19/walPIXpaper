package com.rifqi3g.wallpixpaper.wallpaper;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rifqi3g.wallpixpaper.wallpaper.Saved.SaveObj;
import com.rifqi3g.wallpixpaper.wallpaper.helper.RealmHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ExampleJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
    Realm realm;
    RealmHelper realmHelper;
    List<SaveObj> favObjs;
    Random random;
    SaveObj saveObj;
    String urlurl;
    Random rand= new Random();
    ArrayList<String> givenList = new ArrayList<String>();


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG, "Job started");

        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    Log.e(TAG, "run: " + i);
                    if (jobCancelled) {
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Realm.init(getApplicationContext());
                RealmConfiguration configuration = new RealmConfiguration.Builder().build();
                realm = Realm.getInstance(configuration);

                realmHelper = new RealmHelper(realm);
                favObjs = new ArrayList<>();

                favObjs = realmHelper.getFav();

                int size = favObjs.size();

                for (int i=0;i<size;i++){
                    urlurl = favObjs.get(i).getLargeImageURL();
                    Log.e("urlurl", urlurl);

//                    givenList = Arrays.asList(urlurl);
                    givenList.add(favObjs.get(i).getLargeImageURL());
                }

                int numberOfElements = 1;

                for (int i1 = 0; i1 < numberOfElements; i1++) {
                    int randomIndex = rand.nextInt(givenList.size());
                    String randomElement = givenList.get(randomIndex);

                    Log.e("random", randomElement);

                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(randomElement)
                            .centerCrop()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    try {
                                        WallpaperManager.getInstance(getApplicationContext()).setBitmap(resource);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                }

                Log.e(TAG, "Job finished");

                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
