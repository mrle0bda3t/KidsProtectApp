package com.loan555.kisdapplication2.model.apiCall

import com.loan555.kisdapplication2.model.BaiVietLienQuan
import com.loan555.kisdapplication2.model.view.Detail

data class ResultCallDetail(
    val status: String,
    val baiVietLienQuan: List<BaiVietLienQuan>?
)

