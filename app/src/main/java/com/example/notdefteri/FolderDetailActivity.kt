package com.example.notdefteri

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notdefteri.databinding.ActivityFolderDetailBinding


class FolderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderDetailBinding
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val folderId = intent.getStringExtra("folder_id")
        val folderName = intent.getStringExtra("folder_name") ?: "Klasör"
        title = folderName

        if (folderId == null) {
            finish()
            return
        }

        binding.notesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        val allNotes = Prefs.loadNotes(this)
        val filteredNotes = allNotes.filter { it.folderId == folderId }

        adapter = NotesAdapter(
            filteredNotes.toMutableList(),
            onClick = { item ->
                if (item is Note) {
                    val intent = Intent(this, AddNoteActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_NOTE, item)
                    startActivity(intent)
                }
            },
            onFolderIconClick = { _, _ -> },
            onSelectionChanged = { _ -> }
        )

        binding.notesRecyclerView.adapter = adapter
    }
}