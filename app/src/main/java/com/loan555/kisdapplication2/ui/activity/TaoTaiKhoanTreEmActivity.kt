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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.database.AppPreferences
import com.loan555.kisdapplication2.model.TaoTaiKhoanTre
import com.loan555.kisdapplication2.repository.myTag
import com.loan555.kisdapplication2.viewmodel.ApiTaiKhoanViewModel
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.*
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.btnChonAnh
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.btnTaoTaiKhoanTreEm
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.chonNgaySinh
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.imgView
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.mess
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.nhapDiaChi
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.nhapHuyen
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.nhapNgaySinh
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.nhapTen
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.nhapThanhPho
import kotlinx.android.synthetic.main.activity_tao_tai_khoan_tre_em.nhapXa

class TaoTaiKhoanTreEmActivity : AppCompatActivity() {

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
        viewModel = ViewModelProvider(this).get(ApiTaiKhoanViewModel::class.java)
        setContentView(R.layout.activity_tao_tai_khoan_tre_em)

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
                        val tk = TaoTaiKhoanTre(
                            token,
                            nhapTenTaiKhoan.text.toString().trim(),
                            nhapMatKhau.text.toString().trim(),
                            nhapTen.text.toString().trim(),
                            nhapNgaySinh.text.toString(),
                            nhapXa.text.toString().trim(),
                            nhapHuyen.text.toString().trim(),
                            nhapThanhPho.text.toString().trim(),
                            nhapDiaChi.text.toString().trim(),
                            mUri!!,
                            nhapLaiMatKhau.text.toString().trim()
                        )
                        viewModel.taoTaiKhoanTreEm(this, tk)
                    }
                } else (
                        mess.setText("Bạn chưa chọn ảnh")
                        )
            } catch (e: Exception) {
                Log.e(myTag, "error taoTKtre : ${e.message}")
            }
        }
    }

    private fun initController() {
        viewModel.getResultTaoTaiKhoanTreEm().observe(this, {
            if (it != null) {
                mProgress.dismiss()
                when (it.status) {
                    "success" -> {
                        Toast.makeText(this, "Đăng ký tài khoản trẻ thành công", Toast.LENGTH_SHORT)
                            .show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    else -> {
                        mess.text = "${it.status}: ${it.msg}"
                    }
                }
            }
        })
    }

    private fun openGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        resultLauncher.launch(Intent.createChooser(intent, "select picture"))
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