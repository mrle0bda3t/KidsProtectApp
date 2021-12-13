package com.loan555.kisdapplication2.JavaCode.Fragment;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loan555.kisdapplication2.JavaCode.ActivityKid;
import com.loan555.kisdapplication2.JavaCode.Adapter.HistoryAdapterDay;
import com.loan555.kisdapplication2.JavaCode.DatabaseHelper;
import com.loan555.kisdapplication2.JavaCode.Model.History;
import com.loan555.kisdapplication2.JavaCode.Model.HistoryDay;
import com.loan555.kisdapplication2.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private static final String TAG = "KA.HistoryAdapter";
    HistoryAdapterDay historyAdapter;
    String idkid;
    RecyclerView historyRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvEmpty;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActivityKid activity = (ActivityKid) getActivity();
        idkid = activity.getMyData();
        historyRecyclerView = view.findViewById(R.id.recycler_history);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        tvEmpty = view.findViewById(R.id.tvEmptyHistory);
        loadHistory();
        initEvent();
    }

    private void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHistory();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadHistory() {
        DatabaseHelper dh = DatabaseHelper.getInstance(getContext());
        List<History> histories = new ArrayList<>();
        Cursor datahistory = dh.getHistory(idkid);
        if (datahistory.getCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.INVISIBLE);
            int idkid = datahistory.getColumnIndex("idkid");
            int url = datahistory.getColumnIndex("daddr");
            int time = datahistory.getColumnIndex("time");
            int status = datahistory.getColumnIndex("status");
            int tenapp = datahistory.getColumnIndex("nameapp");
            while (datahistory.moveToNext()) {
                History history = new History();
                history.setTreEm(datahistory.getString(idkid));
                history.setDiaChi(datahistory.getString(url));
                history.setThoiGianYeuCau(datahistory.getString(time));
                history.setTinhTrang(datahistory.getString(status));
                history.setTenapp(datahistory.getString(tenapp));
                histories.add(history);
            }
        }
        clearList(histories);
        setHistoryRecycler(clearListDay(histories));

    }

    private void clearList(List<History> histories) {
        int i = 0;
        while (i < histories.size() - 2) {
            while (i < histories.size() - 2 && histories.get(i).diaChi.equals(histories.get(i + 1).diaChi)) {
                histories.remove(i + 1);
            }
            i++;
        }
    }

    private ArrayList<HistoryDay> clearListDay(List<History> histories) {
        ArrayList<HistoryDay> historyDayList = new ArrayList<>();
        for (int i = 0; i < histories.size(); i++) {
            HistoryDay historyDay = new HistoryDay();
            String day = histories.get(i).getThoiGianYeuCau().substring(0, 10);
            if (i > 0 && historyDayList.get(historyDayList.size() - 1).getDay().equals(day)) {
                historyDayList.get(historyDayList.size() - 1).addToList(histories.get(i));
            } else {
                historyDay.setDay(day);
                historyDay.addToList(histories.get(i));
                historyDayList.add(historyDay);
            }
        }
        return historyDayList;
    }

    private void setHistoryRecycler(ArrayList<HistoryDay> historyDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        historyRecyclerView.setLayoutManager(layoutManager);
        historyAdapter = new HistoryAdapterDay(getContext(), historyDataList);
        historyRecyclerView.setAdapter(historyAdapter);
    }
}