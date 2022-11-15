
package com.example.popupmenu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


//import com.example.notesapp.Models.Notes;

import com.example.popupmenu.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

    public class NewNoteActivity extends AppCompatActivity {

        EditText editText_title, editText_notes;
        Button save_button;
        Notes notes;
        boolean isOldNote = false;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_note);
            Toolbar newNotes = (Toolbar)findViewById(R.id.toolbar_notes);
            setSupportActionBar(newNotes);

            // Get a support ActionBar corresponding to this toolbar
            ActionBar ab = getSupportActionBar();
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);

            editText_title = findViewById(R.id.editText_title);
            editText_notes = findViewById(R.id.editText_notes);
            notes = new Notes();
            try {
                notes = (Notes) getIntent().getSerializableExtra("old_note");
                editText_title.setText(notes.getTitle());
                editText_notes.setText(notes.getNotes());
                isOldNote = true;

            } catch(Exception e){
                e.printStackTrace();
            }

            save_button = findViewById(R.id.save_button);
            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = editText_title.getText().toString();
                    String description = editText_notes.getText().toString();

                    if (description.isEmpty()){
                        Toast.makeText(NewNoteActivity.this, "please add notes ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyy HH:mm a");

                    Date date = new Date();

                    if (!isOldNote){
                        notes = new Notes();
                    }

                    notes.setTitle(title);
                    notes.setNotes(description);
                    notes.setDate(formatter.format(date));

                    Intent intent = new Intent();
                    intent.putExtra("note", notes);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });





        }
    }
