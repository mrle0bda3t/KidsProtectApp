package com.loan555.kisdapplication2.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.loan555.kisdapplication2.api.ApiUserConfig
import com.loan555.kisdapplication2.api.Client
import com.loan555.kisdapplication2.model.DanhSachTreResult
import com.loan555.kisdapplication2.model.ResultTaoTaiKhoanTreEm
import com.loan555.kisdapplication2.model.TaoTaiKhoanNguoiLon
import com.loan555.kisdapplication2.model.TaoTaiKhoanTre
import com.loan555.kisdapplication2.model.apiCall.*
import com.loan555.kisdapplication2.model.view.TaiKhoanPost
import com.loan555.kisdapplication2.util.RealPathUtil.getRealPath
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class TaiKhoanRepository {
    val resultCreateAccount: MutableLiveData<ResultCreateAccount> = MutableLiveData()
    val resultXacThuc: MutableLiveData<ResultXacThuc> = MutableLiveData()
    val resultGetAllTreEm: MutableLiveData<DanhSachTreResult> = MutableLiveData()
    val resultTaoTaiKhoanTreEm: MutableLiveData<ResultTaoTaiKhoanTreEm> = MutableLiveData()
    val resultUpdateInfo: MutableLiveData<ResultUpdate> = MutableLiveData()
    val resultError: MutableLiveData<String> = MutableLiveData()

    private val client: Client = ApiUserConfig.retrofit.create(Client::class.java)

    fun createAccount(account: TaiKhoanPost) {
        val call = client.creatAccount(
            account.tenTaiKhoan,
            account.matKhau,
            account.email,
            account.sdt,
            account.loaiTaiKhoan,
            account.nhapLaiMatKhau
        )
        call.enqueue(object : Callback<ResultCreateAccount> {
            override fun onResponse(
                call: Call<ResultCreateAccount>?,
                response: Response<ResultCreateAccount>?
            ) {
                when (response?.code()) {
                    200 -> {
                        if (response?.body() != null) {
                            resultCreateAccount.value = response.body()
                        }
                    }
                    400 -> {
                        val gson = Gson()
                        val errorBody: ErrorBody =
                            gson.fromJson(
                                response.errorBody().string(),
                                ErrorBody::class.java
                            )
                        resultCreateAccount.value =
                            ResultCreateAccount(
                                errorBody.status,
                                "",
                                errorBody.message
                            )
                    }
                }
            }

            override fun onFailure(call: Call<ResultCreateAccount>?, t: Throwable?) {
                Log.e(myTag, "Error call api createAccount : ${t?.message}")
                if (t != null) {
                    resultCreateAccount.value =
                        ResultCreateAccount("failed", "", t.message!!)
                }
            }
        })
    }

    fun xacThuc(xacThuc: PostXacThuc): MutableLiveData<ResultXacThuc> {
        val call = client.xacThuc(xacThuc)
        call.enqueue(object : Callback<ResultXacThuc> {
            override fun onResponse(
                call: Call<ResultXacThuc>?,
                response: Response<ResultXacThuc>?
            ) {
                when (response?.code()) {
                    200 -> {
                        if (response?.body() != null) {
                            resultXacThuc.value = response.body()
                            Log.d(myTag, "Dữ liệu xác thực từ server ${resultXacThuc.value}")
                        }
                    }
                    400 -> {
                        val gson = Gson()
                        val errorBody: ErrorBody =
                            gson.fromJson(
                                response.errorBody().string(),
                                ErrorBody::class.java
                            )
                        resultXacThuc.value =
                            ResultXacThuc(
                                errorBody.status,
                                null,
                                null,
                                null,
                                null
                            )
                    }
                }
            }

            override fun onFailure(call: Call<ResultXacThuc>?, t: Throwable?) {
                Log.e(myTag, "Error call api xacThuc : ${t?.message}")
                resultXacThuc.value = ResultXacThuc("failed", null, null, null, t?.message)
            }
        })
        return resultXacThuc
    }

    fun getDanhSachTreEm(token: String): MutableLiveData<DanhSachTreResult> {
        val call = client.getAllTreEm(token)
        call.enqueue(object : Callback<DanhSachTreResult> {
            override fun onResponse(
                call: Call<DanhSachTreResult>?,
                response: Response<DanhSachTreResult>?
            ) {
                if (response?.body() != null) {
                    resultGetAllTreEm.value = response.body()
                } else {
                    Log.e(myTag, "getDanhSachTreEm error code = ${response?.code()}")
                    resultGetAllTreEm.value = DanhSachTreResult("failed", null)
                }
            }

            override fun onFailure(call: Call<DanhSachTreResult>?, t: Throwable?) {
                Log.e(myTag, "Error call api getDanhSachTreEm : ${t?.message}")
                resultGetAllTreEm.value = DanhSachTreResult("failed", null)
            }
        })
        return resultGetAllTreEm
    }

    fun getResultTaoTaiKhoanTreEm(
        context: Context,
        tk: TaoTaiKhoanTre
    ): MutableLiveData<ResultTaoTaiKhoanTreEm> {
        val tenTK: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.tenTK)
        val matKhau: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.matKhau)
        val ten: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.ten)
        val ngaySinh: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.ngaySinh)
        val xa: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.xa)
        val huyen: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.huyen)
        val diaChi: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.diaChi)
        val tinh: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.tinh)
        val nhapLaiMatKhau: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.nhapLaiMatKhau)

        val strRealPath = getRealPath(context, tk.uri)
        Log.e(myTag, strRealPath)
        val anh = File(strRealPath)
        val requestBody: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), anh)
        val mtp = MultipartBody.Part.createFormData("files", anh.name, requestBody)
        val call = client.taoTaiKhoanTreEm(
            tk.token,
            tenTK,
            matKhau,
            ten,
            ngaySinh,
            xa,
            huyen,
            tinh,
            diaChi,
            mtp,
            nhapLaiMatKhau
        )
        call.enqueue(object : Callback<ResultTaoTaiKhoanTreEm> {
            override fun onResponse(
                call: Call<ResultTaoTaiKhoanTreEm>?,
                response: Response<ResultTaoTaiKhoanTreEm>?
            ) {
                when (response?.code()) {
                    200 -> {
                        if (response.body() != null) {
                            resultTaoTaiKhoanTreEm.value = response.body()
                            Log.e(
                                myTag,
                                "getResultTaoTaiKhoanTreEm success ${resultTaoTaiKhoanTreEm.value}"
                            )
                        }
                    }
                    400 -> {
                        val error = response.errorBody().string()
                        val gson = Gson()
                        val bodyError: ErrorBodyRegisterKids =
                            gson.fromJson(error, ErrorBodyRegisterKids::class.java)
                        resultTaoTaiKhoanTreEm.value =
                            ResultTaoTaiKhoanTreEm(bodyError.status, null, bodyError.msg)
                    }
                }
            }

            override fun onFailure(call: Call<ResultTaoTaiKhoanTreEm>?, t: Throwable?) {
                Log.e(myTag, "Error call api getResultTaoTaiKhoanTreEm : ${t?.message}")
                resultTaoTaiKhoanTreEm.value = ResultTaoTaiKhoanTreEm("failed", null, t?.message)
            }
        })
        return resultTaoTaiKhoanTreEm
    }

    fun capNhatThongTinNguoiBaoHo(
        context: Context,
        tk: TaoTaiKhoanNguoiLon
    ): MutableLiveData<ResultUpdate> {
        val ten: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.ten)
        val ngaySinh: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.ngaySinh)
        val xa: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.xa)
        val huyen: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.huyen)
        val diaChi: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.diaChiCuThe)
        val tinh: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.tinh_ThanhPho)
        val cmnd: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), tk.cmnd)

        val strRealPath = getRealPath(context, tk.avt)
        Log.e(myTag, strRealPath)
        val anh = File(strRealPath)
        val requestBody: RequestBody =
            RequestBody.create("multipart/from-data".toMediaTypeOrNull(), anh)
        val mtp = MultipartBody.Part.createFormData("files", anh.name, requestBody)
        val call = client.capNhatThongTinNguoiBaoHo(
            tk.token,
            ten,
            ngaySinh,
            mtp,
            cmnd,
            xa,
            huyen,
            tinh,
            diaChi
        )
        call.enqueue(object : Callback<ResultUpdate> {
            override fun onResponse(
                call: Call<ResultUpdate>?,
                response: Response<ResultUpdate>?
            ) {
                when (response?.code()) {
                    200 -> {
                        if (response.body() != null) {
                            resultUpdateInfo.value = response.body()
                            Log.e(
                                myTag,
                                "capNhatThongTinNguoiBaoHo success ${resultUpdateInfo.value}"
                            )
                        }
                    }
                    400 -> {
                        val error = response.errorBody().string()
                        val gson = Gson()
                        val bodyError: ErrorBodyRegisterKids =
                            gson.fromJson(error, ErrorBodyRegisterKids::class.java)
                        resultError.value = bodyError.msg
                    }
                }
            }

            override fun onFailure(call: Call<ResultUpdate>?, t: Throwable?) {
                Log.e(myTag, "Error call api capNhatThongTinNguoiBaoHo : ${t?.message}")
                resultError.value = t?.message
            }
        })
        return resultUpdateInfo
    }
}