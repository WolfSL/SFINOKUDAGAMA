package com.flexiv.sfino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flexiv.sfino.utill.SharedPreference;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView flex_logo;
    private TextView appname;
    public Activity fa;

    private ProgressDialog dialog;

    private void doAnimation() {
        fa = this;

        flex_logo = (ImageView) findViewById(R.id.flex_logo);
        appname = findViewById(R.id.appname);

        SharedPreference.setSettings(this);

        System.out.println(SharedPreference.settings_pin);

        int splash_tine = 1200;
        Handler h = new Handler();

        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://raw.githubusercontent.com/caspersoftlk/SFINO-KUDAGAMA-UPDATES/master/update-changelog.json")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Log.d("Latest Version", update.getLatestVersion());
                        Log.d("Latest Version Code", update.getLatestVersionCode().toString());
                        Log.d("Release notes", update.getReleaseNotes());
                        Log.d("URL", update.getUrlToDownload().toString());
                        Log.d("Is update available?", Boolean.toString(isUpdateAvailable));

                        if(isUpdateAvailable){
                            dialog = new ProgressDialog(MainActivity.this);
                            dialog.setTitle("Downloading new Update : v"+update.getLatestVersionCode().toString());
                            dialog.setMessage("Doing something, please wait.\n\n"+update.getReleaseNotes());
                            dialog.setCancelable(false);
                            dialog.show();
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(update.getUrlToDownload().toString()));

                            request.setTitle("SFINO Downloading..");  //set title for notification in status_bar
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  //flag for if you want to show notification in status or not

                            String nameOfFile = "app.apk";    //if you want to give file_name manually

//                            String nameOfFile = URLUtil.guessFileName(update.getUrlToDownload().toString(), null, MimeTypeMap.getFileExtensionFromUrl(update.getUrlToDownload().toString())); //fetching name of file and type from server

                            File f = new File(Environment.getExternalStorageDirectory() + "/SFINOUpdates");       // location, where to download file in external directory
                            if (!f.exists()) {
                                f.mkdirs();
                            }

                            System.out.println("File path --- "+f.getPath());
                            request.setDestinationInExternalPublicDir("SFINOUpdates", nameOfFile);
                            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            long enq = downloadManager.enqueue(request);



                            BroadcastReceiver receiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    String action = intent.getAction();
                                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                                        DownloadManager.Query query = new DownloadManager.Query();
                                        query.setFilterById(enq);

                                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        Cursor c = downloadManager.query(query);
                                        if (c.moveToFirst()) {
                                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                                //TODO : Use this local uri and launch intent to open file
                                                System.out.println(uriString);
                                                File file = new File(uriString.replace("file:///",""));
                                                Intent promptInstall = new Intent(Intent.ACTION_VIEW);

                                                Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

                                                System.out.println(photoURI.getPath()+"-------");


                                                promptInstall.setDataAndType(photoURI,
                                                        "application/vnd.android.package-archive");
                                                promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                startActivity(promptInstall);
                                            }
                                            dialog.dismiss();
                                        }
                                    }
                                }
                            };
                            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }else{
                            Log.e("Update :","Not awailable");
                            h.postDelayed(() -> {
                                Pair[] pairs = new Pair[2];
                                pairs[0] = new Pair<View, String>(flex_logo, "flex_logo");
                                pairs[1] = new Pair<View, String>(appname, "appname");
                                Intent i = new Intent(MainActivity.this, Login.class);
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                                startActivity(i, options.toBundle());
                            }, splash_tine);
                        }

                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                        h.postDelayed(() -> {
                            Pair[] pairs = new Pair[2];
                            pairs[0] = new Pair<View, String>(flex_logo, "flex_logo");
                            pairs[1] = new Pair<View, String>(appname, "appname");
                            Intent i = new Intent(MainActivity.this, Login.class);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                            startActivity(i, options.toBundle());
                        }, splash_tine);
                    }
                });
        appUpdaterUtils.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    1);

        } else {
            doAnimation();
        }
    }
}
