package com.example.popupmenu;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.popupmenu.Adapters.NoteListAdapter;
import com.example.popupmenu.Database.RoomDB;
import com.example.popupmenu.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    List<Notes> notes = new ArrayList<>();
    CardView notes_container;
    RoomDB database;
    FloatingActionButton fab_add;
    Notes selectedNote;
    Toolbar delete_toolbar;
    Boolean noteHighlight = false;
    Boolean fingerPrint = false;


    // create a CancellationSignal
    // variable and assign a
    // value null to it
    private CancellationSignal cancellationSignal = null;

    // create an authenticationCallback
    private BiometricPrompt.AuthenticationCallback authenticationCallback;

    @RequiresApi(api = Build.VERSION_CODES.P)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_home);

        fab_add = findViewById(R.id.fab_add);

        database = RoomDB.getInstance(this);

        notes = database.mainDOA().getAll();

        updateRecycler(notes);

        delete_toolbar = findViewById(R.id.recent_delete_menu);
        actionMenu(delete_toolbar);

        findViewById(R.id.recent_delete_menu);

        // Setting onClick behavior to the button
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewNote();
            }
        });

        delete_toolbar.setVisibility(View.GONE);

//        fingerprint authentication

        authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            // here we need to implement two methods
            // onAuthenticationError and
            // onAuthenticationSucceeded If the
            // fingerprint is not recognized by the
            // app it will call onAuthenticationError
            // and show a toast
            @Override
            public void onAuthenticationError(
                    int errorCode, CharSequence errString)
            {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication Error : " + errString);
            }
            // If the fingerprint is recognized by the
            // app then it will call
            // onAuthenticationSucceeded and show a
            // toast that Authentication has Succeed
            // Here you can also start a new activity
            // after that
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result)
            {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication Succeeded");
                fingerPrint = true;
            }


        };
    }

    public void actionMenu(Toolbar delete_toolbar){
        delete_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete:

                        database.mainDOA().delete(selectedNote);
                        notes.remove(selectedNote);
                        noteListAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Note deleted!", Toast.LENGTH_SHORT).show();
                        delete_toolbar.setVisibility(View.GONE);
                        updateRecycler(notes);
                        return true;

                    case R.id.close_highlight:
                        delete_toolbar.setVisibility(View.GONE);
                        updateRecycler(notes);
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if(resultCode == Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDOA().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDOA().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
        else if (requestCode==102){
            if (resultCode ==Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDOA().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
                notes.clear();
                notes.addAll(database.mainDOA().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
    }



    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayout.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(noteListAdapter);

    }
    private final NotesClickListener notesClickListener=new NotesClickListener() {
        @Override
        public void onLongClick(Notes notes, CardView notes_container) {
            selectedNote = notes;
            delete_toolbar.setVisibility(View.VISIBLE);
            notes_container.setCardBackgroundColor(getColor(R.color.pending_gray));
            noteHighlight = true;


        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onClick(Notes notes) {
                if (fingerPrint){
                    Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                    intent.putExtra("old_note", notes);
                    startActivityForResult(intent, 102);
                }

            // This creates a dialog of biometric
            // auth and it requires title , subtitle
            // , and description In our case there
            // is a cancel button by clicking it, it
            // will cancel the process of
            // fingerprint authentication
            BiometricPrompt biometricPrompt = new BiometricPrompt
                    .Builder(getApplicationContext())
                    .setTitle("Title of Prompt")
                    .setSubtitle("Subtitle")
                    .setDescription("Uses FP")
                    .setNegativeButton("Cancel", getMainExecutor(), new DialogInterface.OnClickListener() {
                        @Override
                        public void
                        onClick(DialogInterface dialogInterface, int i)
                        {
                            notifyUser("Authentication Cancelled");
                            Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
                            intent.putExtra("old_note", notes);
                            startActivityForResult(intent, 102);
                        }
                    }).build();

            // start the authenticationCallback in
            // mainExecutor
            biometricPrompt.authenticate(
                    getCancellationSignal(),
                    getMainExecutor(),
                    authenticationCallback);

        }


    };

    // it will be called when
    // authentication is cancelled by
    // the user
    private CancellationSignal getCancellationSignal()
    {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                new CancellationSignal.OnCancelListener() {
                    @Override public void onCancel()
                    {
                        notifyUser("Authentication was Cancelled by the user");
                    }
                });
        return cancellationSignal;
    }


    // it checks whether the
    // app the app has fingerprint
    // permission
    @RequiresApi(Build.VERSION_CODES.M)
    private Boolean checkBiometricSupport()
    {
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isDeviceSecure()) {
            notifyUser("Fingerprint authentication has not been enabled in settings");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint Authentication Permission is not enabled");
            return false;
        }
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return true;
        }
        else
            return true;
    }

    public  void openNewNote(){
        Intent intent = new Intent(this, NewNoteActivity.class);
        startActivityForResult(intent, 101);
    }

    private void notifyUser(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}

