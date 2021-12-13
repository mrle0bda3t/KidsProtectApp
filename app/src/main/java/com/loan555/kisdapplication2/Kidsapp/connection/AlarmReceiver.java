package com.loan555.kisdapplication2.Kidsapp.connection;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.Kidsapp.DatabaseHelper;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dh = DatabaseHelper.getInstanceNew(context);
        int block = intent.getIntExtra("block",-1);
        String nameapp = intent.getStringExtra("nameapp");
        dh.updateBlockInApp(nameapp,block);
        Log.d("infor", "Giá trị của block: "+String.valueOf(block));
        ServiceSinkhole.stop("switch off", context, false);
        ServiceSinkhole.reload("changed notify", context, false);
        ServiceSinkhole.reload("changed filter", context, false);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("filter", true).apply();
        prefs.edit().putBoolean("log_app", true).apply();
        prefs.edit().putBoolean("notify_access", true).apply();
    }

}
