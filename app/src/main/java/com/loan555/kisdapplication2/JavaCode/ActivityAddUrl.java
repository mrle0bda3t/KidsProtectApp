package com.loan555.kisdapplication2.JavaCode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.R;
import com.loan555.kisdapplication2.constant.EventTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityAddUrl extends AppCompatActivity {
    private static final String TAG = "KA.AcAddUrl";
    ImageView btn_them;
    TextView btn_ok;
    TextView title;
    LinearLayout addurl;
    String idbl;
    String idBl;
    String namebl;
    List<EditText> listurl = new ArrayList<>();
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_url);
        addurl = findViewById(R.id.addurlview);
        btn_ok = findViewById(R.id.btn_ok);
        btn_them = findViewById(R.id.btnMoreToolbarCommon);
        back = findViewById(R.id.btnBackToolbarCommon);
        title = findViewById(R.id.titlToolbarCommon);
        Intent i = getIntent();
        idbl = i.getStringExtra("idbl");
        idBl = i.getStringExtra("idBl");
        namebl = i.getStringExtra("namebl");
        title.setText("Thêm URL - " + namebl);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                DatabaseHelper dh = DatabaseHelper.getInstance(ActivityAddUrl.this);
                DateTimeFormatter dtf = null;
                dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                // Update từng url
                if(listurl.size()!=0){
                for (int i = 0; i < listurl.size(); i++) {
                    if (listurl.get(i).getText().toString() != "") {
                        if (!dh.CheckIsDataAlreadyInDBorNot("url", "url", listurl.get(i).getText().toString())) {
                            // Đặt thông tin chặn với
                            dh.updateUrl(listurl.get(i).getText().toString(), dtf.format(now), "", idBl);
                            String datasend = "";
                            datasend = "{\"loaiCapNhat\":\"" + "Them" + "\",\"maDanhSach\":\"" + idBl + "\",\"thongTinChan\":\"" + listurl.get(i).getText().toString() + "\",\"bieuThucChinhQuy\": false }";
                            JSONObject json = null;
                            try {
                                json = new JSONObject(datasend);
                                Log.i(TAG, String.format("Cập nhật blackList %s", json.toString()));
                                SocketHandler.getSocket().emit(EventTypes.capNhatBlackList, json);
                            } catch (JSONException e) {
                                Log.e("error", "Có lỗi xảy ra khi thêm url vào blacklist");
                                Toast.makeText(ActivityAddUrl.this,
                                        "Có lỗi xảy ra khi thêm blacklist! \n Vui Lòng thử lại sau", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(ActivityAddUrl.this, getString(R.string.messNotSuccess), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                listurl.clear();
                Toast.makeText(ActivityAddUrl.this, getString(R.string.taoURLThanhCong), Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
                finish();}
            }
        });
        btn_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = new EditText(ActivityAddUrl.this);
                et.setInputType(1);
                et.setHint("Nhập domain cần chặn");
                listurl.add(et);
                addurl.addView(et);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}