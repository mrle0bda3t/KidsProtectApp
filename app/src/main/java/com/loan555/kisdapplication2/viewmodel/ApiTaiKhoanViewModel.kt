package com.loan555.kisdapplication2.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.loan555.kisdapplication2.model.TaoTaiKhoanNguoiLon
import com.loan555.kisdapplication2.model.TaoTaiKhoanTre
import com.loan555.kisdapplication2.model.apiCall.PostXacThuc
import com.loan555.kisdapplication2.model.view.TaiKhoanPost
import com.loan555.kisdapplication2.repository.TaiKhoanRepository
import com.loan555.kisdapplication2.ui.activity.DataLoginCustom

class ApiTaiKhoanViewModel(application: Application) : AndroidViewModel(application) {

    val accLogin: MutableLiveData<DataLoginCustom> = MutableLiveData()

    private val callApiRepository = TaiKhoanRepository()

    fun getResultTaoTaiKhoan() = callApiRepository.resultCreateAccount
    fun getResultXacThuc() = callApiRepository.resultXacThuc

    fun taoTaiKhoan(acc: TaiKhoanPost) = callApiRepository.createAccount(acc)

    fun xacThuc(xt: PostXacThuc) = callApiRepository.xacThuc(xt)

    fun loadDanhSachTreEm(token: String) = callApiRepository.getDanhSachTreEm(token)

    fun getResultTaoTaiKhoanTreEm() = callApiRepository.resultTaoTaiKhoanTreEm

    fun taoTaiKhoanTreEm(context: Context, taoTaiKhoanTre: TaoTaiKhoanTre) =
        callApiRepository.getResultTaoTaiKhoanTreEm(context, taoTaiKhoanTre)

    fun getError() = callApiRepository.resultError

    fun getResultUpdateInfo() = callApiRepository.resultUpdateInfo

    fun updateInfo(context: Context, taoTK: TaoTaiKhoanNguoiLon) =
        callApiRepository.capNhatThongTinNguoiBaoHo(context, taoTK)
}