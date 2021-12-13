package com.loan555.kisdapplication2.ui.fragment

import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.loan555.kidsapplication.model.view.ThongTinThanhVienAdapter
import com.loan555.kisdapplication2.JavaCode.*
import com.loan555.kisdapplication2.JavaCode.Model.History
import com.loan555.kisdapplication2.JavaCode.Model.Kid
import com.loan555.kisdapplication2.Kidsapp.ActivityMain
import com.loan555.kisdapplication2.Kidsapp.Util
import com.loan555.kisdapplication2.Kidsapp.connection.ServiceSinkhole
import com.loan555.kisdapplication2.Kidsapp.constants.EventTypesKid
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.constant.EventTypes
import com.loan555.kisdapplication2.database.AppPreferences
import com.loan555.kisdapplication2.database.NotificationEntity
import com.loan555.kisdapplication2.model.apiCall.PostXacThuc
import com.loan555.kisdapplication2.repository.myTag
import com.loan555.kisdapplication2.ui.activity.*
import com.loan555.kisdapplication2.ui.adapter.AccountAdapter
import com.loan555.kisdapplication2.viewmodel.ApiTaiKhoanViewModel
import com.loan555.kisdapplication2.viewmodel.MainViewModel
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.historyitems.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class NotificationsFragment : Fragment() {
    //region INITVARIABLES
    private val TAG = "KA.NotificationFragment"
    private var isLogout = false;
    private var dialogVpn: androidx.appcompat.app.AlertDialog? = null
    private var idtre = ""
    private val REQUEST_VPN = 1
    private val REQUEST_LOGCAT = 3
    private var maDongBo = ""
    private var maDongBobl = ""
    var dialog: ProgressDialog? = null

    private var viewModel: ApiTaiKhoanViewModel? = null
    private var mainViewModel: MainViewModel? = null

    fun getisLogout(): Boolean? {
        return isLogout
    }

    fun setisLogout(isLogout: Boolean) {
        this.isLogout = isLogout
    }

    fun getMaDongBobl(): String? {
        return maDongBobl
    }

    fun setMaDongBobl(maDongBobl: String) {
        this.maDongBobl = maDongBobl
    }

    fun getMaDongBo(): String? {
        return maDongBo
    }

    fun setMaDongBo(maDongBo: String) {
        this.maDongBo = maDongBo
    }

    //    private val mainViewModel: MainViewModel by lazy {
//        MainViewModel(application)
//    }
// endregion
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                doPlay(data)
            }
        }
    private var resultLauncherRegisterKids =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                loadLaiDangSach()
            }
        }

    private var resultLauncherUpdate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) loadInfo(it.data)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.let { ApiTaiKhoanViewModel(it.application) }
        mainViewModel = activity?.let { MainViewModel(it.application) }
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initController()
        initEvent()
        initData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initController() {
        viewModel?.accLogin?.observe(viewLifecycleOwner, { dataLoginCustom ->
            if (dataLoginCustom != null) {
                itemLogined.visibility = View.VISIBLE
                tenDangNha.text = dataLoginCustom.tenTaiKhoan
                itemUser.visibility = View.GONE
                if (dataLoginCustom.avata != null)
                    Glide.with(this.requireContext()).load(dataLoginCustom.avata)
                        .into(avatar)

                recycleView.layoutManager =
                    LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

                // Nếu loại tài khoản là tài khoản người lớn
                if (dataLoginCustom.loaiTaiKhoanID == "60cc55d9ffdd4b0015125f8d") {
                    AppPreferences.init(this.requireContext())
                    val tokent = AppPreferences.token
                    val tokenbaoho = tokent
                    val options = IO.Options.builder()
                        .setAuth(java.util.Collections.singletonMap("token", tokenbaoho))
                        .build()
                    val socket =
                        IO.socket(
                            java.net.URI.create("http://34.134.234.78"),
                            options
                        )
                    val dh = DatabaseHelper.getInstance(context)
                    dialog = ProgressDialog.show(
                        context,
                        "Đồng bộ",
                        "Đang đồng bộ, vui lòng đợi ...",
                        true
                    )
                    socket.connect()
                    socket.on(EventTypes.connect, ketnoithanhcong)
                    socket.on(EventTypes.connect_error, ketnoiloi)
                    socket.on(EventTypes.disconnect, huyketnoi)
                    socket.on(EventTypes.thongBao, thongbao)
                    socket.on(
                        EventTypes.dongBoThongTinTruyCapThanhCong,
                        dongBoThongTinTruyCapThanhCong
                    )
                    socket.on(EventTypes.dongBoThongTinTruyCapLoi, dongBoThongTinTruyCapLoi)
                    socket.on(EventTypes.thongTinTruyCap, thongTinTruyCap)
                    socket.on(EventTypes.dongBoLichSuLoi, dongBoLichSuLoi)
                    socket.on(EventTypes.xacThucDongBoLichSuLoi, xacThucDongBoLichSuLoi)
                    socket.on(EventTypes.xacThucDongBoLichSuThanhCong, xacThucDongBoLichSuThanhCong)
                    socket.on(EventTypes.yeuCauCapNhatLichSu, yeuCauCapNhatLichSu)
                    socket.on(EventTypes.dongBoBlackListloi, dongBoBlackListLoi)
                    socket.on(EventTypes.thongTinCapNhatBlackList, thongTinCapNhatBlackList)
                    socket.on(EventTypes.xacThucThanhCong, xacThucThanhCong)
                    socket.on(EventTypes.capNhatBlackListLoi, capNhatBlackListLoi)
                    socket.on(EventTypes.capNhatBlackListThanhCong, capNhatBlackListThanhCong)
                    socket.on(EventTypes.yeuCauCapNhatBlackList, yeuCauCapNhatBlackList)
                    socket.on(EventTypes.capNhatThongTinApDungLoi, capNhatThongTinApDungLoi)
                    socket.on(
                        EventTypes.capNhatThongTinApDungThanhCong,
                        capNhatThongTinApDungThanhCong
                    )
                    socket.on(EventTypes.thongTinTruyCapKetThuc, thongTinTruyCapKetThuc)
                    socket.on(EventTypes.themAppLoi, themAppLoi)
                    socket.on(EventTypes.themAppThanhCong, themAppThanhCong)
                    socket.on(EventTypes.xacThucThemAppChanThanhCong, xacThucThemAppChanThanhCong)
                    // Thông báo lại cho người bảo hộ nếu trẻ em truy cập domain không phù hợp
                    socket.on(EventTypes.DomainKhongPhuHop, DomainKhongPhuHop)
                    // Lưu lại socket
                    SocketHandler.setSocket(socket)


                    btnQUanLyBlackList.visibility = View.VISIBLE
                    btnUpdate.visibility = View.VISIBLE
                    layoutSw.visibility = View.VISIBLE
                    // On/off switch
                    laoutListChil.visibility = View.VISIBLE
                    layoutSw.visibility = View.GONE

                    dataLoginCustom.token?.let { it1 ->
                        Log.e(TAG, "token = $it1")
                        viewModel!!.loadDanhSachTreEm(it1).observe(viewLifecycleOwner, {
                            mainViewModel?.insertKids(it.danhSachTreEm!!)
                            Log.d(TAG, "Load view tài khoản người bảo hộ")
                            val adapter: AccountAdapter =
                                AccountAdapter(this.requireContext(), onItemClick).apply {
                                    val newList: MutableList<ThongTinThanhVienAdapter> =
                                        mutableListOf()
                                    newList += ThongTinThanhVienAdapter(
                                        null,
                                        "Tạo tài khoản trẻ em",
                                        ""
                                    )
                                    it.danhSachTreEm?.forEach {
                                        newList += ThongTinThanhVienAdapter(
                                            it.anhChanDung,
                                            it.ten,
                                            it.id
                                        )
                                        Log.d(TAG, "idtre : " + it.id);
                                        if (!dh.CheckIsDataAlreadyInDBorNot(
                                                "kid",
                                                "idkid",
                                                it.id
                                            )
                                        ) {
                                            dh.updateKids(it.id, it.ten,it.anhChanDung);
                                        }
                                    }
                                    Log.d(TAG, "Danh sách trẻ em : $newList")
                                    setList(newList)
                                }
                            recycleView.adapter = adapter
                        })
                    }
                } else {
                    // Tài khoản trẻ em
                    // Ghi đè datahelper nên không cần set lại
                    var dhKid =
                        com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
                    if (getisLogout() == true) {
                        dhKid =
                            com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstanceNew(this.context)
                    }
                    Log.d(TAG, "Load view cho tài khoản trẻ em")
                    SetBlockTime()
                    AppPreferences.init(this.requireContext())
                    btnQUanLyBlackList.visibility = View.GONE
                    btnUpdate.visibility = View.GONE
                    laoutListChil.visibility = View.GONE
                    layoutSw.visibility = View.VISIBLE
                    AppPreferences.init(this.requireContext())
                    val tokentreem = AppPreferences.token
                    val options = IO.Options.builder()
                        .setAuth(Collections.singletonMap("token", tokentreem))
                        .build()

                    val socket = IO.socket(
                        URI.create("http://34.134.234.78"),
                        options
                    )
                    socket.connect()
                    socket.on(EventTypesKid.connect, KetNoiThanhCongTreEm)
                    socket.on(EventTypesKid.connect_error, KetNoiLoiTreEm)
                    socket.on(EventTypesKid.disconnect, HuyKetNoiTreEm)
                    socket.on(EventTypesKid.thongBao, ThongBaoTreEm)
                    socket.on(EventTypesKid.taoThongTinTruyCapLoi, TaoThongTinTruyCapLoi)
                    socket.on(
                        EventTypesKid.taoThongTinTruyCapThanhCong,
                        taothongtintruycapthanhcong2
                    )

                    socket.on(EventTypesKid.xacThucThanhCong, xacThucThanhCong2)
                    socket.on(
                        EventTypesKid.xacThucDongBoBlackListThanhCong,
                        xacThucDongBoBlackListThanhCong2
                    )
                    socket.on(EventTypesKid.xacThucDongBoBlackListLoi, xacThucDongBoBlackListLoi2)
                    socket.on(EventTypesKid.thongTinCapNhatBlackList, thongTinCapNhatBlackList2)
                    socket.on(
                        EventTypesKid.yeuCauCapNhatThongTinApDung,
                        yeuCauCapNhatThongTinApDung2
                    )
                    socket.on(EventTypesKid.yeuCauCapNhatAppChan, yeuCauCapNhatAppChan2)
                    socket.on(EventTypesKid.yeuCauCapNhatBlackList, yeuCauCapNhatBlackList2)
                    com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.setSocket(socket)

                    // Lấy thông tin url trong blackList ???
                    if (dhKid != null) {
                        val dataurl = dhKid.url
                        if (dataurl.count == 0) {
                            Log.d(TAG, "Không có url nào")
                        } else {
                            val urln = dataurl.getColumnIndex("url")
                            while (dataurl.moveToNext()) {
                                val url = dataurl.getString(urln)
                                Log.d(TAG, String.format("bảng url : %s", url))
                            }
                        }
                    }
                    val running = true
                    var idtre = AppPreferences.idNguoiDung
                    val prefs =
                        PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
                    val enabled = prefs.getBoolean("enabled", false)
                    if (enabled == true) {
                        ServiceSinkhole.reload("changed notify", (activity as MainActivity), false)
                        ServiceSinkhole.reload("changed filter", (activity as MainActivity), false)
                        prefs.edit().putBoolean("filter", true).apply()
                        prefs.edit().putBoolean("log_app", true).apply()
                        prefs.edit().putBoolean("notify_access", true).apply()
                        checkExtras((activity as MainActivity).getIntent())
                    }


                    // On/off switch
                    swEnabled.setChecked(enabled)
                    swEnabled.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                        prefs.edit().putBoolean("enabled", isChecked).apply()
                        if (isChecked) {
                            try {
                                val alwaysOn = Settings.Secure.getString(
                                    (activity as MainActivity).getContentResolver(),
                                    "always_on_vpn_app"
                                )
                                Log.i(TAG, "Always-on=$alwaysOn")
                                if (!TextUtils.isEmpty(alwaysOn)) if ((activity as MainActivity).getPackageName() == alwaysOn) {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                                        prefs.getBoolean("filter", false)
                                    ) {
                                        val lockdown = Settings.Secure.getInt(
                                            (activity as MainActivity).getContentResolver(),
                                            "always_on_vpn_lockdown",
                                            0
                                        )
                                        Log.i(TAG, "Lockdown=$lockdown")
                                        if (lockdown != 0) {
                                            swEnabled.setChecked(false)
                                            Toast.makeText(
                                                (activity as MainActivity),
                                                R.string.msg_always_on_lockdown,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@OnCheckedChangeListener
                                        }
                                    }
                                } else {
                                    swEnabled.setChecked(false)
                                    Toast.makeText(
                                        (activity as MainActivity),
                                        R.string.msg_always_on,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@OnCheckedChangeListener
                                }
                            } catch (ex: Throwable) {
                                Log.e(
                                    TAG, """
                                             $ex
                                             ${Log.getStackTraceString(ex)}
                                             """.trimIndent()
                                )
                            }

                            val filter = prefs.getBoolean("filter", false)
                            if (filter && Util.isPrivateDns((activity as MainActivity))) Toast.makeText(
                                (activity as MainActivity),
                                R.string.msg_private_dns,
                                Toast.LENGTH_LONG
                            ).show()

                            try {
                                val prepare = VpnService.prepare((activity as MainActivity))
                                if (prepare == null) {
                                    Log.i(TAG, "Prepare done")
                                    onActivityResult(
                                        REQUEST_VPN,
                                        Activity.RESULT_OK,
                                        null
                                    )
                                } else {
                                    // Show dialog
                                    val inflater = LayoutInflater.from((activity as MainActivity))
                                    val view = inflater.inflate(R.layout.vpn, null, false)
                                    dialogVpn =
                                        androidx.appcompat.app.AlertDialog.Builder((activity as MainActivity))
                                            .setView(view)
                                            .setCancelable(false)
                                            .setPositiveButton(
                                                android.R.string.yes
                                            ) { dialog, which ->
                                                if (running) {
                                                    Log.i(TAG, "Start intent=$prepare")
                                                    try {
                                                        // com.android.vpndialogs.ConfirmDialog required
                                                        startActivityForResult(
                                                            prepare,
                                                            REQUEST_VPN
                                                        )
                                                    } catch (ex: Throwable) {
                                                        Log.e(
                                                            TAG,
                                                            """
                            $ex
                            ${Log.getStackTraceString(ex)}
                            """.trimIndent()
                                                        )
                                                        onActivityResult(
                                                            REQUEST_VPN,
                                                            Activity.RESULT_CANCELED,
                                                            null
                                                        )
                                                        prefs.edit().putBoolean("enabled", false)
                                                            .apply()
                                                    }
                                                }
                                            }
                                            .setOnDismissListener { dialogVpn = null }
                                            .create()
                                    dialogVpn!!.show()
                                }
                            } catch (ex: Throwable) {
                                // Prepare failed
                                Log.e(
                                    TAG, """
                                         $ex
                                         ${Log.getStackTraceString(ex)}
                                         """.trimIndent()
                                )
                                prefs.edit().putBoolean("enabled", false).apply()
                            }
                        } else ServiceSinkhole.stop("switch off", (activity as MainActivity), false)

                        // On/off switch
                        if (enabled) (activity as MainActivity).checkDoze()
                        ServiceSinkhole.reload("changed notify", (activity as MainActivity), false)
                        ServiceSinkhole.reload("changed filter", (activity as MainActivity), false)
                        prefs.edit().putBoolean("filter", true).apply()
                        prefs.edit().putBoolean("log_app", true).apply()
                        prefs.edit().putBoolean("notify_access", true).apply()
                        checkExtras((activity as MainActivity).getIntent())
                    })
                }
            } else {
                itemUser.visibility = View.VISIBLE
                itemLogined.visibility = View.GONE
            }
        })
    }

    // region INITDATA
    private fun initEvent() {
        btLogin.setOnClickListener {
            startLoginActivity()
        }
        btnUpdate.setOnClickListener {
            btnUpdateClick()
        }
        btnLogout.setOnClickListener {
            showDialog()
        }
        btnQUanLyBlackList.setOnClickListener {
            AppPreferences.init(this.requireContext())
            Log.d(TAG, "idNguoiDung= ${AppPreferences.idNguoiDung}")
            Intent(this.context, ActivityBlacklist::class.java).apply {
//                putExtra("idNguoiDung", AppPreferences.idNguoiDung)
                startActivity(this)
            }
        }
    }

    private fun initData() {
        // ------------------- cho nay de lay data tu sharêrentces
        //------------------ kiem tra dang nhap
        AppPreferences.init(this.requireContext())
        val username = AppPreferences.userName
        val pass = AppPreferences.password
        if (username != "" && pass != "") {
            viewModel?.xacThuc(PostXacThuc(username!!, pass!!))?.observe(viewLifecycleOwner, {
                if (it != null) {
                    var dataCustom: DataLoginCustom? = null
                    if (it.data?.loaiTaiKhoan?.id == "60cc55d9ffdd4b0015125f8d") {
                        // nguoi lon
                        Log.d(myTag, "Đăng nhập với tài khoản người lớn")
                        dataCustom = DataLoginCustom(
                            it.data.id,
                            it.data.tenTaiKhoan,
                            it.data.loaiTaiKhoan.id,
                            it.token,
                            it.data.thongTinCaNhan?.anhChanDung
                        )
                    } else if (it.loaiTaiKhoan?.id == "60cc55e7ffdd4b0015125f8e") {
                        // tre em
                        Log.d(myTag, "Đăng nhập với tài khoản trẻ em")
                        dataCustom = DataLoginCustom(
                            it.data!!.id,
                            it.data.tenTaiKhoan,
                            it.loaiTaiKhoan!!.id,
                            it.token,
                            it.data.thongTinCaNhan?.anhChanDung
                        )
                    }
                    if (dataCustom != null) {
                        viewModel!!.accLogin.value = dataCustom
                        Log.d(myTag, "dataLoginCustom = $dataCustom")
                    }
                }
            })
        }
    }

    private fun doPlay(intent: Intent?) {
        Log.d(myTag, "doPlay ")
        val bundle = intent?.getBundleExtra("bundleLogin")
        if (bundle != null) {
            val loginData = bundle.getSerializable("dataLogin") as DataLoginCustom
            viewModel?.accLogin?.value = loginData
            Log.d(myTag, "doPlay $loginData")
        }
    }

    private fun btnUpdateClick() {
        resultLauncherUpdate.launch(Intent(this.requireContext(), AccountActivity::class.java))
    }

    private fun loadInfo(data: Intent?) {
        val url = data?.getStringExtra("avatar")
        Log.d(myTag, "anh chan dung = $url")
        if (url != null) {
            Glide.with(this.requireContext()).load(url).into(avatar)
        }
    }

    private fun loadLaiDangSach() {
        AppPreferences.init(this.requireContext())
        val tokent = AppPreferences.token
        if (tokent != null) {
            viewModel?.loadDanhSachTreEm(tokent)?.observe(viewLifecycleOwner, {
                val adapter: AccountAdapter =
                    AccountAdapter(this.requireContext(), onItemClick).apply {
                        val newList: MutableList<ThongTinThanhVienAdapter> =
                            mutableListOf()
                        newList += ThongTinThanhVienAdapter(
                            null,
                            "Tạo tài khoản trẻ em",
                            ""
                        )
                        val dh = DatabaseHelper.getInstance(context)
                        it.danhSachTreEm?.forEach {
                            newList += ThongTinThanhVienAdapter(
                                it.anhChanDung,
                                it.ten,
                                it.id
                            )
                        }
                        Log.d(TAG, "Danh sách trẻ em = $newList")
                        setList(newList)
                    }
                recycleView.adapter = adapter
            })
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this.context, LoginActivity::class.java)
        resultLauncher.launch(intent)
    }

    private val onItemClick: (Int, List<ThongTinThanhVienAdapter>) -> Unit = { pos, info ->
        if (pos == 0) {
            Log.d(myTag, " click ${info[0].thongTin}")

            AppPreferences.init(this.requireContext())
            when (AppPreferences.idLoaiNguoiDung) {
                "60cc55d9ffdd4b0015125f8d" -> {
                    // nguoi lon
                    onClickRequestPermistion()
                }
                "60cc55e7ffdd4b0015125f8e" -> {
                    //Tre em
                }
            }
        } else {
            when (AppPreferences.idLoaiNguoiDung) {
                "60cc55d9ffdd4b0015125f8d" -> {
                    // nguoi lon
                    Log.d(myTag, " click ${info[pos].thongTin}")
                    Intent(this.context, ActivityKid::class.java).apply {
                        putExtra("idTre", info[pos].id)
                        putExtra("tkTre", info[pos].thongTin)
                        startActivity(this)
                    }
                }
                "60cc55e7ffdd4b0015125f8e" -> {
                    //Tre em
                }
            }
        }
    }

    private fun onClickRequestPermistion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startActivity(Intent(this.context, TaoTaiKhoanTreEmActivity::class.java))
            return
        } else {
            if (this.context?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                resultLauncherRegisterKids.launch(
                    Intent(
                        this.context,
                        TaoTaiKhoanTreEmActivity::class.java
                    )
                )
            } else {
                val permission = listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                if (activity != null) {
                    ActivityCompat.requestPermissions(
                        this.requireActivity(),
                        permission.toTypedArray(), 1
                    )
                }
            }
        }
    }

    private fun showDialog() {
        val alertDialog = AlertDialog.Builder(this.context)
        alertDialog.apply {
            setMessage("Bạn muốn đăng xuất tài khoản này?")
            setPositiveButton("Có") { _, _ ->
                btnQUanLyBlackList.visibility = View.GONE
                recycleView.adapter =
                    AccountAdapter(this@NotificationsFragment.requireContext(), onItemClick)
                viewModel?.accLogin?.postValue(null)
                AppPreferences.init(this@NotificationsFragment.requireContext())
                AppPreferences.idLoaiNguoiDung = ""
                AppPreferences.userName = ""
                AppPreferences.password = ""
                AppPreferences.token = ""
                (activity as MainActivity).setMaDongBo2("")
                setMaDongBo("")
                setMaDongBobl("")
                setisLogout(true)
                val dh = DatabaseHelper.getInstance(context)
                val dhkid = com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(context)
                dh.ClearDb()
                dhkid.ClearDb()
                val prefs = PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
                prefs.edit().putBoolean("enabled", false).apply()
                dh.deleteDB("ParentsAppProtect", context)
                dhkid.deleteDB("Kidsapp", context)

            }
            setNegativeButton("Không") { _, _ -> }
        }.create().show()
    }

    // endregion
// region SOCKETPARENTS
    private val ketnoithanhcong = Emitter.Listener {
        println("Kết nối thành công")
        var dh = DatabaseHelper.getInstance(context)
        if (getisLogout() == true) {
            dh = DatabaseHelper.getInstanceNew(context)
        }

        // Mã đồng bộ blackList
        val sync = dh.sync
        // Mã đồng bộ blackList
        val blsync = dh.blSync
        // Kiểm tra có tồn tại mã blackList hay không
        if (blsync.count == 0) {
            Log.d(TAG, "Không có mã blackList")
        } else {
            // Tồn tại mã blacklist trong thiết bị
            Log.d(TAG, "Tồn tại mã blackList trong thiết bị");
            val blsyn = blsync.getColumnIndex("idblsync")
            while (blsync.moveToNext()) {
                // Nhận mã đồng bộ blacklis
                val maDongBoBlackList = blsync.getString(blsyn)
                Log.i(
                    TAG,
                    String.format("Mã đồng bộ blackList %s", maDongBoBlackList)
                );
                setMaDongBobl(maDongBoBlackList)
            }
        }

        // Nếu như không tồn tại mã đồng bộ blackList thì tiến hành gửi không có mã để nhận lại mã
        if (getMaDongBobl().equals("")) {
            SocketHandler.getSocket().emit(EventTypes.dongBoBlackList)
        } else {
            // Nếu có mã thì gửi kèm mã đã lưu trước đó
            var datasend = ""
            datasend = "{\"maDongBoBlackList\":\"" + getMaDongBobl() + "\"}"
            Log.i(
                TAG,
                String.format("Thông tin gửi đồng bô blackList %s", datasend)
            )
            var jsonDongBoBlackList: JSONObject? = null
            try {
                jsonDongBoBlackList = JSONObject(datasend)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            SocketHandler.getSocket().emit(EventTypes.dongBoBlackList, jsonDongBoBlackList)
        }
        // Tiến hành đồng bộ lịch sử
        // Nếu không có mã đồng bộ
        if (sync.count == 0) {
            Log.d(TAG, "Không có dữ liệu idsync")
        } else {
            val syn = sync.getColumnIndex("idsync")
            while (sync.moveToNext()) {
                val maDongBoThongTinTruyCap = sync.getString(syn)
                Log.i(
                    TAG,
                    String.format(
                        "Tìm thấy mã đồng bộ thông tin truy cập trong thiết bị %s",
                        maDongBoThongTinTruyCap
                    )
                )
                setMaDongBo(maDongBoThongTinTruyCap)
            }
        }

        // Nếu không có mã đồng bộ gửi thông tin đồng bộ thông tin truy cập mới
        if (getMaDongBo().equals("")) {
            SocketHandler.getSocket().emit(EventTypes.dongBoLichSuTruyCap)
        } else {
            var datasend = ""
            datasend = "{\"maThongTinDongBoLichSu\":\"" + getMaDongBo() + "\"}"
            Log.d(
                TAG,
                String.format(
                    "Thông tin yêu cầu đồng bộ thông tin truy cập %s",
                    datasend
                )
            )
            var jsonThongTinDongBoThongTinTruyCap: JSONObject? = null
            try {
                jsonThongTinDongBoThongTinTruyCap = JSONObject(datasend)
                SocketHandler.getSocket().emit(
                    EventTypes.dongBoLichSuTruyCap,
                    jsonThongTinDongBoThongTinTruyCap
                )
            } catch (e: JSONException) {
                Log.e(
                    TAG,
                    "Có lỗi xảy ra trong quá trình đồng bộ thông tin truy cập"
                )
                e.printStackTrace()
            }
        }
    }
    private val ketnoiloi = Emitter.Listener { println("Kết nối thất bại") }
    private val huyketnoi = Emitter.Listener { println("Đã bị ngắt kết nối") }
    private val thongbao = Emitter.Listener { arg ->
        Log.i(
            TAG,
            String.format("Thông báo %s", arg[0].toString())
        );
    }
    private val dongBoThongTinTruyCapThanhCong =
        Emitter.Listener { args ->
            println("dongbothongtintruycapthanhcong")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }
    private val dongBoThongTinTruyCapLoi =
        Emitter.Listener { args ->
            println("dongbothongtintruycaploi")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }
    private val thongTinTruyCap =
        Emitter.Listener { args ->
            val dh = DatabaseHelper.getInstance(context)
            Log.i(TAG, " Bắt đầu nhận thông tin truy cập ở đây")
            val ob = args[0] as JSONObject
            println(ob.toString())
            val kid = Kid()
            try {
                kid.idKid = ob.getString("maTreEm")
                val history = History()
                if (ob.getJSONArray("thongTinTruyCap").length() >= 1) {
                    for (j in 0 until ob.getJSONArray("thongTinTruyCap").length()) {
                        history.set_id(
                            ob.getJSONArray("thongTinTruyCap").getJSONObject(j).getString("_id")
                        )
                        if (ob.getJSONArray("thongTinTruyCap").getJSONObject(j)
                                .getJSONObject("diaChi").getString("url").contains("::")
                        ) {
                            val diachi = ob.getJSONArray("thongTinTruyCap").getJSONObject(j)
                                .getJSONObject("diaChi").getString("url").split("::").toTypedArray()
                            var app = diachi[0]
                            val url = diachi[1]
                            if (app.compareTo("-1") == 0) {
                                Log.d(TAG, "app = root rồi")
                                app = "root"
                            }
                            if (app.compareTo("root") != 0) {
                                history.setDiaChi(url)
                                history.setTenapp(app)
                                history.setTreEm(
                                    ob.getJSONArray("thongTinTruyCap").getJSONObject(j)
                                        .getString("treEm")
                                )
                                val utcFormat: DateFormat =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                                val date = utcFormat.parse(
                                    ob.getJSONArray("thongTinTruyCap").getJSONObject(j)
                                        .getString("thoiGianYeuCau")
                                )
                                val pstFormat: DateFormat =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                                val dayFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                                pstFormat.timeZone = TimeZone.getTimeZone("PST")
                                history.setThoiGianYeuCau(pstFormat.format(date))
                                history.setTinhTrang(
                                    ob.getJSONArray("thongTinTruyCap").getJSONObject(j)
                                        .getString("tinhTrang")
                                )
                                history.setTimelong(
                                    dateToLong(
                                        pstFormat.format(date),
                                        "yyyy-MM-dd'T'HH:mm:ss.SSS"
                                    )
                                )
                                dh.updateHistory(
                                    history.getDiaChi(),
                                    history.getThoiGianYeuCau(),
                                    kid.idKid,
                                    history.getTinhTrang(),
                                    history.getTenapp(),
                                    history.getTimelong(),
                                    dayFormat.format(date)
                                )
                            }
                            if (app.compareTo("root") != 0) {
                                Log.d(TAG, "app không = root rồi")
                                if (!dh.CheckIsAppAlreadyIDBorNot("app", app, kid.idKid)) {
                                    dh.updateApp(app, kid.idKid, "6:00", "18:00", 0, "")
                                }
                            }

                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    private val dongBoLichSuLoi =
        Emitter.Listener { args ->
            println("Đồng bộ lịch sử lỗi")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }
    private val dongBoLichThanhCong =
        Emitter.Listener { args ->
            println("dongbolichsuthanhcong")
            val ob = args[0] as JSONArray
            val ten = ob.toString()
            println(ten)
        }
    private val themAppLoi =
        Emitter.Listener { args ->
            println("themAppLoi")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }
    private val themAppThanhCong =
        Emitter.Listener { args ->
            println("themAppThanhCong")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
            val dh = DatabaseHelper.getInstance(context)
            val idapp = ob.getJSONObject("data").getJSONObject("app").getString("_id")
            val tenApp = ob.getJSONObject("data").getJSONObject("app").getString("tenApp")
            val idkid = ob.getJSONObject("data").getString("maTreEm")
            try {
                val timeStart = ob.getJSONObject("data").getJSONObject("lichTruyCap")
                    .getString("thoiGianBatDauTrongNgay")
                val timeEnd = ob.getJSONObject("data").getJSONObject("lichTruyCap")
                    .getString("thoiGianKetThucTrongNgay")
                if (Integer.parseInt(timeStart) < 0 && Integer.parseInt(timeEnd) <= 0) {
                    if (dh.CheckIsAppInDB(idkid, tenApp)) {
                        val miStart = -Integer.parseInt(timeStart) % 60
                        var minStart: String = miStart.toString() + ""
                        if (miStart == 0) minStart = "00"
                        else if (miStart < 10) minStart = "0$miStart"
                        val hourStart = -Integer.parseInt(timeStart) / 60
                        val miEnd = -Integer.parseInt(timeEnd) % 60
                        var minEnd: String = miEnd.toString() + ""
                        if (miEnd == 0) minEnd = "00"
                        else if (miEnd < 10) minEnd = "0$miEnd"
                        val hourEnd = -Integer.parseInt(timeEnd) / 60
                        dh.updateIdApp(
                            idkid,
                            tenApp,
                            idapp,
                            hourStart.toString() + ":" + minStart,
                            hourEnd.toString() + ":" + minEnd
                        )
                        dh.updateActivateApp(0, idkid, tenApp)
                    }
                } else {
                    val miStart = Integer.parseInt(timeStart) % 60
                    var minStart: String = miStart.toString() + ""
                    if (miStart == 0) minStart = "00"
                    else if (miStart < 10) minStart = "0$miStart"
                    val hourStart = Integer.parseInt(timeStart) / 60
                    val miEnd = Integer.parseInt(timeEnd) % 60
                    var minEnd: String = miEnd.toString() + ""
                    if (miEnd == 0) minEnd = "00"
                    else if (miEnd < 10) minEnd = "0$miEnd"
                    val hourEnd = Integer.parseInt(timeEnd) / 60
                    dh.updateIdApp(
                        idkid,
                        tenApp,
                        idapp,
                        hourStart.toString() + ":" + minStart,
                        hourEnd.toString() + ":" + minEnd
                    )
                    dh.updateActivateApp(1, idkid, tenApp)
                }

            } catch (e: Exception) {
                Log.e(TAG, ten)
                dh.updateActivateApp(0, idkid, tenApp)
            }
            SocketHandler.getSocket().emit("xacThucThemAppChanThanhCong")
        }
    private val xacThucThemAppChanThanhCong =
        Emitter.Listener { args ->
            println("xacThucThemAppChanThanhCong")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }

    private val DomainKhongPhuHop =
        Emitter.Listener { args ->
            Log.i(
                TAG,
                String.format("Kiểm tra blacklist với AI thành công %s", args[0].toString())
            )
            val dh = DatabaseHelper.getInstance(context)
            val ob = args[0] as JSONObject
            val kid = Kid()
            val idkid = ob.getString("idTreEm")
            val domain = ob.getString("domain")
            kid.setIdKid(idkid)
            val datakid = dh.getKidbyId(kid.idKid)
            if (datakid.count == 0) {
                Log.d(TAG, "Không có kid nào")
            } else {
                val namekidT = datakid.getColumnIndex("namekid")
                val anhChanDungT = datakid.getColumnIndex("anhChanDung")
                while (datakid.moveToNext()) {
                    val namekid = datakid.getString(namekidT)
                    val anhChanDung = datakid.getString(anhChanDungT)
                    Log.d(TAG, String.format("Tên kid: %s", namekid))
                    kid.setNameKid(namekid)
                    kid.setAnhChanDung(anhChanDung)
                }
            }
            (activity as MainActivity).sendNotification(domain, 1)
            val sdf = SimpleDateFormat("hh:mm:ss dd/MM/yyyy")
            val currentDate = sdf.format(Date())
            val insertNotification = mainViewModel?.insertNotification(
                NotificationEntity(
                    "Truy cập vào địa chỉ nhạy cảm ",
                    "Trẻ em " + kid.getNameKid(),
                    currentDate, " truy cập vào trang " + domain,
                    kid.anhChanDung
                )
            )
            Log.i(TAG, String.format("Trẻ em truy cập domain không phù hợp %s", args[0].toString()))
        }

    private val thongTinTruyCapKetThuc = Emitter.Listener {
        println("Thông tin truy cập kết thúc ! Gửi yêu cầu xác nhận yêu cầu xác nhận đồng bộ lịch sử truy cập")
        SocketHandler.getSocket().emit("yeuCauXacNhanDongBoLichSuTruyCap")
    }
    private val xacThucDongBoLichSuLoi =
        Emitter.Listener { args ->
            println("Xác thực đồng bộ lịch sử lỗi")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }
    private val xacThucDongBoLichSuThanhCong =
        Emitter.Listener { args ->
            val dh = DatabaseHelper.getInstance(context)
            println("xacthucdongbolichsuthanhcong")
            val ten = args[0] as String
            println(ten)
            dh.updatesync(ten)
            setMaDongBo(ten)
            dialog!!.dismiss()
        }
    private val yeuCauCapNhatLichSu =
        Emitter.Listener { args ->
            val dh = DatabaseHelper.getInstance(context)
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(String.format("Yêu cầu cập nhật lịch sử %s", ten))
            for (i in 0 until ob.length()) {
                try {
                    val kid = Kid()
                    val history = History()
                    history.set_id(ob.getJSONArray("data").getJSONObject(i).getString("_id"))
                    val diachi = ob.getJSONArray("data").getJSONObject(i).getJSONObject("diaChi")
                        .getString("url").split("::").toTypedArray()
                    var app = diachi[0]
                    val url = diachi[1]
                    if (app.compareTo("-1") == 0) {
                        app = "root"
                    }
                    history.setDiaChi(url)
                    history.setTenapp(app)
                    println(history.getDiaChi())
                    history.setTreEm(ob.getJSONArray("data").getJSONObject(i).getString("treEm"))
                    kid.setIdKid(history.getTreEm())
                    val datakid = dh.getKidbyId(kid.idKid)
                    if (datakid.count == 0) {
                        Log.d(TAG, "Không có kid nào")
                    } else {
                        val namekidT = datakid.getColumnIndex("namekid")
                        val anhChanDungT = datakid.getColumnIndex("anhChanDung")
                        while (datakid.moveToNext()) {
                            val namekid = datakid.getString(namekidT)
                            val anhChanDung = datakid.getString(anhChanDungT)
                            Log.d(TAG, String.format("Tên kid: %s", namekid))
                            kid.setNameKid(namekid)
                            kid.setAnhChanDung(anhChanDung)
                        }
                    }
                    val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val date = utcFormat.parse(
                        ob.getJSONArray("data").getJSONObject(i).getString("thoiGianYeuCau")
                    )
                    val pstFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    pstFormat.timeZone = TimeZone.getTimeZone("PST")
                    history.setThoiGianYeuCau(pstFormat.format(date))
                    val dayFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                    history.setTimelong(
                        dateToLong(
                            pstFormat.format(date),
                            "yyyy-MM-dd'T'HH:mm:ss.SSS"
                        )
                    )
                    history.setTinhTrang(
                        ob.getJSONArray("data").getJSONObject(i).getString("tinhTrang")
                    )
                    dh.updateHistory(
                        history.getDiaChi(),
                        history.getThoiGianYeuCau(),
                        kid.getIdKid(),
                        history.getTinhTrang(),
                        app,
                        history.getTimelong(),
                        dayFormat.format(date)
                    )
                    if (history.getTinhTrang().contains("DaChan")) {
                        (activity as MainActivity).sendNotification(history.getDiaChi(), 0)
                        val sdf = SimpleDateFormat("hh:mm:ss dd/MM/yyyy")
                        val currentDate = sdf.format(Date())
                        val insertNotification = mainViewModel?.insertNotification(
                            NotificationEntity(
                                "Truy cập vào ứng dụng chặn ",
                                "Trẻ em " + kid.getNameKid(),
                                currentDate,
                                " truy cập vào trang " + history.getDiaChi(),
                                kid.anhChanDung
                            )
                        )
                    }
                    if (app.compareTo("root") != 0) {
                        if (!dh.CheckIsAppAlreadyIDBorNot("app", app, kid.idKid)) {

                            dh.updateApp(app, kid.idKid, "6:00", "18:00", 0, "")
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            SocketHandler.getSocket().emit("capNhatLichSuThanhCong")
        }
    private val dongBoBlackListLoi =
        Emitter.Listener { args ->
            println("dongBoBlackListLoi")
            val ten = args[0] as String
            println(ten)
        }
    private val thongTinCapNhatBlackList =
        Emitter.Listener { args ->
            val dh = DatabaseHelper.getInstance(context)
            val ob = args[0] as JSONObject
            println(String.format("Thông tin cập nhật blackList %s", ob.toString()))
            // Cập nhật thông tin blackList
            try {
                val thongTinBlackList = ob.getJSONArray("thongTinBlackList")
                val n = thongTinBlackList.length()
                for (i in 0 until n) {
                    val nguoidung =
                        thongTinBlackList.getJSONObject(i).getString("nguoiDung")
                    val idbl = thongTinBlackList.getJSONObject(i)
                        .getString("danhSach")
                    // Kiểm tra thông tin người dùng có đúng ở trong thiết bị hay không?
                    if (nguoidung.contains(AppPreferences.idNguoiDung.toString())) {
                        val loaicapnhat = thongTinBlackList.getJSONObject(i)
                            .getString("loaiCapNhat")
                        if (loaicapnhat.contains("Them")) {
                            val namebl = thongTinBlackList.getJSONObject(i)
                                .getString("tenDanhSach")
                            // Thêm blacklist vào database
                            dh.updateBlackList(namebl, "", idbl)
                            val datakid = dh.getKid()
                            // Thêm blacklist thì auto áp dụng blacklist đó cho tất cả các bé
                            if (datakid.count == 0) {
                                Log.d(TAG, "Không có kid nào")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format("Cập nhật áp dụng cho idkid: %s", idkid)
                                    )
                                    dh.updateApplybl(idbl, idkid, "")
                                }
                            }
                            Log.i(TAG, String.format("Thêm BlackList %s ", namebl))
                        } else if (loaicapnhat.contains("Xoa")) {

                            // Nếu id của BlackList tồn tại
                            dh.deleteBlacklistbyId(idbl)
                            val datakid = dh.getKid()
                            // Xoá blacklist thì auto bỏ áp dụng blacklist đó cho tất cả các bé
                            if (datakid.count == 0) {
                                Log.d(TAG, "Không có kid nào")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format("Cập nhật xoá áp dụng cho idkid: %s", idkid)
                                    )
                                    dh.deleteApplybl(idkid, idbl);
                                }
                            }
                            Log.i(TAG, String.format("Xóa BlackList %s ", idbl))
                        }
                    } else {
                        val loaiNguoiDung = thongTinBlackList.getJSONObject(i)
                            .getString("loaiNguoiDung")
                        if (loaiNguoiDung.equals("NguoiQuanTri")) {
                            val loaicapnhat = thongTinBlackList.getJSONObject(i)
                                .getString("loaiCapNhat")
                            if (loaicapnhat.contains("Them")) {
                                val namebl = thongTinBlackList.getJSONObject(i)
                                    .getString("tenDanhSach")
                                // Thêm blacklist vào database
                                dh.updateBlackList(namebl, "", idbl)
                                Log.i(TAG, String.format("Thêm BlackList %s ", namebl))
                            } else if (loaicapnhat.contains("Xoa")) {

                                // Nếu id của BlackList tồn tại
                                dh.deleteBlacklistbyId(idbl)
                                Log.i(TAG, String.format("Xóa BlackList %s ", idbl))
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Có lỗi xảy ra khi đồng bộ blackList")
                e.printStackTrace()
            }

            // Cập nhật thông tin chặn
            try {
                val n = ob.getJSONArray("thongTinChan").length()
                for (i in 0 until n) {
                    val loaicapnhat =
                        ob.getJSONArray("thongTinChan").getJSONObject(i).getString("loaiCapNhat")
                    if (loaicapnhat.contains("Them")) {
                        val url = ob.getJSONArray("thongTinChan").getJSONObject(i)
                            .getJSONObject("thongTinChan").getString("thongTin")
                        val idchan =
                            ob.getJSONArray("thongTinChan").getJSONObject(i).getString("_id")
                        val idbl =
                            ob.getJSONArray("thongTinChan").getJSONObject(i).getString("danhSach")
                        val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val date = utcFormat.parse(
                            ob.getJSONArray("thongTinChan").getJSONObject(i).getString("thoiGian")
                        )
                        val pstFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                        pstFormat.timeZone = TimeZone.getTimeZone("PST")
                        dh.updateUrl(url, pstFormat.format(date), idchan, idbl)
                    } else if (loaicapnhat.contains("Xoa")) {
                        val idbl =
                            ob.getJSONArray("thongTinChan").getJSONObject(i).getString("danhSach")
                        dh.deleteBlacklistbyId(idbl)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            // Thông tin áp dụng blackList
            try {
                val x = ob.getJSONArray("thongTinApDungBlackList").length();

                if (x != 0) {
                    val n = ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0).length()
                    for (i in 0 until n) {
                        Log.i(TAG,"Thông tin cập nhật BL người lớn : "+ ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
                            .getJSONObject(i).toString())
                        val loaicapnhat =
                            ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
                                .getJSONObject(i)
                                .getString("loaiApDung")
                        if (loaicapnhat.compareTo("ApDung") == 0) {
                            val idbl = ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
                                .getJSONObject(i).getString("danhSach")
                            val idkid = ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
                                .getJSONObject(i).getString("treEm")
                            val utcFormat: DateFormat =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val date = utcFormat.parse(
                                ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
                                    .getJSONObject(i).getString("thoiGianApDung")
                            )
                            val pstFormat: DateFormat =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                            pstFormat.timeZone = TimeZone.getTimeZone("PST")
                            if (!dh.CheckIsBlAlreadyInApplyblorNot(
                                    "applybl",
                                    idbl,
                                    idkid
                                )
                            ) {
                                dh.updateApplybl(idbl, idkid, pstFormat.format(date))
                            }
                        } else if (loaicapnhat.compareTo("BoApDung") == 0) {
                            val idbl = ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
                                .getJSONObject(i).getString("danhSach")
                            val idkid = ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
                                .getJSONObject(i).getString("treEm")
                            dh.deleteApplybl(idkid, idbl)
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            // Thông tin cập nhật lịch truy cập
            try {
                val thongTinCapNhatLichTruyCap = ob.getJSONArray("thongTinCapNhatLichTruyCap")
                Log.i(
                    TAG,
                    String.format(
                        "Thông tin cập nhật lịch truy cập %s",
                        thongTinCapNhatLichTruyCap.toString()
                    )
                )
                val n = thongTinCapNhatLichTruyCap.length()
                for (i in 0 until n) {
                    val loaicapnhat =
                        thongTinCapNhatLichTruyCap.getJSONObject(i).getString("loaiCapNhat")
                    if (loaicapnhat.contains("Them")) {
                        val tenApp = thongTinCapNhatLichTruyCap.getJSONObject(i)
                            .getJSONObject("App").getString("tenApp")
                        val idapp =
                            thongTinCapNhatLichTruyCap.getJSONObject(i).getJSONObject("LichTruyCap")
                                .getString("app")
                        Log.d(
                            TAG,"lịch truy cập người lớn : " + thongTinCapNhatLichTruyCap.getJSONObject(i)
                                .getJSONObject("LichTruyCap").toString()
                        )
                        val idkid =
                            thongTinCapNhatLichTruyCap.getJSONObject(i)
                                .getJSONObject("LichTruyCap").getString("treEm")
                        try {
                            val timeStart =
                                thongTinCapNhatLichTruyCap.getJSONObject(i)
                                    .getJSONObject("LichTruyCap").getInt("thoiGianBatDauTrongNgay")
                                    .toString()
                            val timeEnd = thongTinCapNhatLichTruyCap.getJSONObject(i)
                                .getJSONObject("LichTruyCap").getInt("thoiGianKetThucTrongNgay")
                                .toString()
//                        val timeEnd = thongTinCapNhatLichTruyCap.getJSONObject(i).getJSONObject("LichTruyCap").getString("thoiGianKetThucTrongNgay")

                            if (!dh.CheckIsAppInDB(idkid, tenApp)) {
                                if (Integer.parseInt(timeStart) < 0 && Integer.parseInt(timeEnd) <= 0) {
                                    val miStart = -Integer.parseInt(timeStart) % 60
                                    var minStart: String = miStart.toString() + ""
                                    if (miStart == 0) minStart = "00"
                                    else if (miStart < 10) minStart = "0$miStart"
                                    val hourStart = -Integer.parseInt(timeStart) / 60
                                    val miEnd = -Integer.parseInt(timeEnd) % 60
                                    var minEnd: String = miEnd.toString() + ""
                                    if (miEnd == 0) minEnd = "00"
                                    else if (miEnd < 10) minEnd = "0$miEnd"
                                    val hourEnd = -Integer.parseInt(timeEnd) / 60
                                    dh.updateApp(
                                        tenApp,
                                        idkid,
                                        hourStart.toString() + ":" + minStart,
                                        hourEnd.toString() + ":" + minEnd,
                                        0,
                                        idapp
                                    )
                                } else {
                                    val miStart = Integer.parseInt(timeStart) % 60
                                    var minStart: String = miStart.toString() + ""
                                    if (miStart == 0) minStart = "00"
                                    else if (miStart < 10) minStart = "0$miStart"
                                    val hourStart = Integer.parseInt(timeStart) / 60
                                    val miEnd = Integer.parseInt(timeEnd) % 60
                                    var minEnd: String = miEnd.toString() + ""
                                    if (miEnd == 0) minEnd = "00"
                                    else if (miEnd < 10) minEnd = "0$miEnd"
                                    val hourEnd = Integer.parseInt(timeEnd) / 60
                                    dh.updateApp(
                                        tenApp,
                                        idkid,
                                        hourStart.toString() + ":" + minStart,
                                        hourEnd.toString() + ":" + minEnd,
                                        1,
                                        idapp
                                    )
                                }
                            } else {
                                if (Integer.parseInt(timeStart) < 0 && Integer.parseInt(timeEnd) <= 0) {
                                    val miStart = -Integer.parseInt(timeStart) % 60
                                    var minStart: String = miStart.toString() + ""
                                    if (miStart == 0) minStart = "00"
                                    else if (miStart < 10) minStart = "0$miStart"
                                    val hourStart = -Integer.parseInt(timeStart) / 60
                                    val miEnd = -Integer.parseInt(timeEnd) % 60
                                    var minEnd: String = miEnd.toString() + ""
                                    if (miEnd == 0) minEnd = "00"
                                    else if (miEnd < 10) minEnd = "0$miEnd"
                                    val hourEnd = -Integer.parseInt(timeEnd) / 60
                                    dh.updateTimeApp(
                                        idkid,
                                        tenApp,
                                        hourStart.toString() + ":" + minStart,
                                        hourEnd.toString() + ":" + minEnd,
                                        0
                                    )
                                } else {
                                    val miStart = Integer.parseInt(timeStart) % 60
                                    var minStart: String = miStart.toString() + ""
                                    if (miStart == 0) minStart = "00"
                                    else if (miStart < 10) minStart = "0$miStart"
                                    val hourStart = Integer.parseInt(timeStart) / 60
                                    val miEnd = Integer.parseInt(timeEnd) % 60
                                    var minEnd: String = miEnd.toString() + ""
                                    if (miEnd == 0) minEnd = "00"
                                    else if (miEnd < 10) minEnd = "0$miEnd"
                                    val hourEnd = Integer.parseInt(timeEnd) / 60
                                    dh.updateTimeApp(
                                        idkid,
                                        tenApp,
                                        hourStart.toString() + ":" + minStart,
                                        hourEnd.toString() + ":" + minEnd,
                                        1
                                    )
                                }
                            }
                        } catch (error: Exception) {
                            Log.e(TAG, "Lỗi gửi về lịch truy cập mà không có giờ")
                            dh.updateActivateApp(0, idkid, tenApp)
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            SocketHandler.getSocket().emit("yeuCauXacThucDongBoBlackList")
        }

    private val xacThucThanhCong =
        Emitter.Listener { args ->
            val dh = DatabaseHelper.getInstance(context)
            Log.i(TAG, "Xác thực blackList thành công")
            val maDongBoBlackList = args[0] as String
            Log.i(
                TAG,
                String.format(
                    "Nhận mã đồng bộ blackList %s! Lưu lại vào db",
                    maDongBoBlackList
                )
            )
            // Lưu mã đồng bộ blackList vào db
            dh.updateblacklistsync(maDongBoBlackList)
            // Set mã đồng bộ vào mã sử dụng hiện tại
            setMaDongBobl(maDongBoBlackList)
        }
    private val capNhatBlackListLoi =
        Emitter.Listener { args ->
            println("capNhatBlackListLoi")
            val ob = args[0] as JSONObject
            val msg = ob.getString("msg")
            Log.e(TAG, msg)
            // Update in Ui Thread
            ActivityBlacklist.MakeToastOutSide(msg)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private val capNhatBlackListThanhCong =
        Emitter.Listener { args ->
            Log.i(
                TAG,
                String.format(
                    "Thông tin cập nhật blackList thành công %s",
                    args[0].toString()
                )
            )
            val dh = DatabaseHelper.getInstance(context)
            val ob = args[0] as JSONObject
            val loaiCapNhat: String
            val loaiThongTin: String
            try {
                loaiThongTin = ob.getJSONObject("thongTinCapNhat").getString("loaiThongTin")
                // Nếu loại thông tin là blackList
                if (loaiThongTin.contains("BlackList")) {
                    loaiCapNhat =
                        ob.getJSONObject("thongTinCapNhat").getString("loaiCapNhat")
                    // Nếu loại cập nhật là Thêm
                    if (loaiCapNhat.contains("Them")) {
                        // Id blackList
                        val idbl =
                            ob.getJSONObject("thongTinCapNhat").getJSONObject("danhSach")
                                .getString("_id")

                        // Tên blackList
                        val namebl =
                            ob.getJSONObject("thongTinCapNhat").getJSONObject("danhSach")
                                .getString("tenDanhSach")

                        // Kiểm tra xem có trong db hay chưa
                        val existedNameBLackList =
                            dh.CheckIsDataAlreadyInDBorNot("blacklist", "namebl", namebl)
                        // Nếu tồn tại thì cập nhật id của blackList đã có
                        if (existedNameBLackList) {
                            Log.i(
                                TAG,
                                "Tên blackList đã có trong blackList ! Tiến hành cập nhật"
                            )
                            // Cập nhật id của blackList
                            dh.updateidbl(idbl, namebl)
                            val datakid = dh.getKid()
                            // Thêm blacklist thì auto áp dụng blacklist đó cho tất cả các bé
                            if (datakid.count == 0) {
                                Log.d(TAG, "Không có kid nào")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format(
                                            "Cập nhật áp dụng cho idkid: %s",
                                            idkid
                                        )
                                    )
                                    dh.updateApplybl(idbl, idkid, "")
                                }
                            }

                            // Load lại Activity BlackList
                            Log.i(TAG, "Load lại blackList activity from outside")

                            // Cập nhật danh sách blacklist ở giao diện
                            ActivityBlacklist.loadBlacklistsOutSide()
                        } else {
                            // Nếu không tồn tại thì tạo mới với thông tin được trả về
                            Log.i(
                                TAG,
                                String.format(
                                    "Tên blackList chưa có trong blackList ! Tiến hành thêm blacklist %s ",
                                    namebl
                                )
                            )
                            // Nếu không có thì tạo mới blackList với thông tin nhận được
                            dh.updateBlackList(namebl, "", idbl)
                            val datakid = dh.getKid()
                            // Thêm blacklist thì auto áp dụng blacklist đó cho tất cả các bé
                            if (datakid.count == 0) {
                                Log.d(TAG, "Không có kid nào")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format(
                                            "Cập nhật áp dụng cho idkid: %s",
                                            idkid
                                        )
                                    )
                                    dh.updateApplybl(idbl, idkid, "")
                                }
                            }
                        }
                    } else {
                        Log.i(TAG, "Tiến hành xóa blackList")
                        // Nếu không thì xóa blackList
                        val idbl = ob.getJSONObject("thongTinCapNhat").getString("danhSach")
                        dh.deleteBlacklistbyId(idbl)
                        val datakid = dh.getKid()
                        // Xoá blacklist thì auto bỏ áp dụng blacklist đó cho tất cả các bé
                        if (datakid.count == 0) {
                            Log.d(TAG, "Không có kid nào")
                        } else {
                            val id = datakid.getColumnIndex("idkid")
                            while (datakid.moveToNext()) {
                                val idkid = datakid.getString(id)
                                Log.d(
                                    TAG,
                                    String.format(
                                        "Cập nhật xoá áp dụng cho idkid: %s",
                                        idkid
                                    )
                                )
                                dh.deleteApplybl(idkid, idbl);
                            }
                        }
                    }
                }
                // Nếu là thông tin chặn url
                else if (loaiThongTin.contains("ThongTinChan")) {
                    loaiCapNhat =
                        ob.getJSONObject("thongTinCapNhat").getString("loaiCapNhat")
                    if (loaiCapNhat.contains("Them")) {
                        val idbl = ob.getJSONObject("thongTinCapNhat").getString("danhSach")
                        val idchan =
                            ob.getJSONObject("thongTinCapNhat")
                                .getJSONObject("thongTinChan")
                                .getString("_id")
                        val url = ob.getJSONObject("thongTinCapNhat")
                            .getJSONObject("thongTinChan")
                            .getString("thongTin")
                        if (!dh.CheckIsUrlAlreadyInUrlorNot("url", idbl, url)) {
                            val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                            val now = LocalDateTime.now()
                            dh.updateUrl(url, dtf.format(now), idbl, idchan)
                        } else {
                            dh.updateUrlByidbl(url, idbl, idchan)
                        }
                    } else {
                        val idchan =
                            ob.getJSONObject("thongTinCapNhat").getString("thongTinChan")
                        dh.deleteUrl(idchan)
                    }
                    // Reload Url
                    ActivityBlockurl.loadUrlOutSide()
                } else if (loaiThongTin.contains("thongTinCapNhatLichTruyCap")) {
                    val ttcn = ob.getJSONObject("thongTinCapNhat")
                    val ten = ttcn.toString()
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Có lỗi xảy ra");
                e.printStackTrace()
            }
        }

    private val yeuCauCapNhatBlackList =
        Emitter.Listener { args ->
            val ten = args[0] as String
            println(String.format("Yêu cầu cập nhật blackList %s", ten))
            SocketHandler.getSocket().emit("capNhatBlackListThanhCong")
        }

    private val capNhatThongTinApDungLoi =
        Emitter.Listener { args ->
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(String.format("Cập nhật thông tin áp dụng lỗi %s", ten))
        }

    private val capNhatThongTinApDungThanhCong =
        Emitter.Listener { args ->
            val dh = DatabaseHelper.getInstance(context)
            println("capNhatThongTinApDungThanhCong")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }

    private fun dateToLong(date: String?, fomat: String?): Long {
        var milliseconds: Long = -1
        val f = SimpleDateFormat(fomat)
        f.timeZone = TimeZone.getDefault()
        try {
            val d = f.parse(date)
            milliseconds = d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return milliseconds
    }

    private fun checkExtras(intent: Intent) {
        // Approve request
        if (intent.hasExtra(ActivityMain.EXTRA_APPROVE)) {
            Log.i(TAG, "Requesting VPN approval")
            swEnabled.toggle()
        }
        if (intent.hasExtra(ActivityMain.EXTRA_LOGCAT)) {
            Log.i(TAG, "Requesting logcat")
            val logcat = (activity as MainActivity).getIntentLogcat()
            if (logcat.resolveActivity((activity as MainActivity).packageManager) != null) startActivityForResult(
                logcat,
                REQUEST_LOGCAT
            )
        }
    }

    // endregion
//region SOCKETKIDS
    private val KetNoiThanhCongTreEm =
        Emitter.Listener {
            println("Kết nối thành công loại tài khoản trẻ em");
            var dhKid =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            if (getisLogout() == true) {
                dhKid =
                    com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstanceNew(this.context)
            }
            // Tiến hành đồng bộ blackList
            Log.d(TAG, "Tiến hành đồng bộ blackList")
            val blsync = dhKid.sync
            if (blsync.count == 0) {
                Log.d(TAG, "Không có dữ liệu blackList")
            } else {
                val blsyn = blsync.getColumnIndex("idsync")
                while (blsync.moveToNext()) {
                    (activity as MainActivity).setMaDongBo2(blsync.getString(blsyn))
                }
            }
            // Nếu như không có thông tin đồng bộ blackList thì tiến hành gửi yêu cầu đồng bộ mới
            if ((activity as MainActivity).getMaDongBo2().equals("")) {
                Log.d(TAG, "Tiến hành đồng bộ blackList")
                com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                    .emit(EventTypesKid.dongBoBlackList)
            } else {
                // Nếu như có tồn tại thông tin đồng bộ từ trước thì tiến hành gửi với mã cũ
                var datasend = ""
                datasend =
                    "{\"maDongBoBlackList\":\"" + (activity as MainActivity).getMaDongBo2() + "\"}"
                Log.d(
                    TAG,
                    String.format("Thông tin yêu cầu đồng bộ blackList %s", datasend)
                )
                var jsonThongTinDongBoBlackList: JSONObject? = null
                try {
                    jsonThongTinDongBoBlackList = JSONObject(datasend)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                    .emit(EventTypesKid.dongBoBlackList, jsonThongTinDongBoBlackList)
            }
        }
    private val KetNoiLoiTreEm =
        Emitter.Listener { println("Kết nối thất bại loại tài khoản trẻ em") }
    private val HuyKetNoiTreEm =
        Emitter.Listener { println("Hủy kết nối tài khoản trẻ em") }
    private val ThongBaoTreEm = Emitter.Listener { args ->
        Log.i(
            TAG,
            String.format("Thông báo tài khoản trẻ em %s", args[0].toString())
        )
    }
    private val TaoThongTinTruyCapLoi =
        Emitter.Listener { args ->
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(String.format("Tạo thông tin truy cập lỗi %s", ten))
        }
    private val taothongtintruycapthanhcong2 =
        Emitter.Listener { args -> println("Tạo thông tin truy cập thành công " + args[0].toString()) }

    // Cập nhật blacklist
    private val yeuCauCapNhatBlackList2 =
        Emitter.Listener { args ->
            try {
                val dh =
                    com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
                Log.i(TAG, "Yêu cầu cập nhật blackList")

                // Data yêu cầu cập nhật blacklist
                val ob = args[0] as JSONObject
                // In ra màn hình
                Log.i(
                    TAG,
                    String.format("Nhận yêu cầu cập nhật blacklist %s", ob.toString())
                )

                // Loại cập nhật
                val loaiCapNhat = ob.getString("loaiCapNhat")
                // Loại thông tin
                val loaiThongTin = ob.getString("loaiThongTin")

                // Kiểm tra loại cập nhật
                //
                if (loaiCapNhat.equals("Xoa")) {
                    // Mã danh sách chặn được yêu cầu cập nhật
                    val maDanhSachYeuCauCapNhat = ob.getString("danhSach")
                    // Đây là xoá thông tin chặn
                    if (loaiThongTin.equals("ThongTinChan")) {
                        val thongTinChanYeuCauXoa =
                            ob.getJSONObject("thongTinChan").getString("thongTin")
                        Log.i(
                            TAG,
                            String.format(
                                "Yêu cầu cập nhật blacklist xóa thông tin chặn %s",
                                thongTinChanYeuCauXoa
                            )
                        )
                        dh.deleteUrl(thongTinChanYeuCauXoa)
                        Log.i(TAG, "Xóa thông tin chặn thành công")
                    } else {
                        if (loaiThongTin.equals("BlackList")) {
                            // Tiến hành xóa blacklist với mã đã nhận
                            Log.i(
                                TAG,
                                String.format(
                                    "Nhận yêu cầu xóa blacklist với mã %s",
                                    maDanhSachYeuCauCapNhat
                                )
                            )
                            // xoá black list nên xoá luôn cả url của blacklist đó
                            dh.deleteBlacklist(maDanhSachYeuCauCapNhat)
                            Log.i(
                                TAG,
                                String.format(
                                    "Xóa thành công blacklist %s",
                                    maDanhSachYeuCauCapNhat
                                )
                            )
                            dh.deleteUrlbyIdBl(maDanhSachYeuCauCapNhat)
                        }
                    }
                }
                // Loại cập nhật Thêm
                else {
                    if (loaiCapNhat.equals("Them")) {
                        // Mã danh sách yêu cầu cập nhật
                        val maDanhSachYeuCauCapNhat = ob.getString("danhSach")
                        // Kiểm tra loại thông tin cập nhật
                        if (loaiThongTin.equals("ThongTinChan")) {
                            // Thông tin chặn yêu cầu cập nhật
                            val thongTinChan =
                                ob.getJSONObject("thongTinChan").getString("thongTin")

                            // Thêm thông tin chặn
                            dh.updateUrl(thongTinChan, maDanhSachYeuCauCapNhat, 1)

                            // Cập nhật thành công
                            Log.d(TAG, "Cập nhật url thành công")
                        } else {
                            if (loaiThongTin.equals("BlackList")) {
                                val maBlackList =
                                    ob.getJSONObject("danhSach").getString("_id")

                                // Tiến hành cập nhật thêm blacklist
                                dh.updateBlackList(maBlackList)
                                Log.i(
                                    TAG,
                                    String.format(
                                        "Cập nhật thêm blacklist %s thành công",
                                        maBlackList
                                    )
                                )
                            }
                        }
                    }
                }
                // Reload lại service
                ServiceSinkhole.stop("switch off", (activity as MainActivity), false)
                ServiceSinkhole.reload("changed notify", (activity as MainActivity), false)
                ServiceSinkhole.reload("changed filter", (activity as MainActivity), false)
                val prefs =
                    PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
                prefs.edit().putBoolean("filter", true).apply()
                prefs.edit().putBoolean("log_app", true).apply()
                prefs.edit().putBoolean("notify_access", true).apply()
                // Xác nhận với server đã thành công
                Log.i(TAG, "Gửi yêu cầu thành công tới server")
                com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                    .emit(EventTypes.capNhatBlackListThanhCong, getMaDongBo())
            } catch (error: Exception) {
                Log.e(TAG, "Có lỗi xảy ra trong khi cập nhật yêu cầu cập nhật blacklist")
                Log.e(TAG, error.toString())
            }
        }

    // Cập nhật thông tin áp dụng
    private val yeuCauCapNhatThongTinApDung2 =
        Emitter.Listener { args ->
            val dh =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            println("Yêu cầu cập nhật thông tin áp dụng")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
            try {
                val loaiapdung = ob.getString("loaiApDung")
                Log.d(TAG, "Loại áp dụng là : " + loaiapdung)
                if (loaiapdung.compareTo("ApDung") == 0) {
                    val maDanhSachChanTrongThietBi = ob.getString("danhSach")
                    val idkid = ob.getString("treEm")
                    if (idkid.contains(idtre)) {
                        if (dh.CheckIsDataAlreadyInDBorNot(
                                "blacklist",
                                "idbl",
                                maDanhSachChanTrongThietBi
                            )
                        ) {
                            dh.updateApDungBlacklist(maDanhSachChanTrongThietBi, 1)
                            Log.i(
                                TAG,
                                "Đã áp dụng cho danh sách chặn : " + maDanhSachChanTrongThietBi
                            )
                            dh.updateApDungUrl(maDanhSachChanTrongThietBi, 1)
                            var dhKid =
                                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(
                                    this.context
                                )
                            if (dhKid != null) {
                                val dataurl = dhKid.url
                                if (dataurl.count == 0) {
                                    Log.d(TAG, "Không có url nào")
                                } else {
                                    val urln = dataurl.getColumnIndex("url")
                                    val apdungn = dataurl.getColumnIndex("apdung")
                                    while (dataurl.moveToNext()) {
                                        val url = dataurl.getString(urln)
                                        val apdung = dataurl.getString(apdungn)
                                        Log.d(
                                            TAG,
                                            String.format(
                                                "debug bảng url sau khi áp dụng : %s status %s",
                                                url,
                                                apdung
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else if (loaiapdung.compareTo("BoApDung") == 0) {
                    val maDanhSachChanTrongThietBi = ob.getString("danhSach")
                    dh.updateApDungBlacklist(maDanhSachChanTrongThietBi, 0)
                    Log.i(
                        TAG,
                        "Đã update áp dụng blacklist thành 0 của mã : " + maDanhSachChanTrongThietBi
                    )
                    dh.updateApDungUrl(maDanhSachChanTrongThietBi, 0)
                    Log.i(
                        TAG,
                        "Đã bỏ áp dụng cho danh sách chặn : " + maDanhSachChanTrongThietBi
                    )
                    var dhKid =
                        com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
                    if (dhKid != null) {
                        val dataurl = dhKid.url
                        if (dataurl.count == 0) {
                            Log.d(TAG, "Không có url nào")
                        } else {
                            val urln = dataurl.getColumnIndex("url")
                            val apdungn = dataurl.getColumnIndex("apdung")
                            while (dataurl.moveToNext()) {
                                val url = dataurl.getString(urln)
                                val apdung = dataurl.getString(apdungn)
                                Log.d(
                                    TAG,
                                    String.format(
                                        "debug bảng url sau khi bỏ áp dụng : %s status %s",
                                        url,
                                        apdung
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                .emit(EventTypes.capNhatThongTinApDungThanhCong)
        }
    private val yeuCauCapNhatAppChan2 =
        Emitter.Listener { args ->
            val dh =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            println("yeuCauCapNhatAppChan")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
            val loaiCapNhat = ob.getString("loaiCapNhat")
            val lichTruyCap = ob.getJSONObject("lichTruyCap")
            val tenApp = ob.getJSONObject("app").getString("tenApp")
            var timeStart = ""
            var timeEnd = ""
            try {
                timeStart =
                    lichTruyCap.getString("thoiGianBatDauTrongNgay")
                timeEnd = lichTruyCap.getString("thoiGianKetThucTrongNgay")
            } catch (error: Exception) {
                Log.e(TAG, error.toString());
                timeStart = ""
                timeEnd = ""
            }
            if (timeStart.compareTo("") == 0 && timeEnd.compareTo("") == 0) {
                dh.updateBlockInApp(tenApp,0)
                Log.i(
                    TAG,
                    "Vừa xoá app chặn theo yêu cầu : " + tenApp
                )
            } else if (loaiCapNhat.compareTo("Them") == 0) {
                if (Integer.parseInt(timeStart) > 0 && Integer.parseInt(timeEnd) > 0) {
                    val miStart = Integer.parseInt(timeStart) % 60
                    val hourStart = Integer.parseInt(timeStart) / 60
                    val miEnd = Integer.parseInt(timeEnd) % 60
                    val hourEnd = Integer.parseInt(timeEnd) / 60
                    var miStartStr = ""
                    var miEndStr = ""
                    if (miStart < 10) {
                        miStartStr = "0" + miStart
                    } else {
                        miStartStr = miStart.toString()
                    }
                    if (miEnd < 10) {
                        miEndStr = "0" + miEnd
                    } else {
                        miEndStr = miEnd.toString()
                    }
                    if (dh.CheckIsAppBlockAlreadyInDB(tenApp)) {
                        dh.updateAppBlock(
                            tenApp,
                            hourStart.toString() + ":" + miStartStr,
                            hourEnd.toString() + ":" + miEndStr
                        )
                        SetBlockTime()
                        ServiceSinkhole.stop(
                            "switch off",
                            (activity as MainActivity),
                            false
                        )
                        ServiceSinkhole.reload(
                            "changed notify",
                            (activity as MainActivity),
                            false
                        )
                        ServiceSinkhole.reload(
                            "changed filter",
                            (activity as MainActivity),
                            false
                        )
                        val prefs =
                            PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
                        prefs.edit().putBoolean("filter", true).apply()
                        prefs.edit().putBoolean("log_app", true).apply()
                        prefs.edit().putBoolean("notify_access", true).apply()
                        Log.i(
                            TAG,
                            "Vừa update lại thời gian app theo yêu cầu : " + tenApp + " Thời gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
                        )
                    } else {
                        dh.insertAppBlock(
                            tenApp,
                            hourStart.toString() + ":" + miStartStr,
                            hourEnd.toString() + ":" + miEndStr
                        )
                        SetBlockTime()
                        ServiceSinkhole.stop(
                            "switch off",
                            (activity as MainActivity),
                            false
                        )
                        ServiceSinkhole.reload(
                            "changed notify",
                            (activity as MainActivity),
                            false
                        )
                        ServiceSinkhole.reload(
                            "changed filter",
                            (activity as MainActivity),
                            false
                        )
                        val prefs =
                            PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
                        prefs.edit().putBoolean("filter", true).apply()
                        prefs.edit().putBoolean("log_app", true).apply()
                        prefs.edit().putBoolean("notify_access", true).apply()
                        Log.i(
                            TAG,
                            "Vừa thêm app theo yêu cầu  : " + tenApp + " Thời gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
                        )
                    }
                }
            } else {
                dh.updateBlockInApp(tenApp, 0)
            }

            com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                .emit("xacThucThemAppChanThanhCong")
        }

    private val capNhatThongTinApDungLoi2 =
        Emitter.Listener { args ->
            println("Capnhatthongtinapdungloi")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }

    private val capNhatThongTinApDungThanhCong2 =
        Emitter.Listener { args ->
            println("capNhatThongTinApDungThanhCong")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }
    private val xacThucDongBoBlackListThanhCong2 =
        Emitter.Listener { args ->
            println("xacThucDongBoBlackListThanhCong")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }
    private val xacThucDongBoBlackListLoi2 =
        Emitter.Listener { args ->
            println("xacThucDongBoBlackListLoi")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
        }

    // Thông tin câph nhật blacklist
    private val thongTinCapNhatBlackList2 =
        Emitter.Listener { args ->
            val dh =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            Log.i(TAG, "Đồng bộ blacklist cho tài khoản trẻ em")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            Log.i(TAG, String.format("Nhận thông tin đồng bộ blacklist %s", ten))
            try {
                val thongTinApDungBlackList = ob.getJSONArray("thongTinApDungBlackList")
                Log.i(TAG, "thông tin đồng bộ BL trẻ : "+ob.getJSONArray("thongTinApDungBlackList").toString())
                val thongTinChan = ob.getJSONArray("thongTinChan")
                val thongTinBlackList = ob.getJSONArray("thongTinBlackList")
                val thongTinCapNhatLichTruyCap =
                    ob.getJSONArray("thongTinCapNhatLichTruyCap")

                // Cập nhật blackList
                // Theo thứ tự thời gian 0 -> n : từ cũ tới mới
                if (thongTinBlackList.length() != 0) {
                    for (i in 0 until thongTinBlackList.length()) {
                        val thongTinCapNhatBlackList = thongTinBlackList.getJSONObject(i)
                        val loaiCapNhat = thongTinCapNhatBlackList.getString("loaiCapNhat")
                        if (loaiCapNhat.contains("Xoa")) {
                            val maDanhSachChan =
                                thongTinCapNhatBlackList.getString("danhSach")
                            dh.deleteBlacklist(maDanhSachChan)
                        } else if (loaiCapNhat.contains("Them")) {
                            // Tiến hành cập nhật thêm blacklist
                            // Tạm thời không kiểm tra loại người dùng -> TODO
//                            val loaiNguoiDung = thongTinCapNhatBlackList.getString("loaiNguoiDung")
                            val maDanhSachChan =
                                thongTinCapNhatBlackList.getString("danhSach")
                            // Lưu mã blacklist ( chỉ cần lưu mã blacklist )
                            dh.updateBlackList(maDanhSachChan)
                            Log.i(
                                TAG,
                                String.format("Lưu lại mã blacklist %s", maDanhSachChan)
                            )
                        }
                    }
                }


                // Tuyen fix phần cập nhật blacklist


                if (thongTinApDungBlackList.length() != 0) {
                    for (i in 0 until thongTinApDungBlackList.length()) {
                        val loaiad =
                            thongTinApDungBlackList.getJSONObject(i).getString("loaiApDung")
                        val idkid =
                            thongTinApDungBlackList.getJSONObject(i).getString("treEm")
                        if (loaiad.compareTo("ApDung") == 0) {
                            if (idkid.contains(idtre)) {
                                val maDanhSachChanTrongThietBi =
                                    thongTinApDungBlackList.getJSONObject(i)
                                        .getString("danhSach")
                                if (!dh.CheckIsDataAlreadyInDBorNot(
                                        "blacklist",
                                        "idbl",
                                        maDanhSachChanTrongThietBi
                                    )
                                ) {
                                    dh.updateApDungBlacklist(maDanhSachChanTrongThietBi, 1)
                                    dh.updateApDungUrl(maDanhSachChanTrongThietBi, 1)
                                    Log.i(
                                        TAG,
                                        "mã danh sách chặn áp dụng: " + maDanhSachChanTrongThietBi
                                    )
                                }
                            }
                        } else if (loaiad.compareTo("BoApDung") == 0) {
                            val maDanhSachChanTrongThietBi =
                                thongTinApDungBlackList.getJSONObject(i)
                                    .getString("danhSach")
                            dh.updateApDungBlacklist(maDanhSachChanTrongThietBi, 0)
                            dh.updateApDungUrl(maDanhSachChanTrongThietBi, 0)
                        }
                    }
                }


                // Cập nhật thông tin chặn
                if (thongTinChan.length() != 0) {
                    for (j in 0 until thongTinChan.length()) {
                        val thongTinCapNhatThongTinChan = thongTinChan.getJSONObject(j)
                        Log.i(TAG,"thông tin chặn trẻ em : "+thongTinChan.getJSONObject(j).toString())
                        val loaiCapNhatThongTinChan =
                            thongTinCapNhatThongTinChan.getString("loaiCapNhat")
                        val thongTinChan =
                            thongTinCapNhatThongTinChan.getJSONObject("thongTinChan")
                                .getString("thongTin")
                        val maDanhSachChan =
                            thongTinCapNhatThongTinChan.getString("danhSach")
                        val dbBlackList = dh.blacklist
                        // Tiến hành cập nhật blacklist
                        try {
                            // Đoạn này cần cải thiện tốc độ -> TODO
                            // Lấy thông tin blacklist trong thiết bị
                            val idbl = dbBlackList.getColumnIndex("idbl")
                            val apdung = dbBlackList.getColumnIndex("apdung")
                            // Lướt qua các blacklist trong thiết bị
                            while (dbBlackList.moveToNext()) {
                                // Lấy mã blacklist trong thiết bị
                                val maDanhSachChanTrongThietBi = dbBlackList.getString(idbl)
                                val apdung = dbBlackList.getInt(apdung)
                                // Nếu mã danh sách của thông tin chặn kiểm tra chứa mã blacklist
                                // Nếu có -> tiến hành cập nhật url chặn
                                //
                                if (maDanhSachChan.contains(maDanhSachChanTrongThietBi)) {
                                    val urlchan =
                                        thongTinCapNhatThongTinChan.getJSONObject("thongTinChan")
                                            .getString("thongTin")
                                    if (loaiCapNhatThongTinChan.contains("Them")) {
                                        if (!dh.CheckIsBlAlreadyInURLorNot(
                                                maDanhSachChanTrongThietBi,
                                                urlchan
                                            )
                                        ) {
                                            if (apdung == 1) {
                                                dh.updateUrl(
                                                    urlchan,
                                                    maDanhSachChanTrongThietBi,
                                                    1
                                                )
                                            } else {
                                                dh.updateUrl(
                                                    urlchan,
                                                    maDanhSachChanTrongThietBi,
                                                    0
                                                )
                                            }

                                            Log.i(
                                                TAG,
                                                String.format(
                                                    "Thêm thông tin chặn %s",
                                                    urlchan
                                                )
                                            )
                                            dh.updateAccess(urlchan)
                                        }
                                    } else if (loaiCapNhatThongTinChan.contains("Xoa")) {
                                        // Nếu loại thông tin cập là xóa -> xóa thông tin chặn trong blacklist
                                        // Nếu không có trong blacklist tiến hành xóa
                                        if (dh.CheckIsDataAlreadyInDBorNot(
                                                "url",
                                                "url",
                                                urlchan
                                            )
                                        ) {
                                            dh.deleteUrl(urlchan)
                                            Log.i(
                                                TAG,
                                                String.format(
                                                    "Xóa thông tin chặn %s",
                                                    urlchan
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        } catch (error: Exception) {
                            Log.e(TAG, "Có lỗi trong quá trình cập nhật thông tin chặn")
                            error.printStackTrace()
                        }
                    }
                }

                // Thông tin cập nhật lịch truy cập
                // Tạm thời bỏ qua thông tin lịch truy cập
                if (thongTinCapNhatLichTruyCap.length() != 0) {
                    for (j in 0 until thongTinCapNhatLichTruyCap.length()) {
                        val tenApp =
                            thongTinCapNhatLichTruyCap.getJSONObject(j).getJSONObject("App")
                                .getString("tenApp")
                        try {
                            Log.i(TAG,"lịch áp dụng trẻ em : "+thongTinCapNhatLichTruyCap.getJSONObject(j).toString() )
                            val timeStart =
                                thongTinCapNhatLichTruyCap.getJSONObject(j)
                                    .getString("thoiGianBatDauTrongNgay")
                            val timeEnd = thongTinCapNhatLichTruyCap.getJSONObject(j)
                                .getString("thoiGianKetThucTrongNgay")

                            if (Integer.parseInt(timeStart) > 0 && Integer.parseInt(timeEnd) >= 0) {
                                val miStart = Integer.parseInt(timeStart) % 60
                                val hourStart = Integer.parseInt(timeStart) / 60
                                val miEnd = Integer.parseInt(timeEnd) % 60
                                val hourEnd = Integer.parseInt(timeEnd) / 60
                                var miStartStr = ""
                                var miEndStr = ""
                                if (miStart < 10) {
                                    miStartStr = "0" + miStart
                                } else {
                                    miStartStr = miStart.toString()
                                }
                                if (miEnd < 10) {
                                    miEndStr = "0" + miEnd
                                } else {
                                    miEndStr = miEnd.toString()
                                }
                                if (dh.CheckIsAppBlockAlreadyInDB(tenApp)) {
                                    dh.updateAppBlock(
                                        tenApp,
                                        hourStart.toString() + ":" + miStartStr,
                                        hourEnd.toString() + ":" + miEndStr
                                    )
                                    Log.i(
                                        TAG,
                                        "Vừa update lại thời gian app : " + tenApp + " Thời gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
                                    )
                                    SetBlockTime()
                                    ServiceSinkhole.stop(
                                        "switch off",
                                        (activity as MainActivity),
                                        false
                                    )
                                    ServiceSinkhole.reload(
                                        "changed notify",
                                        (activity as MainActivity),
                                        false
                                    )
                                    ServiceSinkhole.reload(
                                        "changed filter",
                                        (activity as MainActivity),
                                        false
                                    )
                                    val prefs =
                                        PreferenceManager.getDefaultSharedPreferences(
                                            activity as MainActivity
                                        )
                                    prefs.edit().putBoolean("filter", true).apply()
                                    prefs.edit().putBoolean("log_app", true).apply()
                                    prefs.edit().putBoolean("notify_access", true).apply()
                                } else {
                                    dh.insertAppBlock(
                                        tenApp,
                                        hourStart.toString() + ":" + miStartStr,
                                        hourEnd.toString() + ":" + miEndStr
                                    )
                                    Log.i(
                                        TAG,
                                        "Vừa thêm app : " + tenApp + " Thời gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
                                    )
                                    SetBlockTime()
                                    ServiceSinkhole.stop(
                                        "switch off",
                                        (activity as MainActivity),
                                        false
                                    )
                                    ServiceSinkhole.reload(
                                        "changed notify",
                                        (activity as MainActivity),
                                        false
                                    )
                                    ServiceSinkhole.reload(
                                        "changed filter",
                                        (activity as MainActivity),
                                        false
                                    )
                                    val prefs =
                                        PreferenceManager.getDefaultSharedPreferences(
                                            activity as MainActivity
                                        )
                                    prefs.edit().putBoolean("filter", true).apply()
                                    prefs.edit().putBoolean("log_app", true).apply()
                                    prefs.edit().putBoolean("notify_access", true).apply()
                                }

                            } else {
                                dh.updateBlockInApp(tenApp,0)
                                SetBlockTime()
                                ServiceSinkhole.stop(
                                    "switch off",
                                    (activity as MainActivity),
                                    false
                                )
                                ServiceSinkhole.reload(
                                    "changed notify",
                                    (activity as MainActivity),
                                    false
                                )
                                ServiceSinkhole.reload(
                                    "changed filter",
                                    (activity as MainActivity),
                                    false
                                )
                                val prefs =
                                    PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
                                prefs.edit().putBoolean("filter", true).apply()
                                prefs.edit().putBoolean("log_app", true).apply()
                                prefs.edit().putBoolean("notify_access", true).apply()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Lỗi gửi về lịch truy cập mà không có giờ")
                            Log.d(TAG,"Tên app lúc này : "+ tenApp)
                            dh.updateBlockInApp(tenApp, 0)
                            SetBlockTime()
                            ServiceSinkhole.stop(
                                "switch off",
                                (activity as MainActivity),
                                false
                            )
                            ServiceSinkhole.reload(
                                "changed notify",
                                (activity as MainActivity),
                                false
                            )
                            ServiceSinkhole.reload(
                                "changed filter",
                                (activity as MainActivity),
                                false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("error", "Có lỗi xảy ra trong khi đồng bộ blacklist")
                e.printStackTrace()
            }
            com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                .emit(EventTypesKid.yeuCauXacThucDongBoBlackList, getMaDongBo())
        }
    private val xacThucThanhCong2 =
        Emitter.Listener { args ->
            val dh =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            println("xacThucThanhCong")
            val ten = args[0] as String
            println(ten)
            dh.updatesync(ten)
            (activity as MainActivity).setMaDongBo2(ten)
        }

    // endregion
    open fun SetBlockTime() {
        val dh =
            com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstanceNew(this.context)
        val dataappblock = dh.appBlock
        if (dataappblock.count == 0) {
            Log.d(TAG, "Không có dữ liệu trong appblock")
        } else {
            Log.i(TAG, "Cập nhật lại chặn app trong broadcastReciever")
            val nameappT = dataappblock.getColumnIndex("nameapp")
            val timeStartT = dataappblock.getColumnIndex("timeStart")
            val timeEndT = dataappblock.getColumnIndex("timeEnd")
            val blockT = dataappblock.getColumnIndex("block")
            val idT = dataappblock.getColumnIndex("ID")
            while (dataappblock.moveToNext()) {
                val nameapp = dataappblock.getString(nameappT)
                val timeStart = dataappblock.getString(timeStartT)
                val timeEnd = dataappblock.getString(timeEndT)
                val block = dataappblock.getInt(blockT)
                val id = dataappblock.getInt(idT)
                val sdf: DateFormat = SimpleDateFormat("HH:mm")

                val dtTimeStart = sdf.parse(timeStart)
                val dtTimeEnd = sdf.parse(timeEnd)
                Log.d("infor","TimeStart : "+dtTimeStart.hours)
                (activity as MainActivity).SetAlarm(
                    id + 10000,
                    dtTimeEnd.hours,
                    dtTimeEnd.minutes,
                    nameapp,
                    0
                )
                (activity as MainActivity).SetAlarm(
                    id,
                    dtTimeStart.hours,
                    dtTimeStart.minutes,
                    nameapp,
                    1
                )

            }
        }
    }
}