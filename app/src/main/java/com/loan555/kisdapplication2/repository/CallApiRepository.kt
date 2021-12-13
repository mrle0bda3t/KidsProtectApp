package com.loan555.kisdapplication2.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.loan555.kisdapplication2.api.ApiConfig
import com.loan555.kisdapplication2.api.Client
import com.loan555.kisdapplication2.model.apiCall.ResultCallDetail
import com.loan555.kisdapplication2.model.apiCall.ResultCallDinhDangThongTinTuyenTruyen
import com.loan555.kisdapplication2.model.apiCall.ResultCallListThongTinTuyenTruyen
import com.loan555.kisdapplication2.model.apiCall.ResultCallLoaiThongTinTuyenTruyen
import com.loan555.kisdapplication2.model.view.DinhDangThongTinTuyenTruyen
import com.loan555.kisdapplication2.model.view.LoaiThongTinTuyenTruyen
import com.loan555.kisdapplication2.model.view.ThongTinTuyenTruyen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val myTag = "aaa"

class CallApiRepository(application: Application) {

    val listLoaiThongTinTuyenTruyen: MutableLiveData<List<LoaiThongTinTuyenTruyen>> =
        MutableLiveData(listOf())
    val listDinhDangThongTinTuyenTruyen: MutableLiveData<List<DinhDangThongTinTuyenTruyen>> =
        MutableLiveData(listOf())
    val listThongTinTuyenTruyen: MutableLiveData<List<ThongTinTuyenTruyen>> =
        MutableLiveData(listOf())
    val detail: MutableLiveData<ResultCallDetail> = MutableLiveData()
    private val client: Client = ApiConfig.retrofit.create(Client::class.java)

    fun getLoaiThongTinTuyenTruyen(): MutableLiveData<List<LoaiThongTinTuyenTruyen>> {
        val call = client.getLoaiThongTinTuyenTruyen()
        call.enqueue(object : Callback<ResultCallLoaiThongTinTuyenTruyen> {
            override fun onResponse(
                call: Call<ResultCallLoaiThongTinTuyenTruyen>?,
                response: Response<ResultCallLoaiThongTinTuyenTruyen>?
            ) {
                if (response?.body() != null) {
                    if (response.body().status == 200) {
                        val data: List<LoaiThongTinTuyenTruyen> = response.body().data
                        listLoaiThongTinTuyenTruyen.value = data
                    }
                } else Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${response?.code()}"
                )
            }

            override fun onFailure(call: Call<ResultCallLoaiThongTinTuyenTruyen>?, t: Throwable?) {
                Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${t?.message}"
                )
            }
        })
        return listLoaiThongTinTuyenTruyen
    }

    fun getDinhDangThongTinTuyenTruyen(): MutableLiveData<List<DinhDangThongTinTuyenTruyen>> {
        val call = client.getDinhDangThongTinTuyenTruyen()
        call.enqueue(object : Callback<ResultCallDinhDangThongTinTuyenTruyen> {
            override fun onResponse(
                call: Call<ResultCallDinhDangThongTinTuyenTruyen>?,
                response: Response<ResultCallDinhDangThongTinTuyenTruyen>?
            ) {
                if (response?.body() != null) {
                    if (response.body().status == 200) {
                        val data: List<DinhDangThongTinTuyenTruyen> = response.body().data
                        listDinhDangThongTinTuyenTruyen.value = data
                    }
                } else Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${response?.code()}"
                )
            }

            override fun onFailure(
                call: Call<ResultCallDinhDangThongTinTuyenTruyen>?,
                t: Throwable?
            ) {
                Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${t?.message}"
                )
            }

        })
        return listDinhDangThongTinTuyenTruyen
    }

    fun getListThongTinTuyenTruyen(limit: Int): MutableLiveData<List<ThongTinTuyenTruyen>> {
        val call = client.getListThongTinTuyenTruyen(limit)
        call.enqueue(object : Callback<ResultCallListThongTinTuyenTruyen> {
            override fun onResponse(
                call: Call<ResultCallListThongTinTuyenTruyen>?,
                response: Response<ResultCallListThongTinTuyenTruyen>?
            ) {
                if (response?.body() != null) {
                    if (response.body().status == "success") {
                        val data: List<ThongTinTuyenTruyen> = response.body().thongTinTuyenTruyen
                        listThongTinTuyenTruyen.value = data
                    }
                } else Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${response?.code()}"
                )
            }

            override fun onFailure(
                call: Call<ResultCallListThongTinTuyenTruyen>?,
                t: Throwable?
            ) {
                Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${t?.message}"
                )
            }

        })
        return listThongTinTuyenTruyen
    }

    fun getThongTinTuyenTruyenTheoTheLoai(
        theLoai: String,
        limit: Int
    ): MutableLiveData<List<ThongTinTuyenTruyen>> {
        val call = client.getThongTinTuyenTruyenTheoTheLoai(theLoai, limit)
        call.enqueue(object : Callback<ResultCallListThongTinTuyenTruyen> {
            override fun onResponse(
                call: Call<ResultCallListThongTinTuyenTruyen>?,
                response: Response<ResultCallListThongTinTuyenTruyen>?
            ) {
                if (response?.body() != null) {
                    if (response.body().status == "success") {
                        val data: List<ThongTinTuyenTruyen> = response.body().thongTinTuyenTruyen
                        listThongTinTuyenTruyen.value = data
                    }
                } else Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${response?.code()}"
                )
            }

            override fun onFailure(
                call: Call<ResultCallListThongTinTuyenTruyen>?,
                t: Throwable?
            ) {
                Log.e(
                    myTag,
                    "call api getLoaiThongTinTuyenTruyen error code = ${t?.message}"
                )
            }

        })
        return listThongTinTuyenTruyen
    }

    fun getDetail(id: String): MutableLiveData<ResultCallDetail> {
        val call = client.getDetail(id)
        call.enqueue(object : Callback<ResultCallDetail> {
            override fun onResponse(
                call: Call<ResultCallDetail>?,
                response: Response<ResultCallDetail>?
            ) {
                if (response?.body() != null) {
                    if (response.body().status == "success") {
                        detail.value = response.body()
                    }
                }
            }

            override fun onFailure(
                call: Call<ResultCallDetail>?,
                t: Throwable?
            ) {
                Log.d("aaa","error call detail: ${t?.message}")
            }

        })
        return detail
    }

}