package com.loan555.kisdapplication2.JavaCode;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "KA.DatabaseHelper";

    private static final String DB_NAME = "ParentsAppProtect";
    private static final int DB_VERSION = 21;

    private static boolean once = true;
    private static List<LogChangedListener> logChangedListeners = new ArrayList<>();
    private static List<AccessChangedListener> accessChangedListeners = new ArrayList<>();
    private static List<ForwardChangedListener> forwardChangedListeners = new ArrayList<>();

    private static HandlerThread hthread = null;
    private static Handler handler = null;

    private static final Map<Integer, Long> mapUidHosts = new HashMap<>();

    private final static int MSG_LOG = 1;
    private final static int MSG_ACCESS = 2;
    private final static int MSG_FORWARD = 3;

    private SharedPreferences prefs;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    static {
        hthread = new HandlerThread("DatabaseHelper");
        hthread.start();
        handler = new Handler(hthread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                handleChangedNotification(msg);
            }
        };
    }

    private static DatabaseHelper dh = null;

    public static DatabaseHelper getInstance(Context context) {
        if (dh == null)
            dh = new DatabaseHelper(context.getApplicationContext());
        return dh;
    }
    public static DatabaseHelper getInstanceNew(Context context) {
        dh = new DatabaseHelper(context.getApplicationContext());
        return dh;
    }
    public static void clearCache() {
        synchronized (mapUidHosts) {
            mapUidHosts.clear();
        }
    }

    @Override
    public void close() {
        Log.w(TAG, "Database is being closed");
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (!once) {
            once = true;

            File dbfile = context.getDatabasePath(DB_NAME);
            if (dbfile.exists()) {
                Log.w(TAG, "Deleting " + dbfile);
                dbfile.delete();
            }

            File dbjournal = context.getDatabasePath(DB_NAME + "-journal");
            if (dbjournal.exists()) {
                Log.w(TAG, "Deleting " + dbjournal);
                dbjournal.delete();
            }
            File dbshm = context.getDatabasePath(DB_NAME + "-shm");
            if (dbshm.exists()) {
                Log.w(TAG, "Deleting " + dbshm);
                dbshm.delete();
            }
            File dbwal = context.getDatabasePath(DB_NAME + "-wal");
            if (dbwal.exists()) {
                Log.w(TAG, "Deleting " + dbwal);
                dbwal.delete();
            }
        }
    }
    public void deleteDB(String DB_NAME, Context context){
        File dbfile = context.getDatabasePath(DB_NAME);
        if (dbfile.exists()) {
            Log.w(TAG, "Deleting " + dbfile);
            dbfile.delete();
        }

        File dbjournal = context.getDatabasePath(DB_NAME + "-journal");
        if (dbjournal.exists()) {
            Log.w(TAG, "Deleting " + dbjournal);
            dbjournal.delete();
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database " + DB_NAME + " version " + DB_VERSION);
        createTableHistory(db);
        createTableKid(db);
        createTableBlackList(db);
        createTableIdSync(db);
        createTableIdBlSync(db);
        createTableUrl(db);
        createTableApplyBl(db);
        createTableApp(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.enableWriteAheadLogging();
        super.onConfigure(db);
    }
    private void createTableHistory(SQLiteDatabase db){
        Log.i(TAG,"Creating history table");
        db.execSQL("CREATE TABLE history (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", idkid TEXT NOT NULL" +
                ", daddr TEXT" +
                ", time TEXT" +
                ", status TEXT" +
                ", nameapp TEXT" +
                ", timeLong LONG"+
                ", timeStr TEXT"+
                ");");
    }
    private void createTableKid(SQLiteDatabase db){
        Log.i(TAG,"Creating kid table");
        db.execSQL("CREATE TABLE kid (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", idkid TEXT NOT NULL" +
                ", namekid TEXT NOT NULL" +
                ", anhChanDung TEXT NOT NULL" +
                ");");
    }
    private void createTableBlackList(SQLiteDatabase db){
        Log.i(TAG,"Creating Blacklist table");
        db.execSQL("CREATE TABLE blacklist (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", idbl TEXT" +
                ", namebl TEXT NOT NULL" +
                ", typebl TEXT " +
                ");");
    }
    private void createTableIdSync(SQLiteDatabase db){
        Log.i(TAG,"Creating Sync table");
        db.execSQL("CREATE TABLE sync (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", idsync TEXT NOT NULL" +
                ");");
    }
    private void createTableIdBlSync(SQLiteDatabase db){
        Log.i(TAG,"Creating BlSync table");
        db.execSQL("CREATE TABLE blsync (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", idblsync TEXT NOT NULL" +
                ");");
    }
    private void createTableUrl(SQLiteDatabase db){
        Log.i(TAG,"Creating Url table");
        db.execSQL("CREATE TABLE url (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", idchan TEXT" +
                ", url TEXT NOT NULL" +
                ", time TEXT NOT NULL" +
                ", idbl TEXT NOT NULL" +
                ");");
    }
    private void createTableApplyBl(SQLiteDatabase db){
        Log.i(TAG,"Creating ApplyBl table");
        db.execSQL("CREATE TABLE applybl (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", idbl TEXT NOT NULL" +
                ", idkid TEXT NOT NULL" +
                ", time TEXT NOT NULL" +
                ", typeapply TEXT" +
                ");");
    }
    private void createTableApp(SQLiteDatabase db){
        Log.i(TAG,"Creating app table");
        db.execSQL("CREATE TABLE app (" +
                " ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", nameapp TEXT NOT NULL" +
                ", timeStart TEXT NOT NULL" +
                ", timeEnd TEXT NOT NULL" +
                ", idkid TEXT" +
                ", activate INTERGER NOT NULL" +
                ", idapp TEXT" +
                ");");
    }

    private boolean columnExists(SQLiteDatabase db, String table, String column) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + table + " LIMIT 0", null);
            return (cursor.getColumnIndex(column) >= 0);
        } catch (Throwable ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, DB_NAME + " upgrading from version " + oldVersion + " to " + newVersion);

        db.beginTransaction();
        try {
            if (oldVersion < 2) {
                if (!columnExists(db, "log", "version"))
                    db.execSQL("ALTER TABLE log ADD COLUMN version INTEGER");
                if (!columnExists(db, "log", "protocol"))
                    db.execSQL("ALTER TABLE log ADD COLUMN protocol INTEGER");
                if (!columnExists(db, "log", "uid"))
                    db.execSQL("ALTER TABLE log ADD COLUMN uid INTEGER");
                oldVersion = 2;
            }
            if (oldVersion < 3) {
                if (!columnExists(db, "log", "port"))
                    db.execSQL("ALTER TABLE log ADD COLUMN port INTEGER");
                if (!columnExists(db, "log", "flags"))
                    db.execSQL("ALTER TABLE log ADD COLUMN flags TEXT");
                oldVersion = 3;
            }
            if (oldVersion < 4) {
                if (!columnExists(db, "log", "connection"))
                    db.execSQL("ALTER TABLE log ADD COLUMN connection INTEGER");
                oldVersion = 4;
            }
            if (oldVersion < 5) {
                if (!columnExists(db, "log", "interactive"))
                    db.execSQL("ALTER TABLE log ADD COLUMN interactive INTEGER");
                oldVersion = 5;
            }
            if (oldVersion < 6) {
                if (!columnExists(db, "log", "allowed"))
                    db.execSQL("ALTER TABLE log ADD COLUMN allowed INTEGER");
                oldVersion = 6;
            }
            if (oldVersion < 7) {
                db.execSQL("DROP TABLE log");
                oldVersion = 8;
            }
            if (oldVersion < 8) {
                if (!columnExists(db, "log", "data"))
                    db.execSQL("ALTER TABLE log ADD COLUMN data TEXT");
                db.execSQL("DROP INDEX idx_log_source");
                db.execSQL("DROP INDEX idx_log_dest");
                db.execSQL("CREATE INDEX idx_log_source ON log(saddr)");
                db.execSQL("CREATE INDEX idx_log_dest ON log(daddr)");
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_log_uid ON log(uid)");
                oldVersion = 8;
            }
            if (oldVersion < 9) {
                oldVersion = 9;
            }
            if (oldVersion < 10) {
                db.execSQL("DROP TABLE log");
                db.execSQL("DROP TABLE access");
                oldVersion = 10;
            }
            if (oldVersion < 12) {
                db.execSQL("DROP TABLE access");
                oldVersion = 12;
            }
            if (oldVersion < 13) {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_log_dport ON log(dport)");
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_log_dname ON log(dname)");
                oldVersion = 13;
            }
            if (oldVersion < 14) {
                oldVersion = 14;
            }
            if (oldVersion < 15) {
                db.execSQL("DROP TABLE access");
                oldVersion = 15;
            }
            if (oldVersion < 16) {
                oldVersion = 16;
            }
            if (oldVersion < 17) {
                if (!columnExists(db, "access", "sent"))
                    db.execSQL("ALTER TABLE access ADD COLUMN sent INTEGER");
                if (!columnExists(db, "access", "received"))
                    db.execSQL("ALTER TABLE access ADD COLUMN received INTEGER");
                oldVersion = 17;
            }
            if (oldVersion < 18) {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_access_block ON access(block)");
                db.execSQL("DROP INDEX idx_dns");
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_dns ON dns(qname, aname, resource)");
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_dns_resource ON dns(resource)");
                oldVersion = 18;
            }
            if (oldVersion < 19) {
                if (!columnExists(db, "access", "connections"))
                    db.execSQL("ALTER TABLE access ADD COLUMN connections INTEGER");
                oldVersion = 19;
            }
            if (oldVersion < 20) {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_access_daddr ON access(daddr)");
                oldVersion = 20;
            }
            if (oldVersion < 21) {
                oldVersion = 21;
            }

            if (oldVersion == DB_VERSION) {
                db.setVersion(oldVersion);
                db.setTransactionSuccessful();
                Log.i(TAG, DB_NAME + " upgraded to " + DB_VERSION);
            } else
                throw new IllegalArgumentException(DB_NAME + " upgraded to " + oldVersion + " but required " + DB_VERSION);

        } catch (Throwable ex) {
            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
        } finally {
            db.endTransaction();
        }
    }
    public void updateblacklistsync(String idblsync){
        int rows = 0;
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("idblsync", idblsync);
                rows = db.update("blsync", cv, "ID = 1",null);
                if(rows == 0){
                    Log.d("nnnnnnnnnn","nnnnnnnnnnnnn");
                    if (db.insert("blsync", null, cv) == -1)
                        Log.e(TAG, "Insert access failed");
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updatesync(String idsync){
        int rows = 0;
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("idsync", idsync);
                rows = db.update("sync", cv, "ID = 1",null);
                if(rows == 0){
                    Log.d("llllllllllllllllll","llllllllllllllllllllllll");
                    if (db.insert("sync", null, cv) == -1)
                        Log.e(TAG, "Insert access failed");
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateidbl(String idbl, String namebl){
        int rows = 0;
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("idbl", idbl);
                rows = db.update("blacklist", cv, "namebl = ?",new String[]{namebl});
                if(rows == 0){
                    if (db.insert("sync", null, cv) == -1)
                        Log.e(TAG, "Insert access failed");
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    //    public void updateUrlbyidbl(String idbl, String url){
//        int rows = 0;
//        lock.writeLock().lock();
//        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//            db.beginTransactionNonExclusive();
//            try {
//                ContentValues cv = new ContentValues();
//                cv.put("url", url);
//                rows = db.update("url", cv, "namebl = ?",new String[]{namebl});
//                if(rows == 0){
//                    if (db.insert("sync", null, cv) == -1)
//                        Log.e(TAG, "Insert access failed");
//                }
//                db.setTransactionSuccessful();
//            } finally {
//                db.endTransaction();
//            }
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
    public void updateUrlByidbl(String url, String idbl,String idchan){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("idchan", idchan);
                db.update("url",cv, "url = ? AND idbl = ?",new String[]{url,idbl});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateTimeApp(String idkid, String nameApp,String timeStart, String timeEnd, Integer activate){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("timeStart", timeStart);
                cv.put("timeEnd", timeEnd);
                cv.put("activate", activate);
                db.update("app",cv, "idkid = ? AND nameapp = ?",new String[]{idkid,nameApp});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateIdApp(String idkid, String nameApp,String idapp, String timeStart,String timeEnd){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("idapp", idapp);
                cv.put("timeStart", timeStart);
                cv.put("timeEnd", timeEnd);
                db.update("app",cv, "idkid = ? AND nameapp = ?",new String[]{idkid,nameApp});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateOneTimeApp(String idkid, String nameApp,String time, Integer type){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                if(type==0){
                    cv.put("timeStart", time);
                }
                else if(type==1){
                    cv.put("timeEnd", time);
                }
                db.update("app",cv, "idkid = ? AND nameapp = ?",new String[]{idkid,nameApp});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateTimeNotActivate(String idkid, String nameApp,String timeStart, String timeEnd){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("timeStart", timeStart);
                cv.put("timeEnd", timeEnd);

                db.update("app",cv, "idkid = ? AND nameapp = ?",new String[]{idkid,nameApp});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateUrl(String url,String time,String idchan, String idbl){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("url", url);
                cv.put("time", time);
                cv.put("idchan", idchan);
                cv.put("idbl", idbl);
                if (db.insert("url", null, cv) == -1)
                    Log.e(TAG, "Insert access failed");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateBlackList(String namebl, String typebl, String idbl){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("idbl", idbl);
                cv.put("namebl", namebl);
                cv.put("typebl", typebl);
                if (db.insert("blacklist", null, cv) == -1)
                    Log.e(TAG, "Insert access failed");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateKids(String idkid, String namekid, String anhChanDung){
        if(!CheckIsDataAlreadyInDBorNot("kid","idkid",idkid)){
            lock.writeLock().lock();
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.beginTransactionNonExclusive();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("idkid", idkid);
                    cv.put("namekid", namekid);
                    cv.put("anhChanDung", anhChanDung);
                    if (db.insert("kid", null, cv) == -1)
                        Log.e(TAG, "Insert access failed");
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                lock.writeLock().unlock();
            }
            notifyLogChanged();
        }

    }
    public void updateApplybl(String idbl, String idkid, String time){
        lock.writeLock().lock();
        if(!CheckIsDataAlreadyInDBorNot("applybl","idbl",idbl)){
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.beginTransactionNonExclusive();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("time", time);
                    cv.put("idbl", idbl);
                    cv.put("idkid", idkid);
                    if (db.insert("applybl", null, cv) == -1)
                        Log.e(TAG, "Insert access failed");
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        else{
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.beginTransactionNonExclusive();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("time", time);
                    if (db.update("applybl",cv, "idbl=? AND idkid=?", new String[]{idbl,idkid}) == -1)
                        Log.e(TAG, "Insert access failed");
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        notifyLogChanged();
    }
    public void updateActivateApp(Integer activate, String idkid, String nameapp){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("activate", activate);
                db.update("app", cv, "idkid = ? AND nameapp = ?",new String[]{idkid,nameapp});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public void updateApp(String nameapp, String idkid, String timeStart, String timeEnd, int activate, String idapp){
        lock.writeLock().lock();
        if(!CheckIsAppAlreadyIDBorNot("app",nameapp,idkid)) {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.beginTransactionNonExclusive();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("nameapp", nameapp);
                    cv.put("idkid", idkid);
                    cv.put("timeStart", timeStart);
                    cv.put("timeEnd", timeEnd);
                    cv.put("activate", activate);
                    cv.put("idapp", idapp);
                    if (db.insert("app", null, cv) == -1)
                        Log.e(TAG, "Insert access failed");
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        else{

            try {
                SQLiteDatabase db = this.getWritableDatabase();
                db.beginTransactionNonExclusive();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("timeStart", timeStart);
                    cv.put("timeEnd", timeEnd);
                    cv.put("activate", activate);
                    if (db.update("app", cv,"nameapp = ? AND idkid = ?",new String[]{nameapp,idkid} ) == -1)
                        Log.e(TAG, "Insert access failed");
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        notifyLogChanged();
    }

    // History
    public void updateHistory(String dname, String time, String idkid, String status, String tenapp, Long timelong, String timeStr){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("time", time);
                cv.put("daddr", dname);
                cv.put("idkid", idkid);
                cv.put("status", status);
                cv.put("nameapp",tenapp);
                cv.put("timeLong",timelong);
                cv.put("timeStr",timeStr);
                if (db.insert("history", null, cv) == -1)
                    Log.e(TAG, "Insert access failed");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
        notifyLogChanged();
    }
    public Cursor getSync(){
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query("sync", null, "ID = 1", null, null, null, null);
        } finally {
            lock.readLock().unlock();
        }
    }
    public Cursor getBlSync(){
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query("blsync", null, "ID = 1", null, null, null, null);
        } finally {
            lock.readLock().unlock();
        }
    }
    public Cursor getKidbyId(String idkid){
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query("kid", null, "idkid = ?", new String[]{idkid}, null, null, null);
        } finally {
            lock.readLock().unlock();
        }
    }
    public Cursor getKid(){
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query("kid", null, null, null, null, null, null);
        } finally {
            lock.readLock().unlock();
        }
    }
    public Cursor getBlacklist(){
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query("blacklist", null, null, null, null, null, null);
        } finally {
            lock.readLock().unlock();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Cursor getAppChart(String idkid) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor data = db.rawQuery("SELECT nameapp, COUNT(nameapp) AS soluong FROM " + "history" + " where idkid = '" + idkid + "'GROUP BY nameapp ORDER BY soluong DESC LIMIT 5", null);
            return data;

        }
        catch (Exception e){
            Log.d("Lỗi","Get data đồ thị trong lỗi");
            return null;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Cursor getblockUrlChart(String idkid, Integer typeOfHistory, Integer typeOfFilter){
        SQLiteDatabase db = this.getWritableDatabase();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        Long timecur = dateToLong(dtf.format(now),"yyyy-MM-dd'T'HH:mm:ss.SSS");
        Long timecompare = 0L;
        if(typeOfFilter==0){
            timecompare = timecur - 1L*115740741L; // Lọc trong 1 ngày
        }
        else if(typeOfFilter==1){
            timecompare = timecur - 7L*115740741L; // Lọc trong 7 ngày
        }
        else if(typeOfFilter==2){
            timecompare = timecur - 30L*115740741L; // Lọc trong 1 tháng
        }
        else{
            timecompare = timecur - 90L*115740741L; // Lọc trong 3 tháng
        }
        try {
            if(typeOfHistory==0){
                Cursor data = db.rawQuery("SELECT status, timeStr,timeLong, COUNT(status) AS soluong FROM " + "history" + " where idkid = '" + idkid + "' and timeLong > "+timecompare+" and status = 'KetNoi' GROUP BY timeStr ORDER BY timeStr ASC", null);
                return data;
            }
            else{
                Cursor data = db.rawQuery("SELECT status, timeStr,timeLong, COUNT(status) AS soluong FROM " + "history" + " where idkid = '" + idkid + "' and timeLong > "+timecompare+" and status = 'DaChan' GROUP BY timeStr ORDER BY timeStr ASC", null);
                return data;
            }
        }
        catch (Exception e){
            Log.d("lỗi truy vấn dữ liệu","Không get được dữ liệu lịch sử chặn, truy cập");
            return null;
        }
    }
    public Cursor getAppBl(String idkid){
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query("app", null, "idkid = ?", new String[]{idkid}, null, null, null);
        } finally {
            lock.readLock().unlock();
        }
    }
    public static long dateToLong(String date, String fomat) {
        long milliseconds = -1;
        SimpleDateFormat f = new SimpleDateFormat(fomat);
        f.setTimeZone(TimeZone.getDefault());
        try {
            Date d = f.parse(date);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
    public Cursor getBlacklist(String idbl){
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.query("blacklist", null, "ID=?", new String[]{idbl}, null, null, null);
        } finally {
            lock.readLock().unlock();
        }
    }
    public Cursor getHistory(String idkid) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // There is a segmented index on uid
            // There is an index on block
            return db.query("history", null, "idkid=?", new String[]{idkid}, null, null, "time DESC");
//            return db.query("history", null, "idkid="+"\""+idkid+"\"", null, null, null, "time");
        } finally {
            lock.readLock().unlock();
        }
    }
    public Cursor getUrl(String idbl) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // There is a segmented index on uid
            // There is an index on block
            return db.query("url", null, "idbl=?",new String[]{idbl}, null, null, "time DESC");
//            return db.query("history", null, "idkid="+"\""+idkid+"\"", null, null, null, "time");
        } finally {
            lock.readLock().unlock();
        }

    }
    public Cursor getApplybl(String idkid) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // There is a segmented index on uid
            // There is an index on block
            return db.query("applybl", null, "idkid=?",new String[]{idkid}, null, null, "time DESC");
//            return db.query("history", null, "idkid="+"\""+idkid+"\"", null, null, null, "time");
        } finally {
            lock.readLock().unlock();
        }
    }
    //    public Cursor getBlacklist() {
//        lock.readLock().lock();
//        try {
//            SQLiteDatabase db = this.getReadableDatabase();
//            // There is a segmented index on uid
//            // There is an index on block
//            return db.query("blacklist", null, null, null, null, null, "time");
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
    public void deleteKid(String idkid){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                db.delete("kid", "idkid = ?", new String[]{idkid});

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            db.execSQL("VACUUM");
        } finally {
            lock.writeLock().unlock();
        }

        notifyLogChanged();
    }
    public void deleteApplybl(String idkid, String idbl){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                db.delete("applybl", "idkid = ? AND idbl = ?", new String[]{idkid,idbl});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            db.execSQL("VACUUM");
        } finally {
            lock.writeLock().unlock();
        }

        notifyLogChanged();
    }
    public void deleteUrl(String idurl){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                db.delete("url", "ID = ?", new String[]{idurl});

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            db.execSQL("VACUUM");
        } finally {
            lock.writeLock().unlock();
        }

        notifyLogChanged();
    }
    public void deleteBlacklist(String id){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                db.delete("blacklist", "ID = ?", new String[]{id});

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            db.execSQL("VACUUM");
        } finally {
            lock.writeLock().unlock();
        }

        notifyLogChanged();
    }
    public void deleteBlacklistbyId(String idbl){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                db.delete("blacklist", "idbl = ?", new String[]{idbl});

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            db.execSQL("VACUUM");
        } finally {
            lock.writeLock().unlock();
        }

        notifyLogChanged();
    }
    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield, String fieldValue) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TableName, null, dbfield+ "= "+"\""+fieldValue+"\"", null, null, null, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }
    public boolean CheckIsAppInDB(String idkid, String nameapp) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query("app", null, "nameapp=? AND idkid = ?", new String[]{nameapp,idkid}, null, null, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }
    public boolean CheckIsUrlAlreadyInUrlorNot( String table,String idbl, String url) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(table, null, "idbl=? AND url=?", new String[]{idbl,url}, null, null, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    public boolean CheckIsBlAlreadyInApplyblorNot( String table,String idbl, String idkid) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(table, null, "idbl=? AND idkid=?", new String[]{idbl,idkid}, null, null, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }
    public boolean CheckIsAppAlreadyIDBorNot( String table,String tenApp, String idkid) {
        lock.readLock().lock();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(table, null, "nameapp=? AND idkid=?", new String[]{tenApp,idkid}, null, null, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }
    public void deleteHistory(String url){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                db.delete("history", "daddr = ?", new String[]{url});

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            db.execSQL("VACUUM");
        } finally {
            lock.writeLock().unlock();
        }

        notifyLogChanged();
    }
    public void ClearDb() {
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                db.delete("history", null, null);
                db.delete("kid", null, null);
                db.delete("blacklist", null, null);
                db.delete("sync", null, null);
                db.delete("blsync", null, null);
                db.delete("url", null, null);
                db.delete("applybl", null, null);
                db.delete("app", null, null);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }

        notifyAccessChanged();
    }
    public void changeStatus(String url, String status){
        lock.writeLock().lock();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                ContentValues cv = new ContentValues();
                cv.put("status", status);
                // There is a segmented index on uid, version, protocol, daddr and dport
                db.update("history", cv, "daddr = ?",
                        new String[]{url});

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    private void notifyLogChanged() {
        Message msg = handler.obtainMessage();
        msg.what = MSG_LOG;
        handler.sendMessage(msg);
    }

    private void notifyAccessChanged() {
        Message msg = handler.obtainMessage();
        msg.what = MSG_ACCESS;
        handler.sendMessage(msg);
    }

    private void notifyForwardChanged() {
        Message msg = handler.obtainMessage();
        msg.what = MSG_FORWARD;
        handler.sendMessage(msg);
    }

    private static void handleChangedNotification(Message msg) {
        // Batch notifications
        try {
            Thread.sleep(1000);
            if (handler.hasMessages(msg.what))
                handler.removeMessages(msg.what);
        } catch (InterruptedException ignored) {
        }

        // Notify listeners
        if (msg.what == MSG_LOG) {
            for (LogChangedListener listener : logChangedListeners)
                try {
                    listener.onChanged();
                } catch (Throwable ex) {
                    Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                }

        } else if (msg.what == MSG_ACCESS) {
            for (AccessChangedListener listener : accessChangedListeners)
                try {
                    listener.onChanged();
                } catch (Throwable ex) {
                    Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                }

        } else if (msg.what == MSG_FORWARD) {
            for (ForwardChangedListener listener : forwardChangedListeners)
                try {
                    listener.onChanged();
                } catch (Throwable ex) {
                    Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                }
        }
    }

    public interface LogChangedListener {
        void onChanged();
    }

    public interface AccessChangedListener {
        void onChanged();
    }

    public interface ForwardChangedListener {
        void onChanged();
    }
}
