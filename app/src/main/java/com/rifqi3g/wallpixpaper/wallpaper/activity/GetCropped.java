package com.rifqi3g.wallpixpaper.wallpaper.activity;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rifqi3g.wallpixpaper.wallpaper.R;
import com.rifqi3g.wallpixpaper.wallpaper.Saved.SaveActivity;
import com.rifqi3g.wallpixpaper.wallpaper.helper.TouchImageView;

import java.io.IOException;

public class GetCropped extends DialogFragment {


    Bitmap cop;
    TouchImageView imgCropped;
    RelativeLayout set;
    ImageButton back;

    public static GetCropped newInstance() {
        GetCropped f = new GetCropped();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        View v = inflater.inflate(R.layout.get_cropped, container, false);

        final View overlay = v.findViewById(R.id.myLyt);

        back = v.findViewById(R.id.backGC);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        });


        imgCropped = v.findViewById(R.id.imgCropped);
        set = v.findViewById(R.id.lytSetWallpaper);

        cop = getArguments().getParcelable("cropped");

        imgCropped.setImageBitmap(cop);

        Log.e("cop", cop.toString());

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), R.string.set_wallpaper, Toast.LENGTH_SHORT).show();
                getActivity().finish();
                setWallpaper();
                SaveActivity.getInstance().cancelJob();

            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

//    public void runSetWallpaper(/*String url*/){
//
//        Toast.makeText(getActivity().getApplicationContext(), R.string.set_wallpaper, Toast.LENGTH_SHORT).show();
//
//        Glide.with(this)
//                .asBitmap()
//                .load(url)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        chefBitmap = resource;
//
//                        setWallpaper();
//                    }
//                });
//    }

    public void setWallpaper(){

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, logTitle);
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity().getApplicationContext());

//        if (android.os.Build.VERSION.SDK_INT >= 24){
//            try {
//                wallpaperManager.setBitmap(cop, null, true, WallpaperManager.FLAG_LOCK);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }

        try {
            wallpaperManager.setBitmap(cop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
