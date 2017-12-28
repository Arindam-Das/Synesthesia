package com.appprojects.arindam.synesthesia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appprojects.arindam.synesthesia.util.SongCollector;
import com.appprojects.arindam.synesthesia.util.SongDatabaseHelper;

import java.security.InvalidAlgorithmParameterException;
import java.util.Set;

public class SplashActivity extends AppCompatActivity {

    private SongDatabaseHelper helper = null;

    public SongDatabaseHelper getHelper() {
        if(helper == null){
            helper = SongDatabaseHelper.getHelper(this);
        }
        return helper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences =
                getSharedPreferences(DirectorySetterActivity.SP_FILE_NAME, MODE_PRIVATE);
        Set<String> directoryPathSet = sharedPreferences
                .getStringSet(DirectorySetterActivity.SP_SEARCH_DIR_KEY, new ArraySet<String>());

        SongCollector songCollector = new SongCollector(getHelper());
        songCollector.setTaskToBeDoneOnCompletion(new SongCollector.Task() {
            @Override
            public void doTask() {
                SplashActivity.this
                        .startActivity(
                                new Intent(SplashActivity.this, MainActivity.class));
            }
        });

        for(String path : directoryPathSet){
            try { songCollector.addSearchDirectory(path);}
            catch (InvalidAlgorithmParameterException e)
            { e.printStackTrace(); }
        }

        Thread thread = new Thread(songCollector);
        thread.start();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        if(helper != null) {
            helper.close();
            helper = null;
        }
        super.onPause(); finish();
    }

    @Override
    protected void onStop() {
        if(helper != null){
            helper.close();
            helper = null;
        }
        super.onStop(); finish();
    }
}
