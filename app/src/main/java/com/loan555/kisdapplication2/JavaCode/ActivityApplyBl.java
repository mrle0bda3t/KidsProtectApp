package com.loan555.kisdapplication2.JavaCode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.loan555.kisdapplication2.JavaCode.Adapter.ApplyBlAdapter;
import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityApplyBl extends AppCompatActivity {
    private static final String TAG = "KA.AcApplyBl";
    RecyclerView applyblRecyclerView;
    ApplyBlAdapter applyBlAdapter;
    List<Blacklist> blacklists = new ArrayList<>();
    ImageView back;
    FrameLayout btntacvu;
    Button btn_xacnhan;
    String idkid;
    TextView tentre;
    String namekid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_bl);
        applyblRecyclerView = findViewById(R.id.recycler_applybl);
        back = findViewById(R.id.back);
        btntacvu = findViewById(R.id.btntacvu);
        btn_xacnhan = findViewById(R.id.btn_xacnhan);
        tentre = findViewById(R.id.tentre);
        Intent i = getIntent();
        idkid = i.getStringExtra("idkid");
        namekid = i.getStringExtra("namekid");
        tentre.setText(namekid);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityApplyBl.this, ActivityKid.class);
                i.putExtra("idkid",idkid);
                i.putExtra("namekid",namekid);
                ActivityApplyBl.this.startActivity(i);
            }
        });
        loadApplybl(idkid);
    }
    private void loadApplybl(String idkid){
        DatabaseHelper dh = DatabaseHelper.getInstance(this);
        Cursor datablacklist = dh.getBlacklist();
        if (datablacklist.getCount() == 0) {
            Log.d("Data","Không có dữ liệu áp dụng blackList");
        }
        else{
            int idbl = datablacklist.getColumnIndex("ID");
            int idBl = datablacklist.getColumnIndex("idbl");
            int namebl = datablacklist.getColumnIndex("namebl");
            int typebl = datablacklist.getColumnIndex("typebl");
            while (datablacklist.moveToNext()) {
                Blacklist bl = new Blacklist();
                Log.d("Data", String.format("Tên blackList : %s" ,datablacklist.getString(namebl)));
                if (dh.CheckIsBlAlreadyInApplyblorNot("applybl",datablacklist.getString(idBl), idkid)) {
                    Log.d("Check", String.format("Thông tin áp dụng đã có trong db ! %s" , datablacklist.getString(idBl)));
                    bl.setIdkid(idkid);
                }
                else{
                    Log.d("Check", String.format("Thông tin áp dụng chưa có trong db ! %s" , datablacklist.getString(idBl)));
                    bl.setIdkid("");
                }
                bl.setIdbl(datablacklist.getString(idBl));
                bl.setIdBl(datablacklist.getString(idbl));
                bl.setNameBl(datablacklist.getString(namebl));
                bl.setTypeBl(datablacklist.getString(typebl));
                blacklists.add(bl);
            }
            setApplyblRecycler(blacklists,idkid);
        }
//        DatabaseHelper dh = DatabaseHelper.getInstance(this);
//
//        Cursor datablacklist = dh.getApplybl(idkid);
//        if (datablacklist.getCount() == 0) {
//            Log.d("Khong co du lieu","Khong co du lieu");
//        }
//        else{
//            int idbl = datablacklist.getColumnIndex("idbl");
//            while (datablacklist.moveToNext()) {
//                Cursor databl = dh.getBlacklist(datablacklist.getString(idbl));
//                int ID = datablacklist.getColumnIndex("ID");
//                int namebl = datablacklist.getColumnIndex("namebl");
//                int typebl = datablacklist.getColumnIndex("typebl");
//                while(databl.moveToNext()){
//                    Blacklist bl = new Blacklist();
//                    bl.setIdBl(databl.getString(ID));
//                    bl.setNameBl(databl.getString(namebl));
//                    bl.setTypeBl(databl.getString(typebl));
//                    blacklists.add(bl);
//                }
//            }
//            setApplyblRecycler(blacklists);
//        }
    }
    private void setApplyblRecycler(List<Blacklist> BlacklistDataList,String idkid) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        applyblRecyclerView.setLayoutManager(layoutManager);
        applyBlAdapter = new ApplyBlAdapter(this,BlacklistDataList,idkid);
        applyblRecyclerView.setAdapter(applyBlAdapter);
    }
}