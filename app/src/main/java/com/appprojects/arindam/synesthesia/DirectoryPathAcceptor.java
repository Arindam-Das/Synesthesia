package com.appprojects.arindam.synesthesia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DirectoryPathAcceptor extends Activity {

    static final String INTENT_KEY = "directory_path";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_path_acceptor);

        //obtain handles to UI elements and handle callback events
        final Button adder = findViewById(R.id.add_custom_directory);
        adder.setEnabled(false);
        Button cancelAddition  = findViewById(R.id.cancel_directory_addition);

        final EditText editText = findViewById(R.id.directory_path);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() > 0)
                {adder.setEnabled(true); adder.setTextColor(getResources().getColor(R.color.colorAccent));}
                else {adder.setEnabled(false); adder.setTextColor(getResources().getColor(R.color.disabled));}
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        final Intent intent = new Intent();
        adder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = editText.getText().toString();
                Toast.makeText(DirectoryPathAcceptor.this, path, Toast.LENGTH_SHORT).show();
                intent.putExtra(INTENT_KEY, path);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
