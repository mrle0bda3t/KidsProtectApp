package com.loan555.kisdapplication2.model.apiCall

import com.loan555.kisdapplication2.model.view.ThongTinTuyenTruyen

data class ResultCallListThongTinTuyenTruyen(
    val status: String,
    val totalResults: Long,
    val thongTinTuyenTruyen: List<ThongTinTuyenTruyen>
)
