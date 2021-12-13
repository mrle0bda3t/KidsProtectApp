package com.loan555.kisdapplication2.JavaCode.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.loan555.kisdapplication2.JavaCode.ActivityBlockurl;
import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.JavaCode.SocketHandler;
import com.loan555.kisdapplication2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.BlacklistViewHolder> {
    private static final String TAG = "KA.BlacklistAdapter";
    Context context;
    List<Blacklist> blackLists;
    ContactAdapter mCt;

    public BlacklistAdapter(Context context, List<Blacklist> blackLists, ContactAdapter mContact) {
        this.context = context;
        this.blackLists = blackLists;
        this.mCt = mContact;
    }

    @NonNull
    @Override
    public BlacklistAdapter.BlacklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.blacklistitems, parent, false);

        return new BlacklistAdapter.BlacklistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistAdapter.BlacklistViewHolder holder, int position) {
        holder.tvname.setText(blackLists.get(position).getNameBl());
        holder.xoabl.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
        holder.blacklistView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityBlockurl.class);
                i.putExtra("idbl", blackLists.get(position).getIdBl());
                i.putExtra("idBl", blackLists.get(position).getIdbl());
                i.putExtra("namebl", blackLists.get(position).getNameBl());
                context.startActivity(i);
            }
        });
    }

    private void showDialog(int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Bạn có chắc chắn muốn xóa không?");
        dialog.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBlackList(position);
            }
        });
        dialog.setCancelable(true);
        dialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.create().show();
    }

    private void deleteBlackList(int position) {
        DatabaseHelper dh = DatabaseHelper.getInstance(context);
        dh.deleteBlacklist(blackLists.get(position).getIdBl());
        String datasend = "";
        datasend = "{\"loaiCapNhat\":\"" + "Xoa" + "\",\"maDanhSach\":\"" + blackLists.get(position).getIdbl() + "\"}";
        JSONObject json = null;
        try {
            json = new JSONObject(datasend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SocketHandler.getSocket().emit("capNhatBlackList", json);
        blackLists.remove(position);
        notifyItemRemoved(position);
        if (blackLists.isEmpty()) mCt.checkListEmpty(true);
        else mCt.checkListEmpty(false);
    }

    @Override
    public int getItemCount() {
        return blackLists.size();
    }

    public static class BlacklistViewHolder extends RecyclerView.ViewHolder {
        LinearLayout blacklistView;
        TextView tvname;
        ImageButton xoabl;

        public BlacklistViewHolder(@NonNull View itemView) {
            super(itemView);
            blacklistView = itemView.findViewById(R.id.blacklistitem);
            tvname = itemView.findViewById(R.id.txtName);
            xoabl = itemView.findViewById(R.id.btn_xoabl);
        }
    }

    public interface ContactAdapter {
        void checkListEmpty(Boolean isEmpty);
    }
}
