package com.example.notdefteri

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    var items: MutableList<Any>,
    private val onClick: (Any) -> Unit,
    private val onFolderIconClick: (selectedNotes: List<Note>, folderId: String) -> Unit,
    private val onSelectionChanged: (Int) -> Unit,
    private val onFolderLongClick: ((Folder) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()
    var isSelectionMode = false
        private set

    companion object {
        private const val TYPE_NOTE = 0
        private const val TYPE_FOLDER = 1
    }

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteImage: ImageView = view.findViewById(R.id.noteImage)
        val noteTitle: TextView = view.findViewById(R.id.noteTitle)
        val folderIcon: ImageView = view.findViewById(R.id.folderIcon)
    }

    inner class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val folderIcon: ImageView = view.findViewById(R.id.folderIcon)
        val folderTitle: TextView = view.findViewById(R.id.folderName)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Note -> TYPE_NOTE
            is Folder -> TYPE_FOLDER
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NOTE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
                NoteViewHolder(view)
            }
            TYPE_FOLDER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
                FolderViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is NoteViewHolder -> {
                val note = item as Note
                holder.noteTitle.text = note.title

                holder.folderIcon.visibility = View.GONE
                if (!note.imageUri.isNullOrEmpty()) {
                    holder.noteImage.setImageURI(Uri.parse(note.imageUri))
                } else {
                    holder.noteImage.setImageResource(R.drawable.placeholder_image)
                }

                holder.itemView.setBackgroundColor(
                    if (selectedPositions.contains(position)) Color.parseColor("#9933B5E5") else Color.TRANSPARENT
                )

                holder.itemView.setOnClickListener {
                    if (isSelectionMode) toggleSelection(position) else onClick(note)
                }

                holder.itemView.setOnLongClickListener {
                    if (!isSelectionMode) {
                        isSelectionMode = true
                        toggleSelection(position)
                    } else {
                        clearSelection()
                    }
                    true
                }
            }

            is FolderViewHolder -> {
                val folder = item as Folder
                holder.folderTitle.text = folder.name
                holder.folderIcon.setImageResource(R.drawable.ic_folder)

                holder.itemView.setBackgroundColor(
                    if (selectedPositions.contains(position)) Color.parseColor("#9933B5E5") else Color.TRANSPARENT
                )

                holder.itemView.setOnClickListener {
                    if (isSelectionMode) toggleSelection(position) else onClick(folder)
                }

                holder.itemView.setOnLongClickListener {
                    onFolderLongClick?.invoke(folder)
                    true
                }

                holder.folderIcon.setOnClickListener {
                    val selectedNotes = getSelectedNotes()
                    if (isSelectionMode && selectedNotes.isNotEmpty()) {
                        onFolderIconClick(selectedNotes, folder.id)
                        clearSelection()
                    } else {
                        onClick(folder) // klasör ikonuna tıklayınca klasörü aç
                    }
                }
            }
        }
    }

    fun toggleSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }
        isSelectionMode = selectedPositions.isNotEmpty()
        notifyItemChanged(position)
        onSelectionChanged(selectedPositions.size)
    }

    fun clearSelection() {
        val oldSelections = selectedPositions.toList()
        selectedPositions.clear()
        isSelectionMode = false
        oldSelections.forEach { notifyItemChanged(it) }
        onSelectionChanged(0)
    }

    fun getSelectedNotes(): List<Note> {
        return selectedPositions.mapNotNull {
            items.getOrNull(it) as? Note
        }
    }

    fun getSelectedFolders(): List<Folder> {
        return selectedPositions.mapNotNull {
            items.getOrNull(it) as? Folder
        }
    }

    fun getSelectedPositions(): Set<Int> {
        return selectedPositions.toSet()
    }

    fun updateNotesAndFolders(newItems: MutableList<Any>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = items[oldItemPosition]
                val new = newItems[newItemPosition]
                return old == new
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(oldItemPosition, newItemPosition)
            }
        })
        items = newItems
        clearSelection()
        diffResult.dispatchUpdatesTo(this)
    }
}
