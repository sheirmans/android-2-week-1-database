package com.ucsdextandroid2.todoroom

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.paging.toLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        val recyclerView: RecyclerView = findViewById(R.id.am_recycler_view)

        val addNoteView: View = findViewById(R.id.am_add_note)
    }

}

class NotesAdapter {

}

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

}

private class NoteCardViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {

    val image: ImageView = itemView.findViewById(R.id.vnc_image)
    val titleView: TextView = itemView.findViewById(R.id.vnc_title)
    val textView: TextView = itemView.findViewById(R.id.vnc_text)

    companion object {
        fun inflate(parent: ViewGroup): NoteCardViewHolder = NoteCardViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_holder_note_card, parent, false)
        )
    }

    fun bind(note: Note?) {

    }

}