package com.example.notdefteri

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Folder(
    val id: String = UUID.randomUUID().toString(),
    val name: String
) : Parcelable