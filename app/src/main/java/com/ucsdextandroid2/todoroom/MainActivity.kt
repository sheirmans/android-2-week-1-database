package com.ucsdextandroid2.todoroom

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.paging.toLiveData
import androidx.recyclerview.widget.*

class MainActivity: AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        val recyclerView: RecyclerView = findViewById(R.id.am_recycler_view)
        val adapter = NotesAdapter()
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        recyclerView.adapter = adapter

        adapter.onNoteClickLister = { note ->
            startActivity(NoteActivity.createIntent(this, note))
        }

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {

            super.onItemRangeInserted(positionStart, itemCount)

            if(positionStart == 0)
            recyclerView.layoutManager?.scrollToPosition(0)
        }

        })
        val itemTouchHelper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START or ItemTouchHelper.END) {
                    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        val note = adapter.removeItem(viewHolder.adapterPosition)

                        if(note != null)
                            AppDatabase.get(this@MainActivity).noteDao().deleteNote(note)
                    }
                })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        val addNoteView: View = findViewById(R.id.am_add_note)
        addNoteView.setOnClickListener {
            startActivity(NoteActivity.createIntent(this))
        }

        //onDataChanged()

        AppDatabase.get(this).noteDao()
                .getAllNotesLiveData()
                .observe(this,Observer<List<Note>> { notes ->
                onDataChanged(notes)
                    adapter.submitList(notes)
        })
    }

        private fun onDataChanged(notes: List<Note>) {
//            val notes = AppDatabase.get(this).noteDao().getAllNotes()
            notes.forEach {
                Log.d("MainActivity",it.title + "" + it.text)
            }

            Toast.makeText(this, "Total Number of Notes: " + notes.size, Toast.LENGTH_SHORT).show()
        }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            onDataChanged()
        }
    }*/

}

private class NotesAdapter : ListAdapter<Note, NoteCardViewHolder>(listDiffer) {

    var onNoteClickLister:((Note) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteCardViewHolder {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val viewHolder = NoteCardViewHolder.inflate(parent)

        viewHolder.itemView.setOnClickListener{
            val note = getItem(viewHolder.adapterPosition)
            if(note != null) {
                onNoteClickLister?.invoke(note)
            }
        }

       // return NoteCardViewHolder.inflate(parent)
        return viewHolder

    }

    override fun onBindViewHolder(holder: NoteCardViewHolder, position: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        holder.bind(getItem(position))
    }

    fun removeItem(position: Int): Note? {
        val note = getItem(position)
       // notifyItemRemoved(position)

        return note
    }

   companion object {
        val listDiffer: DiffUtil.ItemCallback<Note> = object: DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.datetime == newItem.datetime
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }
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
        if(note != null) {
            titleView.setText(note.title)
            textView.text = note.text

            if (note.imageUri != null) {
                image.isVisible = true
                image.setImageURI(note.imageUri)
            }
            else {
                image.isVisible = false
            }
        }
        else {
            titleView.text = ""
            textView.text = ""
            image.isVisible = false
        }

    }

}