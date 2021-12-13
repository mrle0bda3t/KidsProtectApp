package com.loan555.kisdapplication2.JavaCode.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.Url;
import com.loan555.kisdapplication2.JavaCode.SocketHandler;
import com.loan555.kisdapplication2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlViewHolder> {
    private static final String TAG = "KA.UrlAdapter";
    Context context;
    List<Url> urlList;
    ContactAdapter mContact;

    public UrlAdapter(Context context, List<Url> urlList, ContactAdapter contactAdapter) {
        this.context = context;
        this.urlList = urlList;
        this.mContact = contactAdapter;
    }

    @NonNull
    @Override
    public UrlAdapter.UrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.urlitems, parent, false);
        return new UrlAdapter.UrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlAdapter.UrlViewHolder holder, int position) {
        DatabaseHelper dh = DatabaseHelper.getInstance(context);
        holder.tvurl.setText(new StringBuilder("").append(urlList.get(position).getUrl()));
        holder.tvtime.setText(new StringBuilder("").append(urlList.get(position).getTime()));
        holder.urllistView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.inflate(R.menu.url);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int menu = menuItem.getItemId();
                        boolean result = false;
                        switch (menu) {
                            case R.id.menu_unblock:
                                dh.deleteUrl(urlList.get(position).getIdurl());
                                String datasend = "";
                                datasend = "{\"loaiCapNhat\":\"" + "Xoa" + "\",\"maThongTinChan\":\"" + urlList.get(position).getIdthongtinchan() + "\"}";
                                Log.d("ThongTinDaChan", datasend);
                                JSONObject json = null;
                                try {
                                    json = new JSONObject(datasend);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SocketHandler.getSocket().emit("capNhatBlackList", json);
                                urlList.remove(position);
                                notifyItemRemoved(position);
                                if (urlList.isEmpty()) mContact.checkListEmpty(true);
                                else mContact.checkListEmpty(false);
                                result = true;
                                break;
                        }
                        return result;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public static class UrlViewHolder extends RecyclerView.ViewHolder {

        LinearLayout urllistView;
        TextView tvurl;
        TextView tvtime;

        public UrlViewHolder(@NonNull View itemView) {
            super(itemView);

            urllistView = itemView.findViewById(R.id.urllistview);
            tvurl = itemView.findViewById(R.id.tvurl);
            tvtime = itemView.findViewById(R.id.tvtime);
        }
    }

    public interface ContactAdapter {
        void checkListEmpty(Boolean isEmpty);
    }
}
