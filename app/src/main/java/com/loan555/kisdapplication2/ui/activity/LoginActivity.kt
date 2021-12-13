package com.loan555.kisdapplication2.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.database.AppPreferences
import com.loan555.kisdapplication2.databinding.LoginFragmentBinding
import com.loan555.kisdapplication2.model.apiCall.PostXacThuc
import com.loan555.kisdapplication2.repository.myTag
import com.loan555.kisdapplication2.viewmodel.ApiTaiKhoanViewModel
import java.io.Serializable

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: ApiTaiKhoanViewModel
    private lateinit var binding: LoginFragmentBinding
    private var pass = ""
    private lateinit var mProgressDialog: ProgressDialog

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                fillData(data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_fragment)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(ApiTaiKhoanViewModel::class.java)
        binding.viewModel = viewModel

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Đang kiểm tra ...")

        initController()
        initEvent()
    }

    private fun initController() {
        viewModel.getResultXacThuc().observe(this, {
            if (it != null) {
                when (it.status) {
                    "success" -> {
                        // --------------------- nen hoi nguoi dung co muon luu tai khoan ddang nhaap ko
                        AppPreferences.init(this)
                        Log.d(myTag, "Đăng nhập thành công $it")
                        binding.mess.setTextColor(Color.parseColor("#03DAC5"))
                        var dataCustom: DataLoginCustom? = null
                        if (it.loaiTaiKhoan?.id == "60cc55e7ffdd4b0015125f8e") {
                            // tre em
                            AppPreferences.idLoaiNguoiDung = it.loaiTaiKhoan?.id
                            AppPreferences.userName = it.data?.tenTaiKhoan
                            AppPreferences.password = pass
                            AppPreferences.token = it.token
                            AppPreferences.idNguoiDung = it.data?.thongTinCaNhan?.id
                            Log.d(myTag, "dang nhap thanh cong : tre em")
                            dataCustom = DataLoginCustom(
                                it.data!!.id,
                                it.data.tenTaiKhoan,
                                it.loaiTaiKhoan.id,
                                it.token,
                                it.data.thongTinCaNhan?.anhChanDung
                            )
                        } else if (it.data?.loaiTaiKhoan?.id == "60cc55d9ffdd4b0015125f8d") {
                            // nguoi lon
                            AppPreferences.idNguoiDung = it.data?.thongTinCaNhan?.id
                            AppPreferences.idLoaiNguoiDung = it.data?.loaiTaiKhoan?.id
                            AppPreferences.userName = it.data?.tenTaiKhoan
                            AppPreferences.password = pass
                            AppPreferences.token = it.token
                            Log.d(myTag, String.format("Đăng nhập thành công với tài khoản người lớn: %s", AppPreferences.userName))
                            dataCustom = DataLoginCustom(
                                it.data.id,
                                it.data.tenTaiKhoan,
                                it.data.loaiTaiKhoan.id,
                                it.token,
                                it.data.thongTinCaNhan?.anhChanDung
                            )
                        }
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        if (dataCustom != null) {
                            setDataLogin(dataCustom)
                            this.finish()
                        }
                    }
                    else -> {
                        Log.d(myTag, "Đăng nhập thất bại : ${it.status}")
                        binding.mess.text = "${it.status} : ${it.message}"
                    }
                }
                mProgressDialog.dismiss()
            }
        })
    }

    private fun initEvent() {
        binding.btLogin.setOnClickListener {
            pass = binding.editTextTextPassword.text.toString()
            val postXacThuc =
                PostXacThuc(
                    binding.editTextTextEmailAddress.text.toString(),
                    binding.editTextTextPassword.text.toString()
                )
            viewModel.xacThuc(postXacThuc)
            mProgressDialog.show()
        }
        binding.btSignUp.setOnClickListener {
            resultLauncher.launch(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setDataLogin(data: DataLoginCustom) {
        Log.d(myTag, "setData $data")
        val myIntent = Intent()
        val bundle = Bundle()
        bundle.putSerializable("dataLogin", data)
        myIntent.putExtra("bundleLogin", bundle)
        setResult(Activity.RESULT_OK, myIntent)
    }

    private fun fillData(data: Intent?) {
        if (data != null) {
            val mail = data.getStringExtra("mailRegister")
            val pass = data.getStringExtra("passRegister")
            binding.editTextTextEmailAddress.setText(mail)
            binding.editTextTextPassword.setText(pass)
        }
    }
}

data class DataLoginCustom(
    var id: String,
    var tenTaiKhoan: String,
    var loaiTaiKhoanID: String,
    var token: String?,
    var avata: String?
) : Serializable