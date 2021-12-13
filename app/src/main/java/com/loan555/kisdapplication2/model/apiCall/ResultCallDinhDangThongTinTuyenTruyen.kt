package com.loan555.kisdapplication2.model.apiCall

import com.loan555.kisdapplication2.model.view.DinhDangThongTinTuyenTruyen

data class ResultCallDinhDangThongTinTuyenTruyen(
    val status: Int,
    val data: List<DinhDangThongTinTuyenTruyen>
)
