package com.rifqi3g.wallpixpaper.wallpaper;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.rifqi3g.wallpixpaper.wallpaper.activity.GetCropped;

public class CropImageView extends AppCompatActivity {

    Bitmap bitmap;
    com.theartofdev.edmodo.cropper.CropImageView cropImageView;

    AppCompatImageButton done, back;

    FloatingActionMenu menuRatio;
    FloatingActionButton btn_custom,btn_vertical,btn_horizontal;
    Integer height,width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_image_view);

//        View overlay = findViewById(R.id.cropLyt);
//        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);

        cropImageView = findViewById(R.id.cropImageView1);
        done = findViewById(R.id.done);
        back = findViewById(R.id.backC);
        menuRatio = findViewById(R.id.menuRetio);
        btn_custom =  findViewById(R.id.btn_custom);
        btn_vertical = findViewById(R.id.btn_vertical);
        btn_horizontal = findViewById(R.id.btn_horizontal);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuRatio.close(true);
            }
        });

        String p = getIntent().getStringExtra("url");
        Log.e("url", p);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        height = displayMetrics.heightPixels + getScreenHeight() ;
        width = displayMetrics.widthPixels + getScreenWidth() ;

        Log.e("Ratio", height+""+" x "+width+"");

        btn_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.clearAspectRatio();
            }
        });

        btn_horizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setAspectRatio(height, width);
            }
        });

        btn_vertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setAspectRatio(width, height);
            }
        });

        Glide.with(this)
                .asBitmap()
                .load(p)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        bitmap = resource;

                        Log.e("bitmap", bitmap.toString());

                        cropImageView.setImageBitmap(bitmap);

                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bitmap cropped = cropImageView.getCroppedImage();
                                Log.e("crop", cropped.toString());
//                                Intent k = new Intent(CropImageView.this, GetCropped.class);
//                                k.putExtra("lplp", cropped);
//                                startActivity(k);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("cropped", cropped);

                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                GetCropped newFragment = GetCropped.newInstance();
                                newFragment.setArguments(bundle);
                                newFragment.show(ft, "cropped");
                            }
                        });
                    }
                });
    }
//    public boolean showNavigationBar(Resources resources)
//    {
//        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
//        return id > 0 && resources.getBoolean(id);
//    }

    private int getScreenHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
    private int getScreenWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.widthPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.widthPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }



}
