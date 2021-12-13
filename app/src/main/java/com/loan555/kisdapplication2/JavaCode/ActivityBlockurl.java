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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.loan555.kisdapplication2.JavaCode.Adapter.UrlAdapter;
import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.JavaCode.Model.Url;
import com.loan555.kisdapplication2.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityBlockurl extends AppCompatActivity implements UrlAdapter.ContactAdapter {
    private static final String TAG = "KA.AcBlockUrl";

    static RecyclerView blockurlRecyclerView;
    static UrlAdapter urlAdapter;
    ImageView btntacvu;
    TextView dschan;
    static TextView tvListEmpty;
    String idbl;
    static String idBl;
    String namebl;
    ImageView back;
    static List<Url> urls = new ArrayList<>();

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
                        loadUrl();
                    }
                }
            });

    public void openSomeActivityForResult() {
        Intent i = new Intent(ActivityBlockurl.this, ActivityAddUrl.class);
        i.putExtra("namebl", namebl);
        i.putExtra("idbl", idbl);
        i.putExtra("idBl", idBl);
        someActivityResultLauncher.launch(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockurl);
        btntacvu = findViewById(R.id.btnMoreToolbarCommon);
        dschan = findViewById(R.id.titlToolbarCommon);
        tvListEmpty = findViewById(R.id.listEmpty);
        Intent i = getIntent();
        idBl = i.getStringExtra("idBl");
        namebl = i.getStringExtra("namebl");
        dschan.setText(namebl);
        back = findViewById(R.id.btnBackToolbarCommon);
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
        blockurlRecyclerView = findViewById(R.id.recycler_blockurl);
        loadUrl();
    }



    public static void loadUrlOutSide() {
        urls.clear();
        DatabaseHelper dh = DatabaseHelper.getInstance(blockurlRecyclerView.getContext());
        Cursor datablacklist = dh.getBlacklist();
        Cursor dataurl = dh.getUrl(idBl);
        if (dataurl.getCount() == 0) {
            Log.d("check", "Không có dữ liệu url ! Bỏ qua!");
            tvListEmpty.setVisibility(View.VISIBLE);
        } else {
            tvListEmpty.setVisibility(View.INVISIBLE);
            int idurl = dataurl.getColumnIndex("ID");
            int _url = dataurl.getColumnIndex("url");
            int time = dataurl.getColumnIndex("time");
            int idbl = dataurl.getColumnIndex("idbl");
            int idchan = dataurl.getColumnIndex("idchan");
            while (dataurl.moveToNext()) {
                Url url = new Url();
                Log.i("data", String.format("idurl : %s", dataurl.getString(idurl)));
                Log.i("data", String.format("url : %s", dataurl.getString(_url)));
                Log.i("data", String.format("time : %s", dataurl.getString(time)));
                Log.i("data", String.format("idbl : %s", dataurl.getString(idbl)));
                Log.i("data", String.format("idchan : %s", dataurl.getString(idchan)));
                url.setIdurl(dataurl.getString(idurl));
                url.setUrl(dataurl.getString(_url));
                url.setTime(dataurl.getString(time));
                url.setIdbl(dataurl.getString(idbl));
                url.setIdthongtinchan(dataurl.getString(idchan));
                urls.add(url);
            }
        }
        // Update in Ui Thread
        ActivityBlockurl.runOnUI(new Runnable() {
            @Override
            public void run() {
                ActivityBlockurl.urlAdapter.notifyDataSetChanged();
                Toast.makeText(ActivityBlockurl.blockurlRecyclerView.getContext(), "Cập nhật domain thành công", 2000);
            }
        });
    }



    private void loadUrl() {
        urls.clear();
        DatabaseHelper dh = DatabaseHelper.getInstance(ActivityBlockurl.this);
        Cursor dataurl = dh.getUrl(idBl);
        if (dataurl.getCount() == 0) {
            Log.d("check", "Không có dữ liệu url ! Bỏ qua!");
            tvListEmpty.setVisibility(View.VISIBLE);
        } else {
            tvListEmpty.setVisibility(View.INVISIBLE);
            int idurl = dataurl.getColumnIndex("ID");
            int _url = dataurl.getColumnIndex("url");
            int time = dataurl.getColumnIndex("time");
            int idbl = dataurl.getColumnIndex("idbl");
            int idchan = dataurl.getColumnIndex("idchan");
            while (dataurl.moveToNext()) {
                Url url = new Url();
                Log.d("debug" , "load url inside");
                Log.i("data", String.format("idurl : %s", dataurl.getString(idurl)));
                Log.i("data", String.format("url : %s", dataurl.getString(_url)));
                Log.i("data", String.format("time : %s", dataurl.getString(time)));
                Log.i("data", String.format("idbl : %s", dataurl.getString(idbl)));
                Log.i("data", String.format("idchan : %s", dataurl.getString(idchan)));
                if(dataurl.getString(idchan).equals(""))
                {
                    continue;
                }
                url.setIdurl(dataurl.getString(idurl));
                url.setUrl(dataurl.getString(_url));
                url.setTime(dataurl.getString(time));
                url.setIdbl(dataurl.getString(idbl));
                url.setIdthongtinchan(dataurl.getString(idchan));
                urls.add(url);
            }
        }
        setUrlRecycler(urls);
    }

    private void setUrlRecycler(List<Url> urlDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ActivityBlockurl.this, LinearLayoutManager.VERTICAL, false);
        blockurlRecyclerView.setLayoutManager(layoutManager);
        urlAdapter = new UrlAdapter(ActivityBlockurl.this, urlDataList, this::checkListEmpty);
        blockurlRecyclerView.setAdapter(urlAdapter);
        urlAdapter.notifyDataSetChanged();
    }

    @Override
    public void checkListEmpty(Boolean isEmpty) {
        if (isEmpty) tvListEmpty.setVisibility(View.VISIBLE);
        else tvListEmpty.setVisibility(View.INVISIBLE);
    }
}