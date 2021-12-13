package com.loan555.kisdapplication2.model.apiCall

import com.google.gson.annotations.SerializedName

data class LoaiTaiKhoan(
    @SerializedName("_id")
    val id: String,
    val tenLoaiTaiKhoan: String
)
