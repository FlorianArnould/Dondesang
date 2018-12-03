package fr.socket.florian.dondesang.ui.notification

import androidx.annotation.StringRes

data class Preference(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val keyRes: Int
)