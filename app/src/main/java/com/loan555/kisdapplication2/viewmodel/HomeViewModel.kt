package com.loan555.kisdapplication2.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.loan555.kisdapplication2.model.view.Detail
import com.loan555.kisdapplication2.repository.CallApiRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    val detail: MutableLiveData<Detail> = MutableLiveData()
    val callApiRepository = CallApiRepository(application)

    fun getListThongTinTuyenTruyen() = callApiRepository.listThongTinTuyenTruyen
    fun loadListThongTin(limit: Int) = callApiRepository.getListThongTinTuyenTruyen(limit)

    fun loadDetail(id: String) = callApiRepository.getDetail(id)
}