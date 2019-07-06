package com.ucsdextandroid2.todoroom

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by rjaylward on 2019-07-05
 */

@Parcelize
data class Note(
        val datetime: Long = System.currentTimeMillis(),
        val title: String?,
        val text: String?,
        val imageUri: Uri? = null
): Parcelable