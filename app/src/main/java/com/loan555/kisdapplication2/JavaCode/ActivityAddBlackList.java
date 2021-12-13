package com.loan555.kisdapplication2.JavaCode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loan555.kisdapplication2.R;
import com.loan555.kisdapplication2.constant.EventTypes;
import com.loan555.kisdapplication2.viewmodel.MainViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAddBlackList extends AppCompatActivity {
    private static final String TAG = "KA.AcAddBlackList";
    EditText tenbl;
    EditText loaibl;
    Button taobl;
    ImageView back;
    TextView title;
    ConstraintLayout toolbar;
    MainViewModel mainViewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_black_list);
        tenbl = findViewById(R.id.edit_tenbl);
        loaibl = findViewById(R.id.edit_loaibl);
        taobl = findViewById(R.id.btn_taobl);
        toolbar = findViewById(R.id.toolbar);
        back = toolbar.findViewById(R.id.btnBackToolbarCommon);
        title = toolbar.findViewById(R.id.titlToolbarCommon);
        title.setText(R.string.taoDanhSachChan);
        toolbar.findViewById(R.id.btnMoreToolbarCommon).setVisibility(View.GONE);
        taobl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper dh = DatabaseHelper.getInstance(ActivityAddBlackList.this);
                // Kiểm tra blackList đã tồn tại trong db hay chưa?
                if (!dh.CheckIsDataAlreadyInDBorNot("blacklist", "namebl", tenbl.getText().toString())) {
                    // Create temporary blackList
                    dh.updateBlackList(tenbl.getText().toString(), loaibl.getText().toString(), "");
                    String datasend = "";
                    datasend = "{\"loaiCapNhat\":\"" + "Them" + "\",\"tenDanhSach\":\"" + tenbl.getText().toString() + "\"}";
                    JSONObject json = null;
                    try {
                        json = new JSONObject(datasend);
                    } catch (JSONException e) {
                        Log.e("error" , "Có lỗi xảy ra trong quá trình thêm blackList");
                        e.printStackTrace();
                    }
                    SocketHandler.getSocket().emit(EventTypes.capNhatBlackList, json);
                    setResult(Activity.RESULT_OK);
                    finish();
                    Toast.makeText(ActivityAddBlackList.this, getString(R.string.loadThemBlackList), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivityAddBlackList.this, getString(R.string.messNotSuccess), Toast.LENGTH_SHORT).show();
                }
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