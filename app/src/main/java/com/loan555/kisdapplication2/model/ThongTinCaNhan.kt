package com.loan555.kisdapplication2.model

import com.google.gson.annotations.SerializedName

data class ThongTinCANhan(
    val diaChi: DiaChi?,

    @SerializedName("_id")
    val id: String?,

    val ten: String?,
    val ngaySinh: String?,
    val anhChanDung: String?,
    val taiKhoan: String?,

    @SerializedName("__v")
    val v: Long?,

    val cmnd: String?
)
