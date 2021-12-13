package com.loan555.kisdapplication2.model.apiCall

import com.loan555.kisdapplication2.model.view.LoaiThongTinTuyenTruyen

data class ResultCallLoaiThongTinTuyenTruyen(
    val status: Int,
    val data: List<LoaiThongTinTuyenTruyen>
)
