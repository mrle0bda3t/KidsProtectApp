package com.loan555.kisdapplication2.model.view

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DinhDangThongTinTuyenTruyen(
    @SerializedName("_id")
    @ColumnInfo(name = "id_danhdang") val id: String,
    @ColumnInfo(name = "tenDinhDang") val tenDinhDang: String,
    @SerializedName("__v")
    @ColumnInfo(name = "v_dinhdang") val v: Long
):Serializable
