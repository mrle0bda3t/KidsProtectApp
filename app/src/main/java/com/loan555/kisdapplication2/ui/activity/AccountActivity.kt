package com.loan555.kisdapplication2.ui.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.database.AppPreferences
import com.loan555.kisdapplication2.model.TaoTaiKhoanNguoiLon
import com.loan555.kisdapplication2.repository.myTag
import com.loan555.kisdapplication2.viewmodel.ApiTaiKhoanViewModel
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    private lateinit var viewModel: ApiTaiKhoanViewModel
    private var mUri: Uri? = null
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                doPlay(data)
            }
        }
    private val mProgress: ProgressDialog by lazy {
        ProgressDialog(this).apply {
            this.setMessage("Đang xác thực")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        viewModel = ApiTaiKhoanViewModel(application)
        initController()
        initEvent()
    }

    private fun initEvent() {
        btnChonAnh.setOnClickListener {
            openGallery()
        }
        val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            nhapNgaySinh.setText("${i2 + 1}/$i3/$i")
        }
        val datePicker = DatePickerDialog(
            this,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            dateSetListener,
            2000,
            0,
            1
        )
        chonNgaySinh.setOnClickListener {
            datePicker.show()
        }
        btnTaoTaiKhoanTreEm.setOnClickListener {
            try {
                if (mUri != null) {
                    mProgress.show()
                    //callApiTaoTaiKHoanTreEm()
                    AppPreferences.init(this)
                    val token = AppPreferences.token
                    if (token != null) {
                        val tk = TaoTaiKhoanNguoiLon(
                            token,
                            nhapTen.text.toString().trim(),
                            nhapNgaySinh.text.toString(),
                            mUri!!,
                            cmnd.text.toString().trim(),
                            nhapXa.text.toString().trim(),
                            nhapHuyen.text.toString().trim(),
                            nhapThanhPho.text.toString().trim(),
                            nhapDiaChi.text.toString().trim()
                        )
                        viewModel.updateInfo(this, tk)
                    }
                } else (
                        mess.setText("Bạn chưa chọn ảnh")
                        )
            } catch (e: Exception) {
                Log.e(myTag, "error updateInfo : ${e.message}")
            }
        }
    }

    private fun initController() {
        viewModel.getResultUpdateInfo().observe(this, {
            mProgress.dismiss()
            if (it != null) {
                when (it.status) {
                    "success" -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent()
                        intent.putExtra("avatar", it.msg?.anhChanDung)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        })
        viewModel.getError().observe(this, {
            mProgress.dismiss()
            if (it != null) {
                mess.text = it
            } else {
                mess.text = ""
            }
        })
    }

    private fun openGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        resultLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"))
    }

    private fun doPlay(data: Intent?) {
        Log.d(myTag, "get bitmap")
        if (data != null) {
            val uri = data.data// uri load tu gallery
            Glide.with(this).load(uri).into(imgView)
            mUri = uri
        }
    }
}