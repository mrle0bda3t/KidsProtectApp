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

                // N???u lo???i t??i kho???n l?? t??i kho???n ng?????i l???n
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
                        "?????ng b???",
                        "??ang ?????ng b???, vui l??ng ?????i ...",
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
                    // Th??ng b??o l???i cho ng?????i b???o h??? n???u tr??? em truy c???p domain kh??ng ph?? h???p
                    socket.on(EventTypes.DomainKhongPhuHop, DomainKhongPhuHop)
                    // L??u l???i socket
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
                            Log.d(TAG, "Load view t??i kho???n ng?????i b???o h???")
                            val adapter: AccountAdapter =
                                AccountAdapter(this.requireContext(), onItemClick).apply {
                                    val newList: MutableList<ThongTinThanhVienAdapter> =
                                        mutableListOf()
                                    newList += ThongTinThanhVienAdapter(
                                        null,
                                        "T???o t??i kho???n tr??? em",
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
                                    Log.d(TAG, "Danh s??ch tr??? em : $newList")
                                    setList(newList)
                                }
                            recycleView.adapter = adapter
                        })
                    }
                } else {
                    // T??i kho???n tr??? em
                    // Ghi ???? datahelper n??n kh??ng c???n set l???i
                    var dhKid =
                        com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
                    if (getisLogout() == true) {
                        dhKid =
                            com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstanceNew(this.context)
                    }
                    Log.d(TAG, "Load view cho t??i kho???n tr??? em")
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

                    // L???y th??ng tin url trong blackList ???
                    if (dhKid != null) {
                        val dataurl = dhKid.url
                        if (dataurl.count == 0) {
                            Log.d(TAG, "Kh??ng c?? url n??o")
                        } else {
                            val urln = dataurl.getColumnIndex("url")
                            while (dataurl.moveToNext()) {
                                val url = dataurl.getString(urln)
                                Log.d(TAG, String.format("b???ng url : %s", url))
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
        // ------------------- cho nay de lay data tu shar??rentces
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
                        Log.d(myTag, "????ng nh???p v???i t??i kho???n ng?????i l???n")
                        dataCustom = DataLoginCustom(
                            it.data.id,
                            it.data.tenTaiKhoan,
                            it.data.loaiTaiKhoan.id,
                            it.token,
                            it.data.thongTinCaNhan?.anhChanDung
                        )
                    } else if (it.loaiTaiKhoan?.id == "60cc55e7ffdd4b0015125f8e") {
                        // tre em
                        Log.d(myTag, "????ng nh???p v???i t??i kho???n tr??? em")
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
                            "T???o t??i kho???n tr??? em",
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
                        Log.d(TAG, "Danh s??ch tr??? em = $newList")
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
            setMessage("B???n mu???n ????ng xu???t t??i kho???n n??y?")
            setPositiveButton("C??") { _, _ ->
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
            setNegativeButton("Kh??ng") { _, _ -> }
        }.create().show()
    }

    // endregion
// region SOCKETPARENTS
    private val ketnoithanhcong = Emitter.Listener {
        println("K???t n???i th??nh c??ng")
        var dh = DatabaseHelper.getInstance(context)
        if (getisLogout() == true) {
            dh = DatabaseHelper.getInstanceNew(context)
        }

        // M?? ?????ng b??? blackList
        val sync = dh.sync
        // M?? ?????ng b??? blackList
        val blsync = dh.blSync
        // Ki???m tra c?? t???n t???i m?? blackList hay kh??ng
        if (blsync.count == 0) {
            Log.d(TAG, "Kh??ng c?? m?? blackList")
        } else {
            // T???n t???i m?? blacklist trong thi???t b???
            Log.d(TAG, "T???n t???i m?? blackList trong thi???t b???");
            val blsyn = blsync.getColumnIndex("idblsync")
            while (blsync.moveToNext()) {
                // Nh???n m?? ?????ng b??? blacklis
                val maDongBoBlackList = blsync.getString(blsyn)
                Log.i(
                    TAG,
                    String.format("M?? ?????ng b??? blackList %s", maDongBoBlackList)
                );
                setMaDongBobl(maDongBoBlackList)
            }
        }

        // N???u nh?? kh??ng t???n t???i m?? ?????ng b??? blackList th?? ti???n h??nh g???i kh??ng c?? m?? ????? nh???n l???i m??
        if (getMaDongBobl().equals("")) {
            SocketHandler.getSocket().emit(EventTypes.dongBoBlackList)
        } else {
            // N???u c?? m?? th?? g???i k??m m?? ???? l??u tr?????c ????
            var datasend = ""
            datasend = "{\"maDongBoBlackList\":\"" + getMaDongBobl() + "\"}"
            Log.i(
                TAG,
                String.format("Th??ng tin g???i ?????ng b?? blackList %s", datasend)
            )
            var jsonDongBoBlackList: JSONObject? = null
            try {
                jsonDongBoBlackList = JSONObject(datasend)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            SocketHandler.getSocket().emit(EventTypes.dongBoBlackList, jsonDongBoBlackList)
        }
        // Ti???n h??nh ?????ng b??? l???ch s???
        // N???u kh??ng c?? m?? ?????ng b???
        if (sync.count == 0) {
            Log.d(TAG, "Kh??ng c?? d??? li???u idsync")
        } else {
            val syn = sync.getColumnIndex("idsync")
            while (sync.moveToNext()) {
                val maDongBoThongTinTruyCap = sync.getString(syn)
                Log.i(
                    TAG,
                    String.format(
                        "T??m th???y m?? ?????ng b??? th??ng tin truy c???p trong thi???t b??? %s",
                        maDongBoThongTinTruyCap
                    )
                )
                setMaDongBo(maDongBoThongTinTruyCap)
            }
        }

        // N???u kh??ng c?? m?? ?????ng b??? g???i th??ng tin ?????ng b??? th??ng tin truy c???p m???i
        if (getMaDongBo().equals("")) {
            SocketHandler.getSocket().emit(EventTypes.dongBoLichSuTruyCap)
        } else {
            var datasend = ""
            datasend = "{\"maThongTinDongBoLichSu\":\"" + getMaDongBo() + "\"}"
            Log.d(
                TAG,
                String.format(
                    "Th??ng tin y??u c???u ?????ng b??? th??ng tin truy c???p %s",
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
                    "C?? l???i x???y ra trong qu?? tr??nh ?????ng b??? th??ng tin truy c???p"
                )
                e.printStackTrace()
            }
        }
    }
    private val ketnoiloi = Emitter.Listener { println("K???t n???i th???t b???i") }
    private val huyketnoi = Emitter.Listener { println("???? b??? ng???t k???t n???i") }
    private val thongbao = Emitter.Listener { arg ->
        Log.i(
            TAG,
            String.format("Th??ng b??o %s", arg[0].toString())
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
            Log.i(TAG, " B???t ?????u nh???n th??ng tin truy c???p ??? ????y")
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
                                Log.d(TAG, "app = root r???i")
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
                                Log.d(TAG, "app kh??ng = root r???i")
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
            println("?????ng b??? l???ch s??? l???i")
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
                String.format("Ki???m tra blacklist v???i AI th??nh c??ng %s", args[0].toString())
            )
            val dh = DatabaseHelper.getInstance(context)
            val ob = args[0] as JSONObject
            val kid = Kid()
            val idkid = ob.getString("idTreEm")
            val domain = ob.getString("domain")
            kid.setIdKid(idkid)
            val datakid = dh.getKidbyId(kid.idKid)
            if (datakid.count == 0) {
                Log.d(TAG, "Kh??ng c?? kid n??o")
            } else {
                val namekidT = datakid.getColumnIndex("namekid")
                val anhChanDungT = datakid.getColumnIndex("anhChanDung")
                while (datakid.moveToNext()) {
                    val namekid = datakid.getString(namekidT)
                    val anhChanDung = datakid.getString(anhChanDungT)
                    Log.d(TAG, String.format("T??n kid: %s", namekid))
                    kid.setNameKid(namekid)
                    kid.setAnhChanDung(anhChanDung)
                }
            }
            (activity as MainActivity).sendNotification(domain, 1)
            val sdf = SimpleDateFormat("hh:mm:ss dd/MM/yyyy")
            val currentDate = sdf.format(Date())
            val insertNotification = mainViewModel?.insertNotification(
                NotificationEntity(
                    "Truy c???p v??o ?????a ch??? nh???y c???m ",
                    "Tr??? em " + kid.getNameKid(),
                    currentDate, " truy c???p v??o trang " + domain,
                    kid.anhChanDung
                )
            )
            Log.i(TAG, String.format("Tr??? em truy c???p domain kh??ng ph?? h???p %s", args[0].toString()))
        }

    private val thongTinTruyCapKetThuc = Emitter.Listener {
        println("Th??ng tin truy c???p k???t th??c ! G???i y??u c???u x??c nh???n y??u c???u x??c nh???n ?????ng b??? l???ch s??? truy c???p")
        SocketHandler.getSocket().emit("yeuCauXacNhanDongBoLichSuTruyCap")
    }
    private val xacThucDongBoLichSuLoi =
        Emitter.Listener { args ->
            println("X??c th???c ?????ng b??? l???ch s??? l???i")
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
            println(String.format("Y??u c???u c???p nh???t l???ch s??? %s", ten))
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
                        Log.d(TAG, "Kh??ng c?? kid n??o")
                    } else {
                        val namekidT = datakid.getColumnIndex("namekid")
                        val anhChanDungT = datakid.getColumnIndex("anhChanDung")
                        while (datakid.moveToNext()) {
                            val namekid = datakid.getString(namekidT)
                            val anhChanDung = datakid.getString(anhChanDungT)
                            Log.d(TAG, String.format("T??n kid: %s", namekid))
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
                                "Truy c???p v??o ???ng d???ng ch???n ",
                                "Tr??? em " + kid.getNameKid(),
                                currentDate,
                                " truy c???p v??o trang " + history.getDiaChi(),
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
            println(String.format("Th??ng tin c???p nh???t blackList %s", ob.toString()))
            // C???p nh???t th??ng tin blackList
            try {
                val thongTinBlackList = ob.getJSONArray("thongTinBlackList")
                val n = thongTinBlackList.length()
                for (i in 0 until n) {
                    val nguoidung =
                        thongTinBlackList.getJSONObject(i).getString("nguoiDung")
                    val idbl = thongTinBlackList.getJSONObject(i)
                        .getString("danhSach")
                    // Ki???m tra th??ng tin ng?????i d??ng c?? ????ng ??? trong thi???t b??? hay kh??ng?
                    if (nguoidung.contains(AppPreferences.idNguoiDung.toString())) {
                        val loaicapnhat = thongTinBlackList.getJSONObject(i)
                            .getString("loaiCapNhat")
                        if (loaicapnhat.contains("Them")) {
                            val namebl = thongTinBlackList.getJSONObject(i)
                                .getString("tenDanhSach")
                            // Th??m blacklist v??o database
                            dh.updateBlackList(namebl, "", idbl)
                            val datakid = dh.getKid()
                            // Th??m blacklist th?? auto ??p d???ng blacklist ???? cho t???t c??? c??c b??
                            if (datakid.count == 0) {
                                Log.d(TAG, "Kh??ng c?? kid n??o")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format("C???p nh???t ??p d???ng cho idkid: %s", idkid)
                                    )
                                    dh.updateApplybl(idbl, idkid, "")
                                }
                            }
                            Log.i(TAG, String.format("Th??m BlackList %s ", namebl))
                        } else if (loaicapnhat.contains("Xoa")) {

                            // N???u id c???a BlackList t???n t???i
                            dh.deleteBlacklistbyId(idbl)
                            val datakid = dh.getKid()
                            // Xo?? blacklist th?? auto b??? ??p d???ng blacklist ???? cho t???t c??? c??c b??
                            if (datakid.count == 0) {
                                Log.d(TAG, "Kh??ng c?? kid n??o")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format("C???p nh???t xo?? ??p d???ng cho idkid: %s", idkid)
                                    )
                                    dh.deleteApplybl(idkid, idbl);
                                }
                            }
                            Log.i(TAG, String.format("X??a BlackList %s ", idbl))
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
                                // Th??m blacklist v??o database
                                dh.updateBlackList(namebl, "", idbl)
                                Log.i(TAG, String.format("Th??m BlackList %s ", namebl))
                            } else if (loaicapnhat.contains("Xoa")) {

                                // N???u id c???a BlackList t???n t???i
                                dh.deleteBlacklistbyId(idbl)
                                Log.i(TAG, String.format("X??a BlackList %s ", idbl))
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                Log.e(TAG, "C?? l???i x???y ra khi ?????ng b??? blackList")
                e.printStackTrace()
            }

            // C???p nh???t th??ng tin ch???n
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

            // Th??ng tin ??p d???ng blackList
            try {
                val x = ob.getJSONArray("thongTinApDungBlackList").length();

                if (x != 0) {
                    val n = ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0).length()
                    for (i in 0 until n) {
                        Log.i(TAG,"Th??ng tin c???p nh???t BL ng?????i l???n : "+ ob.getJSONArray("thongTinApDungBlackList").getJSONArray(0)
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

            // Th??ng tin c???p nh???t l???ch truy c???p
            try {
                val thongTinCapNhatLichTruyCap = ob.getJSONArray("thongTinCapNhatLichTruyCap")
                Log.i(
                    TAG,
                    String.format(
                        "Th??ng tin c???p nh???t l???ch truy c???p %s",
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
                            TAG,"l???ch truy c???p ng?????i l???n : " + thongTinCapNhatLichTruyCap.getJSONObject(i)
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
                            Log.e(TAG, "L???i g???i v??? l???ch truy c???p m?? kh??ng c?? gi???")
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
            Log.i(TAG, "X??c th???c blackList th??nh c??ng")
            val maDongBoBlackList = args[0] as String
            Log.i(
                TAG,
                String.format(
                    "Nh???n m?? ?????ng b??? blackList %s! L??u l???i v??o db",
                    maDongBoBlackList
                )
            )
            // L??u m?? ?????ng b??? blackList v??o db
            dh.updateblacklistsync(maDongBoBlackList)
            // Set m?? ?????ng b??? v??o m?? s??? d???ng hi???n t???i
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
                    "Th??ng tin c???p nh???t blackList th??nh c??ng %s",
                    args[0].toString()
                )
            )
            val dh = DatabaseHelper.getInstance(context)
            val ob = args[0] as JSONObject
            val loaiCapNhat: String
            val loaiThongTin: String
            try {
                loaiThongTin = ob.getJSONObject("thongTinCapNhat").getString("loaiThongTin")
                // N???u lo???i th??ng tin l?? blackList
                if (loaiThongTin.contains("BlackList")) {
                    loaiCapNhat =
                        ob.getJSONObject("thongTinCapNhat").getString("loaiCapNhat")
                    // N???u lo???i c???p nh???t l?? Th??m
                    if (loaiCapNhat.contains("Them")) {
                        // Id blackList
                        val idbl =
                            ob.getJSONObject("thongTinCapNhat").getJSONObject("danhSach")
                                .getString("_id")

                        // T??n blackList
                        val namebl =
                            ob.getJSONObject("thongTinCapNhat").getJSONObject("danhSach")
                                .getString("tenDanhSach")

                        // Ki???m tra xem c?? trong db hay ch??a
                        val existedNameBLackList =
                            dh.CheckIsDataAlreadyInDBorNot("blacklist", "namebl", namebl)
                        // N???u t???n t???i th?? c???p nh???t id c???a blackList ???? c??
                        if (existedNameBLackList) {
                            Log.i(
                                TAG,
                                "T??n blackList ???? c?? trong blackList ! Ti???n h??nh c???p nh???t"
                            )
                            // C???p nh???t id c???a blackList
                            dh.updateidbl(idbl, namebl)
                            val datakid = dh.getKid()
                            // Th??m blacklist th?? auto ??p d???ng blacklist ???? cho t???t c??? c??c b??
                            if (datakid.count == 0) {
                                Log.d(TAG, "Kh??ng c?? kid n??o")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format(
                                            "C???p nh???t ??p d???ng cho idkid: %s",
                                            idkid
                                        )
                                    )
                                    dh.updateApplybl(idbl, idkid, "")
                                }
                            }

                            // Load l???i Activity BlackList
                            Log.i(TAG, "Load l???i blackList activity from outside")

                            // C???p nh???t danh s??ch blacklist ??? giao di???n
                            ActivityBlacklist.loadBlacklistsOutSide()
                        } else {
                            // N???u kh??ng t???n t???i th?? t???o m???i v???i th??ng tin ???????c tr??? v???
                            Log.i(
                                TAG,
                                String.format(
                                    "T??n blackList ch??a c?? trong blackList ! Ti???n h??nh th??m blacklist %s ",
                                    namebl
                                )
                            )
                            // N???u kh??ng c?? th?? t???o m???i blackList v???i th??ng tin nh???n ???????c
                            dh.updateBlackList(namebl, "", idbl)
                            val datakid = dh.getKid()
                            // Th??m blacklist th?? auto ??p d???ng blacklist ???? cho t???t c??? c??c b??
                            if (datakid.count == 0) {
                                Log.d(TAG, "Kh??ng c?? kid n??o")
                            } else {
                                val id = datakid.getColumnIndex("idkid")
                                while (datakid.moveToNext()) {
                                    val idkid = datakid.getString(id)
                                    Log.d(
                                        TAG,
                                        String.format(
                                            "C???p nh???t ??p d???ng cho idkid: %s",
                                            idkid
                                        )
                                    )
                                    dh.updateApplybl(idbl, idkid, "")
                                }
                            }
                        }
                    } else {
                        Log.i(TAG, "Ti???n h??nh x??a blackList")
                        // N???u kh??ng th?? x??a blackList
                        val idbl = ob.getJSONObject("thongTinCapNhat").getString("danhSach")
                        dh.deleteBlacklistbyId(idbl)
                        val datakid = dh.getKid()
                        // Xo?? blacklist th?? auto b??? ??p d???ng blacklist ???? cho t???t c??? c??c b??
                        if (datakid.count == 0) {
                            Log.d(TAG, "Kh??ng c?? kid n??o")
                        } else {
                            val id = datakid.getColumnIndex("idkid")
                            while (datakid.moveToNext()) {
                                val idkid = datakid.getString(id)
                                Log.d(
                                    TAG,
                                    String.format(
                                        "C???p nh???t xo?? ??p d???ng cho idkid: %s",
                                        idkid
                                    )
                                )
                                dh.deleteApplybl(idkid, idbl);
                            }
                        }
                    }
                }
                // N???u l?? th??ng tin ch???n url
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
                Log.e(TAG, "C?? l???i x???y ra");
                e.printStackTrace()
            }
        }

    private val yeuCauCapNhatBlackList =
        Emitter.Listener { args ->
            val ten = args[0] as String
            println(String.format("Y??u c???u c???p nh???t blackList %s", ten))
            SocketHandler.getSocket().emit("capNhatBlackListThanhCong")
        }

    private val capNhatThongTinApDungLoi =
        Emitter.Listener { args ->
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(String.format("C???p nh???t th??ng tin ??p d???ng l???i %s", ten))
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
            println("K???t n???i th??nh c??ng lo???i t??i kho???n tr??? em");
            var dhKid =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            if (getisLogout() == true) {
                dhKid =
                    com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstanceNew(this.context)
            }
            // Ti???n h??nh ?????ng b??? blackList
            Log.d(TAG, "Ti???n h??nh ?????ng b??? blackList")
            val blsync = dhKid.sync
            if (blsync.count == 0) {
                Log.d(TAG, "Kh??ng c?? d??? li???u blackList")
            } else {
                val blsyn = blsync.getColumnIndex("idsync")
                while (blsync.moveToNext()) {
                    (activity as MainActivity).setMaDongBo2(blsync.getString(blsyn))
                }
            }
            // N???u nh?? kh??ng c?? th??ng tin ?????ng b??? blackList th?? ti???n h??nh g???i y??u c???u ?????ng b??? m???i
            if ((activity as MainActivity).getMaDongBo2().equals("")) {
                Log.d(TAG, "Ti???n h??nh ?????ng b??? blackList")
                com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                    .emit(EventTypesKid.dongBoBlackList)
            } else {
                // N???u nh?? c?? t???n t???i th??ng tin ?????ng b??? t??? tr?????c th?? ti???n h??nh g???i v???i m?? c??
                var datasend = ""
                datasend =
                    "{\"maDongBoBlackList\":\"" + (activity as MainActivity).getMaDongBo2() + "\"}"
                Log.d(
                    TAG,
                    String.format("Th??ng tin y??u c???u ?????ng b??? blackList %s", datasend)
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
        Emitter.Listener { println("K???t n???i th???t b???i lo???i t??i kho???n tr??? em") }
    private val HuyKetNoiTreEm =
        Emitter.Listener { println("H???y k???t n???i t??i kho???n tr??? em") }
    private val ThongBaoTreEm = Emitter.Listener { args ->
        Log.i(
            TAG,
            String.format("Th??ng b??o t??i kho???n tr??? em %s", args[0].toString())
        )
    }
    private val TaoThongTinTruyCapLoi =
        Emitter.Listener { args ->
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(String.format("T???o th??ng tin truy c???p l???i %s", ten))
        }
    private val taothongtintruycapthanhcong2 =
        Emitter.Listener { args -> println("T???o th??ng tin truy c???p th??nh c??ng " + args[0].toString()) }

    // C???p nh???t blacklist
    private val yeuCauCapNhatBlackList2 =
        Emitter.Listener { args ->
            try {
                val dh =
                    com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
                Log.i(TAG, "Y??u c???u c???p nh???t blackList")

                // Data y??u c???u c???p nh???t blacklist
                val ob = args[0] as JSONObject
                // In ra m??n h??nh
                Log.i(
                    TAG,
                    String.format("Nh???n y??u c???u c???p nh???t blacklist %s", ob.toString())
                )

                // Lo???i c???p nh???t
                val loaiCapNhat = ob.getString("loaiCapNhat")
                // Lo???i th??ng tin
                val loaiThongTin = ob.getString("loaiThongTin")

                // Ki???m tra lo???i c???p nh???t
                //
                if (loaiCapNhat.equals("Xoa")) {
                    // M?? danh s??ch ch???n ???????c y??u c???u c???p nh???t
                    val maDanhSachYeuCauCapNhat = ob.getString("danhSach")
                    // ????y l?? xo?? th??ng tin ch???n
                    if (loaiThongTin.equals("ThongTinChan")) {
                        val thongTinChanYeuCauXoa =
                            ob.getJSONObject("thongTinChan").getString("thongTin")
                        Log.i(
                            TAG,
                            String.format(
                                "Y??u c???u c???p nh???t blacklist x??a th??ng tin ch???n %s",
                                thongTinChanYeuCauXoa
                            )
                        )
                        dh.deleteUrl(thongTinChanYeuCauXoa)
                        Log.i(TAG, "X??a th??ng tin ch???n th??nh c??ng")
                    } else {
                        if (loaiThongTin.equals("BlackList")) {
                            // Ti???n h??nh x??a blacklist v???i m?? ???? nh???n
                            Log.i(
                                TAG,
                                String.format(
                                    "Nh???n y??u c???u x??a blacklist v???i m?? %s",
                                    maDanhSachYeuCauCapNhat
                                )
                            )
                            // xo?? black list n??n xo?? lu??n c??? url c???a blacklist ????
                            dh.deleteBlacklist(maDanhSachYeuCauCapNhat)
                            Log.i(
                                TAG,
                                String.format(
                                    "X??a th??nh c??ng blacklist %s",
                                    maDanhSachYeuCauCapNhat
                                )
                            )
                            dh.deleteUrlbyIdBl(maDanhSachYeuCauCapNhat)
                        }
                    }
                }
                // Lo???i c???p nh???t Th??m
                else {
                    if (loaiCapNhat.equals("Them")) {
                        // M?? danh s??ch y??u c???u c???p nh???t
                        val maDanhSachYeuCauCapNhat = ob.getString("danhSach")
                        // Ki???m tra lo???i th??ng tin c???p nh???t
                        if (loaiThongTin.equals("ThongTinChan")) {
                            // Th??ng tin ch???n y??u c???u c???p nh???t
                            val thongTinChan =
                                ob.getJSONObject("thongTinChan").getString("thongTin")

                            // Th??m th??ng tin ch???n
                            dh.updateUrl(thongTinChan, maDanhSachYeuCauCapNhat, 1)

                            // C???p nh???t th??nh c??ng
                            Log.d(TAG, "C???p nh???t url th??nh c??ng")
                        } else {
                            if (loaiThongTin.equals("BlackList")) {
                                val maBlackList =
                                    ob.getJSONObject("danhSach").getString("_id")

                                // Ti???n h??nh c???p nh???t th??m blacklist
                                dh.updateBlackList(maBlackList)
                                Log.i(
                                    TAG,
                                    String.format(
                                        "C???p nh???t th??m blacklist %s th??nh c??ng",
                                        maBlackList
                                    )
                                )
                            }
                        }
                    }
                }
                // Reload l???i service
                ServiceSinkhole.stop("switch off", (activity as MainActivity), false)
                ServiceSinkhole.reload("changed notify", (activity as MainActivity), false)
                ServiceSinkhole.reload("changed filter", (activity as MainActivity), false)
                val prefs =
                    PreferenceManager.getDefaultSharedPreferences(activity as MainActivity)
                prefs.edit().putBoolean("filter", true).apply()
                prefs.edit().putBoolean("log_app", true).apply()
                prefs.edit().putBoolean("notify_access", true).apply()
                // X??c nh???n v???i server ???? th??nh c??ng
                Log.i(TAG, "G???i y??u c???u th??nh c??ng t???i server")
                com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler.getSocket()
                    .emit(EventTypes.capNhatBlackListThanhCong, getMaDongBo())
            } catch (error: Exception) {
                Log.e(TAG, "C?? l???i x???y ra trong khi c???p nh???t y??u c???u c???p nh???t blacklist")
                Log.e(TAG, error.toString())
            }
        }

    // C???p nh???t th??ng tin ??p d???ng
    private val yeuCauCapNhatThongTinApDung2 =
        Emitter.Listener { args ->
            val dh =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            println("Y??u c???u c???p nh???t th??ng tin ??p d???ng")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            println(ten)
            try {
                val loaiapdung = ob.getString("loaiApDung")
                Log.d(TAG, "Lo???i ??p d???ng l?? : " + loaiapdung)
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
                                "???? ??p d???ng cho danh s??ch ch???n : " + maDanhSachChanTrongThietBi
                            )
                            dh.updateApDungUrl(maDanhSachChanTrongThietBi, 1)
                            var dhKid =
                                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(
                                    this.context
                                )
                            if (dhKid != null) {
                                val dataurl = dhKid.url
                                if (dataurl.count == 0) {
                                    Log.d(TAG, "Kh??ng c?? url n??o")
                                } else {
                                    val urln = dataurl.getColumnIndex("url")
                                    val apdungn = dataurl.getColumnIndex("apdung")
                                    while (dataurl.moveToNext()) {
                                        val url = dataurl.getString(urln)
                                        val apdung = dataurl.getString(apdungn)
                                        Log.d(
                                            TAG,
                                            String.format(
                                                "debug b???ng url sau khi ??p d???ng : %s status %s",
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
                        "???? update ??p d???ng blacklist th??nh 0 c???a m?? : " + maDanhSachChanTrongThietBi
                    )
                    dh.updateApDungUrl(maDanhSachChanTrongThietBi, 0)
                    Log.i(
                        TAG,
                        "???? b??? ??p d???ng cho danh s??ch ch???n : " + maDanhSachChanTrongThietBi
                    )
                    var dhKid =
                        com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
                    if (dhKid != null) {
                        val dataurl = dhKid.url
                        if (dataurl.count == 0) {
                            Log.d(TAG, "Kh??ng c?? url n??o")
                        } else {
                            val urln = dataurl.getColumnIndex("url")
                            val apdungn = dataurl.getColumnIndex("apdung")
                            while (dataurl.moveToNext()) {
                                val url = dataurl.getString(urln)
                                val apdung = dataurl.getString(apdungn)
                                Log.d(
                                    TAG,
                                    String.format(
                                        "debug b???ng url sau khi b??? ??p d???ng : %s status %s",
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
                    "V???a xo?? app ch???n theo y??u c???u : " + tenApp
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
                            "V???a update l???i th???i gian app theo y??u c???u : " + tenApp + " Th???i gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
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
                            "V???a th??m app theo y??u c???u  : " + tenApp + " Th???i gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
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

    // Th??ng tin c??ph nh???t blacklist
    private val thongTinCapNhatBlackList2 =
        Emitter.Listener { args ->
            val dh =
                com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(this.context)
            Log.i(TAG, "?????ng b??? blacklist cho t??i kho???n tr??? em")
            val ob = args[0] as JSONObject
            val ten = ob.toString()
            Log.i(TAG, String.format("Nh???n th??ng tin ?????ng b??? blacklist %s", ten))
            try {
                val thongTinApDungBlackList = ob.getJSONArray("thongTinApDungBlackList")
                Log.i(TAG, "th??ng tin ?????ng b??? BL tr??? : "+ob.getJSONArray("thongTinApDungBlackList").toString())
                val thongTinChan = ob.getJSONArray("thongTinChan")
                val thongTinBlackList = ob.getJSONArray("thongTinBlackList")
                val thongTinCapNhatLichTruyCap =
                    ob.getJSONArray("thongTinCapNhatLichTruyCap")

                // C???p nh???t blackList
                // Theo th??? t??? th???i gian 0 -> n : t??? c?? t???i m???i
                if (thongTinBlackList.length() != 0) {
                    for (i in 0 until thongTinBlackList.length()) {
                        val thongTinCapNhatBlackList = thongTinBlackList.getJSONObject(i)
                        val loaiCapNhat = thongTinCapNhatBlackList.getString("loaiCapNhat")
                        if (loaiCapNhat.contains("Xoa")) {
                            val maDanhSachChan =
                                thongTinCapNhatBlackList.getString("danhSach")
                            dh.deleteBlacklist(maDanhSachChan)
                        } else if (loaiCapNhat.contains("Them")) {
                            // Ti???n h??nh c???p nh???t th??m blacklist
                            // T???m th???i kh??ng ki???m tra lo???i ng?????i d??ng -> TODO
//                            val loaiNguoiDung = thongTinCapNhatBlackList.getString("loaiNguoiDung")
                            val maDanhSachChan =
                                thongTinCapNhatBlackList.getString("danhSach")
                            // L??u m?? blacklist ( ch??? c???n l??u m?? blacklist )
                            dh.updateBlackList(maDanhSachChan)
                            Log.i(
                                TAG,
                                String.format("L??u l???i m?? blacklist %s", maDanhSachChan)
                            )
                        }
                    }
                }


                // Tuyen fix ph???n c???p nh???t blacklist


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
                                        "m?? danh s??ch ch???n ??p d???ng: " + maDanhSachChanTrongThietBi
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


                // C???p nh???t th??ng tin ch???n
                if (thongTinChan.length() != 0) {
                    for (j in 0 until thongTinChan.length()) {
                        val thongTinCapNhatThongTinChan = thongTinChan.getJSONObject(j)
                        Log.i(TAG,"th??ng tin ch???n tr??? em : "+thongTinChan.getJSONObject(j).toString())
                        val loaiCapNhatThongTinChan =
                            thongTinCapNhatThongTinChan.getString("loaiCapNhat")
                        val thongTinChan =
                            thongTinCapNhatThongTinChan.getJSONObject("thongTinChan")
                                .getString("thongTin")
                        val maDanhSachChan =
                            thongTinCapNhatThongTinChan.getString("danhSach")
                        val dbBlackList = dh.blacklist
                        // Ti???n h??nh c???p nh???t blacklist
                        try {
                            // ??o???n n??y c???n c???i thi???n t???c ????? -> TODO
                            // L???y th??ng tin blacklist trong thi???t b???
                            val idbl = dbBlackList.getColumnIndex("idbl")
                            val apdung = dbBlackList.getColumnIndex("apdung")
                            // L?????t qua c??c blacklist trong thi???t b???
                            while (dbBlackList.moveToNext()) {
                                // L???y m?? blacklist trong thi???t b???
                                val maDanhSachChanTrongThietBi = dbBlackList.getString(idbl)
                                val apdung = dbBlackList.getInt(apdung)
                                // N???u m?? danh s??ch c???a th??ng tin ch???n ki???m tra ch???a m?? blacklist
                                // N???u c?? -> ti???n h??nh c???p nh???t url ch???n
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
                                                    "Th??m th??ng tin ch???n %s",
                                                    urlchan
                                                )
                                            )
                                            dh.updateAccess(urlchan)
                                        }
                                    } else if (loaiCapNhatThongTinChan.contains("Xoa")) {
                                        // N???u lo???i th??ng tin c???p l?? x??a -> x??a th??ng tin ch???n trong blacklist
                                        // N???u kh??ng c?? trong blacklist ti???n h??nh x??a
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
                                                    "X??a th??ng tin ch???n %s",
                                                    urlchan
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        } catch (error: Exception) {
                            Log.e(TAG, "C?? l???i trong qu?? tr??nh c???p nh???t th??ng tin ch???n")
                            error.printStackTrace()
                        }
                    }
                }

                // Th??ng tin c???p nh???t l???ch truy c???p
                // T???m th???i b??? qua th??ng tin l???ch truy c???p
                if (thongTinCapNhatLichTruyCap.length() != 0) {
                    for (j in 0 until thongTinCapNhatLichTruyCap.length()) {
                        val tenApp =
                            thongTinCapNhatLichTruyCap.getJSONObject(j).getJSONObject("App")
                                .getString("tenApp")
                        try {
                            Log.i(TAG,"l???ch ??p d???ng tr??? em : "+thongTinCapNhatLichTruyCap.getJSONObject(j).toString() )
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
                                        "V???a update l???i th???i gian app : " + tenApp + " Th???i gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
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
                                        "V???a th??m app : " + tenApp + " Th???i gian : " + hourStart.toString() + ":" + miStartStr + ":" + hourEnd.toString() + ":" + miEndStr
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
                            Log.e(TAG, "L???i g???i v??? l???ch truy c???p m?? kh??ng c?? gi???")
                            Log.d(TAG,"T??n app l??c n??y : "+ tenApp)
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
                Log.e("error", "C?? l???i x???y ra trong khi ?????ng b??? blacklist")
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
            Log.d(TAG, "Kh??ng c?? d??? li???u trong appblock")
        } else {
            Log.i(TAG, "C???p nh???t l???i ch???n app trong broadcastReciever")
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