package com.rifqi3g.wallpixpaper.wallpaper.Saved;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rifqi3g.wallpixpaper.wallpaper.R;
import com.rifqi3g.wallpixpaper.wallpaper.activity.SlideShowActivity;
import com.rifqi3g.wallpixpaper.wallpaper.helper.RealmHelper;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AdapterSave extends RecyclerView.Adapter<AdapterSave.MyViewHolder> {
    private List<SaveObj> saveObjs;
    AppCompatActivity context;
    SaveObj favObj;
    RealmHelper realmHelper;
    Realm realm;

    public AdapterSave(AppCompatActivity context, List<SaveObj> favObjs) {
        this.context = context;
        this.saveObjs = favObjs;
    }

    @Override
    public int getItemCount() {
        return saveObjs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        ToggleButton btn_save;
        ProgressBar progressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            btn_save = itemView.findViewById(R.id.btn_save);
            progressBar = itemView.findViewById(R.id.progressBar);


            Realm.init(context);
            RealmConfiguration configuration = new RealmConfiguration.Builder().build();
            realm = Realm.getInstance(configuration);
            realmHelper = new RealmHelper(realm);
        }
    }

    @Override
    public AdapterSave.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final AdapterSave.MyViewHolder holder, final int position) {
        final SaveObj image = saveObjs.get(position);
        String imgurl = image.getLargeImageURL();

        Glide.with(context).load(image.getLargeImageURL())
                .thumbnail(0.5f)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_placeholder_b)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.thumbnail);



        if (imgurl.isEmpty()){
            holder.btn_save.setVisibility(View.GONE);
        }else {
            holder.btn_save.setVisibility(View.VISIBLE);
        }

        final SaveObj favObj1 = realm.where(SaveObj.class).equalTo("id",saveObjs.get(position).getId()).findFirst();

        Log.e("idku ", saveObjs.get(position).getId().toString());

        if (favObj1 == null) {
            holder.btn_save.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_not_save));
        }else{
            holder.btn_save.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_saved));
        }
        holder.btn_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                intentBroadcastFunction();
                SaveObj favObj2 = realm.where(SaveObj.class).equalTo("id",saveObjs.get(position).getId()).findFirst();
                Log.e("checked", ""+position);
                if (favObj2 != null) {

                    holder.btn_save.setBackgroundResource(R.drawable.ic_not_save);
                    realmHelper = new RealmHelper(realm);
                    realmHelper.delete(saveObjs.get(position).getId());
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
                else {
                    holder.btn_save.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_saved));

                    favObj = new SaveObj();
                    favObj.setId(saveObjs.get(position).getId());
                    favObj.setLargeImageURL(saveObjs.get(position).getLargeImageURL());
                    favObj.setWebformatHeight(saveObjs.get(position).getWebformatHeight());
                    favObj.setWebformatWidth(saveObjs.get(position).getWebformatWidth());
                    favObj.setLikes(saveObjs.get(position).getLikes());
                    favObj.setImageWidth(saveObjs.get(position).getImageWidth());
                    favObj.setViews(saveObjs.get(position).getViews());
                    favObj.setComments(saveObjs.get(position).getComments());
                    favObj.setPageURL(saveObjs.get(position).getPageURL());
                    favObj.setImageHeight(saveObjs.get(position).getImageHeight());
                    favObj.setWebformatURL(saveObjs.get(position).getWebformatURL());
                    favObj.setType(saveObjs.get(position).getType());
                    favObj.setPreviewHeight(saveObjs.get(position).getPreviewHeight());
                    favObj.setTags(saveObjs.get(position).getTags());
                    favObj.setDownloads(saveObjs.get(position).getDownloads());
                    favObj.setUser(saveObjs.get(position).getUser());
                    favObj.setFavorites(saveObjs.get(position).getFavorites());
                    favObj.setImageSize(saveObjs.get(position).getImageSize());
                    favObj.setPreviewWidth(saveObjs.get(position).getPreviewWidth());
                    favObj.setUserImageURL(saveObjs.get(position).getUserImageURL());
                    favObj.setPreviewURL(saveObjs.get(position).getPreviewURL());

                    Log.e("id1a",saveObjs.get(position).getId().toString());

                    realmHelper = new RealmHelper(realm);
                    realmHelper.save(favObj);

                    notifyDataSetChanged();
                }
            }
        });

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
                SlideShowActivity newFragment = SlideShowActivity.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");

            }
        });
    }
    private void intentBroadcastFunction() {

        Intent intentBroadcast = new Intent("broadcast");
        Bundle bundle = new Bundle();
        bundle.putString("valueString", "data");
        intentBroadcast.putExtra("bundleBroadcast", bundle);
        context.sendBroadcast(intentBroadcast);
    }
}
