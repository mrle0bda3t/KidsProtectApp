package com.loan555.kisdapplication2.JavaCode;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loan555.kisdapplication2.JavaCode.Adapter.BlacklistAdapter;
import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityBlacklist extends AppCompatActivity implements BlacklistAdapter.ContactAdapter {
    private static final String TAG = "KA.AcBlacklist";
    public static RecyclerView blacklistRecyclerView;
    public static BlacklistAdapter blacklistAdapter;
    ImageView btntacvu;
    ImageView back;
    TextView title;
    TextView tvListEmpty;
    public static List<Blacklist> blacklists = new ArrayList<>();

    public static Handler UIHandler;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        loadBlacklists();
                    }
                }
            });

    public void openSomeActivityForResult() {
        Intent intent = new Intent(this, ActivityAddBlackList.class);
        someActivityResultLauncher.launch(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        blacklistRecyclerView = findViewById(R.id.recycler_blacklist);
        tvListEmpty = findViewById(R.id.listEmpty);
        btntacvu = findViewById(R.id.btnMoreToolbarCommon);
        back = findViewById(R.id.btnBackToolbarCommon);
        title = findViewById(R.id.titlToolbarCommon);
        title.setText(R.string.danhSachChan);
        btntacvu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSomeActivityForResult();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadBlacklists();
    }

    public void loadBlacklists() {
        blacklists.clear();
        DatabaseHelper dh = DatabaseHelper.getInstance(blacklistRecyclerView.getContext());

        Cursor datablacklist = dh.getBlacklist();
        if (datablacklist.getCount() == 0) {
            Log.d("data", "Không có dữ liệu blackList");
            tvListEmpty.setVisibility(View.VISIBLE);
        } else {
            tvListEmpty.setVisibility(View.INVISIBLE);
            int idbl = datablacklist.getColumnIndex("ID");
            int idBl = datablacklist.getColumnIndex("idbl");
            int namebl = datablacklist.getColumnIndex("namebl");
            int typebl = datablacklist.getColumnIndex("typebl");
            while (datablacklist.moveToNext()) {
                if (!datablacklist.getString(idBl).equals("")) {
                    Blacklist bl = new Blacklist();
                    bl.setIdbl(datablacklist.getString(idBl));
                    bl.setIdBl(datablacklist.getString(idbl));
                    bl.setNameBl(datablacklist.getString(namebl));
                    bl.setTypeBl(datablacklist.getString(typebl));
                    blacklists.add(bl);
                    Log.d("data", "Thông tin blackList: " + bl.getNameBl() + "/" + bl.getIdBl() + "/" + bl.getTypeBl());
                }
            }
            setBlacklistRecycler(blacklists);
        }
    }

    public static void MakeToastOutSide(String toastString) {
        // Update in Ui Thread
        ActivityBlacklist.runOnUI(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActivityBlacklist.blacklistRecyclerView.getContext(), toastString, 2000);
            }
        });
    }

    public static void loadBlacklistsOutSide() {
        blacklists.clear();
        DatabaseHelper dh = DatabaseHelper.getInstance(blacklistRecyclerView.getContext());
        Cursor datablacklist = dh.getBlacklist();
        if (datablacklist.getCount() == 0) {
            Log.d("data", "Không có dữ liệu blackList");
        } else {
            int idbl = datablacklist.getColumnIndex("ID");
            int idBl = datablacklist.getColumnIndex("idbl");
            int namebl = datablacklist.getColumnIndex("namebl");
            int typebl = datablacklist.getColumnIndex("typebl");
            while (datablacklist.moveToNext()) {
                if (!datablacklist.getString(idBl).equals("")) {
                    Blacklist bl = new Blacklist();
                    bl.setIdbl(datablacklist.getString(idBl));
                    bl.setIdBl(datablacklist.getString(idbl));
                    bl.setNameBl(datablacklist.getString(namebl));
                    bl.setTypeBl(datablacklist.getString(typebl));
                    blacklists.add(bl);
                    Log.d("data", "Thông tin blackList: " + bl.getNameBl() + "/" + bl.getIdBl() + "/" + bl.getTypeBl());
                }
            }
        }
        // Update in Ui Thread
        ActivityBlacklist.runOnUI(new Runnable() {
            @Override
            public void run() {
                ActivityBlacklist.blacklistAdapter.notifyDataSetChanged();
                Toast.makeText(ActivityBlacklist.blacklistRecyclerView.getContext(), "Cập nhật blacklist thành công", 2000);
            }
        });
    }

    public void setBlacklistRecycler(List<Blacklist> BlacklistDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(blacklistRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        blacklistRecyclerView.setLayoutManager(layoutManager);
        blacklistAdapter = new BlacklistAdapter(this, BlacklistDataList, this::checkListEmpty);
        blacklistRecyclerView.setAdapter(blacklistAdapter);
        blacklistAdapter.notifyDataSetChanged();
    }

    @Override
    public void checkListEmpty(Boolean isEmpty) {
        if (isEmpty) tvListEmpty.setVisibility(View.VISIBLE);
        else tvListEmpty.setVisibility(View.INVISIBLE);
    }
}