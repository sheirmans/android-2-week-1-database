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
        val adapter = NotesAdapter()
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        recyclerView.adapter = adapter

        adapter.itemClickListener = {
            startActivity(NoteActivity.createIntent(this, it))
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if(positionStart == 0)
                    recyclerView.layoutManager?.scrollToPosition(0)
            }
        })

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.removeNote(adapter.removeItem(viewHolder.adapterPosition))
            }

        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        val addNoteView: View = findViewById(R.id.am_add_note)
        addNoteView.setOnClickListener {
            startActivity(NoteActivity.createIntent(this))
        }

        viewModel.notesLiveData.observe(this, Observer<PagedList<Note>> { notes ->
            adapter.submitList(notes)
        })
    }

}

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val notesLiveData: LiveData<PagedList<Note>> = AppDatabase.get(getApplication())
            .notesDao()
            .getAllNotesPaged()
            .toLiveData(Config(
                    pageSize = 10,
                    prefetchDistance = 20,
                    enablePlaceholders = true
            ))

    fun removeNote(note: Note?) {
        if(note != null)
            AppDatabase.get(getApplication()).notesDao().deleteNote(note)
    }
}

private class NotesAdapter : PagedListAdapter<Note, NoteCardViewHolder>(noteDiffer) {

    var itemClickListener: ((Note) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteCardViewHolder {
        return NoteCardViewHolder.inflate(parent).apply {
            this.itemView.setOnClickListener {
                val item = getItem(this.adapterPosition)
                if(item != null)
                    itemClickListener?.invoke(item)
            }
        }
    }

    override fun onBindViewHolder(holder: NoteCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun removeItem(position: Int): Note? {
        return getItem(position).also {
            notifyItemRemoved(position)
        }
    }

    companion object {
        private val noteDiffer = object : DiffUtil.ItemCallback<Note>() {

            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
                    oldItem.datetime == newItem.datetime

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
                    oldItem == newItem

        }
    }
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
        image.isVisible = note?.imageUri != null
        image.setImageURI(note?.imageUri)
        titleView.setTextOrHide(note?.title)
        textView.setTextOrHide(note?.text)
    }

}