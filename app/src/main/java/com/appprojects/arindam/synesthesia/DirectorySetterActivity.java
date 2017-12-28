package com.appprojects.arindam.synesthesia;

import android.content.Intent;
import android.content.SharedPreferences;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.util.ArraySet;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appprojects.arindam.synesthesia.util.listui.DirectoryElement;
import com.appprojects.arindam.synesthesia.util.listui.DirectoryElementAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DirectorySetterActivity extends AppCompatActivity {
    static final int REQUEST_PATH = 0x5AF;
    static final String SP_FILE_NAME = "directories";
    static final String SP_SEARCH_DIR_KEY = "search_directories";

    private List<DirectoryElement> directories;
    DirectoryElementAdapter directoryElementAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_setter);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)actionBar.setDisplayHomeAsUpEnabled(true);

        directories = new ArrayList<>();
        sharedPreferences = getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet(SP_SEARCH_DIR_KEY, new ArraySet<String>());
        Toast.makeText(this, set.toString(), Toast.LENGTH_SHORT).show();

        for(String dirPath : set){
            DirectoryElement element = new DirectoryElement(new File(dirPath));
            directories.add(element); }

        directoryElementAdapter =
                new DirectoryElementAdapter(getApplicationContext(),
                        R.layout.directory_list_element, directories);
        ListView listView = (ListView)findViewById(R.id.directories_list_view);
        listView.setAdapter(directoryElementAdapter);

        FloatingActionButton floatingActionButton =
                (FloatingActionButton)findViewById(R.id.directory_adder);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(DirectorySetterActivity.this,
                        DirectoryPathAcceptor.class), REQUEST_PATH);

                Snackbar.make(view, "Add a new directory.", Snackbar.LENGTH_SHORT).show();
            }
        });


        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.directory_element_menu, menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PATH){
            if(resultCode == RESULT_OK){
                String path = data.getStringExtra(DirectoryPathAcceptor.INTENT_KEY);

                File file = new File(path);
                if(!file.isDirectory()) {
                    Toast.makeText(this, "Given path doesn't represent a directory.",
                            Toast.LENGTH_SHORT).show(); return;
                }
                DirectoryElement element = new DirectoryElement(file);
                this.directories.add(element);
                this.directoryElementAdapter.notifyDataSetChanged();
                Snackbar.make(findViewById(R.id.directory_setter_root),
                        "Directory addition successful", Snackbar.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED)
                Snackbar.make(findViewById(R.id.directory_setter_root),
                        "Directory addition cancelled", Snackbar.LENGTH_SHORT).show();
        }else Toast.makeText(this, "Invalid Request", Toast.LENGTH_SHORT).show();
        cacheDirectories();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.play_all_in_this_dir:
                return true;
            case R.id.remove_from_list:
                Toast.makeText(this, "\""+directories.get(info.position).getName()+
                        "\" was removed from your directory list.", Toast.LENGTH_SHORT).show();
                this.directories.remove(info.position);
                this.directoryElementAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;
        }
    }

    

    private void cacheDirectories(){
        Set<String> directory_set = new ArraySet<>();
        for(DirectoryElement element : directories)
            directory_set.add(element.getPath());
        sharedPreferences.edit()
                .putStringSet(SP_SEARCH_DIR_KEY, directory_set).apply();
        Toast.makeText(this, sharedPreferences.getStringSet(SP_SEARCH_DIR_KEY,
                new ArraySet<String>()).toString(), Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onStop() { cacheDirectories();
        super.onStop();  }

    @Override
    protected void onPause(){ cacheDirectories();
        super.onPause(); }
}
