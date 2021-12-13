package com.loan555.kisdapplication2.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationEntity(
    var title: String = "",
    var message: String = "",
    var time: String = "",
    var appUrl: String = "",
    var avatarApp: String? = "",
    var read : Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
