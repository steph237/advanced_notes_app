package com.example.popupmenu;

import androidx.cardview.widget.CardView;

import com.example.popupmenu.Models.Notes;

public interface NotesClickListener {


    void onLongClick(Notes notes, CardView notes_container);

    void onClick(Notes notes);
}
