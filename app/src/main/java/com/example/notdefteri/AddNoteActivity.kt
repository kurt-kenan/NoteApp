package com.example.notdefteri

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.notdefteri.databinding.ActivityAddNoteBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private var note: Note? = null
    private var imageUri: String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${UUID.randomUUID()}.jpg"))
                UCrop.of(it, destinationUri)
                    .withAspectRatio(1f, 1f)
                    .start(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        note = intent.getParcelableExtra(MainActivity.EXTRA_NOTE)

        note?.let {
            binding.titleEditText.setText(it.title)
            binding.descEditText.setText(it.description)
            imageUri = it.imageUri
            imageUri?.let { uri -> binding.profileImageView.setImageURI(Uri.parse(uri)) }
        }

        binding.profileImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val desc = binding.descEditText.text.toString().trim()

            if (title.isEmpty()) {
                binding.titleEditText.error = "Başlık boş olamaz"
                return@setOnClickListener
            }

            val newNote = note?.copy(title = title, description = desc, imageUri = imageUri)
                ?: Note(title = title, description = desc, imageUri = imageUri)

            val resultIntent = Intent()
            resultIntent.putExtra(MainActivity.EXTRA_NOTE, newNote)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    resultUri?.let {
                        imageUri = it.toString()
                        binding.profileImageView.setImageURI(it)
                    }
                }

                UCrop.RESULT_ERROR -> {
                    UCrop.getError(data!!)?.printStackTrace()
                }
            }
        }
    }
}
