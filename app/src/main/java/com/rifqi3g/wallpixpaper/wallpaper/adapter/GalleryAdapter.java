package com.rifqi3g.wallpixpaper.wallpaper.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.rifqi3g.wallpixpaper.wallpaper.R;
import com.rifqi3g.wallpixpaper.wallpaper.Saved.SaveObj;
import com.rifqi3g.wallpixpaper.wallpaper.activity.SlideshowDialogFragment;
import com.rifqi3g.wallpixpaper.wallpaper.helper.RealmHelper;
import com.rifqi3g.wallpixpaper.wallpaper.model.Image;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by Lincoln on 31/03/16.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private ArrayList<Image> images;
    private AppCompatActivity mContext;
    SaveObj favObj;
    RealmHelper realmHelper;
    Realm realm;
    private Fragment getquoteFrag;


    public GalleryAdapter(AppCompatActivity context, ArrayList<Image> list) {
        mContext = context;
        this.images = list;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        ToggleButton btn_save;
        ProgressBar progressBar;


        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            btn_save = view.findViewById(R.id.btn_save);
            progressBar = view.findViewById(R.id.progressBar);

            Realm.init(mContext);
            RealmConfiguration configuration = new RealmConfiguration.Builder().build();
            realm = Realm.getInstance(configuration);
            realmHelper = new RealmHelper(realm);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Image image = images.get(position);
        String imgurl = image.getLargeImageURL();

//        Glide.with(mContext).load(image.getLargeImageURL())
//                .thumbnail(0.5f)
////                .crossFade()
////                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.ic_placeholder_b)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(holder.thumbnail);
        Picasso.get().load(image.getLargeImageURL()).placeholder(R.drawable.ic_placeholder_b).into(holder.thumbnail, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void onError(Exception e) {


            }
        });


        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");

            }
        });

        if (imgurl.isEmpty()){
            holder.btn_save.setVisibility(View.GONE);
        }else {
            holder.btn_save.setVisibility(View.VISIBLE);
        }

        final SaveObj favObj1 = realm.where(SaveObj.class).equalTo("id",images.get(position).getId()).findFirst();

        Log.e("idku ", images.get(position).getId().toString());

        if (favObj1 == null) {
            holder.btn_save.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_not_save));
        }else{
            holder.btn_save.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_saved));
        }
        holder.btn_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveObj favObj2 = realm.where(SaveObj.class).equalTo("id",images.get(position).getId()).findFirst();
                Log.e("checked", ""+position);

                if (favObj2 != null) {

                    holder.btn_save.setBackgroundResource(R.drawable.ic_not_save);
                    realmHelper = new RealmHelper(realm);
                    realmHelper.delete(images.get(position).getId());
                    Log.e("id2a",images.get(position).getId().toString());
                }
                else {
                    holder.btn_save.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_saved));

                    favObj = new SaveObj();
                    favObj.setId(images.get(position).getId());
                    favObj.setLargeImageURL(images.get(position).getLargeImageURL());
                    favObj.setWebformatHeight(images.get(position).getWebformatHeight());
                    favObj.setWebformatWidth(images.get(position).getWebformatWidth());
                    favObj.setLikes(images.get(position).getLikes());
                    favObj.setImageWidth(images.get(position).getImageWidth());
                    favObj.setViews(images.get(position).getViews());
                    favObj.setComments(images.get(position).getComments());
                    favObj.setPageURL(images.get(position).getPageURL());
                    favObj.setImageHeight(images.get(position).getImageHeight());
                    favObj.setWebformatURL(images.get(position).getWebformatURL());
                    favObj.setType(images.get(position).getType());
                    favObj.setPreviewHeight(images.get(position).getPreviewHeight());
                    favObj.setTags(images.get(position).getTags());
                    favObj.setDownloads(images.get(position).getDownloads());
                    favObj.setUser(images.get(position).getUser());
                    favObj.setFavorites(images.get(position).getFavorites());
                    favObj.setImageSize(images.get(position).getImageSize());
                    favObj.setPreviewWidth(images.get(position).getPreviewWidth());
                    favObj.setUserImageURL(images.get(position).getUserImageURL());
                    favObj.setPreviewURL(images.get(position).getPreviewURL());

                    Log.e("id1a",images.get(position).getId().toString());

                    realmHelper = new RealmHelper(realm);
                    realmHelper.save(favObj);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}