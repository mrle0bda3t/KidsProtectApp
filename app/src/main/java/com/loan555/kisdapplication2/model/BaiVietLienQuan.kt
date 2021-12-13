package com.loan555.kisdapplication2.model

import com.google.gson.annotations.SerializedName

data class BaiVietLienQuan(
    @SerializedName("_id")
    val id: String,
    val tenBaiViet: String,
    val tacGia: String,
    val noiDung: String,
    val chuThich: String,
    val loaiThongTinTuyenTruyen: String,
    val dinhDangThongTinTuyenTruyen: String,
    val ngayDang: String,
    val tinhTrang: String,
    val anhDaiDien: String,
    @SerializedName("__v")
    val v: Long
)
