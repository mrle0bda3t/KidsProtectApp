package com.loan555.kisdapplication2.Kidsapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.loan555.kisdapplication2.R;

public class WidgetLockdown extends AppWidgetProvider {
    private static final String TAG = "NetGuard.WidgetLock";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        update(appWidgetIds, appWidgetManager, context);
    }

    private static void update(int[] appWidgetIds, AppWidgetManager appWidgetManager, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean lockdown = prefs.getBoolean("lockdown", false);

        try {
            try {
                Intent intent = new Intent(lockdown ? WidgetAdmin.INTENT_LOCKDOWN_OFF : WidgetAdmin.INTENT_LOCKDOWN_ON);
                intent.setPackage(context.getPackageName());
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                for (int id : appWidgetIds) {
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetlockdown);
                    views.setOnClickPendingIntent(R.id.ivEnabled, pi);
                    views.setImageViewResource(R.id.ivEnabled, lockdown ? R.drawable.ic_baseline_account_circle_24 : R.drawable.ic_baseline_add_24);
                    appWidgetManager.updateAppWidget(id, views);
                }
            } catch (Throwable ex) {
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            }
        } catch (Throwable ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
        }
    }

    public static void updateWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetLockdown.class));
        update(appWidgetIds, appWidgetManager, context);
    }
}
