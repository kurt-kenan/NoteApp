package com.example.notdefteri

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Prefs {

    private const val PREF_NOTES = "notes_pref"
    private const val PREF_FOLDERS = "folders_pref"
    private const val KEY_NOTES = "notes_list"
    private const val KEY_FOLDERS = "folders_list"

    private val gson = Gson()

    fun loadNotes(context: Context): MutableList<Note> {
        val json = context.getSharedPreferences(PREF_NOTES, Context.MODE_PRIVATE)
            .getString(KEY_NOTES, "[]")
        val type = object : TypeToken<MutableList<Note>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveNotes(context: Context, notes: List<Note>) {
        context.getSharedPreferences(PREF_NOTES, Context.MODE_PRIVATE).edit()
            .putString(KEY_NOTES, gson.toJson(notes))
            .apply()
    }

    fun loadFolders(context: Context): MutableList<Folder> {
        val json = context.getSharedPreferences(PREF_FOLDERS, Context.MODE_PRIVATE)
            .getString(KEY_FOLDERS, "[]")
        val type = object : TypeToken<MutableList<Folder>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveFolders(context: Context, folders: List<Folder>) {
        context.getSharedPreferences(PREF_FOLDERS, Context.MODE_PRIVATE).edit()
            .putString(KEY_FOLDERS, gson.toJson(folders))
            .apply()
    }

    fun clearAll(context: Context) {
        context.getSharedPreferences(PREF_NOTES, Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences(PREF_FOLDERS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
