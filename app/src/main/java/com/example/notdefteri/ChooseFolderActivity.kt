package com.example.notdefteri

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notdefteri.databinding.ActivityChooseFolderBinding

class ChooseFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseFolderBinding
    private var folderList = mutableListOf<Folder>()
    private var selectedFolderPosition = -1
    private var selectedNotes: List<Note> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedNotes = intent.getParcelableArrayListExtra(MainActivity.EXTRA_SELECTED_NOTES) ?: emptyList()

        folderList = Prefs.loadFolders(this)

        val folderNames = folderList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, folderNames)
        binding.folderListView.adapter = adapter
        binding.folderListView.choiceMode = android.widget.ListView.CHOICE_MODE_SINGLE

        binding.folderListView.setOnItemClickListener { _, _, position, _ ->
            selectedFolderPosition = position
        }

        binding.chooseButton.setOnClickListener {
            if (selectedFolderPosition == -1) {
                Toast.makeText(this, "Klasör seçiniz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedFolder = folderList[selectedFolderPosition]
            val notesList = Prefs.loadNotes(this)

            selectedNotes.forEach { selectedNote ->
                val index = notesList.indexOfFirst { it.id == selectedNote.id }
                if (index >= 0) {
                    notesList[index] = notesList[index].copy(folderId = selectedFolder.id)
                }
            }

            Prefs.saveNotes(this, notesList)
            Toast.makeText(this, "Notlar klasöre taşındı", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}