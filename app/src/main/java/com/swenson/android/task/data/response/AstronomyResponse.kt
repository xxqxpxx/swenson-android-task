package com.swenson.android.task.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AstronomyResponse(
    val copyright: String?,
    val date: String,
    val explanation: String,
    var hdurl: String?,
    val media_type: String,
    val service_version: String,
    val title: String,
    val url: String,
) : Parcelable

