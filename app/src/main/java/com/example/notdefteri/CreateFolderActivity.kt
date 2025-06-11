package com.example.notdefteri

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notdefteri.databinding.ActivityCreateFolderBinding

class CreateFolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFolderBinding
    private var selectedNotes: List<Note> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedNotes = intent.getParcelableArrayListExtra(MainActivity.EXTRA_SELECTED_NOTES) ?: emptyList()

        binding.createButton.setOnClickListener {
            val folderName = binding.folderNameEditText.text.toString().trim()
            if (folderName.isEmpty()) {
                binding.folderNameEditText.error = "Klasör adı boş olamaz"
                return@setOnClickListener
            }

            val folderList = Prefs.loadFolders(this)
            val newFolder = Folder(name = folderName)
            folderList.add(newFolder)
            Prefs.saveFolders(this, folderList)

            if (selectedNotes.isNotEmpty()) {
                val notesList = Prefs.loadNotes(this)
                selectedNotes.forEach { note ->
                    val index = notesList.indexOfFirst { it.id == note.id }
                    if (index >= 0) {
                        notesList[index] = notesList[index].copy(folderId = newFolder.id)
                    }
                }
                Prefs.saveNotes(this, notesList)
            }

            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}