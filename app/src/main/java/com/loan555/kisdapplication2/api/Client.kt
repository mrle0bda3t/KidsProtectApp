package com.loan555.kisdapplication2.api

import com.loan555.kisdapplication2.model.DanhSachTreResult
import com.loan555.kisdapplication2.model.ResultTaoTaiKhoanTreEm
import com.loan555.kisdapplication2.model.apiCall.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Client {
    @GET("loaithongtintuyentruyen")
    fun getLoaiThongTinTuyenTruyen(): Call<ResultCallLoaiThongTinTuyenTruyen>

    @GET("dinhdangthongtintuyentruyen")
    fun getDinhDangThongTinTuyenTruyen(): Call<ResultCallDinhDangThongTinTuyenTruyen>

    //http://34.134.234.78/thongtintuyentruyen/?limit=20
    @GET("thongtintuyentruyen/")
    fun getListThongTinTuyenTruyen(
        @Query("limit") limit: Int
    ): Call<ResultCallListThongTinTuyenTruyen>

    @GET("thongtintuyentruyen")
    fun getThongTinTuyenTruyenTheoTheLoai(
        @Query("theloai") theloai: String,
        @Query("limit") limit: Int
    ): Call<ResultCallListThongTinTuyenTruyen>

    @GET("thongtintuyentruyen/{id}")
    fun getDetail(
        @Path("id") id: String
    ): Call<ResultCallDetail>

    @FormUrlEncoded
    @POST("taikhoan/dangki")
    fun creatAccount(
        @Field("tenTaiKhoan") tenTaiKhoan: String,
        @Field("matKhau") matKhau: String,
        @Field("email") email: String,
        @Field("sdt") sdt: String,
        @Field("loaiTaiKhoan") loaiTaiKhoan: String,
        @Field("nhapLaiMatKhau") nhapLaiMatKhau: String
    ): Call<ResultCreateAccount>

    @POST("taikhoan/xacthuc")
    fun xacThuc(@Body xacThuc: PostXacThuc): Call<ResultXacThuc>

    @Multipart
    @POST("taikhoan/taotaikhoantreem")
    fun taoTaiKhoanTreEm(
        @Header("x-auth-token") token: String,
        @Part("tenTaiKhoan") tenTaiKhoan: RequestBody,
        @Part("matKhau") matKhau: RequestBody,
        @Part("ten") ten: RequestBody,
        @Part("ngaySinh") ngaySinh: RequestBody,
        @Part("xa") xa: RequestBody,
        @Part("huyen") huyen: RequestBody,
        @Part("tinh_ThanhPho") tinh_ThanhPho: RequestBody,
        @Part("diaChiCuThe") diaChiCuThe: RequestBody,
        @Part avt: MultipartBody.Part,
        @Part("nhapLaiMatKhau") nhapLaiMatKhau: RequestBody
    ): Call<ResultTaoTaiKhoanTreEm>

    //http://34.134.234.78/taikhoan/xacthuc
    @GET("treem/thongtin")
    fun getAllTreEm(@Header("x-auth-token") token: String): Call<DanhSachTreResult>

    @Multipart
    @POST("taikhoan/themthongtin")
    fun capNhatThongTinNguoiBaoHo(
        @Header("x-auth-token") token: String,
        @Part("ten") ten: RequestBody,
        @Part("ngaySinh") ngaySinh: RequestBody,
        @Part avt: MultipartBody.Part,
        @Part("cmnd") cmnd: RequestBody,
        @Part("xa") xa: RequestBody,
        @Part("huyen") huyen: RequestBody,
        @Part("tinh_ThanhPho") tinh_ThanhPho: RequestBody,
        @Part("diaChiCuThe") diaChiCuThe: RequestBody
    ): Call<ResultUpdate>
}