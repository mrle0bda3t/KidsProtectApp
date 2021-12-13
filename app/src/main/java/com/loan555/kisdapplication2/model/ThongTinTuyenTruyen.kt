package com.loan555.kisdapplication2.model.view

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ThongTinTuyenTruyen(
    @SerializedName("_id") var id: String,
    var tenBaiViet: String,
    var tacGia: String,
    var chuThich: String,
    var loaiThongTinTuyenTruyen: LoaiThongTinTuyenTruyen,
    var dinhDangThongTinTuyenTruyen: DinhDangThongTinTuyenTruyen,
    var ngayDang: String,
    var tinhTrang: String,
    var anhDaiDien: String,
    @SerializedName("noiDung") var noidung: String,
) : Serializable
