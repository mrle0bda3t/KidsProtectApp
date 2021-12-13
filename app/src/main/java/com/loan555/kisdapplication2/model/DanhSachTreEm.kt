package com.loan555.kisdapplication2.model

import com.google.gson.annotations.SerializedName


data class DanhSachTreResult(
    val status: String,
    val danhSachTreEm: List<DanhSachTreEm>?
)

data class DanhSachTreEm(
    val diaChi: DiaChi,

    @SerializedName("_id")
    val id: String,

    val ten: String,
    val ngaySinh: String,
    val anhChanDung: String
)

data class DiaChi(
    val xa: String,
    val huyen: String,

    @SerializedName("tinh_ThanhPho")
    val tinhThanhPho: String,

    val diaChiCuThe: String
)