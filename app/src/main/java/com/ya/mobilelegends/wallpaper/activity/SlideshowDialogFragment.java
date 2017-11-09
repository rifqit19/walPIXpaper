package com.ya.mobilelegends.wallpaper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ya.mobilelegends.wallpaper.R;
import com.ya.mobilelegends.wallpaper.model.Image;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate, btnWallpaper;
    private ImageView img_download;
    private int selectedPosition = 0;
    Bitmap chefBitmap;

    String logTitle = "null";
    private FirebaseAnalytics mFirebaseAnalytics;

    ProgressDialog mProgressDialog;

// instantiate it within the onCreate method

    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        img_download = v.findViewById(R.id.img_download);
        lblDate = (TextView) v.findViewById(R.id.date);
        btnWallpaper = (TextView) v.findViewById(R.id.btnWallpaper);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        images = (ArrayList<Image>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               checkWallpaperPermission();
            }
        });

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        final Image image = images.get(position);
        lblTitle.setText(image.getName());
        lblDate.setText(image.getTimestamp());

        logTitle = image.getName();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //your action

                try {
                    chefBitmap = Glide.with(getActivity())
                            .asBitmap()
                            .load(image.getLarge())
                            .submit()
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        };
        AsyncTask.execute(runnable);

        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkExternalPermission(image.getLarge());
            }
        });


    }

    public void setWallpaper(){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, logTitle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Toast.makeText(getActivity().getApplicationContext(), R.string.set_wallpaper, Toast.LENGTH_SHORT).show();

        WallpaperManager wallpaperManager =
                WallpaperManager.getInstance(getActivity().getApplicationContext());
        try {
            wallpaperManager.setBitmap(chefBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void downloadFile(String str_url){
        String nama_file = "", extFile = "";
        try {
            URL url = new URL(str_url);
            String filename[] = url.getFile().split("/");

            extFile = filename[filename.length - 1].split("\\.")[1];
            nama_file = filename[filename.length - 1].split("\\.")[0];
        } catch (MalformedURLException | IndexOutOfBoundsException | NullPointerException e){
            e.printStackTrace();
        }

        if(extFile != null) {
            nama_file += "." + extFile;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str_url));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nama_file);
        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(getActivity().getApplicationContext(), R.string.download_image, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = view.findViewById(R.id.image_preview);

            final Image image = images.get(position);


            Glide.with(getActivity()).load(image.getLarge())
                    .thumbnail(0.5f)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public void checkExternalPermission(final String str_url){
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    downloadFile(str_url);
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    Toast.makeText(getActivity().getApplicationContext(), R.string.akses_ditolak, Toast.LENGTH_SHORT).show();
                }

            };

            TedPermission.with(getActivity())
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage(R.string.ijin_akses_memory)
                    .setDeniedMessage(R.string.alert_permission_denies)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();


//

        } else {

            downloadFile(str_url);

        }
    }

    public void checkWallpaperPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    setWallpaper();
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.akses_ditolak, Toast.LENGTH_SHORT).show();
                }

            };

            TedPermission.with(getActivity())
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage(R.string.ijin_atur_wallpaper)
                    .setDeniedMessage(R.string.alert_permission_denies)
                    .setPermissions(Manifest.permission.SET_WALLPAPER)
                    .check();
//

        } else {

            setWallpaper();

        }
    }
}
