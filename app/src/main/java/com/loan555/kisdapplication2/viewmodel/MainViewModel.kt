package com.loan555.kisdapplication2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.loan555.kisdapplication2.database.NotificationEntity
import com.loan555.kisdapplication2.database.Repository
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import com.loan555.kisdapplication2.database.KidsEntity
import com.loan555.kisdapplication2.model.DanhSachTreEm
import com.loan555.kisdapplication2.model.view.Detail
import com.loan555.kisdapplication2.repository.CallApiRepository
import kotlinx.coroutines.Dispatchers

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext = application.applicationContext
    private val repository = Repository(application)

    fun insertNotification(notificationEntity: NotificationEntity) = viewModelScope.launch {
        repository.insert(notificationEntity)
    }

    fun deleteNotification(id: Long) = viewModelScope.launch {
        repository.deleteNotification(id)
    }

    fun markRead(id: Long) = viewModelScope.launch {
        repository.updateNotification(id)
    }

    fun getAllNotification() = repository.getAllNotification()
    val detail: MutableLiveData<Detail> = MutableLiveData()
    val callApiRepository = CallApiRepository(application)

    fun getListThongTinTuyenTruyen() = callApiRepository.listThongTinTuyenTruyen
    fun loadListThongTin(limit: Int) = callApiRepository.getListThongTinTuyenTruyen(limit)

    fun loadDetail(id: String) = callApiRepository.getDetail(id)

    //Lấy danh sách trẻ em
    fun getListKids() = repository.getAllKids()

    //Thêm danh sách trẻ em
    fun insertKids(danhSachTre: List<DanhSachTreEm>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteListKids()
            danhSachTre.forEach {
                repository.insertKids(KidsEntity(it.id, it.ten, it.ngaySinh, it.anhChanDung))
            }
            //Log.d("aaa","ds tre = ${getListKids()}")
        }
    }
}