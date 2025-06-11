package com.example.notdefteri

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val imageUri: String? = null,
    val folderId: String? = null
) : Parcelable

