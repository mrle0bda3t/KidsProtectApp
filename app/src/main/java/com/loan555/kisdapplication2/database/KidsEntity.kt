package com.loan555.kisdapplication2.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class KidsEntity(
    @PrimaryKey
    @SerializedName("_id")
    var id: String,
    var ten: String? = null,
    var ngaySinh: String? = null,
    var anhChanDung: String? = null
)
