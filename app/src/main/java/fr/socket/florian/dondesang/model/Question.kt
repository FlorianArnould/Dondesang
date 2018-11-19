package fr.socket.florian.dondesang.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(val text: String, val message: String, val image: String, val next: String) : Parcelable