package com.rifqi3g.wallpixpaper.wallpaper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;
import com.rifqi3g.wallpixpaper.wallpaper.CropImageView;
import com.rifqi3g.wallpixpaper.wallpaper.R;
import com.rifqi3g.wallpixpaper.wallpaper.model.Image;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle/*, lblDate*/;
    FloatingActionButton btnWallpaper,img_download,share,web;
    private int selectedPosition = 0;
    Bitmap chefBitmap,bitmap;
    String author,downloadUrl,url;
    Integer height,width,id;
    FloatingActionMenu floatingActionMenu;
    AppCompatImageButton back;

    String url_large = "";

    String logTitle = "null";
    private FirebaseAnalytics mFirebaseAnalytics;

    ProgressDialog mProgressDialog;

// instantiate it within the onCreate method

    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        img_download = v.findViewById(R.id.img_download);
        back = v.findViewById(R.id.backSF);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

//        View overlay = v.findViewById(R.id.slideShowFragmentLyt);
//        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);

//        lblDate = (TextView) v.findViewById(R.id.date);

        floatingActionMenu = v.findViewById(R.id.material_design_android_floating_action_menu);
        btnWallpaper = (FloatingActionButton) v.findViewById(R.id.btnWallpaper);
        share = v.findViewById(R.id.btn_share);
        web = v.findViewById(R.id.webweb);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        images = (ArrayList<Image>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        myViewPagerAdapter.notifyDataSetChanged();
        btnWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkWallpaperPermission(url_large);

            }
        });

        final String pos = images.get(selectedPosition).getPageURL();
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(pos);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSharePermission();

            }
        });

        setCurrentItem(selectedPosition);

        Log.e("position", selectedPosition+" ");


        id = images.get(selectedPosition).getId();
        author = images.get(selectedPosition).getTags();
        width = images.get(selectedPosition).getImageWidth();
        height = images.get(selectedPosition).getImageHeight();
        url = images.get(selectedPosition).getPageURL();
        downloadUrl = images.get(selectedPosition).getLargeImageURL();


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
        lblTitle.setText(image.getTags());
        /*lblDate.setText(" ");*/

        logTitle = image.getTags();

        url_large = image.getLargeImageURL();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //your actio
                try {

                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        };
        AsyncTask.execute(runnable);

        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkExternalPermission(image.getLargeImageURL());
            }
        });
    }


//    private BaseTarget setBitmapWallpaper = new BaseTarget<BitmapDrawable>() {
//        @Override
//        public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
//            // do something with the bitmap
//            // for demonstration purposes, let's set it to an imageview
//            Log.e("resource ready", "jalan");
//            chefBitmap = bitmap.getBitmap();
//
//            setWallpaper();
//        }
//
//        @Override
//        public void getSize(SizeReadyCallback cb) {
////            cb.onSizeReady(250, 250);
//        }
//
//        @Override
//        public void removeCallback(SizeReadyCallback cb) {}
//    };

//    public void setWallpaper(){
//
//        DisplayMetrics metrics = new DisplayMetrics();
//        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//
//
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, logTitle);
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//
//        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity().getApplicationContext());
//
//        try {
//            wallpaperManager.setBitmap(chefBitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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

            PhotoView imageViewPreview = view.findViewById(R.id.image_preview);
            final ProgressBar progressBar = view.findViewById(R.id.progressBar);

            Log.e("posVP", position +" ");

            final Image image = images.get(position);

            imageViewPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                }
            });

            Picasso.get().load(image.getLargeImageURL()).into(imageViewPreview, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);

                }
            });

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

        } else {

            downloadFile(str_url);
        }
    }

    public Uri getImageUri(Context context,Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"imageBitmap",null);
        return  Uri.parse(path);
    }

    public void shareArticle(){
        int vpPosition = viewPager.getCurrentItem();
        Log.e("vpPosition", vpPosition+" ");

        final Image imageSel = images.get(vpPosition);

        Glide.with(this)
                .asBitmap()
                .load(imageSel.getLargeImageURL())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        bitmap = resource;
                        Uri imageUri = getImageUri(getActivity(),bitmap);
                        ShareCompat.IntentBuilder
                                .from(getActivity())
                                .setType("image/*")
                                .setStream(imageUri)
                                .setText(imageSel.getTags())
                                .setChooserTitle("Share With")
                                .startChooser();
                    }
                });
    }

    public void checkSharePermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    shareArticle();
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.akses_ditolak, Toast.LENGTH_SHORT).show();
                }

            };
            TedPermission.with(getContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();

        } else {
            shareArticle();
        }
    }

//    public void runSetWallpaper(String url){
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

    public void checkWallpaperPermission(final String url){
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
//                    runSetWallpaper(url);
                    Intent p = new Intent(getActivity(), CropImageView.class);
                    p.putExtra("url", url_large);
                    startActivity(p);
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

        } else {

//            runSetWallpaper(url);
            Intent p = new Intent(getActivity(), CropImageView.class);
            p.putExtra("url", url_large);
            startActivity(p);

        }
    }


}
