package com.loan555.kisdapplication2.model.view

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Detail(
    @SerializedName("_id")
    val id: String,
    val tenBaiViet: String,
    val tacGia: String,
    val noiDung: String,
    val chuThich: String,
    val loaiThongTinTuyenTruyen: LoaiThongTinTuyenTruyen,
    val dinhDangThongTinTuyenTruyen: DinhDangThongTinTuyenTruyen,
    val ngayDang: String,
    val tinhTrang: String,
    val anhDaiDien: String
): Serializable
