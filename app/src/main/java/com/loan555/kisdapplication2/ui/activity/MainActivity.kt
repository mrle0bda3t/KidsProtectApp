package com.loan555.kisdapplication2.ui.activity


import android.app.*
import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.viewpager.widget.ViewPager
import com.loan555.kisdapplication2.Kidsapp.ActivityMain
import com.loan555.kisdapplication2.Kidsapp.Util
import com.loan555.kisdapplication2.Kidsapp.adapter.AdapterRule
import com.loan555.kisdapplication2.Kidsapp.connection.AlarmReceiver
import com.loan555.kisdapplication2.Kidsapp.connection.ServiceSinkhole
import com.loan555.kisdapplication2.R
import com.loan555.kisdapplication2.constant.*
import com.loan555.kisdapplication2.database.NotificationEntity
import com.loan555.kisdapplication2.databinding.ActivityMainBinding
import com.loan555.kisdapplication2.databinding.FragmentNotificationsBinding
import com.loan555.kisdapplication2.ui.adapter.ViewPagerMainAdapter
import com.loan555.kisdapplication2.ui.fragment.HomeFragment
import com.loan555.kisdapplication2.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kotlinx.android.synthetic.main.toolbar_main.view.*
import java.util.*

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener,
    HomeFragment.LoadDataResult {
    private val TAG = "NetGuard.Main"

    private val adapter: AdapterRule? = null
    private var dialogDoze: androidx.appcompat.app.AlertDialog? = null

    private val REQUEST_VPN = 1
    private val REQUEST_INVITE = 2
    private val REQUEST_LOGCAT = 3
    val REQUEST_ROAMING = 4
    private val MIN_SDK = Build.VERSION_CODES.LOLLIPOP_MR1
    var idtre: String? = null
    val ACTION_RULES_CHANGED = "com.loan555.kisdapplication2.Kidsapp.ACTION_RULES_CHANGED"
    val ACTION_QUEUE_CHANGED = "com.loan555.kisdapplication2.Kidsapp.ACTION_QUEUE_CHANGED"
    val EXTRA_REFRESH = "Refresh"
    val EXTRA_SEARCH = "Search"
    val EXTRA_RELATED = "Related"
    val EXTRA_APPROVE = "Approve"
    val EXTRA_LOGCAT = "Logcat"
    val EXTRA_CONNECTED = "Connected"
    val EXTRA_METERED = "Metered"
    val EXTRA_SIZE = "Size"
    private var maDongBo2 = ""
    private var _binding: FragmentNotificationsBinding? = null
    fun getMaDongBo2(): String? {
        return maDongBo2
    }

    fun setMaDongBo2(maDongBo2: String) {
        this.maDongBo2 = maDongBo2
    }

    private val CHANNEL_ID = "chanel_id_example_01"
    private val NOTIFICATION_ID = 1
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BroadcastReceiver
    private var resultOk = false
    private val mainViewModel: MainViewModel by lazy {
        MainViewModel(application)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.titleToolbar.text = getString(R.string.app_name)
        createNotificationChanel()
        initViewPager()
        initNavigation()
        intiListentner()
//        mainViewModel.insertNotification(NotificationEntity("test 3","mess",Calendar.getInstance().timeInMillis))
    }

    private fun intiListentner() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val connMgr =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                if (wifi!!.isConnected || mobile!!.isConnected) {
                    binding.internetDisconnect.visibility = View.GONE
                    if (!resultOk) {
                        binding.pagerMain.adapter =
                            ViewPagerMainAdapter(supportFragmentManager, this@MainActivity)
                    }
                } else {
                    binding.internetDisconnect.visibility = View.VISIBLE
                }
            }
        }
        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        mainViewModel.getAllNotification().observe(this, {
            initNotification(it)
        })
        btnMoreToolbar.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    private fun initNotification(it: List<NotificationEntity>?) {
        it?.let { list ->
            val notificationNotRead = list.filter { !it.read }
            if (notificationNotRead.isNotEmpty()) {
                toolbar.haveNotification.visibility = View.VISIBLE
                toolbar.numberNotification.text =
                    if (notificationNotRead.size < 99) notificationNotRead.size.toString() else "99+"
            } else {
                toolbar.haveNotification.visibility = View.INVISIBLE
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Cho phép truy cập",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, TaoTaiKhoanTreEmActivity::class.java))
            }
        }
        if (requestCode == REQUEST_ROAMING) if (grantResults[0] == PackageManager.PERMISSION_GRANTED) ServiceSinkhole.reload(
            "permission granted",
            this,
            false
        )
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initViewPager() {
        binding.pagerMain.adapter = ViewPagerMainAdapter(supportFragmentManager, this)
        binding.pagerMain.offscreenPageLimit = 2
        binding.pagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.navView.menu.findItem(R.id.navigation_home).isChecked = true
                    }
                    1 -> {
                        binding.navView.menu.findItem(R.id.navigation_dashboard).isChecked =
                            true
                    }
                    2 -> {
                        binding.navView.menu.findItem(R.id.navigation_notifications).isChecked =
                            true
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    private fun initNavigation() {
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding.pagerMain.currentItem = 0
                    true
                }
                R.id.navigation_dashboard -> {
                    binding.pagerMain.currentItem = 1
                    true
                }
                R.id.navigation_notifications -> {
                    binding.pagerMain.currentItem = 2
                    true
                }
                else -> true
            }
        }
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "KA"
            val descriptionText = "Đang chạy"
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val chanel: NotificationChannel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(chanel)
        }
    }

    fun sendNotification(name: String, type : Int) {
        val bitmapLargeIcon = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.ic_launcher_foreground
        )
        var title = ""
        var contentText = ""
        if(type == 0){
            title = "Phát hiện truy cập vào trang chặn"
            contentText = "Địa chỉ : " + name
        }
        else if(type == 1){
            title = "Phát hiện truy cập vào trang nhạy cảm"
            contentText = "Địa chỉ : " + name
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(contentText)
            .setLargeIcon(bitmapLargeIcon)
            .setOngoing(false)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }


    override fun dataOk() {
        resultOk = true
        binding.chuanBiDulieu.visibility = View.GONE
        binding.navView.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Util.logExtras(data)
        if (requestCode == REQUEST_VPN) {
            // Handle VPN approval
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit().putBoolean("enabled", resultCode == RESULT_OK).apply()
            if (resultCode == RESULT_OK) {
                ServiceSinkhole.start("prepared", this)
                val on = Toast.makeText(this, R.string.msg_on, Toast.LENGTH_LONG)
                on.setGravity(Gravity.CENTER, 0, 0)
                on.show()
                checkDoze()
            } else if (resultCode == RESULT_CANCELED) Toast.makeText(
                this,
                R.string.msg_vpn_cancelled,
                Toast.LENGTH_LONG
            ).show()
        } else if (requestCode == REQUEST_INVITE) {
            // Do nothing
        } else if (requestCode == REQUEST_LOGCAT) {
            // Send logcat by e-mail
            if (resultCode == RESULT_OK) {
                var target = data?.data
                if (data!!.hasExtra("org.openintents.extra.DIR_PATH")) target =
                    Uri.parse(target.toString() + "/logcat.txt")
                Log.i(TAG, "Export URI=$target")
                Util.sendLogcat(target, this)
            }
        } else {
            Log.w(TAG, "Unknown activity result request=$requestCode")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, name: String) {
        Log.i(TAG, "Preference " + name + "=" + prefs.all[name])
        if ("enabled" == name) {
            // Get enabled
            val enabled = prefs.getBoolean(name, false)

            // Display disabled warning
//                val tvDisabled = findViewById<TextView>(R.id.tvDisabled)
//                tvDisabled.visibility = if (enabled) View.GONE else View.VISIBLE

            // Check switch state
//                val swEnabled = findViewById<SwitchCompat>(R.id.swEnabled)
//                if (swEnabled.isChecked != enabled) swEnabled.isChecked = enabled
        } else if ("whitelist_wifi" == name || "screen_on" == name || "screen_wifi" == name || "whitelist_other" == name || "screen_other" == name || "whitelist_roaming" == name || "show_user" == name || "show_system" == name || "show_nointernet" == name || "show_disabled" == name || "sort" == name || "imported" == name) {
//            updateApplicationList(null);
            val llWhitelist = findViewById<LinearLayout>(R.id.llWhitelist)
            val screen_on = prefs.getBoolean("screen_on", true)
            val whitelist_wifi = prefs.getBoolean("whitelist_wifi", false)
            val whitelist_other = prefs.getBoolean("whitelist_other", false)
            val hintWhitelist = prefs.getBoolean("hint_whitelist", true)
            llWhitelist.visibility =
                if (!(whitelist_wifi || whitelist_other) && screen_on && hintWhitelist) View.VISIBLE else View.GONE
        } else if ("manage_system" == name) {
            invalidateOptionsMenu()
            val llSystem = findViewById<LinearLayout>(R.id.llSystem)
            val system = prefs.getBoolean("manage_system", false)
            val hint = prefs.getBoolean("hint_system", true)
            llSystem.visibility = if (!system && hint) View.VISIBLE else View.GONE
        } else if ("theme" == name || "dark_theme" == name) recreate()
    }

    private val accessChangedListener =
        com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.AccessChangedListener { runOnUiThread { if (adapter != null && adapter.isLive()) adapter.notifyDataSetChanged() } }

    public fun checkDoze() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val doze = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            if (Util.batteryOptimizing(this) && packageManager.resolveActivity(
                    doze,
                    0
                ) != null
            ) {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                if (!prefs.getBoolean("nodoze", false)) {
                    val inflater = LayoutInflater.from(this)
                    val view = inflater.inflate(R.layout.doze, null, false)
                    val cbDontAsk = view.findViewById<CheckBox>(R.id.cbDontAsk)
                    dialogDoze = AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .setPositiveButton(
                            android.R.string.yes
                        ) { dialog, which ->
                            prefs.edit().putBoolean("nodoze", cbDontAsk.isChecked).apply()
                            startActivity(doze)
                        }
                        .setNegativeButton(
                            android.R.string.no
                        ) { dialog, which ->
                            prefs.edit().putBoolean("nodoze", cbDontAsk.isChecked).apply()
                        }
                        .setOnDismissListener {
                            dialogDoze = null
                            checkDataSaving()
                        }
                        .create()
                    dialogDoze!!.show()
                } else checkDataSaving()
            } else checkDataSaving()
        }
    }

    public fun checkDataSaving() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val settings = Intent(
                Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            if (Util.dataSaving(this) && packageManager.resolveActivity(
                    settings,
                    0
                ) != null
            ) try {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                if (!prefs.getBoolean("nodata", false)) {
                    val inflater = LayoutInflater.from(this)
                    val view = inflater.inflate(R.layout.datasaving, null, false)
                    val cbDontAsk = view.findViewById<CheckBox>(R.id.cbDontAsk)
                    dialogDoze = AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .setPositiveButton(
                            android.R.string.yes
                        ) { dialog, which ->
                            prefs.edit().putBoolean("nodata", cbDontAsk.isChecked).apply()
                            startActivity(settings)
                        }
                        .setNegativeButton(
                            android.R.string.no
                        ) { dialog, which ->
                            prefs.edit().putBoolean("nodata", cbDontAsk.isChecked).apply()
                        }
                        .setOnDismissListener { dialogDoze = null }
                        .create()
                    dialogDoze!!.show()
                }
            } catch (ex: Throwable) {
            }
        }
    }

    public fun getIntentLogcat(): Intent {
        val intent: Intent
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (Util.isPackageInstalled("org.openintents.filemanager", this)) {
                intent = Intent("org.openintents.action.PICK_DIRECTORY")
            } else {
                intent = Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse("https://play.google.com/store/apps/details?id=org.openintents.filemanager")
            }
        } else {
            intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TITLE, "logcat.txt")
        }
        return intent
    }

    public val onRulesChanged: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Util.logExtras(intent)
//                val dhkid = com.loan555.kisdapplication2.Kidsapp.DatabaseHelper.getInstance(context)
//                var dataappblock: Cursor
//                dataappblock = dhkid.appBlock
//                Log.d("vao day roi","aaaaaaaa")
//                if(dataappblock.count!=null&&adapter!=null){
//                    Log.d("vao day roi","sssssssss")
//                    adapter.setDisconnected()
//                }
            if (adapter != null) if (intent.hasExtra(ActivityMain.EXTRA_CONNECTED) && intent.hasExtra(
                    ActivityMain.EXTRA_METERED
                )
            ) {
                if (intent.getBooleanExtra(ActivityMain.EXTRA_CONNECTED, false)) {
                    if (intent.getBooleanExtra(
                            ActivityMain.EXTRA_METERED,
                            false
                        )
                    ) adapter.setMobileActive() else adapter.setWifiActive()

                } else {
                    adapter.setDisconnected()
                }
            }
        }
    }
    public fun SetAlarm(id:Int, hour:Int, minute:Int, nameapp:String, block:Int){
        val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
        intent.putExtra("nameapp",nameapp)
        intent.putExtra("block", block)
        val alarmIntent: PendingIntent = PendingIntent.getBroadcast(this@MainActivity,id,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
        val startTime = Calendar.getInstance()
        startTime[Calendar.HOUR_OF_DAY] = hour
        startTime[Calendar.MINUTE] = minute
        startTime[Calendar.SECOND] = 0
        val alarmStartTime: Long = startTime.getTimeInMillis()
        alarm[AlarmManager.RTC_WAKEUP, alarmStartTime] = alarmIntent
    }
}
