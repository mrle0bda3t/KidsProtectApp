package com.loan555.kisdapplication2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.loan555.kisdapplication2.repository.CallApiRepository

class PagerLoaiTinViewModel(application: Application) : AndroidViewModel(application) {
    val callApiRepository = CallApiRepository(application)

    fun getListThongTinTuyenTruyenTheoTheLoai() = callApiRepository.listThongTinTuyenTruyen

    fun loadListThongTinTheoTheLoai(theLoai: String, limit: Int) =
        callApiRepository.getThongTinTuyenTruyenTheoTheLoai(theLoai, limit)

}