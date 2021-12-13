package com.loan555.kisdapplication2.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.model.view.TaiKhoanPost
import com.loan555.kisdapplication2.viewmodel.ApiTaiKhoanViewModel
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.mess

private const val IDLoaiTaiKhoanNguoiBaoHo = "60cc55d9ffdd4b0015125f8d"

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: ApiTaiKhoanViewModel
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(this).get(ApiTaiKhoanViewModel::class.java)
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Đang kiểm tra ...")

        initController()
        initEvent()
        initData()
    }

    private fun initController() {
        viewModel.getResultTaoTaiKhoan().observe(this, {
            if (it != null) {
                if (it.status != "failed") {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra("mailRegister", tenDangNhap.text.toString().trim())
                    intent.putExtra("passRegister", matKhau.text.toString().trim())
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    mess.text = it.message
                }
            }
            mProgressDialog.dismiss()
        })
    }

    private fun initEvent() {
        dangKy.setOnClickListener {
            val acc = TaiKhoanPost(
                tenDangNhap.text.toString(),
                mail.text.toString(),
                matKhau.text.toString(),
                sdt.text.toString(),
                IDLoaiTaiKhoanNguoiBaoHo,
                nhapLaiPass.text.toString()
            )
            viewModel.taoTaiKhoan(acc)
            mProgressDialog.show()
        }
        btnDangNhap.setOnClickListener {
            finish()
        }
    }

    private fun initData() {

    }

}