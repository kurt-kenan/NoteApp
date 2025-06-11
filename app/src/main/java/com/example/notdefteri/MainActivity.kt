package com.example.notdefteri

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notdefteri.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NotesAdapter

    private var notesList = mutableListOf<Note>()
    private var folderList = mutableListOf<Folder>()
    private var filteredFolderId: String? = null

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_SELECTED_NOTES = "selected_notes"
    }

    private val noteResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val returnedNote = data?.getParcelableExtra<Note>(EXTRA_NOTE)
                returnedNote?.let { handleReturnedNote(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        updateUI()
    }

    private fun setupRecyclerView() {
        binding.notesRecyclerView.layoutManager = GridLayoutManager(this, 2)

        adapter = NotesAdapter(
            mutableListOf(),
            onClick = { item ->
                if (!adapter.isSelectionMode) {
                    when (item) {
                        is Folder -> {
                            filteredFolderId = item.id
                            updateUI()
                        }
                        is Note -> {
                            val intent = Intent(this, AddNoteActivity::class.java)
                            intent.putExtra(EXTRA_NOTE, item)
                            noteResultLauncher.launch(intent)
                        }
                    }
                } else {
                    toggleSelection(item)
                }
            },
            onFolderIconClick = { selectedNotes, folderId ->
                moveNotesToFolder(selectedNotes, folderId)
            },
            onSelectionChanged = { count -> toggleDeleteBar(count) },
            onFolderLongClick = { folder -> confirmFolderDeletion(folder) }
        )

        binding.notesRecyclerView.adapter = adapter
    }

    private fun setupButtons() {
        binding.addNoteButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            noteResultLauncher.launch(intent)
        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedItems()
        }

        binding.cancelButton.setOnClickListener {
            adapter.clearSelection()
            hideDeleteBar()
        }

        binding.folderButton.setOnClickListener {
            val selectedNotes = adapter.getSelectedNotes()
            if (selectedNotes.isEmpty()) {
                Toast.makeText(this, "Klasöre eklemek için not seç", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showFolderDialog(selectedNotes)
        }
    }

    private fun loadData() {
        notesList = Prefs.loadNotes(this)
        folderList = Prefs.loadFolders(this).toMutableList()
    }

    private fun updateUI() {
        // Silinmiş boş klasörleri temizle
        val nonEmptyFolders = folderList.filter { folder ->
            notesList.any { it.folderId == folder.id }
        }.toMutableList()

        folderList = nonEmptyFolders
        Prefs.saveFolders(this, folderList)

        val visibleNotes = if (filteredFolderId == null) {
            notesList.filter { it.folderId == null }
        } else {
            notesList.filter { it.folderId == filteredFolderId }
        }

        val combinedItems = if (filteredFolderId == null) {
            folderList + visibleNotes
        } else {
            visibleNotes
        }

        adapter.updateNotesAndFolders(combinedItems.toMutableList())

        binding.titleText.text = folderList.find { it.id == filteredFolderId }?.name ?: "Notlarım"
        toggleEmptyView()
    }

    private fun toggleSelection(item: Any) {
        val position = adapter.items.indexOf(item)
        if (position != -1) {
            adapter.toggleSelection(position)
        }
    }

    private fun toggleEmptyView() {
        val isEmpty = adapter.itemCount == 0
        binding.emptyText.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun toggleDeleteBar(count: Int) {
        if (count > 0) {
            binding.deleteBar.visibility = android.view.View.VISIBLE
            binding.folderButton.visibility = android.view.View.VISIBLE
            binding.selectedCountText.text = "$count seçildi"
        } else {
            hideDeleteBar()
        }
    }

    private fun hideDeleteBar() {
        binding.deleteBar.visibility = android.view.View.GONE
        binding.folderButton.visibility = android.view.View.GONE
    }

    private fun confirmFolderDeletion(folder: Folder) {
        AlertDialog.Builder(this)
            .setTitle("Klasörü Sil")
            .setMessage("Bu klasörü silmek istediğinize emin misiniz?")
            .setPositiveButton("Sil") { _, _ ->
                notesList = notesList.map {
                    if (it.folderId == folder.id) it.copy(folderId = null) else it
                }.toMutableList()
                folderList.removeAll { it.id == folder.id }
                Prefs.saveFolders(this, folderList)
                Prefs.saveNotes(this, notesList)
                adapter.clearSelection()
                updateUI()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun deleteSelectedItems() {
        val selectedNotes = adapter.getSelectedNotes()
        val selectedFolders = adapter.getSelectedFolders()

        notesList.removeAll { note -> selectedNotes.any { it.id == note.id } }

        selectedFolders.forEach { folder ->
            notesList = notesList.map {
                if (it.folderId == folder.id) it.copy(folderId = null) else it
            }.toMutableList()
        }

        folderList.removeAll { f -> selectedFolders.any { it.id == f.id } }

        Prefs.saveNotes(this, notesList)
        Prefs.saveFolders(this, folderList)

        adapter.clearSelection()
        updateUI()
        Toast.makeText(this, "${selectedNotes.size} not ve ${selectedFolders.size} klasör silindi", Toast.LENGTH_SHORT).show()
    }

    private fun moveNotesToFolder(selectedNotes: List<Note>, folderId: String) {
        selectedNotes.forEach { note ->
            val index = notesList.indexOfFirst { it.id == note.id }
            if (index >= 0) {
                notesList[index] = notesList[index].copy(folderId = folderId)
            }
        }
        Prefs.saveNotes(this, notesList)
        updateUI()
    }

    private fun handleReturnedNote(note: Note) {
        val index = notesList.indexOfFirst { it.id == note.id }
        if (index >= 0) {
            notesList[index] = note
        } else {
            notesList.add(0, note)
        }
        Prefs.saveNotes(this, notesList)
        updateUI()
    }

    private fun showFolderDialog(selectedNotes: List<Note>) {
        val options = arrayOf("Yeni klasör oluştur", "Mevcut klasöre ekle")
        AlertDialog.Builder(this)
            .setTitle("Klasör Seç")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(this, CreateFolderActivity::class.java)
                        intent.putExtra(EXTRA_SELECTED_NOTES, ArrayList(selectedNotes))
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(this, ChooseFolderActivity::class.java)
                        intent.putExtra(EXTRA_SELECTED_NOTES, ArrayList(selectedNotes))
                        startActivity(intent)
                    }
                }
            }
            .show()

        adapter.clearSelection()
        hideDeleteBar()
    }

    override fun onBackPressed() {
        if (filteredFolderId != null) {
            filteredFolderId = null
            updateUI()
        } else {
            super.onBackPressed()
        }
    }
}
