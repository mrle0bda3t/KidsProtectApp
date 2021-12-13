package com.loan555.kisdapplication2.JavaCode.Fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loan555.kisdapplication2.JavaCode.ActivityKid;
import com.loan555.kisdapplication2.JavaCode.Adapter.ApplyBlAdapter;
import com.loan555.kisdapplication2.JavaCode.Adapter.ApplyBlAppAdapter;
import com.loan555.kisdapplication2.JavaCode.Adapter.BlacklistAdapter;
import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.Blacklist;
import com.loan555.kisdapplication2.JavaCode.Model.BlacklistApp;
import com.loan555.kisdapplication2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlacklistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlacklistFragment extends Fragment {
    private static final String TAG = "KA.BlacklistFragment";
    RecyclerView applyblRecyclerView;
    RecyclerView appLyBlAppRv;
    ApplyBlAdapter applyBlAdapter;
    List<Blacklist> blacklists = new ArrayList<>();
    ImageView back;
    FrameLayout btntacvu;
    Button btn_xacnhan;
    String idkid;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlacklistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlacklistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlacklistFragment newInstance(String param1, String param2) {
        BlacklistFragment fragment = new BlacklistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blacklist, container, false);
        applyblRecyclerView = view.findViewById(R.id.recycler_applybl);
        appLyBlAppRv = view.findViewById(R.id.recycler_appblApp);
        back = view.findViewById(R.id.back);
        btntacvu = view.findViewById(R.id.btntacvu);
        btn_xacnhan = view.findViewById(R.id.btn_xacnhan);
        ActivityKid activity = (ActivityKid) getActivity();
        idkid = activity.getMyData();

        loadApplybl(idkid);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerViewBlApp();
    }

    private void initRecyclerViewBlApp() {
        DatabaseHelper dh = DatabaseHelper.getInstance(getContext());
        appLyBlAppRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        Cursor dataApp = dh.getAppBl(idkid);
        if(dataApp.getCount()==0){
            Log.i("infor BlacklistFragment", "Không có dữ liệu trong bảng app");
        }
        else{
            ArrayList<BlacklistApp> bl = new ArrayList<>();
            int activate = dataApp.getColumnIndex("activate");
            int id = dataApp.getColumnIndex("ID");
            int nameapp = dataApp.getColumnIndex("nameapp");
            int timeStart = dataApp.getColumnIndex("timeStart");
            int timeEnd = dataApp.getColumnIndex("timeEnd");
            while (dataApp.moveToNext()) {
                BlacklistApp blapp = new BlacklistApp();
                if (dh.CheckIsAppInDB(idkid,dataApp.getString(nameapp))) {
                    blapp.setIdkid(idkid);
                } else {
                    blapp.setIdkid(null);
                }
                blapp.setId(dataApp.getString(id));
                blapp.setNameBl(dataApp.getString(nameapp));
                blapp.setTimeStart(dataApp.getString(timeStart));
                blapp.setTimeEnd(dataApp.getString(timeEnd));
                blapp.setActivate(Integer.parseInt(dataApp.getString(activate)));
                switch (dataApp.getString(nameapp)) {
                    case "Chrome":
                        blapp.setImg(R.drawable.chrome);
                        break;
                    case "Messenger":
                        blapp.setImg(R.drawable.messenger);
                        break;
                    case "Gmail":
                        blapp.setImg(R.drawable.gmail);
                        break;
                    case "TikTok":
                        blapp.setImg(R.drawable.tiktok);
                        break;
                    case "Instagram":
                        blapp.setImg(R.drawable.instagram);
                        break;
                    case "YouTube":
                        blapp.setImg(R.drawable.youtube);
                        break;
                    case "Zalo":
                        blapp.setImg(R.drawable.zalo);
                        break;
                    case "Facebook":
                        blapp.setImg(R.drawable.facebook);
                        break;
                    default:
                        blapp.setImg(R.drawable.icondefault);
                }
                bl.add(blapp);
            }
            ApplyBlAppAdapter applyBlAppAdapter = new ApplyBlAppAdapter(getContext(), bl, idkid);
            appLyBlAppRv.setAdapter(applyBlAppAdapter);
            applyBlAppAdapter.notifyDataSetChanged();
        }
    }

    private void loadApplybl(String idkid) {
        DatabaseHelper dh = DatabaseHelper.getInstance(getContext());
        Cursor datablacklist = dh.getBlacklist();
        if (datablacklist.getCount() == 0) {
            Log.i("infor BlacklistFragment", "Không có dữ liệu trong bảng app");
        } else {
            int idbl = datablacklist.getColumnIndex("ID");
            int idBl = datablacklist.getColumnIndex("idbl");
            int namebl = datablacklist.getColumnIndex("namebl");
            int typebl = datablacklist.getColumnIndex("typebl");
            while (datablacklist.moveToNext()) {
                Blacklist bl = new Blacklist();
                if (dh.CheckIsBlAlreadyInApplyblorNot("applybl", datablacklist.getString(idBl), idkid)) {
                    bl.setIdkid(idkid);
                } else {
                    bl.setIdkid("");
                }
                bl.setIdbl(datablacklist.getString(idBl));
                bl.setIdBl(datablacklist.getString(idbl));
                bl.setNameBl(datablacklist.getString(namebl));
                bl.setTypeBl(datablacklist.getString(typebl));
                blacklists.add(bl);
            }
            setApplyblRecycler(blacklists, idkid);
        }

    }

    private void setApplyblRecycler(List<Blacklist> BlacklistDataList, String idkid) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        applyblRecyclerView.setLayoutManager(layoutManager);
        applyBlAdapter = new ApplyBlAdapter(getContext(), BlacklistDataList, idkid);
        applyblRecyclerView.setAdapter(applyBlAdapter);
    }
}