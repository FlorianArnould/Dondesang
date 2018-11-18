package fr.socket.florian.dondesang.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import java.util.Date

@Parcelize
data class Donation(val location: String, val date: Date) : Parcelable
