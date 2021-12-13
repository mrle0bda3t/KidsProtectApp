package com.loan555.kisdapplication2.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class ResultTaoTaiKhoanTreEm(
    val status: String,
    val data: TaiKhoanTreEm?,
    val msg: String?
)

data class TaiKhoanTreEm(
    val taiKhoan: TaiKhoan,
    val chiTiet: ChiTiet
)

data class TaoTaiKhoanTre(
    val token: String,
    val tenTK: String,
    val matKhau: String,
    val ten: String,
    val ngaySinh: String,
    val xa: String,
    val huyen: String,
    val tinh: String,
    val diaChi: String,
    val uri: Uri,
    val nhapLaiMatKhau: String
)

data class ChiTiet(
    @SerializedName("_id")
    val id: String,

    val ten: String,
    val ngaySinh: String,
    val diaChi: DiaChi,
    val anhChanDung: String,
    val nguoiBaoHo: String,

    @SerializedName("__v")
    val v: Long
)

data class TaiKhoan(
    val tenTaiKhoan: String,

    @SerializedName("_id")
    val id: String,

    val ngayDangKi: String
)
