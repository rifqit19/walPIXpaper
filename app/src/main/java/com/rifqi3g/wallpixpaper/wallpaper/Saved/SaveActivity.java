package com.rifqi3g.wallpixpaper.wallpaper.Saved;

import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.rifqi3g.wallpixpaper.wallpaper.ExampleJobService;
import com.rifqi3g.wallpixpaper.wallpaper.R;
import com.rifqi3g.wallpixpaper.wallpaper.helper.RealmHelper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SaveActivity extends AppCompatActivity {

    ImageButton back, deleteAll,autoBtn;
    Realm realm;
    RealmHelper realmHelper;
    RecyclerView recyclerView;
    AdapterSave adapterFav;
    List<SaveObj> favObjs;
    Toolbar toolbarFav;
    Switch autoSwitch;
    RelativeLayout emptyLyt;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferences;
    private static SaveActivity instance;


    Dialog alertDialog1;
    CharSequence[] values = {" 15 Minute "," 30 Minute "," 1 Hour "," 6 Hour "," 12 Hour "," 1 Day"};

    private static final String TAG = "SaveActivity";
    JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        instance = this;

        back = findViewById(R.id.backSave);
        deleteAll = findViewById(R.id.deleteAll);
        toolbarFav = findViewById(R.id.toolbarFav);
        emptyLyt = findViewById(R.id.emptyLayout);
//        autoBtn = findViewById(R.id.auto);
        autoSwitch = findViewById(R.id.switchAuto);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackIntent = new Intent();
                setResult(RESULT_OK, BackIntent);
                finish();
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favObjs.isEmpty()) {
                    Toast.makeText(SaveActivity.this, "Favorite is empty", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(SaveActivity.this)
                            .setTitle("Delete All")
                            .setMessage("Are you sure\nwill delete all your favorites?")
                            .setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    autoSwitch.setChecked(false);
                                    autoSwitch.setEnabled(false);
                                    realmHelper.deleteAll();
                                    emptyLyt.setVisibility(View.VISIBLE);
                                    Toast.makeText(SaveActivity.this,
                                            "All favorite items are deleted", Toast.LENGTH_SHORT).show();
                                    adapterFav.notifyDataSetChanged();
                                }
                            }).show();
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerSave);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(SaveActivity.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mGridLayoutManager);

        Realm.init(SaveActivity.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

        realmHelper = new RealmHelper(realm);
        favObjs = new ArrayList<>();

        favObjs = realmHelper.getFav();

        intentBroadcastRealmData();
        realmDataCondition();
        show();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = sharedPreferences.edit();
        autoSwitch.setChecked(sharedPreferences.getBoolean("isChecked",false));

//        if (favObjs.isEmpty()){
//            autoSwitch.setEnabled(false);
//        }else {
//            autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked){
//                        preferences.putBoolean("isChecked",true);
//                        String manufacturer = android.os.Build.MANUFACTURER;
//                        try {
//                            Intent intent = new Intent();
//                            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
//                                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
//                                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//                            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
//                                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//                            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
//                                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
//                            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
//                                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//                            }
//                            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                            if (list.size() > 0) {
//                                startActivity(intent);
//                                setJobScheduler();
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                    else {
//                        preferences.putBoolean("isChecked",false);
//                        cancelJob();
//                    }
//                    preferences.commit();
//                }
//            });
//        }


        if (favObjs.isEmpty()){
            autoSwitch.setEnabled(false);
        }else {
            autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        preferences.putBoolean("isChecked",true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(SaveActivity.this);
                        builder.setTitle("Warning!");
                        builder.setMessage("If you want to activate the automatic wallpaper\nYou must give permission");
                        builder.setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    autoSwitch.setChecked(false);
                                    cancelJob();
                                }
                            });
                        builder.setPositiveButton("Allow it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String manufacturer = android.os.Build.MANUFACTURER;
                                    try {
                                        Intent intent = new Intent();
                                        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                                            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                                        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                                            intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                                        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                                            intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                                        } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                                            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                                        } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                                        }

                                        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                        if (list.size() > 0) {
                                            startActivity(intent);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    autoSwitch.setChecked(true);
                                    CreateAlertDialogWithRadioButtonGroup();
                                }

                            }
                            );
                        Dialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if (keyCode == KeyEvent.KEYCODE_BACK){
                                    dialog.cancel();
                                    autoSwitch.setChecked(false);
                                    cancelJob();
                                }

                                return false;
                            }
                        });
                        dialog.show();

                    }
                    else {
                        preferences.putBoolean("isChecked",false);
                        autoSwitch.setChecked(false);
                        cancelJob();
                    }
                    preferences.commit();
                }
            });
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        toolbarFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);

            }
        });

        if (favObjs.isEmpty()) {
            emptyLyt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            adapterFav.notifyDataSetChanged();
        } else {
            emptyLyt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapterFav.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapterFav.notifyDataSetChanged();
        show();
    }

    @Override
    protected void onResume() {
        realmDataCondition();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiverRealmData);
        super.onDestroy();
    }

    public void show() {
        adapterFav = new AdapterSave(this, favObjs);
        recyclerView.setAdapter(adapterFav);
    }

    @Override
    public void onBackPressed() {
        Intent BackIntent = new Intent();
        setResult(RESULT_OK, BackIntent);
        finish();
    }

    private void intentBroadcastRealmData() {

        IntentFilter intentFilter = new IntentFilter("broadcast");
        registerReceiver(broadcastReceiverRealmData, intentFilter);
    }

    private BroadcastReceiver broadcastReceiverRealmData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras().getBundle("bundleBroadcast");

            if (bundle != null) {
                realmDataCondition();
            } else {
                Log.e("BUNDLE REALM DATA", "IS NULL");
            }
        }
    };

    private void realmDataCondition() {

        if (favObjs.isEmpty()) {
            emptyLyt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLyt.setVisibility(View.GONE);
        }

    }
    public void setJobScheduler(int period){
        ComponentName componentName = new ComponentName(this, ExampleJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
//                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo./*NETWORK_TYPE_UNMETERED*/NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(period * 15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.e(TAG, "Job scheduled");
        } else {
            Log.e(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        autoSwitch.setChecked(false);
        Log.e(TAG, "Job cancelled");

    }
    public static SaveActivity getInstance() {
        return instance;
    }



    public void CreateAlertDialogWithRadioButtonGroup(){


        final AlertDialog.Builder builder = new AlertDialog.Builder(SaveActivity.this);

//        builder.setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                alertDialog1.cancel();
//                autoSwitch.setChecked(false);
//                cancelJob();
//            }
//        });
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                alertDialog1.cancel();
//            }
//        });

        builder.setTitle("Select Your Period");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        setJobScheduler(1);
                        break;
                    case 1:
                        setJobScheduler(2);
                        break;
                    case 2:
                        setJobScheduler(4);
                        break;
                    case 3:
                        setJobScheduler(24);
                        break;
                    case 4:
                        setJobScheduler(48);
                        break;
                    case 5:
                        setJobScheduler(76);
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK){
                    alertDialog1.cancel();
                    autoSwitch.setChecked(false);
                }
                return false;
            }
        });
        alertDialog1.show();

    }


}