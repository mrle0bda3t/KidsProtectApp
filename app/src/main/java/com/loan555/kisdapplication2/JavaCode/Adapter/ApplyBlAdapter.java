package com.loan555.kisdapplication2.JavaCode.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.JavaCode.SocketHandler;
import com.loan555.kisdapplication2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApplyBlAdapter extends RecyclerView.Adapter<ApplyBlAdapter.ApplyblViewHolder>{
    private static final String TAG = "KA.ApplyBlAdapter";
    Context context;
    List<Blacklist> blackLists;
    String idkid;
    public ApplyBlAdapter(Context context, List<Blacklist> blackLists, String idkid) {
        this.context = context;
        this.blackLists = blackLists;
        this.idkid = idkid;
    }

    @NonNull
    @Override
    public ApplyBlAdapter.ApplyblViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.applyblitems, parent, false);
        return new ApplyBlAdapter.ApplyblViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyBlAdapter.ApplyblViewHolder holder, int position) {
        if(blackLists.get(position).getIdkid()!=""){
            holder.btn_apply.setChecked(true);
        }
        else{
            holder.btn_apply.setChecked(false);
        }
        holder.txtName.setText(blackLists.get(position).getNameBl());
        holder.btn_apply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseHelper dh = DatabaseHelper.getInstance(context);
                if(isChecked){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    if(!dh.CheckIsBlAlreadyInApplyblorNot("applybl",blackLists.get(position).getIdbl(),idkid)){
                        dh.updateApplybl(blackLists.get(position).getIdbl(),idkid,dtf.format(now));
                    }
                    String datasend = "";
                    datasend = "{\"maBlackList\":\""+blackLists.get(position).getIdbl()+"\",\"maTreEm\":\""+idkid+"\",\"loaiApDung\":\""+"ApDung"+"\"}";
                    Log.d("aaaaaaaa",datasend);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(datasend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SocketHandler.getSocket().emit("capNhatThongTinApDung",json);
                    Toast.makeText(context, "AP DUNG THANH CONG", Toast.LENGTH_LONG).show();
                }
                else{
                    if(dh.CheckIsBlAlreadyInApplyblorNot("applybl",blackLists.get(position).getIdbl(),idkid)) {
                        dh.deleteApplybl(idkid, blackLists.get(position).getIdbl());
                    }
                    String datasend = "";
                    datasend = "{\"maBlackList\":\""+blackLists.get(position).getIdbl()+"\",\"maTreEm\":\""+idkid+"\",\"loaiApDung\":\""+"BoApDung"+"\"}";
                    JSONObject json = null;
                    try {
                        json = new JSONObject(datasend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SocketHandler.getSocket().emit("capNhatThongTinApDung",json);
                    Toast.makeText(context, "BO DUNG THANH CONG", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return blackLists.size();
    }

    public  static class ApplyblViewHolder extends RecyclerView.ViewHolder{

        LinearLayout applyitems;
        Switch btn_apply;
        TextView txtName;
        public ApplyblViewHolder(@NonNull View itemView) {
            super(itemView);
            applyitems = itemView.findViewById(R.id.applyblitems);
            btn_apply = itemView.findViewById(R.id.btn_apply);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}
