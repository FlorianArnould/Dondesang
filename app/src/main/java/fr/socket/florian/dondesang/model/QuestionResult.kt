package fr.socket.florian.dondesang.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestionResult(val succeed: Boolean, val errorMessage: String = "") : Parcelable