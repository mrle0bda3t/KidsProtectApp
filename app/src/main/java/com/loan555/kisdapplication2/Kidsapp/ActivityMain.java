package com.loan555.kisdapplication2.Kidsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loan555.kisdapplication2.Kidsapp.adapter.AdapterRule;
import com.loan555.kisdapplication2.Kidsapp.connection.ServiceSinkhole;
import com.loan555.kisdapplication2.Kidsapp.connection.SocketHandler;
import com.loan555.kisdapplication2.Kidsapp.widget.ReceiverAutostart;
import com.loan555.kisdapplication2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

import static java.util.Collections.singletonMap;

// Event types for socket
import com.loan555.kisdapplication2.Kidsapp.constants.EventTypesKid;

public class ActivityMain extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "NetGuard.Main";

    private boolean running = false;
    private SwitchCompat swEnabled;
    private AdapterRule adapter = null;
    private AlertDialog dialogVpn = null;
    private AlertDialog dialogDoze = null;

    private static final int REQUEST_VPN = 1;
    private static final int REQUEST_INVITE = 2;
    private static final int REQUEST_LOGCAT = 3;
    public static final int REQUEST_ROAMING = 4;

    private static final int MIN_SDK = Build.VERSION_CODES.LOLLIPOP_MR1;
    String idtre;
    String tokentreem;
    public static final String ACTION_RULES_CHANGED = "com.loan555.kisdapplication2.Kidsapp.ACTION_RULES_CHANGED";
    public static final String ACTION_QUEUE_CHANGED = "com.loan555.kisdapplication2.Kidsapp.ACTION_QUEUE_CHANGED";
    public static final String EXTRA_REFRESH = "Refresh";
    public static final String EXTRA_SEARCH = "Search";
    public static final String EXTRA_RELATED = "Related";
    public static final String EXTRA_APPROVE = "Approve";
    public static final String EXTRA_LOGCAT = "Logcat";
    public static final String EXTRA_CONNECTED = "Connected";
    public static final String EXTRA_METERED = "Metered";
    public static final String EXTRA_SIZE = "Size";
    public static final String Server_url = "http://34.134.234.78";
    private String maDongBo = "";

    public String getMaDongBo() {
        return maDongBo;
    }

    public void setMaDongBo(String maDongBo) {
        this.maDongBo = maDongBo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        running = true;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = prefs.getBoolean("enabled", false);
        boolean initialized = prefs.getBoolean("initialized", false);

        // Upgrade
        ReceiverAutostart.upgrade(initialized, this);
        if (!getIntent().hasExtra(EXTRA_APPROVE)) {
            if (enabled)
                ServiceSinkhole.start("UI", this);
            else
                ServiceSinkhole.stop("UI", this, false);
        }
        swEnabled = findViewById(R.id.swEnabled);
        swEnabled.setChecked(enabled);
        boolean isChecked = true;
        Log.i(TAG, "Switch=" + isChecked);
        prefs.edit().putBoolean("enabled", isChecked).apply();
        if (isChecked) {
            try {
                String alwaysOn = Settings.Secure.getString(getContentResolver(), "always_on_vpn_app");
                Log.i(TAG, "Always-on=" + alwaysOn);
                if (!TextUtils.isEmpty(alwaysOn))
                    if (getPackageName().equals(alwaysOn)) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                                prefs.getBoolean("filter", false)) {
                            int lockdown = Settings.Secure.getInt(getContentResolver(), "always_on_vpn_lockdown", 0);
                            Log.i(TAG, "Lockdown=" + lockdown);
                            if (lockdown != 0) {
                                swEnabled.setChecked(false);
                                Toast.makeText(ActivityMain.this, R.string.msg_always_on_lockdown, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    } else {
                        swEnabled.setChecked(false);
                        Toast.makeText(ActivityMain.this, R.string.msg_always_on, Toast.LENGTH_LONG).show();
                        return;
                    }
            } catch (Throwable ex) {
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            }

            boolean filter = prefs.getBoolean("filter", false);
            if (filter && Util.isPrivateDns(ActivityMain.this))
                Toast.makeText(ActivityMain.this, R.string.msg_private_dns, Toast.LENGTH_LONG).show();

            try {
                final Intent prepare = VpnService.prepare(ActivityMain.this);
                if (prepare == null) {
                    Log.i(TAG, "Prepare done");
                    onActivityResult(REQUEST_VPN, RESULT_OK, null);
                } else {
                    // Show dialog
                    LayoutInflater inflater = LayoutInflater.from(ActivityMain.this);
                    View view = inflater.inflate(R.layout.vpn, null, false);
                    dialogVpn = new AlertDialog.Builder(ActivityMain.this)
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (running) {
                                        Log.i(TAG, "Start intent=" + prepare);
                                        try {
                                            // com.android.vpndialogs.ConfirmDialog required
                                            startActivityForResult(prepare, REQUEST_VPN);
                                        } catch (Throwable ex) {
                                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                                            onActivityResult(REQUEST_VPN, RESULT_CANCELED, null);
                                            prefs.edit().putBoolean("enabled", false).apply();
                                        }
                                    }
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    dialogVpn = null;
                                }
                            })
                            .create();
                    dialogVpn.show();
                }
            } catch (Throwable ex) {
                // Prepare failed
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                prefs.edit().putBoolean("enabled", false).apply();
            }

        } else
            ServiceSinkhole.stop("switch off", ActivityMain.this, false);
        // On/off switch


//        swEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });
        if (enabled)
            checkDoze();

        // Disabled warning
        TextView tvDisabled = findViewById(R.id.tvDisabled);
        tvDisabled.setVisibility(enabled ? View.GONE : View.VISIBLE);
        ServiceSinkhole.reload("changed notify", this, false);
        ServiceSinkhole.reload("changed filter", this, false);
        prefs.edit().putBoolean("filter", true).apply();
        prefs.edit().putBoolean("log_app", true).apply();
        prefs.edit().putBoolean("notify_access", true).apply();
        checkExtras(getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.i(TAG, "onActivityResult request=" + requestCode + " result=" + requestCode + " ok=" + (resultCode == RESULT_OK));
        Util.logExtras(data);

        if (requestCode == REQUEST_VPN) {
            // Handle VPN approval
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putBoolean("enabled", resultCode == RESULT_OK).apply();
            if (resultCode == RESULT_OK) {
                ServiceSinkhole.start("prepared", this);

                Toast on = Toast.makeText(ActivityMain.this, R.string.msg_on, Toast.LENGTH_LONG);
                on.setGravity(Gravity.CENTER, 0, 0);
                on.show();

                checkDoze();
            } else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, R.string.msg_vpn_cancelled, Toast.LENGTH_LONG).show();

        } else if (requestCode == REQUEST_INVITE) {
            // Do nothing

        } else if (requestCode == REQUEST_LOGCAT) {
            // Send logcat by e-mail
            if (resultCode == RESULT_OK) {
                Uri target = data.getData();
                if (data.hasExtra("org.openintents.extra.DIR_PATH"))
                    target = Uri.parse(target + "/logcat.txt");
                Log.i(TAG, "Export URI=" + target);
                Util.sendLogcat(target, this);
            }

        } else {
            Log.w(TAG, "Unknown activity result request=" + requestCode);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ROAMING)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ServiceSinkhole.reload("permission granted", this, false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String name) {
        Log.i(TAG, "Preference " + name + "=" + prefs.getAll().get(name));
        if ("enabled".equals(name)) {
            // Get enabled
            boolean enabled = prefs.getBoolean(name, false);

            // Display disabled warning
            TextView tvDisabled = findViewById(R.id.tvDisabled);
            tvDisabled.setVisibility(enabled ? View.GONE : View.VISIBLE);

            // Check switch state
            SwitchCompat swEnabled = findViewById(R.id.swEnabled);
            if (swEnabled.isChecked() != enabled)
                swEnabled.setChecked(enabled);

        } else if ("whitelist_wifi".equals(name) ||
                "screen_on".equals(name) ||
                "screen_wifi".equals(name) ||
                "whitelist_other".equals(name) ||
                "screen_other".equals(name) ||
                "whitelist_roaming".equals(name) ||
                "show_user".equals(name) ||
                "show_system".equals(name) ||
                "show_nointernet".equals(name) ||
                "show_disabled".equals(name) ||
                "sort".equals(name) ||
                "imported".equals(name)) {
//            updateApplicationList(null);

            final LinearLayout llWhitelist = findViewById(R.id.llWhitelist);
            boolean screen_on = prefs.getBoolean("screen_on", true);
            boolean whitelist_wifi = prefs.getBoolean("whitelist_wifi", false);
            boolean whitelist_other = prefs.getBoolean("whitelist_other", false);
            boolean hintWhitelist = prefs.getBoolean("hint_whitelist", true);
            llWhitelist.setVisibility(!(whitelist_wifi || whitelist_other) && screen_on && hintWhitelist ? View.VISIBLE : View.GONE);

        } else if ("manage_system".equals(name)) {
            invalidateOptionsMenu();
            LinearLayout llSystem = findViewById(R.id.llSystem);
            boolean system = prefs.getBoolean("manage_system", false);
            boolean hint = prefs.getBoolean("hint_system", true);
            llSystem.setVisibility(!system && hint ? View.VISIBLE : View.GONE);

        } else if ("theme".equals(name) || "dark_theme".equals(name))
            recreate();
    }

    private DatabaseHelper.AccessChangedListener accessChangedListener = new DatabaseHelper.AccessChangedListener() {
        @Override
        public void onChanged() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null && adapter.isLive())
                        adapter.notifyDataSetChanged();
                }
            });
        }
    };

    private void checkExtras(Intent intent) {
        // Approve request
        if (intent.hasExtra(EXTRA_APPROVE)) {
            Log.i(TAG, "Requesting VPN approval");
            swEnabled.toggle();
        }

        if (intent.hasExtra(EXTRA_LOGCAT)) {
            Log.i(TAG, "Requesting logcat");
            Intent logcat = getIntentLogcat();
            if (logcat.resolveActivity(getPackageManager()) != null)
                startActivityForResult(logcat, REQUEST_LOGCAT);
        }
    }

    private void checkDoze() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Intent doze = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            if (Util.batteryOptimizing(this) && getPackageManager().resolveActivity(doze, 0) != null) {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (!prefs.getBoolean("nodoze", false)) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View view = inflater.inflate(R.layout.doze, null, false);
                    final CheckBox cbDontAsk = view.findViewById(R.id.cbDontAsk);
                    dialogDoze = new AlertDialog.Builder(this)
                            .setView(view)
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prefs.edit().putBoolean("nodoze", cbDontAsk.isChecked()).apply();
                                    startActivity(doze);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prefs.edit().putBoolean("nodoze", cbDontAsk.isChecked()).apply();
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    dialogDoze = null;
                                    checkDataSaving();
                                }
                            })
                            .create();
                    dialogDoze.show();
                } else
                    checkDataSaving();
            } else
                checkDataSaving();
        }
    }

    private void checkDataSaving() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Intent settings = new Intent(
                    Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            if (Util.dataSaving(this) && getPackageManager().resolveActivity(settings, 0) != null)
                try {
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (!prefs.getBoolean("nodata", false)) {
                        LayoutInflater inflater = LayoutInflater.from(this);
                        View view = inflater.inflate(R.layout.datasaving, null, false);
                        final CheckBox cbDontAsk = view.findViewById(R.id.cbDontAsk);
                        dialogDoze = new AlertDialog.Builder(this)
                                .setView(view)
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        prefs.edit().putBoolean("nodata", cbDontAsk.isChecked()).apply();
                                        startActivity(settings);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        prefs.edit().putBoolean("nodata", cbDontAsk.isChecked()).apply();
                                    }
                                })
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        dialogDoze = null;
                                    }
                                })
                                .create();
                        dialogDoze.show();
                    }
                } catch (Throwable ex) {
                    Log.e(TAG, ex + "\n" + ex.getStackTrace());
                }
        }
    }

    private Intent getIntentLogcat() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (Util.isPackageInstalled("org.openintents.filemanager", this)) {
                intent = new Intent("org.openintents.action.PICK_DIRECTORY");
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=org.openintents.filemanager"));
            }
        } else {
            intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, "logcat.txt");
        }
        return intent;
    }

    private Emitter.Listener ketnoithanhcong = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("ketnoithanhcong");

        }
    };
    private Emitter.Listener ketnoiloi = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("ketnoiloi");

        }
    };
    private Emitter.Listener huyketnoi = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("huyketnoi");

        }
    };
    private Emitter.Listener thongbao = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("thongbao");

        }
    };
    private Emitter.Listener taothongtintruycaploi = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("taothongtintruycaploi");
            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            System.out.println(ten);

        }
    };
    private Emitter.Listener taothongtintruycapthanhcong = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("taothongtintruycapthanhcong");

        }
    };

    private Emitter.Listener yeuCauCapNhatBlackList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("YeucaucapnhatBlacklist");

            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            System.out.println(ten);
            String idds = "";
            String url = "";
            try {
                idds = ob.getString("danhSach");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject ttchan = ob.getJSONObject("thongTinChan");
                url = ttchan.getString("thongTin");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (url != "" && idds != "") {
//                dh.updateUrl(url, idds);
                Log.d("daluuroi", "ttttttttttttttttt");
            } else {
                Log.d("khongluudc", "mmmmmmmmmm");
            }
            SocketHandler.getSocket().emit("capNhatBlackListThanhCong", getMaDongBo());

        }
    };
    private Emitter.Listener yeuCauCapNhatThongTinApDung = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("yeuCauCapNhatThongTinApDung");

            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            System.out.println(ten);
            try {
                String loaiapdung = ob.getString("loaiApDung");
                if (loaiapdung.contains("ApDung")) {
                    String idds = ob.getString("danhSach");
                    String idkid = ob.getString("treEm");
                    if (idkid.contains(idtre)) {
                        if (!dh.CheckIsDataAlreadyInDBorNot("blacklist", "idbl", idds)) {
                            dh.updateBlackList(idds);
                        }
                    }
                } else if (loaiapdung.contains("BoApDung")) {
                    String idds = ob.getString("danhSach");
                    dh.deleteBlacklist(idds);
//                    dh.deleteBlacklistbyidbl(idds);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SocketHandler.getSocket().emit("capNhatThongTinApDungThanhCong");
        }
    };
    private Emitter.Listener capNhatThongTinApDungLoi = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("Capnhatthongtinapdungloi");

            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            System.out.println(ten);
        }
    };

    private Emitter.Listener capNhatThongTinApDungThanhCong = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("capNhatThongTinApDungThanhCong");

            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            ;
            System.out.println(ten);


        }
    };
    private Emitter.Listener xacThucDongBoBlackListThanhCong = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("xacThucDongBoBlackListThanhCong");

            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            ;
            System.out.println(ten);
        }
    };
    private Emitter.Listener xacThucDongBoBlackListLoi = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("xacThucDongBoBlackListLoi");
            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            ;
            System.out.println(ten);
        }
    };
    private Emitter.Listener thongTinCapNhatBlackList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("thongTinCapNhatBlackList");

            JSONObject ob = (JSONObject) args[0];
            String ten = ob.toString();
            ;
            try {
                JSONArray ttadbl = ob.getJSONArray("thongTinApDungBlackList");
                JSONArray ttchan = ob.getJSONArray("thongTinChan");
                if (ttadbl.length() != 0) {
                    for (int i = 0; i < ttadbl.length(); i++) {
                        String loaiad = ttadbl.getJSONObject(i).getString("loaiApDung");
                        String idkid = ttadbl.getJSONObject(i).getString("treEm");
                        if (loaiad.contains("ApDung")) {
                            if (idkid.contains(idtre)) {
                                String idds = ttadbl.getJSONObject(i).getString("danhSach");
                                if (!dh.CheckIsDataAlreadyInDBorNot("blacklist", "idbl", idds)) {
                                    dh.updateBlackList(idds);
                                }
                            }
                        } else if (loaiad.contains("BoApDung")) {
                            String idds = ttadbl.getJSONObject(i).getString("danhSach");
                            dh.deleteBlacklist(idds);
//                            dh.deleteBlacklistbyidbl(idds);
                        }
                    }
                }
                if (ttchan.length() != 0) {
                    for (int j = 0; j < ttchan.length(); j++) {
                        String loaicnchan = ttchan.getJSONObject(j).getString("loaiCapNhat");
                        String iddschan = ttchan.getJSONObject(j).getString("danhSach");
                        Cursor databl = dh.getBlacklist();
                        if (databl.getCount() == 0) {
                            Log.d("Khong co du lieu bl", "Khong co du lieu bl");
                        } else {
                            int idbl = databl.getColumnIndex("idbl");
                            while (databl.moveToNext()) {
                                String idds = databl.getString(idbl);
                                if (iddschan.contains(idds)) {
                                    String urlchan = ttchan.getJSONObject(j).getJSONObject("thongTinChan").getString("thongTin");
                                    if (loaicnchan.contains("Them")) {
                                        if (!dh.CheckIsBlAlreadyInURLorNot(idds, urlchan)) {
//                                            dh.updateUrl(urlchan, idds);
                                            dh.updateAccess(urlchan);
                                        }

                                    } else if (loaicnchan.contains("Xoa")) {
                                        if (dh.CheckIsDataAlreadyInDBorNot("url", "url", urlchan)) {
                                            dh.deleteUrl(urlchan);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(ten);
            SocketHandler.getSocket().emit("yeuCauXacThucDongBoBlackList", getMaDongBo());

        }
    };
    private Emitter.Listener xacThucThanhCong = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            DatabaseHelper dh = DatabaseHelper.getInstance(ActivityMain.this);
            System.out.println("xacThucThanhCong");

            String ten = (String) args[0];
            System.out.println(ten);
            dh.updatesync(ten);
            setMaDongBo(ten);
        }
    };
}