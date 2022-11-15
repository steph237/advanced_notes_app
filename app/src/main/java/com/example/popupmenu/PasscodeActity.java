package com.example.popupmenu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class PasscodeActity extends AppCompatActivity {


    EditText editText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_actity);

        button = findViewById(R.id.passcode_confirm);
        editText = findViewById(R.id.pin);

        Toolbar passcodeTb = (Toolbar)findViewById(R.id.toolbar_passcode);
        setSupportActionBar(passcodeTb);

        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


    }
}