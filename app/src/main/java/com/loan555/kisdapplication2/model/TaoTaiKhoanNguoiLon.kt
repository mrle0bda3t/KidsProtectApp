package com.loan555.kisdapplication2.model

import android.net.Uri

data class TaoTaiKhoanNguoiLon(
    val token : String,
    val ten: String,
    val ngaySinh: String,
    val avt: Uri,
    val cmnd: String,
    val xa: String,
    val huyen: String,
    val tinh_ThanhPho: String,
    val diaChiCuThe: String
)
