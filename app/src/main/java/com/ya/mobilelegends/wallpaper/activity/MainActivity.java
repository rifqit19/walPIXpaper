package com.ya.mobilelegends.wallpaper.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ya.mobilelegends.wallpaper.R;
import com.ya.mobilelegends.wallpaper.app.AppController;
import com.ya.mobilelegends.wallpaper.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import com.ya.mobilelegends.wallpaper.adapter.GalleryAdapter;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static final String endpoint = "http://anangpambudi.com/api/wallpaper.json";
    private ArrayList<Image> images;
    private ArrayList<Image> filter_images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    //search
    SearchView searchView;
    boolean mSearchCheck;
    SearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        filter_images = new ArrayList<>();

         recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", filter_images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages();
    }

    public void setAdapter(ArrayList<Image> arrayList){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new GalleryAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(mAdapter);

        Log.e("size" , "" + images.size());
    }

    private void fetchImages() {

        pDialog.setMessage(getString(R.string.mengambil_data));
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        pDialog.hide();

                        images.clear();
                        filter_images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);
                                filter_images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        setAdapter(filter_images);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
                fetchImages();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {


            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            // implement your search here
            if (s.isEmpty()){
                filter_images.clear();
                filter_images.addAll(images);
                setAdapter(filter_images);
            }else {
                filterImage(s);
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        menu.findItem(R.id.search).setVisible(true);
        final View menuItem = menu.findItem(R.id.search).getActionView();


        searchView = (SearchView) menuItem;
        searchView.setQueryHint(getString(R.string.cari));

        ((EditText) searchView.findViewById(R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.colorHint));
        searchView.setOnQueryTextListener(onQuerySearchView);


        return true;
    }


    public void filterImage(String query){
        filter_images.clear();
        for (int i = 0; i < images.size(); i++) {
            Image item = images.get(i);

            if (item.getName().toLowerCase().contains(query.toLowerCase()))
                filter_images.add(item);

        }

        setAdapter(filter_images);

        if (filter_images.size() == 0){
            Toast.makeText(getApplicationContext(), getString(R.string.data_tidak_ketemu), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case android.R.id.home:

                if(!searchView.isIconified()) {
                    searchView.setIconified(true);
                }else{
                    finish();

                }

                break;

            case R.id.refresh:
                fetchImages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}