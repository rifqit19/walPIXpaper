package com.rifqi3g.wallpixpaper.wallpaper.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rifqi3g.wallpixpaper.wallpaper.AbouteApp;
import com.rifqi3g.wallpixpaper.wallpaper.GetDataService;
import com.rifqi3g.wallpixpaper.wallpaper.R;
import com.rifqi3g.wallpixpaper.wallpaper.RetrofitCilentInstance;
import com.rifqi3g.wallpixpaper.wallpaper.Saved.SaveActivity;
import com.rifqi3g.wallpixpaper.wallpaper.adapter.GalleryAdapter;
import com.rifqi3g.wallpixpaper.wallpaper.helper.EndlessOnScrollListener;
import com.rifqi3g.wallpixpaper.wallpaper.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Image> images;
    private ArrayList<Image> filter_images;
    private ProgressDialog pDialog;
    MainActivity mainActivity = this;
    GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    boolean isLoading;


    //search
    SearchView searchView;
    boolean mSearchCheck;
    SearchManager searchManager;
    ProgressBar progressBar;
    String ss;

    String q = "modern";

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    int id, position;
    final List<MenuItem> items = new ArrayList<>();
    Menu menu;
    Integer posMenu;
    FloatingActionButton saveBtn;
    RelativeLayout lytNoResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lytNoResult = findViewById(R.id.lytNoResult);

//        View overlay = findViewById(R.id.activity_main);
//        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        q = "modern";

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar1);
        saveBtn = findViewById(R.id.savesaveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sv = new Intent(MainActivity.this, SaveActivity.class);
                mainActivity.startActivityForResult(sv,2);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        dl = (DrawerLayout) findViewById(R.id.activity_main);

        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close) {

            public void onDrawerClosed(View view) {
                if (posMenu != null) {
                    setAdapter();
                }
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }


        };

        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        filter_images = new ArrayList<>();

//        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("images", filter_images);
//                bundle.putInt("position", position);
//
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
//                newFragment.setArguments(bundle);
//                newFragment.show(ft, "slideshow");
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));

        setAdapter();

        nv = (NavigationView) findViewById(R.id.nv);
        menu = nv.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            items.add(menu.getItem(i));
        }

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                id = item.getItemId();

                position = items.indexOf(item);
                posMenu = position + 1;
                Log.e("positionMenu", posMenu + " ");

                switch (id) {
                    case R.id.kat1:
                        images.clear();
                        filter_images.clear();
                        q = "modern";
                        Log.e("q", q);
                        break;
                    case R.id.kat2:
                        images.clear();
                        filter_images.clear();
                        q = "classic";
                        Log.e("q", q);
                        break;
                    case R.id.kat3:
                        images.clear();
                        filter_images.clear();
                        q = "nature";
                        Log.e("q", q);
                        break;
                    case R.id.kat4:
                        images.clear();
                        filter_images.clear();
                        q = "abstract";
                        Log.e("q", q);
                        break;
                    case R.id.kat5:
                        images.clear();
                        filter_images.clear();
                        q = "love";
                        Log.e("q", q);
                        break;
                    case R.id.kat6:
                        images.clear();
                        filter_images.clear();
                        q = "funny";
                        Log.e("q", q);
                        break;
                    case R.id.kat7:
                        images.clear();
                        filter_images.clear();
                        q = "city";
                        Log.e("q", q);
                        break;
                    case R.id.kat8:
                        images.clear();
                        filter_images.clear();
                        q = "animal";
                        Log.e("q", q);
                        break;
                    case R.id.kat9:
                        images.clear();
                        filter_images.clear();
                        q = "art";
                        Log.e("q", q);
                        break;
                    case R.id.kat10:
                        images.clear();
                        filter_images.clear();
                        q = "flora";
                        Log.e("q", q);
                        break;
                    case R.id.kat11:
                        images.clear();
                        filter_images.clear();
                        q = "life style";
                        Log.e("q", q);
                        break;
                    case R.id.about:
                        Intent o = new Intent(MainActivity.this, AbouteApp.class);
                        startActivity(o);
                        break;
                    default:

                        return true;
                }
//                recyclerView.addOnScrollListener(new EndlessOnScrollListener(mLayoutManager) {
//                    @Override
//                    public void onLoadMore(Integer current_page) {
//                        fetchImages(current_page);
//                    }
//                })
                mAdapter.notifyDataSetChanged();
                images.clear();
                filter_images.clear();
                mAdapter.notifyDataSetChanged();
                fetchImages(1);
                mAdapter.notifyDataSetChanged();
                dl.closeDrawers();
                return true;
            }
        });

        nv.getMenu().getItem(0).setChecked(true);

        if (posMenu == null) {
            fetchImages(1);
        }


//        fetchImages(1);

//        fetchImages(1);


//        if (posMenu == null){
//            Log.e("dddd", "kosong");
//            mLayoutManager = new GridLayoutManager(MainActivity.this, 2);
//            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            mAdapter = new GalleryAdapter(MainActivity.this, images);
//            recyclerView.setAdapter(mAdapter);
//            recyclerView.addOnScrollListener(new EndlessOnScrollListener(mLayoutManager) {
//                @Override
//                public void onLoadMore(Integer current_page) {
//                    fetchImages(current_page);
//                }
//            });
//            fetchImages(1);
//            mAdapter.notifyDataSetChanged();
//        }else if (posMenu <= 1){
//            Log.e("dddd", "isi");
//            mLayoutManager = new GridLayoutManager(MainActivity.this, 2);
//            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            mAdapter = new GalleryAdapter(MainActivity.this, images);
//            recyclerView.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

    }

    public void setAdapter() {
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new GalleryAdapter(MainActivity.this, filter_images);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new EndlessOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(Integer current_page) {
                fetchImages(current_page);
            }
        });

        Log.e("size", "" + images.size());
    }

    private void fetchImages(final Integer page) {

        if (page.equals(1)) {
            isLoading = true;
            pDialog = new ProgressDialog(this);
            pDialog.setMessage(getString(R.string.mengambil_data));
            pDialog.show();
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        GetDataService service = RetrofitCilentInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = service.getData("12548524-32ce77b16c3b997a3fbffda64", q, "photo", page);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {

                        String respon = response.body().string();
                        if (page.equals(1)) {
                            images.clear();
                            pDialog.dismiss();
                        }
//                        else {
//                            progressBar.setVisibility(View.GONE);
//                        }
                        JSONObject jsonObj = new JSONObject(respon);

                        String total  = jsonObj.getString("totalHits");
                        Log.e("total hits", total);

                        if (total.equalsIgnoreCase("0")){
                            recyclerView.setVisibility(View.GONE);
                            lytNoResult.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), getString(R.string.data_tidak_ketemu) ,Toast.LENGTH_SHORT).show();
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            lytNoResult.setVisibility(View.GONE);
                        }

                        JSONArray api = jsonObj.getJSONArray("hits");
//                        JSONArray api = new JSONArray(respon);
                        for (int i = 0; i < api.length(); i++) {
                            JSONObject object = api.getJSONObject(i);

                            Image image = new Image();
                            image.setLargeImageURL(object.getString("largeImageURL"));
                            image.setWebformatHeight(object.getInt("webformatHeight"));
                            image.setWebformatWidth(object.getInt("webformatWidth"));
                            image.setLikes(object.getInt("likes"));
                            image.setImageWidth(object.getInt("imageWidth"));
                            image.setId(object.getInt("id"));
//                            image.setUserId(object.getInt("userId"));
                            image.setViews(object.getInt("views"));
                            image.setComments(object.getInt("comments"));
                            image.setPageURL(object.getString("pageURL"));
                            image.setImageHeight(object.getInt("imageHeight"));
                            image.setWebformatURL(object.getString("webformatURL"));
                            image.setType(object.getString("type"));
                            image.setPreviewHeight(object.getInt("previewHeight"));
                            image.setTags(object.getString("tags"));
                            image.setDownloads(object.getInt("downloads"));
                            image.setUser(object.getString("user"));
                            image.setFavorites(object.getInt("favorites"));
                            image.setImageSize(object.getInt("imageSize"));
                            image.setPreviewWidth(object.getInt("previewWidth"));
                            image.setUserImageURL(object.getString("userImageURL"));
                            image.setPreviewURL(object.getString("previewURL"));

                            images.add(image);
                            filter_images.add(image);


                            Log.e("image", image.getLargeImageURL() + " " + q);

                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
//                        Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Souldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void
                        run() {
//                            Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LoCat for possible errors!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        menu.findItem(R.id.search).setVisible(true);
//        final View menuItem = menu.findItem(R.id.search).getActionView();
//        searchView = (SearchView) menuItem;
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        final View menuItem = searchMenuItem.getActionView();
        searchView = (SearchView) menuItem;
        searchView.setQueryHint(getString(R.string.cari));
        ((EditText) searchView.findViewById(R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.colorHint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                recyclerView.setVisibility(View.VISIBLE);
                lytNoResult.setVisibility(View.GONE);
            images.clear();
                ss = s;
                searchView.clearFocus();
                if (s.isEmpty()) {
                    filter_images.clear();
                    if (position == 0){
                        q = "modern";
                    }else if (position == 1){
                        q = "classic";
                    }else if (position == 2){
                        q = "nature";
                    }else if (position == 3){
                        q = "abstract";
                    }else if (position == 4){
                        q = "love";
                    }else if (position == 5){
                        q = "funny";
                    }else if (position == 6){
                        q = "city";
                    }else if (position == 7){
                        q = "animal";
                    }else if (position == 8){
                        q = "art";
                    }else if (position == 9){
                        q = "flora";
                    }else if (position == 10){
                        q = "life style";
                    }
                    fetchImages(1);
                    mAdapter.notifyDataSetChanged();
                } else {
                    filter_images.clear();
                    q = s;
                    fetchImages(1);
                    mAdapter.notifyDataSetChanged();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // implement your search here
//                if (s.isEmpty()) {
//                    filter_images.clear();
//                    filter_images.addAll(images);
//                    mAdapter.notifyDataSetChanged();
//                } else {
//                    filterImage(s);
//                    mAdapter.notifyDataSetChanged();
//                }
                return false;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
//                filter_images.clear();
                recyclerView.setVisibility(View.GONE);
                lytNoResult.setVisibility(View.VISIBLE);
                return true; // Open SearchView !!
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                lytNoResult.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                filter_images.clear();
                if (position == 0){
                    q = "modern";
                }else if (position == 1){
                    q = "classic";
                }else if (position == 2){
                    q = "nature";
                }else if (position == 3){
                    q = "abstract";
                }else if (position == 4){
                    q = "love";
                }else if (position == 5){
                    q = "funny";
                }else if (position == 6){
                    q = "city";
                }else if (position == 7){
                    q = "animal";
                }else if (position == 8){
                    q = "art";
                }else if (position == 9){
                    q = "flora";
                }else if (position == 10){
                    q = "life style";
                }
                fetchImages(1);
                mAdapter.notifyDataSetChanged();
                return true; // Close SearchView
            }
        });


        return true;
    }

    public void filterImage(String query) {
        filter_images.clear();
        for (int i = 0; i < images.size(); i++) {
            Image item = images.get(i);

            if (item.getTags().toLowerCase(Locale.getDefault()).contains(query.toLowerCase()))
                filter_images.add(item);
        }

        if (filter_images.size() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.data_tidak_ketemu), Toast.LENGTH_SHORT).show();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        if (t.onOptionsItemSelected(item))
            return true;


        switch (item.getItemId()) {


            case android.R.id.home:

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                } else {
                    finish();

                }

                break;

            case R.id.refresh:
                filter_images.clear();
                recyclerView.addOnScrollListener(new EndlessOnScrollListener(mLayoutManager) {
                    @Override
                    public void onLoadMore(Integer current_page) {
                        fetchImages(current_page);
                    }
                });
                fetchImages(1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode ==RESULT_OK) {
            mAdapter.notifyDataSetChanged();

        }
    }

}
