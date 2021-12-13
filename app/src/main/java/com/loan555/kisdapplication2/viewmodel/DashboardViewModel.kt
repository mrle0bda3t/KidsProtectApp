package com.loan555.kisdapplication2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.loan555.kisdapplication2.repository.CallApiRepository

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    val callApiRepository = CallApiRepository(application)

    fun getLoaiTin() = callApiRepository.listLoaiThongTinTuyenTruyen

    fun loadLoaiTin() = callApiRepository.getLoaiThongTinTuyenTruyen()
}