package com.loan555.kisdapplication2.model.view

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoaiThongTinTuyenTruyen(
    @SerializedName("_id")
    val id: String,
    val tenLoai: String,
    @SerializedName("__v")
    val v: Long
) : Serializable
